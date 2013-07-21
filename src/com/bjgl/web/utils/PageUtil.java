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
        StringBuffer buffer = new StringBuffer("<div class=\"pagination pagination-right\"><ul>");
        //String url = request.getRequestURI().toString();

        int page = pageBean.getPage();
        int total = (int)Math.ceil((double)pageBean.getCount() / pageBean.getPageSize());

        // 是否显示上一页
        if (page > 1) {
            buffer.append("<li><a href=\"javascript://\" page=\"").append(page - 1).append("\">上一页</a></li>");
        }

        // 生成当前页前后4条，即最多显示9个页码

        int start = (page - 4) <= 0 ? 1 : page - 4;
        int end = (page + 4) > total ? total : page + 4;

        for (int i = start; i <= end; i++) {
            if (i == page) {
                buffer.append("<li class=\"active\"><a href=\"javascript://\" page=\"").append(i).append("\">").append(i).append("</a></li>");
            } else {
                buffer.append("<li><a href=\"javascript://\" page=\"").append(i).append("\">").append(i).append("</a></li>");
            }
        }

        // 是否显示下一页
        if (page < total) {
            buffer.append("<li><a href=\"javascript://\" page=\"").append(page + 1).append("\">下一页</a></li>");
        }

        buffer.append("</ul></div>");
        buffer.append("\n<input type=\"hidden\" name=\"pageBean.page\" value=\"").append(page).append("\"/>\n");

        return buffer.toString();

    }
}
