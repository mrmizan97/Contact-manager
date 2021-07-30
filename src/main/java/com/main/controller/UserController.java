package com.main.controller;

import com.main.dao.ContactRepository;
import com.main.dao.UserRepository;
import com.main.entities.Contact;
import com.main.entities.User;
import com.main.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void commonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.getUserByUserName(username);
//        System.out.println(user);
        model.addAttribute("user", user);
    }

    @GetMapping("dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "user/dashboard";
    }

    @GetMapping("add-contact")
//    @ResponseBody
    public String addContact(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "user/contact/add";
    }

    @PostMapping("contact/store")
    public String storeContact(
            @ModelAttribute Contact contact,
//            @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            //uploading file..
//            if (file.isEmpty()) {
//                System.out.println("File is empty");
//            }
//            else {
//                // upload file
////                contact.setImage(file.getOriginalFilename());
////                File saveFile = new ClassPathResource("/static/img").getFile();
//////                System.out.println(new ClassPathResource("static/image").getFile());
////                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
////                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image is uploaded.");
//            }
            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);
            System.out.println(contact.getEmail());
            System.out.println("User's contact data added.");
            session.setAttribute("message", new Message("Data added.", "success"));
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong.", "danger"));

        }
        return "user/contact/add";
    }

    //perpage =5[n]
    // current page=0[page]
    @GetMapping("contact/index/{page}")
    public String showAllContact(@PathVariable("page") int page, Model model, Principal principal) {
        model.addAttribute("title", "All contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        PageRequest pageable = PageRequest.of(page, 5);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "user/contact/index";
    }
}
