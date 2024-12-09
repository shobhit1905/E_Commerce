package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository ;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder ;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticator(@RequestBody LoginRequest loginRequest){
        Authentication authentication ;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName() , loginRequest.getPassword())) ;
        }
        catch (AuthenticationException exception){
            Map<String, Object> map = new HashMap<>();
            map.put("message" , "Bad Credentials");
            map.put("status" , false) ;

            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails) ;

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse response = new UserInfoResponse(userDetails.getId() , userDetails.getUsername() , jwtToken , roles) ;

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/singup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        // check for existing user
        if(userRepository.existsByUserName(signupRequest.getUserName())){
            return new ResponseEntity<>(new MessageResponse("Error : Username is already taken!") , HttpStatus.BAD_REQUEST) ;
        }

        // check is email is already taken
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(new MessageResponse("Error : Email is already in use!") , HttpStatus.BAD_REQUEST) ;
        }

        // Create new user account
        User user = new User(signupRequest.getUserName(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword())) ;

        Set<String> strRoles = signupRequest.getRoles() ; // will hold role name that needs to be registered
        Set<Role> roles = new HashSet<>(); // hold the role entities that needs to be assigned to the user

        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error : Role not found.")) ;
            roles.add(userRole) ;
        }
        else{
            strRoles.forEach(
                    role -> {
                        switch(role) {
                            case "admin" :
                                Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error : Role not found.")) ;
                                roles.add(adminRole) ;
                                break ;
                            case "seller" :
                                Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                                        .orElseThrow(() -> new RuntimeException("Error : Role not found.")) ;
                                roles.add(sellerRole) ;
                                break ;
                            default :
                                Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error : Role not found.")) ;
                                roles.add(userRole) ;
                                break ;
                        }
                    }
            );
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("User registered successfully") , HttpStatus.CREATED) ;
    }
}
