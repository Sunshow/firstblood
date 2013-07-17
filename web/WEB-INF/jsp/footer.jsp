<%@page contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<script type="text/javascript">
    // 分页器代码
    $(document).ready(function() {
        $('form .pagination li a').click(function() {
            $link = $(this);
            var page = $link.attr('page');

            $form = $link.parents('form');
            $form.find('input[name="pageBean.page"]').val(page).end();
            $form[0].submit();
        });
    });
</script>