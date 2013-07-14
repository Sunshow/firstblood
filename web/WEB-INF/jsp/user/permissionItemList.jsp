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
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$("tr.beover").mouseover(function() {
				$(this).addClass("over");
			}).mouseout(function() {
				$(this).removeClass("over");
			});
		});
	</script>
  </head>
  <body> 	
    <div>
		<div class="titlediv">您所在的位置：系统管理->菜单管理->子权限列表</div>
		<div class="add_div_margin"><a href="${ctx}/user/permissionItem.do?action=input&permissionItem.permissionId=${permissionItem.permissionId}" class="easyui-linkbutton" iconCls="icon-add">添加子权限</a></div>
		<div>
			<div>
				<div class="pagediv">${permission.name}->子权限列表</div>
				<div>
				<table cellpadding="0" cellspacing="0" border="0" class="querytab">
		    		<tr class="font_bold">
		    			<td>序号</td>
		    			<td>编码</td>
		    			<td>子权限名</td>
		    			<td>排序值</td>
		    			<td>子权限控制方法名称</td>
		    			<td>是否有效</td>
		    			<td>备注</td>
		    			<td colspan="2">操作</td>
		    		</tr>
		    		<s:iterator value="permissionItems" status="index">
					<tr class="beover">
		    			<td>${index.count}</td>
		    			<td>${id}</td>
		    			<td><a href="${ctx}/user/permissionItem.do?action=view&permissionItem.id=${id}">${name}</a></td>
		    			<td>${orderView}</td>
		    			<td>${methodName}</td>
		    			<td><s:if test="valid == true">有效</s:if><s:else>无效</s:else></td>
		    			<td>${memo}</td>
		    			<td><a href="${ctx}/user/permissionItem.do?action=input&permissionItem.id=${id}">修改</a></td>
		    		</tr>
		    		</s:iterator>
		    	</table>
		    	</div>
		    	<div class="pagediv">&nbsp;</div>
			</div>
			<div class="margin_10"><center><a href="${ctx}/user/permission.do?permission.menuId=${permission.menuId}" class="easyui-linkbutton" iconCls="icon-reload">返回权限列表</a></center></div>
		</div>
	</div>
  </body>
</html>
