<%@page import="com.abc.entities.*" %>
<%@page import="java.util.*" %>
<%@page contentType="text/html; charset=UTF-8" %>


<%
    User user = (User) request.getAttribute("user");
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    List<Province> provinces = (List<Province>) request.getAttribute("provinces");
%>

<html>
<head>
    <title>Chỉnh sửa hồ sơ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Chỉnh sửa hồ sơ</h2>
    <form method="post" action="<%= request.getContextPath() %>/edit" enctype="multipart/form-data">
        <div class="mb-3">
            <label for="email" class="form-label">Email:</label>
            <input type="email" class="form-control" id="email" name="email" 
                   value="<%= user.getEmail() != null ? user.getEmail() : "" %>" required>
            <span class="text-danger"><%= errors != null && errors.containsKey("email") ? errors.get("email") : "" %></span>
        </div>
        <div class="mb-3">
            <label for="birthday" class="form-label">Ngày sinh:</label>
            <input type="date" class="form-control" id="birthday" name="birthday" value="<%= user.getBirthday() != null ? user.getBirthday().toString() : "" %>" required>
            <span class="text-danger"><%= errors != null && errors.containsKey("birthday") ? errors.get("birthday") : "" %></span>
        </div>
        <div class="mb-3">
            <label for="provinceId" class="form-label">Thành phố:</label>
            <select name="provinceId" id="provinceId" class="form-select" required>
                <%
                    if (provinces != null) {
                        for (Province p : provinces) {
                            boolean selected = user.getProvince() != null && p.getIdProvince() == user.getProvince().getIdProvince();
                %>
                            <option value="<%= p.getIdProvince() %>" <%= selected ? "selected" : "" %>><%= p.getNameProvince() %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>
        <div class="mb-3">
            <label for="avatar" class="form-label">Avatar (JPG, PNG, ≤ 200KB):</label>
            <input type="file" class="form-control" id="avatar" name="avatar">
            <span class="text-danger"><%= errors != null && errors.containsKey("avatar") ? errors.get("avatar") : "" %></span>
            <% if (user.getAvatar() != null && !user.getAvatar().isEmpty()) { %>
                <img src="<%= request.getContextPath() + user.getAvatar() %>" alt="Avatar" class="mt-2" style="max-width: 100px;">
            <% } %>
        </div>
        <button type="submit" class="btn btn-success">Lưu thay đổi</button>
        <a href="<%= request.getContextPath() %>/profile" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>