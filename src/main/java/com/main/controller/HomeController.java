package com.main.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.main.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    Random random = new Random(1000);

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
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String singupUser(@Valid @ModelAttribute("user") User user, Model model,
                             @RequestParam("profileImage") MultipartFile file,
                             BindingResult bindingResult, @RequestParam(value = "agreement",
            defaultValue = "false") boolean agreement, HttpSession httpSession) {
        try {
            if (!agreement) {
//                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and condition");
            }
            if (bindingResult.hasErrors()) {
                System.out.println("ERROR" + bindingResult.toString());
                model.addAttribute("user", user);
                return "auth/signup";
            }
            if (file.isEmpty()) {
                user.setImage("default.jpg");
            } else {
                // upload file
                user.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image is uploaded...");
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
            return "auth/signup";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            model.addAttribute("user", user);
            httpSession.setAttribute("message", new Message("Something went wrong." + e.getMessage(), "alert-danger"));
            return "auth/signup";
        }
    }

    // custom login
    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Signin - Smart Contact Manager.");
        return "auth/signin";
    }

    @GetMapping("/signin-fail")
    public String signinFail() {
        return "auth/signinFail";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Input email - Smart Contact Manager.");
        return "auth/inputEmail";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, Model model, HttpSession session) {
        model.addAttribute("title", "Input OTP- Smart Contact Manager.");
        // generate 4 digit otp
        User user = this.userRepository.getUserByUserName(email);
        if (user == null) {
            session.setAttribute("message", new Message("Email address not found. try again.", "danger", "Error"));
            return "redirect:/forgot-password";
        }
//        int otp = random.nextInt(9999);
        int otp = 1234;
//        String subject="Otp from application";
//        String message="<h3>Your Otp code is "+otp+"</h3>";
//        String to=email;

//        boolean sendEmail = this.emailService.sendEmail(subject, message, to);
//        if (sendEmail==false){
//            session.setAttribute("message", new Message("Something wrong. Check your email address.", "danger", "Error"));
//            return "redirect:/forgot-password";
//        }
        session.setAttribute("myotp", otp);
        session.setAttribute("myemail", email);
        session.setAttribute("message", new Message("Otp  sent to your email address.", "success", "Success"));

        return "auth/inputOtp";
    }

    @PostMapping("/submit-otp")
    public String varifyOtp(@RequestParam("otp") int otp, HttpSession session) {
        int myOtp = (int) session.getAttribute("myotp");
        String myEmail = (String) session.getAttribute("myemail");
        if (myOtp == otp) {
            session.setAttribute("myEmail", myEmail);
            return "auth/changePassword";
        } else {
            session.setAttribute("message", new Message("Invalid otp. try again.", "danger", "Error"));
            return "auth/inputOtp";
        }
    }

    @PostMapping("set-password")
    public String setPassword(
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session) {
        if (!password.equals(confirmPassword)) {
            session.setAttribute("message", new Message("Password does not match with confirm password.", "error", "Error"));
            return "auth/changePassword";
        }
        String myEmail = (String) session.getAttribute("myEmail");
        User currentUser = this.userRepository.getUserByUserName(myEmail);
        currentUser.setPassword(this.bCryptPasswordEncoder.encode(password));
        this.userRepository.save(currentUser);
        session.setAttribute("message", new Message("Password changed.", "success", "Success"));
        return "auth/changePassword";
    }
}

