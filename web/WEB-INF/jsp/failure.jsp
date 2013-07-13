<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>操作失败</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="keywords" content="" />
	<meta http-equiv="description" content="" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
  </head>
  <body>
    	<div style="margin:10px;"><center><h3>操作失败</h3></center></div>
    	<div style="margin:10px;"><center>${errorMessage}</center></div>
    	<c:choose>
		<c:when test="${forwardUrl != null && forwardUrl != ''}">
			<div style="margin:10px;"><center><a href="${ctx}${forwardUrl}" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
		</c:when>
		<c:otherwise>
			<div style="margin:10px;"><center><a href="javascript:history.back();" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
		</c:otherwise>
	</c:choose>
  </body>
</html>
