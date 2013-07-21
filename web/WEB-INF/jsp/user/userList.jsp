<%@page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>角色管理 - ${SITE_NAME}</title>
    <jsp:include page="/WEB-INF/jsp/global_init.jsp"/>
    <link href="<c:url value="/js/jscalendar/skins/aqua/theme.css"/>" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="<c:url value="/js/jscalendar/calendar.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jscalendar/lang/cn_utf8.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jscalendar/calendar-setup.js"/>"></script>
</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<ul class="breadcrumb">
    <li><a href="#">系统管理</a> <span class="divider">/</span></li>
    <li class="active">用户管理</li>
</ul>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <!--Sidebar content-->
            <jsp:include page="/WEB-INF/jsp/sidebar.jsp"/>
        </div>
        <div class="span10">
            <!--Body content-->
            <div class="btn-group table-btn-group">
                <a class="btn btn-primary" href="<c:url value="/user/user.do?action=input"/>">添加用户</a>
            </div>

            <div class="alert alert-info">
                条件查询
            </div>

            <form class="form-inline" action="${ctx}/user/user.do" method="post">
                <input type="hidden" value="query" name="action"/>
                <table class="table table-bordered">
                    <tr>
                        <td>
                            <label>用户名</label>
                            <input type="text" placeholder="用户名" name="username" value="${username}"/>
                        </td>
                        <td>
                            <label>姓名</label>
                            <input type="text" placeholder="姓名" name="name" value="${name}" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>电话</label>
                            <input type="text" placeholder="电话" name="phone" value="${phone}" />
                        </td>
                        <td>
                            <label>是否有效</label>
                            <select name="validValue">
                                <c:choose>
                                    <c:when test="${validValue == null || validValue < 0}">
                                        <option value="-1">全部</option>
                                        <option value="1">有效</option>
                                        <option value="0">无效</option>
                                    </c:when>
                                    <c:when test="${validValue > 0}">
                                        <option value="-1">全部</option>
                                        <option value="1" selected="selected">有效</option>
                                        <option value="0">无效</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="-1">全部</option>
                                        <option value="1">有效</option>
                                        <option value="0" selected="selected">无效</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>登录时间开始</label>
                            <input type="text" id="beginDate" name="beginDate" value="<fmt:formatDate value='${beginDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
                            <input type="button" id="beginDateTrigger" value="选择"/>
                        </td>
                        <td>
                            <label>登录时间结束</label>
                            <input type="text" id="endDate" name="endDate" size="10" value="<fmt:formatDate value='${endDate}' pattern='yyyy-MM-dd HH:mm:ss'/>"/>
                            <input type="button" id="endDateTrigger" value="选择"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <div style="text-align:center">
                                <button class="btn btn-primary" type="submit">查询</button>
                            </div>
                        </td>
                    </tr>
                </table>

                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>用户名</th>
                        <th>姓名</th>
                        <th>电话</th>
                        <th>邮件</th>
                        <th>角色</th>
                        <th>最后登录</th>
                        <td>是否有效</td>
                        <th>备注</th>
                        <th colspan="2">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <s:iterator value="userList">
                        <tr>
                            <td>${id}</td>
                            <td><a href="<c:url value="/user/user.do?action=view&user.id=${id}"/>">${username}</a></td>
                            <td>${name}</td>
                            <td>${tel}</td>
                            <td>${email}</td>
                            <td>${role.name}</td>
                            <td><s:date name="loginTime" format="yyyy-MM-dd HH:mm:ss" /></td>
                            <td><s:if test="valid == true">有效</s:if><s:else>无效</s:else></td>
                            <td>${memo}</td>
                            <td><a href="<c:url value="/user/user.do?action=input&user.id=${id}"/>">修改</a></td>
                            <td><a href="<c:url value="/user/user.do?action=del&user.id=${id}"/>" onclick="return confirm('确实要删除吗？');">删除</a></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>

                ${pageString}

            </form>
        </div>
    </div>
</div>


<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

<script type="text/javascript">
    $(function(){
        Calendar.setup(
                {
                    inputField  : "beginDate",    // ID of the input field
                    ifFormat    : "%Y-%m-%d %H:%M:%S",   // the date format
                    button      : "beginDateTrigger",
                    showsTime   : true,
                    date		: Calendar.initNewDate(),
                    timeFormat  : "24"
                }
        );
        Calendar.setup(
                {
                    inputField  : "endDate",    // ID of the input field
                    ifFormat    : "%Y-%m-%d %H:%M:%S",   // the date format
                    button      : "endDateTrigger",
                    showsTime   : true,
                    date		: Calendar.initNewDate(),
                    timeFormat  : "24"
                }
        );
    });
</script>

</body>
</html>
