package com.server.HealthNet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.UserAuthenticationService;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @GetMapping
    public String welcome() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("ACESSSSED THEEEE HOMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        System.out.println();
        System.out.println();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return "Welcom to HealthNet---you are not registered";
        }
        return "Welcom to HealthNet---you are " + userAuthentication.getRole().toString();
    }
}
