<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>子权限管理</title>
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
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.metadata.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$('#sub').removeAttr('disabled');
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
					"permissionItem.name":{
						required:true,
						maxlength:64,
						minlength:4,
						rangelength:[4,64]
						},
					"permissionItem.methodName":{
						required:true
						},
					"permissionItem.orderView":{
						required:true,
						digits:true
						}
				},
				success: function(label) {
					label.addClass("valid").html("<img src='${ctx}/images/ok.gif' border='0'/>")
				}
			});
		});	
	</script>
  </head>
  <body>  
    <div id="main">
 		<c:choose>
   			<c:when test="${permission.id == null}">
   			<div class="titlediv">您所在的位置：系统管理->菜单管理->子权限添加</div>
   			</c:when>
   			<c:otherwise>
   			<div class="titlediv">您所在的位置：系统管理->菜单管理->子权限修改</div>
   			</c:otherwise>
   		</c:choose>  	
    	<div id="content" class="margin_10">
    	<form id="theform" action="${ctx}/user/permissionItem.do" method="post">
    		<input type="hidden" name="action" value="manage"/>
    		<input type="hidden" name="permissionItem.id" value="${permissionItem.id}"/>
    		<input type="hidden" name="permissionItem.permissionId" value="${permissionItem.permissionId}"/>
    		<div id="form">
	    		<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
		    		<tr><td class="alignright"><span class="red spanmargin">*</span><span>子权限名称：</span></td><td class="alignleft"><input type="text" name="permissionItem.name" maxlength="16" value="${permissionItem.name}" /></td></tr>
		    		<tr><td class="alignright"><span>子权限排序值：</span></td><td class="alignleft"><input type="text" name="permissionItem.orderView" value="${permissionItem.orderView}" /></td></tr>	
		    		<tr>
		    			<td class="alignright"><span>是否有效：</span></td>
		    			<td class="alignleft">
		    				<select name="permissionItem.valid">
		    				<c:choose>
				    			<c:when test="${permissionItem.valid == false}">
				    			<option value="true">有效</option>
		    					<option value="false" selected="selected">无效</option>
				    			</c:when>
				    			<c:otherwise>
				    			<option value="true" selected="selected">有效</option>
		    					<option value="false">无效</option>
				    			</c:otherwise>
				    		</c:choose>
		    			</select>
		    			</td>
		    		</tr>
		    		<tr><td class="alignright"><span class="red spanmargin">*</span><span>子权限控制方法名称：</span></td><td class="alignleft"><input type="text" name="permissionItem.methodName" value="${permissionItem.methodName}" /></td></tr>
		    		<tr><td class="alignright"><span>备注：</span></td><td class="alignleft"><textarea name="permissionItem.memo" cols="30" rows="5">${permissionItem.memo}</textarea></td></tr>
	    		</table>
    		</div>
    		<div id="foot"><center><input id="sub" type="submit" value="提交 "/></center></div>
    		<div class="margin_10"><center><a href="${ctx}/user/permissionItem.do?permissionItem.permissionId=${permissionItem.permissionId}" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
    	</form>
    	</div>
    </div>
  </body>
</html>
