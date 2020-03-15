package com.theodore.servlet;

import com.theodore.anno.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TheodoreDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNameList = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    private List<Handler> handlerList = new ArrayList<>();

    private Map<String, String[]> securityMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1、加载配置文件 springMvc
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2、扫描相关类， 扫描注解
        doScan(properties.getProperty("scanPackage"));

        // 3、初始化bean对象（实现ioc容器，基于注解）
        doInstance();

        // 4、实现依赖注入
        doAutowire();

        // 5，构造一个handlerMapping处理器，处理url 和Method建立关系
        initHandlerMapping();

        // 6.设置权限
        setSecurity();

        System.out.println("theodoreMVC 初始化完成。。。。。");

        // 等待处理请求。。。。
    }

    // 为类和方法设置权限
    private void setSecurity() {
        if (ioc.isEmpty()){return;}
        // 遍历ioc容器，看类上有哪些权限
        for (Map.Entry<String, Object> entry : ioc.entrySet()){
            Class<?> aClass = entry.getValue().getClass();
            // 如果不是Controller，且没有权限，跳过
            if (!aClass.isAnnotationPresent(TheodoreController.class)
                    && !aClass.isAnnotationPresent(Security.class)){continue;}

            Security annotation = aClass.getAnnotation(Security.class);
            String[] value = annotation.value();
            securityMap.put(aClass.getName(), value);
        }


    }

    // 5，构造一个handlerMapping处理器，处理url 和Method建立关系
    private void initHandlerMapping() {
        if (ioc.isEmpty()){return;}
        // 遍历ioc容器
        for (Map.Entry<String, Object> entry : ioc.entrySet()){
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(TheodoreController.class)){continue;}

            String baseUrl = "";
            // 类上注解
            if (aClass.isAnnotationPresent(TheodoreRequestMapping.class)){
                TheodoreRequestMapping annotation = aClass.getAnnotation(TheodoreRequestMapping.class);
                baseUrl = annotation.value();
            }

            // 方法上的注解
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                // 没有
                if (!method.isAnnotationPresent(TheodoreRequestMapping.class)){
                    continue;
                }
                // 标识处理
                TheodoreRequestMapping annotation = method.getAnnotation(TheodoreRequestMapping.class);
                //
                String annotationUrl = annotation.value();
                String url = baseUrl + annotationUrl;

                //
                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(url));

                // 计算参数位置
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class){
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), j);
                    }else {
                        handler.getParamIndexMapping().put(parameter.getName(), j);
                    }

                }

                // 设置类的权限
                if (method.isAnnotationPresent(Security.class)){
                    Security securityAnnotation = method.getAnnotation(Security.class);
                    handler.setSecurity(securityAnnotation.value());
                }

                handlerList.add(handler);

            }
        }
    }

    // 基于注解的依赖注入
    private void doAutowire() {
        if (ioc.isEmpty()){return;}

        //有对象
        for (Map.Entry<String, Object> entry: ioc.entrySet()){
            // 字段名称
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (!field.isAnnotationPresent(TheodoreAutowire.class)){
                    continue;
                }
                // 有注解
                TheodoreAutowire annotation = field.getAnnotation(TheodoreAutowire.class);
                String beanName = annotation.value();
                if ("".equals(beanName.trim())){
                     beanName = field.getType().getName();
                }
                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    // ioc
    // 基于classNameList缓存的类全限的类名，及反射
    private void doInstance()  {
        if (classNameList.size() == 0){return;}
        try{
            for (int i = 0; i < classNameList.size(); i ++){
                String className = classNameList.get(i);
                Class<?> aClass = Class.forName(className);
                // 区分 controller
                if (aClass.isAnnotationPresent(TheodoreController.class)){
                    // 直接名字小写
                    String simpleName = aClass.getSimpleName();
                    simpleName = toLower(simpleName);
                    Object o = aClass.newInstance();
                    ioc.put(simpleName, o);
                }else if (aClass.isAnnotationPresent(TheodoreService.class)){
                    TheodoreService annotation = aClass.getAnnotation(TheodoreService.class);
                    String beanName = annotation.vaule();
                    if (!"".equals(beanName.trim())){
                        ioc.put(beanName, aClass.newInstance());
                    }else {
                        beanName = toLower(aClass.getSimpleName());
                        ioc.put(beanName, aClass.newInstance());
                    }
                    // 如果有接口，将接口放一份
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        ioc.put(anInterface.getName(), aClass.newInstance());
                    }
                }else {
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private String toLower(String str){
        char[] chars = str.toCharArray();
        if (chars[0] >= 'A' && chars[0] <= 'z'){
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    //扫描类
    // com.theodore.demo --->磁盘上的文件夹
    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.", "/");
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();

        for (File file : files) {
            if (file.isDirectory()) { //子包
                //递归
                doScan(scanPackage + "." + file.getName()); // com.theodore.demo.controller
            } else if (file.getName().endsWith(".class")) {
                // 全类名
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNameList.add(className);
            }

        }
    }

    //加载配置文件
    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // 接受处理请求
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    //处理请求
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Handler handler = getHandler(req);
        if (handler == null){
            resp.getWriter().write("404 Not found");
            return;
        }
        // 权限判断
        //1.类权限
        Object controller = handler.getController();
        String username = req.getParameter("username");
        if (securityMap.containsKey(controller.getClass().getName())){
            // 校验
            String[] strings = securityMap.get(controller.getClass().getName());
            if (!Arrays.asList(strings).contains(username)){
                resp.getWriter().write("403 Not security");
                return;
            }
        }
        //2.方法权限判断
        if (handler.getSecurity() != null && handler.getSecurity().length > 0){
            if (!Arrays.asList(handler.getSecurity()).contains(username)){
                resp.getWriter().write("403 Not security");
                return;
            }
        }

        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        Object[] paraValues = new Object[parameterTypes.length];

        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String, String[]> entry: parameterMap.entrySet()){
            String value = StringUtils.join(entry.getValue(), ",");

            if (!handler.getParamIndexMapping().containsKey(entry.getKey())){
                continue;
            }

            Integer index = handler.getParamIndexMapping().get(entry.getKey());
            paraValues[index] = value;
        }

        Integer integer = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
        paraValues[integer] = req;


        Integer integer1 = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        paraValues[integer1] = resp;

        try {
            Object invoke = handler.getMethod().invoke(handler.getController(), paraValues);
            resp.getWriter().write(invoke.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerList.isEmpty()){
            return null;
        }
        String requestURI = req.getRequestURI();

        for (Handler handler : handlerList) {
            Matcher matcher = handler.getPattern().matcher(requestURI);
            if (!matcher.matches()){
                continue;
            }else {
                return handler;
            }

        }
        return null;
    }


}
