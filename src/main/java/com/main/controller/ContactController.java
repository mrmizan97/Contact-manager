package com.main.controller;

import com.main.dao.ContactRepository;
import com.main.dao.UserRepository;
import com.main.entities.Contact;
import com.main.entities.User;
import com.main.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/")
public class ContactController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void commonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
//        System.out.println(user);
        model.addAttribute("user", user);
    }

    @GetMapping("dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "user/dashboard";
    }

    // per page =5[n]
    // current page=0[page]
    @GetMapping("contact/index/{page}")
    public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {
        model.addAttribute("title", "All contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        PageRequest pageable = PageRequest.of(page, 5);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "user/contact/index";
    }

    @GetMapping("contact/create")
//    @ResponseBody
    public String addContact(Model model, @ModelAttribute Contact contact) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", contact);
//        System.out.println(contact.getEmail());
        return "user/contact/add";
    }

    @PostMapping("contact/store")
    public String storeContact(
            @Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
            Model model, @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {
        if (bindingResult.hasErrors()) {
            System.out.println("ERROR" + bindingResult.toString());
            model.addAttribute("contact", contact);
            return "redirect:user/contact/add";
        }
        try {
            if (file.isEmpty()) {
                contact.setImage("default.jpg");
            } else {
                // upload file
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Image is uploaded...");
            }
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Data added.", "success","Success"));
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong.", "danger","Error"));
        }
        return "redirect:/user/contact/index/0";
    }

    // view contact
    @GetMapping("contact/view/{id}")
    public String viewContact(@PathVariable("id") Integer id, Principal principal, Model model) {
        Optional<Contact> contactOptional = this.contactRepository.findById(id);
        Contact contact = contactOptional.get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getFirstName() + ' ' + contact.getLastName());
        }
        return "user/contact/view";
    }
    // edit contact
    @GetMapping("contact/edit/{id}")
    public String editContact(@PathVariable("id") Integer id, Principal principal, Model model, HttpSession session) {
        Contact contact = this.contactRepository.findById(id).get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        } else {
            session.setAttribute("message", new Message("You don't have permission to delete this.", "danger","Error"));
            return "redirect:/user/contact/index/0";
        }
        return "user/contact/edit";
    }
    // update contact
    @PostMapping("contact/update")
    public String updateContact(@ModelAttribute Contact contact,@RequestParam("profileImage")
            MultipartFile file,HttpSession session,Model model,Principal principal){
        try{
            Contact oldContact = this.contactRepository.findById(contact.getId()).get();
            if (!file.isEmpty()){
                // delete old photo
                File oldPhoto = new ClassPathResource("static/img").getFile();
                if (oldPhoto==null){
                    // update new photo
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    contact.setImage(file.getOriginalFilename());
                }
                File deleteFile = new File(oldPhoto, oldContact.getImage());
                deleteFile.delete();
                // update new photo
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            }else {
                contact.setImage(oldContact.getImage());
            }
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Data updated.", "success","Success"));
        }catch (Exception e){
            System.out.println("ERROR"+e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong.", "danger","Error"));
        }
        return "redirect:/user/contact/index/0";
    }
// delete contact
    @GetMapping("contact/delete/{id}")
    public String deleteContact(@PathVariable("id") Integer id, Principal principal, Model model, HttpSession session) throws IOException {
        Contact contact = this.contactRepository.findById(id).get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if (user.getId() == contact.getUser().getId()) {
            // delete old photo
            File oldPhoto = new ClassPathResource("static/img").getFile();
            File deleteFile = new File(oldPhoto, contact.getImage());
            deleteFile.delete();
            this.contactRepository.delete(contact);
            contact.setUser(null);
            session.setAttribute("message", new Message("Data deleted.", "success" ,"Success"));
        } else {
            session.setAttribute("message", new Message("You don't have permission to delete this.", "danger","Error"));
        }
        return "redirect:/user/contact/index/0";
    }


}
