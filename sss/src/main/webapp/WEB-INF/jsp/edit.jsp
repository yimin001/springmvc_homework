<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>sss 编辑页面</title>


    <script type="text/javascript" src="/js/jquery.min.js"></script>

    <script>

    </script>


    <style>
        div{
            padding:10px 10px 0 10px;
        }
    </style>
</head>
<body>


<div>
    <h2>用户信息</h2>
    <fieldset>

        <form method="post" action="/resume/save">
            <input type="hidden" value="${resume.id}" name="id">
            <br>
            <span>姓名：</span>
            <input type="text" name="name" value="${resume.name}" />
            <br>
            <span>地址：</span>
            <input type="text" name="address" value="${resume.address}" />
            <br>
            <span>手机：</span>
            <input type="text" name="phone" value="${resume.phone}" />
            <br>
            <input type="submit" value="保存"/>
        </form>


    </fieldset>
</div>








</body>
</html>
