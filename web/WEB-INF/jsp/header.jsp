<%@page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="navbar navbar-inverse">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="brand" href="#">${NAVBAR_TITLE}</a>
            <div class="nav-collapse collapse">
                <p class="navbar-text pull-right">
                    Logged in as <a href="<c:url value="/user/logout.do"/>" class="navbar-link">${session.USER_SESSION.user.username}</a>
                </p>
                <ul class="nav">
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#about">About</a></li>
                    <li><a href="#contact">Contact</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>