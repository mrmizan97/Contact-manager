package com.main.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.entities.User;
import com.main.helper.Message;
import com.main.dao.UserRepository;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager.");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager.");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Signup - Smart Contact Manager.");
        return "signup";
    }

    @PostMapping("/signup")
    public String singupUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                             @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
                             HttpSession httpSession) {
        try {
            if (!agreement) {
                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and condition");
            }
            if (bindingResult.hasErrors()) {
                System.out.println("ERROR" + bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImage("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println(agreement);
            System.out.println(user);
            this.userRepository.save(user);
            model.addAttribute("user", new User());
            httpSession.setAttribute("message", new Message("Successfully registared.", "alert-success"));
            return "signup";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            model.addAttribute("user", user);
            httpSession.setAttribute("message", new Message("Something went wrong." + e.getMessage(), "alert-danger"));
            return "signup";

        }
    }
// custom login
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Signin here.");
        return "signin";
    }@GetMapping("/signin-fail")
    public String signinFail(){
        return "/signinFail";
    }
}
