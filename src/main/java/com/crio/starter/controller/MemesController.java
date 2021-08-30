package com.crio.starter.controller;

import com.crio.starter.data.*;
import com.crio.starter.exchange.*;
import com.crio.starter.repository.*;
import com.crio.starter.service.*;
import com.crio.starter.service.MemesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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



@Log4j2
@RestController
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

  @GetMapping("/user/login")
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
    //
    //     @PostMapping("/register")
    //     public RedirectView userRegistration(@Valid @ModelAttribute("userForm") UserData userData, final BindingResult bindingResult, final Model model){
    //       log.info("Post register");
    //         if(bindingResult.hasErrors()){
    //             model.addAttribute("registrationForm", userData);
    //             return new RedirectView("register.html");
    //         }
    //         try {
    //             userService.register(userData);
    //         }catch (DataIntegrityViolationException e){
    //             bindingResult.rejectValue("email", "userData.email","An account already exists for this email.");
    //             model.addAttribute("registrationForm", userData);
    //             return new RedirectView("register.html");
    //         }
    //         userService.register(userData);
    //         return new RedirectView("/");
      //  }

  @GetMapping("/memes")
  public List<MemesEntity> getMemes() {

    log.info("getMemes called");
    getResponseMeme getMemeResponse;
    getMemeResponse = memesService.getAllMemes();

    return getMemeResponse.getMemes();
  }

  @GetMapping("/memes/{memeId}")
  public MemesEntity getMeme(@PathVariable String memeId) {

    log.info("getMeme called with {}", memeId);
    MemesEntity getMemeResponse;
    getMemeResponse = memesService.getMemes(memeId);

    return getMemeResponse;
  }

  @PostMapping("/register")
  public String registerUser(@Validated @RequestBody String userJson) {
    log.info("Registering" + userJson);
    ObjectMapper objectMapper = new ObjectMapper();
    UserData userData = new UserData();
    try {
      userData = objectMapper.readValue(userJson, UserData.class);
      log.info(userData.getUsername());
      userService.register(userData);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if(Stream.of(userData.getPassword(),userData.getUsername()).allMatch(Objects::isNull))
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    log.info("registered");
    return "Success";
  }

  @PostMapping("/memes")
  public getPostResponse postMeme(@Validated @RequestBody String memeJson)  {
    log.info("postMeme called with {}", memeJson);
    ObjectMapper objectMapper = new ObjectMapper();
    MemesEntity meme = new MemesEntity();
    try {
      meme = objectMapper.readValue(memeJson, MemesEntity.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    if(Stream.of(meme.getName(),meme.getUrl(),meme.getCaption()).allMatch(Objects::isNull))
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't post empty object");
    return memesService.postMeme(meme);
  }

  @GetMapping("/error")
  public String error(HttpServletRequest request) {
	String message = (String) request.getSession().getAttribute("error.message");
	request.getSession().removeAttribute("error.message");
	return message;
  }

}
