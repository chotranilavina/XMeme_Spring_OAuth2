package com.crio.starter.controller;

import com.crio.starter.data.*;
import com.crio.starter.exchange.*;
import com.crio.starter.repository.*;
import com.crio.starter.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Stream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.*;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.dao.DataIntegrityViolationException;

import com.mongodb.lang.NonNull;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemesController {
@Autowired
UserRepository userRepository;
@Autowired
private final MemesService memesService;
@Autowired
private UserService userService;
private Map<String, LocalDateTime> usersLastAccess = new HashMap<>();

@GetMapping("/")
public RedirectView home() {
        return new RedirectView("/home.html");
}

@GetMapping("/login")
public RedirectView getCurrentUser(@AuthenticationPrincipal @Validated User user, Model model) {
        String username = user.getUsername();
        model.addAttribute("username", username);
        model.addAttribute("lastAccess", usersLastAccess.get(username));
        usersLastAccess.put(username, LocalDateTime.now());
        return new RedirectView("/");
}

@GetMapping("/register")
public RedirectView register(final Model model){
        log.info("Get Register");
        model.addAttribute("userData", new UserData());
        return new RedirectView("register.html");
}
@RequestMapping("/index")
public String index() {
        return "index";
}

@RequestMapping("/webprivate")
public String private_page() {
        return "private";
}
@RequestMapping("/webpublic")
public String loginpub() {
        return "public";
}
@RequestMapping("/webadmin")
public String admin() {
        return "admin";
}

}
