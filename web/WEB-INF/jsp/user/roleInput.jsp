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
	<link rel="stylesheet" href="${ctx}/css/treeview.css" type="text/css" />
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
	<script type="text/javascript"	src="${ctx}/js/jquery.treeview.js"></script>
	<script type="text/javascript">	
		var permissionUrl = "${ctx}/user/role.do?action=findPermissions";
		function toggleSelectChildren(node, bool) {
			node.find('ul > li').each(function() {
				$(this).children('input').attr('checked', bool);
				toggleSelectChildren($(this), bool);
			}).end();
		}
		function selectParent(node) {
			if (!node || node.length == 0) {
				return;
			}
			var parentNode = node.parent('ul').parent('li');
			parentNode.children('input').attr('checked', true);
			selectParent(parentNode);
		}
		$(function() {
			$('#sub').removeAttr('disabled');
			var roleid = $('#temp_role_id').val();
			if(roleid != null){
				permissionUrl = permissionUrl + "&role.id=" + roleid;
			}
			
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
					"role.name":{
						required:true,
						maxlength:64,
						minlength:4,
						rangelength:[4,64]
						}
				},
				success: function(label) {
					label.addClass("valid").html("<img src='${ctx}/images/ok.gif' border='0'/>")
				}
			});
			$("#showbodyul").treeview({
				collapsed: false,
				unique: false,
				url: permissionUrl,
				success:function(obj){
					obj.find('.perms').click(function(){
						toggleSelectChildren($(this).parent(), $(this).attr('checked'));
						if ($(this).attr('checked')) {
							selectParent($(this).parent());
						}
					});
				}
			});
		});	
	</script>
  </head>
  <body>  
    <div id="main">
 		<c:choose>
   			<c:when test="${role.id == null}">
   				<div class="titlediv">您所在的位置：系统管理->角色管理->角色添加</div>
   			</c:when>
   			<c:otherwise>
   				<c:choose>
	   				<c:when test="${func == 'copy'}">
	   					<div class="titlediv">您所在的位置：系统管理->角色管理->角色复制</div>
	   				</c:when>
	   				<c:otherwise>
	   					<div class="titlediv">您所在的位置：系统管理->角色管理->角色修改</div>
	   				</c:otherwise>
	   			</c:choose>
   			</c:otherwise>
   		</c:choose>  	
    	<div id="content" class="margin_10">
    	<form id="theform" action="${ctx}/user/role.do" method="post">
    		<input type="hidden" name="action" value="manage"/>
    		<s:if test="%{func == 'copy'}">
    			<input type="hidden" id="roleid" name="role.id" value=""/>
    		</s:if>
    		<s:else>
    			<input type="hidden" id="roleid" name="role.id" value="${role.id}"/>
    		</s:else>
    		<input type="hidden" id="temp_role_id" name="roleId" value="${role.id}"/>
    		<div id="form">
    			<div>
	    		<table cellpadding="0" cellspacing="0" border="0" style="width:50%" class="querytab">
		    		<tr><td class="alignright"><span class="red spanmargin">*</span><span>角色名称：</span></td><td class="alignleft"><input type="text" name="role.name" value="${role.name}"/></td></tr>	
		    		<tr>
		    			<td class="alignright"><span>是否有效：</span></td>
		    			<td class="alignleft">
		    				<select name="role.valid">
			    				<c:choose>
					    			<c:when test="${role.valid == false}">
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
		    		<tr>
		    			<td class="alignright"><span>是否限制IP：</span></td>
		    			<td class="alignleft">
		    				<select name="role.restriction">
			    				<c:choose>
					    			<c:when test="${role.restriction == true}">
			    					<option value="false">否</option>
					    			<option value="true" selected="selected">是</option>
					    			</c:when>
					    			<c:otherwise>
			    					<option value="false" selected="selected">否</option>
					    			<option value="true">是</option>
					    			</c:otherwise>
					    		</c:choose>
			    			</select>
		    			</td>
		    		</tr>
		    		<tr>
		    			<td class="alignright"><span>有效IP段：</span></td>
		    			<td class="alignleft">
		    			<textarea name="role.restrictionIp">${role.restrictionIp}</textarea>
		    			<br/>(格式：211.211.211.*,201.201.201.*)</td>
		    		</tr>
		    		<tr><td class="alignright"><span>备注：</span></td><td class="alignleft"><textarea name="role.memo" cols="30" rows="5">${role.memo}</textarea></td></tr>
	    		</table>
	    		</div>
	    		<div id="showmain">
	    			<div>选择权限：</div>
					<div id="showbody"><ul id="showbodyul"></ul></div>
				</div>
    		</div>
    		<div id="foot"><center><input id="sub" type="submit" value="提交 "/></center></div>
    		<div class="margin_10"><center><a href="${ctx}/user/role.do" class="easyui-linkbutton" iconCls="icon-reload">返回列表</a></center></div>
    	</form>
    	</div>
    </div>
  </body>
</html>
