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
	<link href="${ctx}/js/jscalendar/skins/aqua/theme.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.validate.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery.metadata.js"></script>
	<script type="text/javascript" src="${ctx}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/jscalendar/calendar.js"></script>
	<script type="text/javascript" src="${ctx}/js/jscalendar/lang/cn_utf8.js"></script>
	<script type="text/javascript" src="${ctx}/js/jscalendar/calendar-setup.js"></script>
	<script type="text/javascript">
		$(function(){
			$('#sub').removeAttr('disabled');
			Calendar.setup(
				    {
				      inputField  : "beginDate",    // ID of the input field
				      ifFormat    : "%Y-%m-%d %H:%M:%S",   // the date format
				      button      : "beginDateTrigger",      // ID of the button
				      showsTime   : true,
				      date		  : Calendar.initNewDate(),
				      timeFormat  : "24"		      
				    }
				  );
				Calendar.setup(
				    {
				      inputField  : "endDate",    // ID of the input field
				      ifFormat    : "%Y-%m-%d %H:%M:%S",   // the date format
				      button      : "endDateTrigger",      // ID of the button
				      showsTime   : true,
				      date		  : Calendar.initNewDate("23:59:59"),
					  timeFormat  : "24"			  
				    }
				  );
				  
			$("tr.beover").mouseover(function() {
				$(this).addClass("over");
			}).mouseout(function() {
				$(this).removeClass("over");
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
					"username":{
							maxlength:64,
							minlength:1,
							rangelength:[1,64]
						},
					"name":{
							maxlength:64,
							minlength:1,
							rangelength:[1,64]
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
    	<div>
  			<div class="titlediv">您所在的位置：系统管理->日志管理->日志列表</div>
  			<div>
  				<div>
  					<div class="pagediv">条件查询</div>
  					<div>
	  					<form id="theform" action="${ctx}/log/log.do" method="post">
	  					<input type="hidden" name="action" value="query"/>
				    	<table cellpadding="0" cellspacing="0" border="0" style="width:96%" class="querytab">
				    		<tr>
				    			<td class="alignleft">用户名:<input type="text" name="username" maxlength="16" value="${username}" /></td>
				    			<td class="alignleft">姓名:<input type="text" name="name" maxlength="16" value="${name}" /></td>
				    			<td class="alignleft">日志类型:
					    			<s:select name="logTypeId" list="logTypes" listKey="value" 
									 	listValue="name" headerKey="0" headerValue="全部"/>
				    			</td>
				    			
				    		</tr>
				    		<tr>
				    			<td class="alignleft">链接:<input type="text" name="url" value="${url}" /></td>
				    			<td class="alignleft">action名称:<input type="text" name="actionName" value="${actionName}" /></td>
				    			<td class="alignleft">参数:<input type="text" name="params" value="${params}" /></td>
				    		</tr>
				    		<tr>
				    			<td class="alignleft">IP:<input type="text" name="ip" value="${ip}" /></td>
				    			<td class="alignleft" colspan="2">创建时间:从<input type="text" id="beginDate" name="beginDate" size="20" value="<fmt:formatDate value='${beginDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"/><input type="button" id="beginDateTrigger" value="选择"/>至<input type="text" id="endDate" name="endDate" size="20" value="<fmt:formatDate value='${endDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"/><input type="button" id="endDateTrigger" value="选择"/></td>
				    		</tr>
				    		<tr>
				    			<td colspan="3"><input id="sub" type="submit" value="查询" /></td>
				    		</tr>
				    	</table>
				    	</form>
			    	</div>
  				</div>
  				<div>
  					<div class="pagediv">结果列表</div>
  					<div>
  					<table cellpadding="0" cellspacing="0" border="0" class="querytab">
			    		<tr class="font_bold">
			    			<td>序号</td>
			    			<td>编码</td>
			    			<td>用户名</td>
			    			<td>姓名</td>
			    			<td>日志类型</td>
			    			<td>创建时间</td>
			    			<td>链接</td>
			    			<td>IP</td>
			    			<td>操作</td>
			    		</tr>
			    		<s:iterator value="logs" status="index">
						<tr class="beover">
			    			<td>${index.count}</td>
			    			<td>${id}</td>
			    			<td>${username}</td>
			    			<td>${name}</td>
			    			<td>${logType.name}</td>
			    			<td><s:date name="createTime" format="yyyy-MM-dd HH:mm:ss" /></td>
			    			<td class="alignleft">${url}</td>
			    			<td>${remoteIP}</td>
			    			<td><a href="${ctx}/log/log.do?action=view&log.id=${id}">查看</a></td>
			    		</tr>
			    		</s:iterator>
			    	</table>
			    	</div>
			    	<div class="pagediv"><center>${pageString}</center></div>
  				</div>
  			</div>
  		</div>
  </body>
</html>
