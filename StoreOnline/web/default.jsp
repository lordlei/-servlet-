<%--
  Created by IntelliJ IDEA.
  User: pc26
  Date: 2017/12/21
  Time: 20:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%
        response.sendRedirect(request.getContextPath()+"/product?method=index");
    %>

</body>
</html>
