<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>角色管理</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="keywords" content="" />
	<meta http-equiv="description" content="" />
	<link href="${ctx}/css/main.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery-1.4.2.js"></script>
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
		<div class="titlediv">您所在的位置：系统管理->角色管理->角色列表</div>
		<div class="add_div_margin"><a href="${ctx}/user/role.do?action=input" class="easyui-linkbutton" iconCls="icon-add">添加角色</a></div>
		<div>
			<div>
				<div class="pagediv">角色列表</div>
				<div>
				<table cellpadding="0" cellspacing="0" border="0" class="querytab">
		    		<tr class="font_bold">
		    			<td>序号</td>
		    			<td>编码</td>
		    			<td>角色名</td>
		    			<td>是否有效</td>
		    			<td>是否限制IP</td>
		    			<td>有效IP段</td>
		    			<td>备注</td>
		    			<td>操作</td>
		    		</tr>
		    		<s:iterator value="roles" status="index">
					<tr class="beover">
		    			<td>${index.count}</td>
		    			<td>${id}</td>
		    			<td><a href="${ctx}/user/role.do?action=view&role.id=${id}">${name}</a></td>
		    			<td><s:if test="valid == true">有效</s:if><s:else>无效</s:else></td>
		    			<td><s:if test="restriction == true">是</s:if><s:else>否</s:else></td>
		    			<td>${restrictionIp}</td>
		    			<td>${memo}</td>
		    			<td>
		    				<a href="${ctx}/user/role.do?action=input&role.id=${id}">修改</a> | 
		    				<a href="${ctx}/user/role.do?action=input&func=copy&role.id=${id}">复制</a>
		    			</td>
		    		</tr>
		    		</s:iterator>
		    	</table>
		    	</div>
		    	<div class="pagediv">&nbsp;</div>
			</div>
		</div>
	</div>
  </body>
</html>
