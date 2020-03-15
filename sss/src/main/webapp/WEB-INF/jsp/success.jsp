<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" isELIgnored="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Insert title here</title>
</head>
<body>
<div> <h2>用户信息</h2></div>
      <div>
          <a href="/resume/edit?id=${resume.id}" style="">
              <button>新增</button>
          </a>
          <table>
              <tr>
                  <td>
                      序号
                  </td>
                  <td>
                      姓名
                  </td>
                  <td>
                      地址
                  </td>
                  <td>
                      手机
                  </td>
                  <td>
                      操作
                  </td>
              </tr>
              <c:forEach items="${resumeList}" var="resume" varStatus="status">
                  <tr>
                      <td>
                          ${status.index +1 }
                      </td>
                      <td>
                              ${resume.name}
                      </td>
                      <td>
                              ${resume.address}
                      </td>
                      <td>
                              ${resume.phone}
                      </td>
                      <td>
                      <a href="/resume/edit?id=${resume.id}">修改</a>
                      <a href="/resume/delete?id=${resume.id}">删除</a>
                      </td>
                  </tr>
              </c:forEach>

          </table>

      </div>

</body>
<script type="text/javascript" src="/js/jquery.min.js"></script>

<script>

    function save(){

    }
    // $(function () {
    //
    //     $("#ajaxBtn").bind("click",function () {
    //         // 发送ajax请求
    //         $.ajax({
    //             url: '/demo/handle07',
    //             type: 'POST',
    //             data: '{"id":"1","name":"李四"}',
    //             contentType: 'application/json;charset=utf-8',
    //             dataType: 'json',
    //             success: function (data) {
    //                 alert(data.name);
    //             }
    //         })
    //
    //     })
    //
    //
    // })
</script>
</html>