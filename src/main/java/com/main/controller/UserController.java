package com.main.controller;

import com.main.entities.User;
import com.main.dao.UserRepository;

import com.main.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @ModelAttribute
    public void commonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
//        System.out.println(user);
        model.addAttribute("user", user);
    }

    @GetMapping("profile/view")
    public String userProfile(Model model){
        model.addAttribute("title","Your profile");
        return  "user/profile/view";
    }
    @GetMapping("profile/edit/{id}")
    public String editProfile(Model model){
        model.addAttribute("title","Edit profile");
        return  "user/profile/edit";
    }
    @PostMapping("profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user, Model model,
                             @RequestParam("profileImage") MultipartFile file,
                             BindingResult bindingResult,Principal principal,  HttpSession httpSession) {
        try {

            if (bindingResult.hasErrors()) {
                System.out.println("ERROR" + bindingResult.toString());
                model.addAttribute("user", user);
                return "user/profile/edit";
            }
            User oldUser = this.userRepository.findById(user.getId()).get();
            if (file.isEmpty()) {
                // delete old photo
                File oldPhoto = new ClassPathResource("static/img").getFile();
                File deleteFile = new File(oldPhoto, oldUser.getImage());
                deleteFile.delete();
                // update new photo
                System.out.println(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                user.setImage(file.getOriginalFilename());
            } else {
                user.setImage(oldUser.getImage());
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            this.userRepository.save(user);
            model.addAttribute("user", user);
            httpSession.setAttribute("message", new Message("Profile updated.", "success"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            model.addAttribute("user", user);
            httpSession.setAttribute("message", new Message("Something went wrong." + e.getMessage(), "danger"));
        }
        return  "user/profile/view";
    }
    @GetMapping("settings")
    public  String getSetting(Model model){
        model.addAttribute("title","Settings");
        return "user/settings/setting";
    } @PostMapping("change/password")
    public  String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
                                  HttpSession session,Principal principal){
        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);

        if (!this.bCryptPasswordEncoder.matches(currentPassword,currentUser.getPassword())){
            session.setAttribute("message",new Message("Current Password does not match.","error","Error"));
        }
       else {
           if (!newPassword.equals(confirmPassword)){
               session.setAttribute("message",new Message("New Password does not match with confirm password.","error","Error"));

           }else {
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message",new Message("Password changed.","success","Success"));
        }}
        return "user/settings/setting";
    }

}
