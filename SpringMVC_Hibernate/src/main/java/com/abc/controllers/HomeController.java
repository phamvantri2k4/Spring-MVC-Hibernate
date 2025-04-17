package com.abc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.abc.entities.Post;
import com.abc.entities.User;
import com.abc.services.FollowService;
import com.abc.services.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private FollowService followService;

    @Autowired
    private PostService postService;

    @GetMapping(value = {"/", "/home"})
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null)
            return "redirect:/login";

        List<User> userfed = followService.getFollowerUser(user.getId());
        List<User> suggestfollow = followService.getSuggestedFollows(user.getId());
        List<Post> posts = postService.getAllPost(user.getId());

        model.addAttribute("userfed", userfed);
        model.addAttribute("suggestfollow", suggestfollow);
        model.addAttribute("posts", posts);

        return "home";
    }

    @GetMapping("/search")
    public String searchUsers(
            @RequestParam(value = "minFollowing", defaultValue = "0") int minFollowing,
            @RequestParam(value = "minFollower", defaultValue = "0") int minFollower,
            Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        List<User> userfed = followService.getFollowerUser(user.getId());
        List<User> suggestfollow = followService.getSuggestedFollows(user.getId());
        List<Post> posts = postService.getAllPost(user.getId());

        // Tìm kiếm user theo số lượng following và follower
        List<User> searchResults = followService.searchUsersByFollowCounts(minFollowing, minFollower);

        model.addAttribute("userfed", userfed);
        model.addAttribute("suggestfollow", suggestfollow);
        model.addAttribute("posts", posts);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("minFollowing", minFollowing);
        model.addAttribute("minFollower", minFollower);

        return "home";
    }
}