<%@page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>用户登录 - ${SITE_NAME}</title>
    <jsp:include page="/WEB-INF/jsp/global_init.jsp"/>
    <style type="text/css">
        body {
            background-color: #f5f5f5;
        }

        .container {
            padding-top: 40px;
            padding-bottom: 40px;
        }

        .form-signin {
            max-width: 300px;
            padding: 19px 29px 29px;
            margin: 0 auto 20px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            box-shadow: 0 1px 2px rgba(0,0,0,.05);
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }
        .form-signin input[type="text"],
        .form-signin input[type="password"] {
            font-size: 16px;
            height: auto;
            margin-bottom: 15px;
            padding: 7px 9px;
        }

        .verify-code {
            margin-bottom: 20px;
        }
    </style>
    <script type="text/javascript">
        var verifyCodeUrl = "/verifyCode?";
        $(function() {
            $('#verifyImg').click(function () {
                $(this).attr('src', verifyCodeUrl + (new Date()).getTime());
            }).trigger('click');
        });
    </script>
</head>
<body>

<div class="navbar navbar-inverse">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="brand" href="#">${NAVBAR_TITLE}</a>
        </div>
    </div>
</div>

<div class="container">

    <form class="form-signin" action="/login.do" method="post">
        <c:if test="${errorMessage != null && errorMessage != ''}">
            <div class="alert alert-error">
                ${errorMessage}
            </div>
        </c:if>

        <h2 class="form-signin-heading">登录</h2>
        <input type="text" name="username" class="input-block-level" placeholder="用户名"/>
        <input type="password" name="password" class="input-block-level" placeholder="密码"/>
        <c:if test="${enableVerifyCode}">
            <input type="text" name="verifyCode" class="input-block-level" placeholder="验证码"/>
            <div class="controls controls-row">
                <img class="verify-code img-polaroid" id="verifyImg" title="看不清？点击图片刷新"/>
            </div>
        </c:if>
        <div class="controls controls-row">
            <button class="btn btn-large btn-primary" type="submit">登录</button>
        </div>
    </form>

</div>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

</body>
</html>
