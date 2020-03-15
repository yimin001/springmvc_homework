<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>sss 登录页面</title>


    <script type="text/javascript" src="/js/jquery.min.js"></script>

    <script>
        $(function () {

            $("#ajaxBtn").bind("click",function () {
                // 发送ajax请求
                $.ajax({
                    url: '/demo/handle07',
                    type: 'POST',
                    data: '{"id":"1","name":"李四"}',
                    contentType: 'application/json;charset=utf-8',
                    dataType: 'json',
                    success: function (data) {
                        alert(data.name);
                    }
                })

            })


        })
    </script>


    <style>
        div{
            padding:10px 10px 0 10px;
        }
    </style>
</head>
<body>


<div>
    <h2>登录页面</h2>
    <fieldset>

        <form method="post" action="/resume/login">
            <spa>用户名:</spa>
            <input type="text" name="username"/>
            <br>
            <spa>密码:</spa>
            <input type="password" name="password"/>
            <br>
            <p>${re}</p>
            <input type="submit" value="登录"/>
        </form>


    </fieldset>
</div>








</body>
</html>
