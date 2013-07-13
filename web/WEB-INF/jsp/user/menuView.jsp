<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>菜单管理</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="keywords" content="" />
	<meta http-equiv="description" content="" />
	<link href="${ctx}/css/main.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
  </head>
  <body>
    <div id="main">
   		<div class="titlediv">您所在的位置：系统管理->菜单管理->菜单查看</div> 	
    	<div class="margin_10">
    		<div>
	    		<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
	    			<tr>
		    			<td class="alignright_30">编码：</td><td class="alignleft">${menu.id}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">菜单名：</td><td class="alignleft">${menu.name}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">菜单链接：</td><td class="alignleft">${menu.url}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">菜单排序值：</td><td class="alignleft">${menu.orderView}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">是否有效：</td><td class="alignleft"><s:if test="menu.valid == true">有效</s:if><s:else>无效</s:else></td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">备注：</td><td class="alignleft">${menu.memo}</td>
		    		</tr>
	    		</table>
    		</div>
    		<div class="margin_10"><center><a href="${ctx}/user/menu.do" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
    	</div>
    </div>
  </body>
</html>
