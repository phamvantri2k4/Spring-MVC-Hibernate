package com.abc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.abc.entities.Province;
import com.abc.entities.User;
import com.abc.services.ProvinceService;
import com.abc.services.UserService;

import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private UserService userService;
    private ProvinceService provinceService;

    @Autowired
    public AuthController(UserService userService, ProvinceService provinceService) {
        this.userService = userService;
        this.provinceService = provinceService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session, Model model) {
        User user = userService.getUserByUserName(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            session.setAttribute("user_id", user.getId());
            return "redirect:/";
        }

        model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("provinces", provinces);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("birthday") String birthday,
            @RequestParam("provinceId") int provinceId,
            Model model) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra username tồn tại
        if (userService.getUserByUserName(username) != null) {
            errors.put("username", "Tên người dùng đã tồn tại");
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.put("email", "Email không hợp lệ");
        } else if (userService.checkEmailExists(email)) {
            errors.put("email", "Email đã tồn tại");
        }

        // Kiểm tra tuổi >= 15
        try {
            LocalDate birthDate = LocalDate.parse(birthday);
            long age = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
            if (age < 15) {
                errors.put("birthday", "Tuổi phải từ 15 trở lên");
            }
        } catch (Exception e) {
            errors.put("birthday", "Ngày sinh không hợp lệ");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            List<Province> provinces = provinceService.getAllProvinces();
            model.addAttribute("provinces", provinces);
            return "register";
        }

        // Tạo user mới
        User user = new User(username, password);
        user.setEmail(email);
        user.setBirthday(Date.valueOf(birthday));
        user.setProvince(provinceService.getProvinceById(provinceId));

        if (userService.registerUser(user)) {
            return "redirect:/login";
        }

        model.addAttribute("error", "Đăng ký thất bại");
        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("provinces", provinces);
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}