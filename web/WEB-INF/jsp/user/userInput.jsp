<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>用户管理</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="keywords" content="" />
	<meta http-equiv="description" content="" />
	<link href="${ctx}/css/main.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<style type="text/css">
		#main{
			width:80%;
		}
	</style>
	<script type="text/javascript" src="${ctx}/js/jquery-1.4.2.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.metadata.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		var checkUrl = "${ctx}/user/user.do?action=check";
		$(function() {
			$('#sub').removeAttr('disabled');
			$("#chgpwd").toggle(
			  function () {
				    $(this).val("取消更改");
				    $("#lip").show();
				    $("#licp").show();
				    $("#password").removeClass("ignore");
				    $("#conPassword").removeClass("ignore");
				    $("#pwdflag").val("1");
				  },
			  function () {
			        $(this).val("更改密码");
			        $("#lip").hide();
				    $("#licp").hide();
				    $("#password").addClass("ignore");
				    $("#conPassword").addClass("ignore");
				    $("#pwdflag").val("0");
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
					"user.userName":{
						required:true,
						maxlength:64,
						minlength:4,
						rangelength:[4,64],
						remote: checkUrl
						},
					"user.password":{
							required:true,
							maxlength:64,
							minlength:6,
							rangelength:[6,16]
						},
					"conPassword":{
							required:true,
							maxlength:64,
							minlength:6,
							rangelength:[6,16],
							equalTo:"#password"
						},
					"user.name":{
						required:true,
						maxlength:8,
						minlength:2
						}
				},
				messages:{
					"user.userName":{
						remote: "该用户名已被使用"
					},
					"conPassword":{
						equalTo:"两次输入密码不一致，请重新输入"
					}
				},
				success: function(label) {
					label.addClass("valid").html("<img src='${ctx}/images/ok.gif' border='0'/>")
				},
				ignore: ".ignore"
			});
		});	
	</script>
  </head>
  <body>
  	<div id="main">
 		<c:choose>
   			<c:when test="${user.id == null}">
   			<div class="titlediv">您所在的位置：系统管理->用户管理->用户添加</div>
   			</c:when>
   			<c:otherwise>
   			<div class="titlediv">您所在的位置：系统管理->用户管理->用户修改</div>
   			</c:otherwise>
   		</c:choose>  	
    	<div id="content" class="margin_10">
    	<form id="theform" action="${ctx}/user/user.do" method="post">
    		<input type="hidden" name="action" value="manage"/>
    		<input type="hidden" name="user.id" value="${user.id}"/>
    		<div id="form">
	    		<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
	    			<tr>
	    			<c:choose>
		    			<c:when test="${user.id == null}">
		    			<td class="alignright"><span class="red spanmargin">*</span><span>用户名：</span></td><td class="alignleft"><input type="text" name="user.userName" maxlength="16" value="${user.userName}" /></td>
		    			</c:when>
		    			<c:otherwise>
		    			<td class="alignright">用户名：</td><td class="alignleft">${user.userName}<input type="hidden" name="user.userName" value="${user.userName}" class="ignore" /></td>
		    			</c:otherwise>
		    		</c:choose>
		    		</tr>
		    		<c:choose>
		    			<c:when test="${user.id == null}">
		    			<tr id="libtn" style="display:none;"><td class="alignright"><span>密码：</span></td><td style="text-align:left"><input type="button" id="chgpwd" value="更改密码"/><input type="hidden" id="pwdflag" name="pwdFlag" value="1"/></td></tr>
		    			<tr id="lip"><td class="alignright"><span class="red spanmargin">*</span><span>密码：</span></td><td class="alignleft"><input id="password" type="password" name="user.password" maxlength="16"/></td></tr>
		    			<tr id="licp"><td class="alignright"><span class="red spanmargin">*</span><span>确认密码：</span></td><td class="alignleft"><input type="password" name="conPassword" maxlength="16"/></td></tr>
		    			</c:when>
		    			<c:otherwise>
		    			<tr id="libtn"><td class="alignright"><span>密码：</span></td><td class="alignleft"><input type="button" id="chgpwd" value="更改密码"/><input type="hidden" id="pwdflag" name="pwdFlag" value="0"/></td></tr>
		    			<tr id="lip" style="display:none;"><td class="alignright"><span class="red spanmargin">*</span><span>新密码：</span></td><td class="alignleft"><input id="password" class="ignore" type="password" name="user.password" maxlength="16"/></td></tr>
		    			<tr id="licp" style="display:none;"><td class="alignright"><span class="red spanmargin">*</span><span>确认密码：</span></td><td class="alignleft"><input id="conPassword" class="ignore" type="password" name="conPassword" maxlength="16"/></td></tr>
		    			</c:otherwise>
		    		</c:choose>	
		    		<tr><td class="alignright"><span class="red spanmargin">*</span><span>姓名：</span></td><td class="alignleft"><input type="text" name="user.name" value="${user.name}"  maxlength="8"/></td></tr>
		    		<tr><td class="alignright"><span>电话：</span></td><td class="alignleft"><input type="text" name="user.tel" value="${user.tel}" /></td></tr>
		    		<tr><td class="alignright"><span>邮件：</span></td><td class="alignleft"><input type="text" name="user.email" value="${user.email}" /></td></tr>
		    		<tr><td class="alignright"><span>选择角色：</span></td><td class="alignleft"><s:select name="user.role.id" list="roles" listKey="id" listValue="name"/></td></tr>	
		    		<tr>
		    		<c:choose>
		    			<c:when test="${user.id == null}">
		    			<td class="alignright"><span>是否有效：</span></td><td class="alignleft"><input type="checkbox" name="checkValid" checked="checked"/></td>
		    			</c:when>
		    			<c:when test="${user.id != null && checkValid == 'on'}">
		    			<td class="alignright"><span>是否有效：</span></td><td class="alignleft"><input type="checkbox" name="checkValid" checked="checked"/></td>
		    			</c:when>
		    			<c:otherwise>
		    			<td class="alignright"><span>是否有效：</span></td><td class="alignleft"><input type="checkbox" name="checkValid"/></td>
		    			</c:otherwise>
		    		</c:choose>
		    		</tr>
		    		<tr><td class="alignright"><span>备注：</span></td><td class="alignleft"><textarea name="user.memo" cols="30" rows="5">${user.memo}</textarea></td></tr>
	    		</table>
    		</div>
    		<div id="foot"><center><input id="sub" type="submit" value="提交 "/></center></div>
    		<div class="margin_10"><center><a href="${ctx}/user/user.do" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
    	</form>
    	</div>
    </div>
  </body>
</html>
