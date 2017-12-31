package br.com.siecola.gae_exemplo1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/test")
public class HelloController {

    private static final Logger log = Logger.getLogger(HelloController.class.getName());

    @GetMapping("/dog/{cachorro}")
    public String hello(@PathVariable String cachorro) {
        log.info("Dog: " + cachorro);

        return "Hello World - " + cachorro;
    }
}