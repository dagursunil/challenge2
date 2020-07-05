package com.journi.challenge.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;



/**
 * 
 * @author sdagur
 *
 */
@Controller
@ApiIgnore
public class RestClientController {

    @RequestMapping("/")
    public String home() {
        return "redirect:swagger-ui.html";
    }
}
