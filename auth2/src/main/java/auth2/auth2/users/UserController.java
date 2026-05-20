package auth2.auth2.users;

import auth2.auth2.Jwt.jwtService;
import auth2.auth2.dtos.loginDto;
import auth2.auth2.dtos.registerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/User")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    jwtService jwt;

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody loginDto login) {
        String Email = login.getEmail();
        String Password = login.getPassword();
        System.out.println("searching the user with this email" + Email);
        System.out.println("user is found");
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(Email, Password));
        try {
            if (auth.isAuthenticated()) {
                Optional<Users> users = usersRepo.findByUserEmail(Email);
                 String token = jwt.JwtToken(users.get());
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                System.out.println("the token is " + token);
                return new ResponseEntity<>( response, HttpStatus.OK);
            }

        }  catch(Exception e){
                System.out.println("Authentication failed for: " + Email + " - Reason: " + e.getMessage());
                return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);


    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody registerDto dto) {

        if (usersRepo.existsByUserEmail(dto.getUserEmail())) {
            return new ResponseEntity<>("already exist", HttpStatus.BAD_REQUEST);
        }

        Users user = new Users();
        user.setUserEmail(dto.getUserEmail());
        user.setUserPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setUserRole(dto.getUserRole());
        usersRepo.save(user);
        return new ResponseEntity<>("Registerd", HttpStatus.CREATED);
    }





   }
