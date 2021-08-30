package com.crio.starter.configuration;

import com.crio.starter.data.*;
import com.crio.starter.exchange.*;
import com.crio.starter.repository.*;
import com.crio.starter.service.*;
import com.crio.starter.service.MemesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.mongodb.lang.NonNull;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableResourceServer
@Log4j2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
@Autowired
UserRepository userRepository;
@Autowired
private final MemesService memesService;
@Autowired
private UserService userService;
private Map<String, LocalDateTime> usersLastAccess = new HashMap<>();
@RequestMapping("/public")
public String public_pagr() {
        return "Page Public";
}
@RequestMapping("/private")
public String private_page() {
        return "Page Private";
}
@RequestMapping("/admin")
public String admin() {
        return "Page Administrator";
}
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
@Override
public void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests().antMatchers("/oauth/token", "/authorize**", "/public").permitAll();
//			 .anyRequest().authenticated();
        http.requestMatchers().antMatchers("/private","/memes**")
        .and().authorizeRequests()
        .antMatchers("/private","/memes").access("hasRole('USER')")
        .and().requestMatchers().antMatchers("/admin")
        .and().authorizeRequests()
        .antMatchers("/admin").access("hasRole('ADMIN')");
}

}
