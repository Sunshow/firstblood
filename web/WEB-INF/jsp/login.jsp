<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>乐和彩后台管理系统</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="keywords" content="" />
	<meta http-equiv="description" content="" />
	<link rel="stylesheet" type="text/css" href="${ctx}/css/main.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
	<script>
		$(function(){
			$('body').layout('panel','west').panel();
			/*
			$('.showpage').click(function(){
				var url = $(this).attr('title');
				$('#contentframe').attr('src',url);
			});
			*/
			$.messager.show({
				title:'提示信息',
				msg:'欢迎登录乐和彩后台管理系统',
				timeout:5000
			});
		});
	</script>
  </head>
  <body class="easyui-layout">	
  	<div region="north" border="false" style="height:60px;"><img src="${ctx}/images/title.png"/></div>
	<div region="west" split="true" title="菜单选择" style="width:200px;padding:10px;">
		<div id="leftmenu" style="width:180px;">
			<ul class="easyui-tree" animate="true">
			<s:iterator value="menus" id="menuBean">
	    		<li state="closed"><span><s:property value="#menuBean.menu.name"/></span>
	    		<s:if test="#menuBean.permissionList != null">
	    		<ul>
	    		<s:iterator value="#menuBean.permissionList">
	    		<li><span><a href="${ctx}${url}" title="${name}" class="showpage" target="contentframe">${name}</a></span></li>
	    		</s:iterator>
	    		</ul>
	    		</s:if>
	    		</li>
	    	</s:iterator>
	    	</ul>
    	</div>
	</div>
	<div id="content" region="center" title="&nbsp;您好，<span class='red'>${userSessionBean.user.name}</span>！欢迎登录！你的角色是：<span class='red'>${userSessionBean.role.name}</span>，你本次访问时间：<fmt:formatDate value='${userSessionBean.user.loginTime}' pattern='yyyy-MM-dd HH:mm:ss'/><c:if test='${userSessionBean.user.lastLoginTime != null}'>，您的上次访问时间：<fmt:formatDate value='${userSessionBean.user.lastLoginTime}' pattern='yyyy-MM-dd HH:mm:ss'/></c:if>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${ctx}/logout.do'>退出</a>">
		<iframe id="contentframe" name="contentframe" src="${ctx}/welcome.jsp" frameborder="0" width="100%" height="100%"></iframe>
	</div>
  </body>
</html>
