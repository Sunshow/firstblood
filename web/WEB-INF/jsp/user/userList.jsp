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
	<link href="${ctx}/js/jscalendar/skins/aqua/theme.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/default/easyui.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/js/easyui/themes/icon.css"/>
	<script type="text/javascript" src="${ctx}/js/jquery-1.4.2.js"></script>
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
				      ifFormat    : "%Y-%m-%d",   // the date format
				      button      : "beginDateTrigger"      // ID of the button
				    }
				  );
			Calendar.setup(
				    {
				      inputField  : "endDate",    // ID of the input field
				      ifFormat    : "%Y-%m-%d",   // the date format
				      button      : "endDateTrigger"      // ID of the button
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
					"userName":{
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
  			<div class="titlediv">您所在的位置：系统管理->用户管理->用户列表</div>
  			<div class="add_div_margin"><a href="${ctx}/user/user.do?action=input" class="easyui-linkbutton" iconCls="icon-add">添加用户</a></div>
  			<div>
  				<div>
  					<div class="pagediv">条件查询</div>
  					<div>
	  					<form id="theform" action="${ctx}/user/user.do" method="post">
	  					<input type="hidden" value="query" name="action"/>
				    	<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
				    		<tr>
				    			<td>用户名:<input type="text" name="userName" maxlength="16" value="${userName}" /></td>
				    			<td>姓名:<input type="text" name="name" maxlength="16" value="${name}" /></td>
				    		</tr>
				    		<tr>
				    			<td>用户角色:
				    				<s:select id="roleID" name="roleID" list="roles" listKey="id" listValue="name" headerKey="-1" headerValue="全部"></s:select>
				    			</td>
				    			<td>是否有效:
				    					<select name="valid">
						    				<c:choose>
								    			<c:when test="${valid == 'false'}">
								    			<option value="">全部</option>
								    			<option value="true">有效</option>
						    					<option value="false" selected="selected">无效</option>
								    			</c:when>
								    			<c:when test="${valid == 'true'}">
								    			<option value="">全部</option>
								    			<option value="true" selected="selected">有效</option>
						    					<option value="false">无效</option>
								    			</c:when>
								    			<c:otherwise>
								    			<option value="" selected="selected">全部</option>
								    			<option value="true">有效</option>
						    					<option value="false">无效</option>
								    			</c:otherwise>
								    		</c:choose>
						    			</select>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>创建时间:从<input type="text" id="beginDate" name="beginDate" size="10" value="<fmt:formatDate value='${beginDate}' pattern='yyyy-MM-dd'/>"/><input type="button" id="beginDateTrigger" value="选择"/></td>
				    			<td>至<input type="text" id="endDate" name="endDate" size="10" value="<fmt:formatDate value='${endDate}' pattern='yyyy-MM-dd'/>"/><input type="button" id="endDateTrigger" value="选择"/></td>
				    		</tr>
				    		<tr>
				    			<td colspan="2"><input id="sub" type="submit" value="查询"/></td>
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
			    			<td>电话</td>
			    			<td>邮件</td>
			    			<td>角色</td>
			    			<td>本次登录时间</td>
			    			<td>上次登录时间</td>
			    			<td>是否有效</td>
			    			<td>备注</td>
			    			<td colspan="2">操作</td>
			    		</tr>
			    		<s:iterator value="users" status="index">
						<tr class="beover">
			    			<td>${index.count}</td>
			    			<td>${id}</td>
			    			<td><a href="${ctx}/user/user.do?action=view&user.id=${id}">${userName}</a></td>
			    			<td>${name}</td>
			    			<td>${tel}</td>
			    			<td>${email}</td>
			    			<td>${role.name}</td>
			    			<td><s:date name="loginTime" format="yyyy-MM-dd HH:mm:ss" /></td>
			    			<td><s:date name="lastLoginTime" format="yyyy-MM-dd HH:mm:ss" /></td>
			    			<td><s:if test="valid == true">有效</s:if><s:else>无效</s:else></td>
			    			<td>${memo}</td>
			    			<td><a href="${ctx}/user/user.do?action=input&user.id=${id}">修改</a></td>
			    			<td><a href="${ctx}/user/user.do?action=del&user.id=${id}" onclick="return confirm('确实要删除吗？');">删除</a></td>
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
