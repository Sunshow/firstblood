<%@page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>角色信息录入 - ${SITE_NAME}</title>
    <jsp:include page="/WEB-INF/jsp/global_init.jsp"/>
    <link rel="stylesheet" href="<c:url value="/css/treeview.css"/>" type="text/css">
    <script type="text/javascript" src="<c:url value="/js/jquery.treeview.js"/>"></script>
</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<ul class="breadcrumb">
    <li><a href="#">系统管理</a> <span class="divider">/</span></li>
    <li><a href="<c:url value="/user/role.do"/>">角色管理</a> <span class="divider">/</span></li>
    <li class="active">角色信息录入</li>
</ul>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <!--Sidebar content-->
            <jsp:include page="/WEB-INF/jsp/sidebar.jsp"/>
        </div>
        <div class="span10">
            <!--Body content-->
            <form class="form-horizontal" action="<c:url value="/user/role.do"/>" method="post">
                <input type="hidden" name="action" value="manage"/>
                <s:if test="%{func == 'copy'}">
                    <input type="hidden" id="roleid" name="role.id" value=""/>
                </s:if>
                <s:else>
                    <input type="hidden" id="roleid" name="role.id" value="${role.id}"/>
                </s:else>
                <input type="hidden" id="temp_role_id" name="roleId" value="${role.id}"/>

                <div class="control-group">
                    <label class="control-label" for="role.name">角色名称</label>

                    <div class="controls">
                        <input type="text" id="role.name" placeholder="角色名称" name="role.name" value="${role.name}">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="role.valid">是否有效</label>

                    <div class="controls">
                        <select id="role.valid" name="role.valid">
                            <c:choose>
                                <c:when test="${!role.valid}">
                                    <option value="true">有效</option>
                                    <option value="false" selected="selected">无效</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="true" selected="selected">有效</option>
                                    <option value="false">无效</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="role.restriction">是否限制IP</label>

                    <div class="controls">
                        <select id="role.restriction" name="role.restriction">
                            <c:choose>
                                <c:when test="${role.restriction}">
                                    <option value="false">否</option>
                                    <option value="true" selected="selected">是</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="false" selected="selected">否</option>
                                    <option value="true">是</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="role.restrictionIp">有效IP段</label>

                    <div class="controls">
                        <textarea id="role.restrictionIp" name="role.restrictionIp">${role.restrictionIp}</textarea>
                        <br/>(格式：211.211.211.*,201.201.201.*)
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="role.memo">备注</label>

                    <div class="controls">
                        <textarea id="role.memo" name="role.memo">${role.memo}</textarea>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">选择权限</label>

                    <div class="controls">
                        <ul id="permissionTreeView"></ul>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button type="submit" class="btn">提交</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script type="text/javascript">
    function toggleSelectChildren(node, bool) {
        node.find('ul > li').each(function () {
            $(this).children('input').prop('checked', bool);
            toggleSelectChildren($(this), bool);
        }).end();
    }
    function selectParent(node) {
        if (!node || node.length == 0) {
            return;
        }
        var parentNode = node.parent('ul').parent('li');
        parentNode.children('input').prop('checked', true);
        selectParent(parentNode);
    }
    $(document).ready(function () {
        var permissionUrl = '<c:url value="/user/role.do?action=findPermissions"/>';

        var roleid = $('#temp_role_id').val();
        if (roleid != null) {
            permissionUrl += '&role.id=' + roleid;
        }

        $('#permissionTreeView').treeview({
            'collapsed': false,
            'unique': false,
            'url': permissionUrl,
            'success': function (obj) {
                obj.find('.perms').click(function () {
                    toggleSelectChildren($(this).parent(), $(this).prop('checked'));
                    if ($(this).prop('checked')) {
                        selectParent($(this).parent());
                    }
                });
            }
        });
    });
</script>

</body>
</html>
