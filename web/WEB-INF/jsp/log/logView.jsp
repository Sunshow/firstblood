<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>日志管理</title>
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
   		<div class="titlediv">您所在的位置：系统管理->日志管理->日志查看</div> 	
    	<div class="margin_10">
    		<div>
	    		<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
	    			<tr>
		    			<td class="alignright_30">编码：</td><td class="alignleft">${log.id}</td>
		    		</tr>
	    			<tr>
		    			<td class="alignright_30">用户名：</td><td class="alignleft">${log.username}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">姓名：</td><td class="alignleft">${log.name}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">日志类型：</td><td class="alignleft">${log.logType.name}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">创建时间：</td><td class="alignleft"><fmt:formatDate value="${log.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">链接：</td><td class="alignleft">${log.url}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">操作名：</td><td class="alignleft">${log.actionName}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">参数：</td><td class="alignleft">${log.params}</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright_30">IP：</td><td class="alignleft">${log.remoteIP}</td>
		    		</tr>
	    		</table>
    		</div>
    		<div class="margin_10"><center><a href="${ctx}/log/log.do" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
    	</div>
    </div>
  </body>
</html>
