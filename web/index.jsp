<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  	<head>
	  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	    <title>欢迎</title>
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="keywords" content="" />
		<meta http-equiv="description" content="" />
		<link rel="stylesheet" type="text/css" href="${ctx}/css/main.css"/>
		<style type="text/css">
			*{
				margin:0 auto;
				padding:0;
			}
		</style>
		<script type="text/javascript" src="${ctx}/js/jquery-1.4.2.js"></script>
		<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
		<script type="text/javascript" src="${ctx}/js/jquery.metadata.js"></script>
		<script type="text/javascript">
			var verifyCodeUrl = "${ctx}/verifyCode?";
			$(function() {
				$('#sub').removeAttr('disabled');
				$('#verifyImg').attr('src',verifyCodeUrl + (new Date()).getTime());
				$('#verifyImg').click(function() {
					$(this).attr('src',verifyCodeUrl + (new Date()).getTime());
				});
				$.validator.setDefaults({
					submitHandler: function(form) {
						$('#sub').attr('disabled','disabled');
						form.submit();
					},
					meta: "validate",
					ignoreTitle: true//解决与google Toolbar的冲突
				});
				$("#theform").validate({
					rules:{
						"userName":{
							required:true,
							maxlength:64,
							minlength:1,
							rangelength:[1,64]
							},
						"password":{
							required:true,
							maxlength:64,
							minlength:1,
							rangelength:[1,64]
						},
						"verifyCode":{
							required:true,
							maxlength:4,
							minlength:1,
							rangelength:[1,4]
						}
					},
					success: function(label) {
						label.addClass("valid").html("<img src='${ctx}/images/ok.gif' border='0'/>");
					}
				});
			});	
		</script>
  	</head>
 	<body>
		<div style="text-align: center;">
			<form id="theform" action="${ctx}/login.do" method="post">
				<div style="height:300px;">
					<h3 style="font-weight:bold;line-height:300px;">欢迎使用乐和彩后台管理系统</h3>
				</div>
				<div style="text-align: center;">
					<span>用户名：
						<input type="text" name="userName" maxlength="16" value="${userName}"/>
					</span>
					<span>密码：
						<input type="password" name="password" maxlength="16" value="${password}"/>
					</span>
					<span>验证码：
						<input type="text" name="verifyCode" maxlength="4"/>
						<img id="verifyImg"/>看不清?点击图片刷新
					</span>
					<span>
						<input id="sub" type="submit" name="sub" value="登录" disabled="disabled"/>
					</span>
				</div>
				<div>
					<span class="red">${errorMessage}</span>
				</div>
			</form>
		</div>
  	</body>
</html>
