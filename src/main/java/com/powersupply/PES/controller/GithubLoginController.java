package com.powersupply.PES.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GithubLoginController {

    @GetMapping("/login/oauth2/code/github")
    public ResponseEntity<String> getCode(@RequestParam String code) {
        return ResponseEntity.ok().body(code);
    }
}
