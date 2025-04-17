package com.abc.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.abc.entities.Post;
import com.abc.entities.Province;
import com.abc.entities.User;
import com.abc.services.PostService;
import com.abc.services.ProvinceService;
import com.abc.services.UserService;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UserController {

    private PostService postService;
    private ProvinceService provinceService;
    private UserService userService;

    @Autowired
    public UserController(PostService postService, ProvinceService provinceService, UserService userService) {
        this.postService = postService;
        this.provinceService = provinceService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profileUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        List<Post> posts = new ArrayList<>();
        posts = postService.getPostById(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);

        return "profile";
    }

    @GetMapping("/edit")
    public String showEditProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("user", user);
        model.addAttribute("provinces", provinces);
        return "editProfile";
    }

    @PostMapping("/edit")
    public String handleEditProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "provinceId", required = false) Integer provinceId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        Map<String, String> errors = new HashMap<>();

        // Kiểm tra email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống");
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.put("email", "Email không hợp lệ");
        } else if (!email.equals(user.getEmail()) && userService.checkEmailExists(email)) {
            errors.put("email", "Email đã tồn tại");
        }

        // Kiểm tra ngày sinh
        if (birthday == null || birthday.trim().isEmpty()) {
            errors.put("birthday", "Ngày sinh không được để trống");
        } else {
            try {
                LocalDate birthDate = LocalDate.parse(birthday);
                long age = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
                if (age < 15) {
                    errors.put("birthday", "Tuổi phải từ 15 trở lên");
                }
            } catch (Exception e) {
                errors.put("birthday", "Ngày sinh không hợp lệ");
            }
        }

        // Kiểm tra provinceId
        if (provinceId == null) {
            errors.put("provinceId", "Vui lòng chọn tỉnh/thành");
        }

        // Kiểm tra file avatar (nếu có)
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = avatarFile.getOriginalFilename();
            if (fileName != null) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (!extension.equals("jpg") && !extension.equals("png")) {
                    errors.put("avatar", "Chỉ chấp nhận file JPG hoặc PNG");
                } else if (avatarFile.getSize() > 200 * 1024) { // 200KB
                    errors.put("avatar", "Dung lượng file không được vượt quá 200KB");
                }
            } else {
                errors.put("avatar", "File avatar không hợp lệ");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("user", user);
            model.addAttribute("provinces", provinceService.getAllProvinces());
            return "editProfile";
        }

        // Xử lý upload avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String uploadDir = session.getServletContext().getRealPath("/resources/images/avatars/");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = user.getId() + "_" + System.currentTimeMillis() + "." + avatarFile.getOriginalFilename().substring(avatarFile.getOriginalFilename().lastIndexOf(".") + 1);
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, avatarFile.getBytes());
                user.setAvatar("/resources/images/avatars/" + fileName);
            } catch (IOException e) {
                errors.put("avatar", "Lỗi khi upload file: " + e.getMessage());
                model.addAttribute("errors", errors);
                model.addAttribute("user", user);
                model.addAttribute("provinces", provinceService.getAllProvinces());
                return "editProfile";
            }
        }

        // Cập nhật thông tin
        user.setEmail(email);
        user.setBirthday(Date.valueOf(birthday));
        user.setProvince(provinceService.getProvinceById(provinceId));

        userService.updateUser(user);
        session.setAttribute("user", user);

        return "redirect:/profile";
    }
}