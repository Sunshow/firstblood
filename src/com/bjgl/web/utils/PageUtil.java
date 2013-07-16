package com.bjgl.web.utils;

import com.bjgl.web.bean.PageBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qatang
 * @function 分页
 * @since 2010-09-15
 */
public class PageUtil {

    public static String getPageString(HttpServletRequest request, PageBean pageBean) {
        if (pageBean.getPageCount() <= 0) {
            return "暂无分页！";
        }
        StringBuffer sb = new StringBuffer();
        String url = request.getRequestURI().toString();

        sb.append("<div class=\"pagination pagination-right\"><ul>\n" +
                "    <li><a href=\"#\">Prev</a></li>\n" +
                "    <li><a href=\"#\">1</a></li>\n" +
                "    <li><a href=\"#\">2</a></li>\n" +
                "    <li><a href=\"#\">3</a></li>\n" +
                "    <li><a href=\"#\">4</a></li>\n" +
                "    <li><a href=\"#\">5</a></li>\n" +
                "    <li><a href=\"#\">Next</a></li>\n" +
                "  </ul></div>");

        return sb.toString();

    }

    public static String getSimplePageString(PageBean pageBean) {
        if (pageBean.getPageCount() <= 0) {
            return "暂无分页！";
        }
        StringBuffer sb = new StringBuffer();
        //生成form
        sb.append("共有").append("<span style='color:#EC8722;'>").append(pageBean.getPageCount()).append("</span>").append("页");
        sb.append("&nbsp;<span style='color:#EC8722;'>").append(pageBean.getCount()).append("</span>").append("条记录");
        sb.append("&nbsp;第");
        sb.append("<select onchange='jumpPage(this.value)'>");
        int showPageCount = 0;
        if (pageBean.getPageCount() > 100) {
            showPageCount = 100;
        } else {
            showPageCount = pageBean.getPageCount();
        }
        for (int i = 1; i <= showPageCount; i++) {
            if (i == pageBean.getPage()) {
                sb.append("<option value='").append(i).append("' selected>").append(i).append("</option>");
            } else {
                sb.append("<option value='").append(i).append("'>").append(i).append("</option>");
            }
        }
        sb.append("</select>");
        sb.append("页");
        sb.append("&nbsp;<a style='color:#EC8722' href='javascript:jumpPage(1);'>首页</a>");
        if (pageBean.getPage() > 1) {
            sb.append("&nbsp;<a style='color:#EC8722' href='javascript:jumpPage(" + (pageBean.getPage() - 1) + ");'>上一页</a>");
        }
        if (pageBean.getPage() < pageBean.getPageCount()) {
            sb.append("&nbsp;<a style='color:#EC8722' href='javascript:jumpPage(" + (pageBean.getPage() + 1) + ");'>下一页</a>");
        }
        sb.append("&nbsp;跳转到第");
        sb.append("<input name='pageBean.page' class='formPageInput' onchange='changePage(this.value)' type='text' style='width:40px;' value='" + pageBean.getPage() + "' />页");
        sb.append("<input type='button' onclick='jumpPageSub()' value=' GO ' />");

        return sb.toString();

    }
}
