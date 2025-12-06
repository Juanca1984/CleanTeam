package com.cleanteam.mandarinplayer.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {
    @GetMapping("/api/secret")
    public String secret() {
        return "Este endpoint está protegido. Si ves esto, estás autenticado.";
    }
}
