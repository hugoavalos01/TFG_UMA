package com.example.tfgapp.Controller;
import com.example.tfgapp.Controller.Dto.UsuarioDto;
import com.example.tfgapp.Entity.User;
import com.example.tfgapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody UsuarioDto user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User usuario = new User();
        usuario.setPassword(user.getPassword());
        usuario.setUsername(user.getUsername());
        userRepository.save(usuario);
        return "Usuario registrado con éxito";
    }



}

