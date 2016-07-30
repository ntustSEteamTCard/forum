/**
 * Created by Dawid Stankiewicz on 10.07.2016
 */
package com.github.szczypioreg.forum.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.szczypioreg.forum.controller.form.NewUserForm;
import com.github.szczypioreg.forum.domain.User;
import com.github.szczypioreg.forum.exception.UserNotFoundException;
import com.github.szczypioreg.forum.service.UserService;


@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/user/{username}")
    public String findUserByUsernameAndViewProfilePage(@PathVariable("username") String username,
            Model model) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        model.addAttribute("user", user);
        return "user";
    }
    
    @RequestMapping(value = "/user/id/{id}")
    public String findUserByIdAndViewProfilePage(@PathVariable("id") int id, Model model) {
        User user = userService.findOne(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        model.addAttribute("user", user);
        return "user";
    }
    
    @RequestMapping(value = "/users")
    public String listOfAllUser(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }
    
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String regiristrationPage(Model model) {
        model.addAttribute("newUser", new NewUserForm());
        return "new_user_form";
    }
    
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String processAndSaveNewUser(@Valid @ModelAttribute("newUser") NewUserForm newUser,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return "new_user_form";
        }
        
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        
        userService.save(user);
        return "redirect:/user/" + user.getUsername();
    }
    
    @RequestMapping(value = "/logout")
    public String logOutAndRedirectToLoginPage(HttpServletRequest request,
            HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }
    
}
