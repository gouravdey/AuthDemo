package com.gd.demoserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/server")
public class ServerController {

    @GetMapping("/test")
    public String test() {
        return "Returning from server";
    }
}
