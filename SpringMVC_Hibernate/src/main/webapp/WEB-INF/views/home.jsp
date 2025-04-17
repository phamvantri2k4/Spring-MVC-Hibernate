<%@page import="com.abc.entities.*" %>
<%@page import="java.util.*" %>
<%@page contentType="text/html; charset=UTF-8" %>


<%
    User user = (User) session.getAttribute("user");
    if (user == null)
        response.sendRedirect("login");
%>

<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Mạng Xã Hội</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">🏠 Trang chủ</a>
        </div>
        <div class="navbar-nav ms-auto d-flex align-items-center">
            <a href="profile">
                <span class="text-white me-3">👤 Hồ sơ của bạn</span>
                <img src="<%= user.getAvatar() != null ? request.getContextPath() + user.getAvatar() : request.getContextPath() + "/resources/images/avt.jpg" %>" alt="Avatar" class="rounded-circle" width="40">
            </a>
        </div>
    </nav>

    <div class="container mt-5 pt-5 flex-grow-1">
        <div class="row">
            <div class="col-md-3">
                <div class="card p-3 mb-3">
                    <p class="fw-bold">Danh sách người theo dõi</p>
                    <%
                    List<User> userfed = (List<User>) request.getAttribute("userfed");
                    int currentUserId = (int) session.getAttribute("user_id");
                    for (User ufed : userfed) {
                    %>
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <div class="d-flex align-items-center">
                            <img src="<%= ufed.getAvatar() != null ? request.getContextPath() + ufed.getAvatar() : request.getContextPath() + "/resources/images/avt.jpg" %>" alt="Avatar" class="rounded-circle me-2" width="30">
                            <span class="text-truncate" style="max-width: 120px;"><%=ufed.getUsername()%></span>
                        </div>
                        <button class="btn btn-sm btn-primary unfollow-btn" data-following="<%=user.getId()%>" data-followed="<%=ufed.getId()%>">Hủy theo dõi</button>
                    </div>
                    <%
                    }
                    %>
                </div>
            </div>

            <div class="col-md-6 mt-0">
                <!-- Form tìm kiếm -->
                <div class="card mb-3">
                    <div class="card-body">
                        <h2>Kết quả tìm kiếm</h2>
                        <form method="get" action="search" class="d-flex align-items-center gap-3">
                            <div>
                                <label>Theo dõi >= </label>
                                <input type="number" name="minFollowing" value="<%= request.getAttribute("minFollowing") != null ? request.getAttribute("minFollowing") : "0" %>" min="0" class="form-control d-inline-block" style="width: 100px;" />
                            </div>
                            <div>
                                <label>Người theo dõi >= </label>
                                <input type="number" name="minFollower" value="<%= request.getAttribute("minFollower") != null ? request.getAttribute("minFollower") : "0" %>" min="0" class="form-control d-inline-block" style="width: 100px;" />
                            </div>
                            <button type="submit" class="btn btn-primary">Tìm</button>
                        </form>
                    </div>
                </div>

                <!-- Hiển thị kết quả tìm kiếm -->
                <%
                List<User> searchResults = (List<User>) request.getAttribute("searchResults");
                if (searchResults != null) {
                    if (!searchResults.isEmpty()) {
                        for (User u : searchResults) {
                %>
                        <div class="card p-3 mb-3">
                            <div class="d-flex align-items-center">
                                <img src="<%= u.getAvatar() != null ? request.getContextPath() + u.getAvatar() : request.getContextPath() + "/resources/images/avt.jpg" %>" alt="Avatar" class="rounded-circle me-2" width="30">
                                <p class="mb-0"><%= u.getUsername() %></p>
                            </div>
                        </div>
                <%
                        }
                    } else {
                %>
                        <div style="text-align: center;">
                            <img src="${pageContext.request.contextPath}/resources/images/notfound.png" alt="Không tìm thấy" style="max-width: 300px;" />
                        </div>
                <%
                    }
                }
                %>

                <div class="card mb-3 text-center">
                    <div class="card-body">
                        <form action="post" method="post">
                            <p class="fw-bold mb-3">Đăng bài</p>
                            <input name="title" type="text" class="form-control mb-3" placeholder="Title">
                            <textarea class="form-control mb-3" id="postBody" name="body" rows="5" placeholder="Viết gì đó..."></textarea>
                            <button type="submit" class="btn btn-danger w-100">Đăng</button>
                        </form>
                    </div>
                </div>
                <%
                List<Post> posts = (List<Post>) request.getAttribute("posts");
                for (Post post : posts) {
                %>
                <div class="card p-3 mb-3">
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <div class="d-flex align-items-center">
                            <img src="<%= user.getAvatar() != null ? request.getContextPath() + user.getAvatar() : request.getContextPath() + "/resources/images/avt.jpg" %>" alt="Avatar" class="rounded-circle me-2" width="30">
                            <b><%=post.getUserId()%></b>
                            <span class="text-muted ms-3">
                                <fmt:parseDate value="<%=post.getCreatedAt().toString()%>" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                                <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                            </span>
                        </div>
                        <div class="dropdown">
                            <button class="btn btn-light btn-sm" type="button" data-bs-toggle="dropdown" aria-expanded="false">⋮</button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="#">Chỉnh sửa trạng thái</a></li>
                                <li><a class="dropdown-item" href="#">Xóa bài</a></li>
                            </ul>
                        </div>
                    </div>
                    <p><strong>Trạng thái:</strong> <%=post.getStatus()%></p>
                    <p><%=post.getTitle()%></p>
                    <p><%=post.getBody()%></p>
                </div>
                <%
                }
                %>
            </div>

            <div class="col-md-3">
                <div class="card p-3 mb-3">
                    <p class="fw-bold">Gợi ý theo dõi</p>
                    <%
                    List<User> suggestfollow = (List<User>) request.getAttribute("suggestfollow");
                    for (User u : suggestfollow) {
                    %>
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <div class="d-flex align-items-center">
                            <img src="<%= u.getAvatar() != null ? request.getContextPath() + u.getAvatar() : request.getContextPath() + "/resources/images/avt.jpg" %>" alt="Avatar" class="rounded-circle me-2" width="30">
                            <span class="text-truncate" style="max-width: 120px;"><%=u.getUsername()%></span>
                        </div>
                        <button class="btn btn-sm btn-primary follow-btn" data-following="<%=user.getId()%>" data-followed="<%=u.getId()%>">Theo dõi</button>
                    </div>
                    <%
                    }
                    %>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-primary text-white text-center py-3 mt-auto">
        <div class="container">
            <p>Công ty TNHH Mạng Xã Hội Việt © 2025</p>
            <p>Ngày phát hành: 15/02/2025</p>
            <p>Bản quyền © 2025. Mọi quyền được bảo lưu.</p>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    document.querySelectorAll('.follow-btn').forEach(button => {
        button.addEventListener('click', function() {
            const followingUserId = this.getAttribute('data-following');
            const followedUserId = this.getAttribute('data-followed');
            
            fetch('/SpringMVC/follow/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ followingUserId, followedUserId })
            }).then(response => response.text())
              .then(data => {
                  location.reload();
              });
        });
    });

    document.querySelectorAll('.unfollow-btn').forEach(button => {
        button.addEventListener('click', function() {
            const followingUserId = this.getAttribute('data-following');
            const followedUserId = this.getAttribute('data-followed');
            
            fetch('/SpringMVC/follow/remove', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ followingUserId, followedUserId })
            }).then(response => response.text())
              .then(data => {
                  location.reload();
              });
        });
    });
    </script>
</body>
</html>