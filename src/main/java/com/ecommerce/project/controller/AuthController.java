package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignUpRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository ;

    @Autowired
    private PasswordEncoder encoder ;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication ;

        try{
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername() , loginRequest.getPassword())) ;
        }
        catch(AuthenticationException e){
            Map<String , Object> map = new HashMap<>() ;
            map.put("message" , "Bad Credentials") ;
            map.put("status" , false) ;

            return new ResponseEntity<>(map , HttpStatus.UNAUTHORIZED) ;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList() ;

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles , cookie.getValue()) ;

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE ,
                cookie.toString())
                .body(response) ;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignUpRequest request){
        if(userRepository.existsByUserName(request.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Username is already taken")) ;
        }

        if(userRepository.existsByUserEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Email is already in use")) ;
        }

        //Create new user

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername() , request.getEmail() , encoder.encode(request.getPassword())) ;

        Set<String> strRoles = request.getRoles() ;

        Set<Role> roles = new HashSet<>();

        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error : Role not found")) ;
            roles.add(userRole) ;
        }
        else{
            strRoles.forEach(role -> {
                switch(role) {
                    case "admin" :
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found")) ;
                        roles.add(adminRole) ;
                        break ;

                    case "seller" :
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found")) ;
                        roles.add(sellerRole) ;
                        break ;

                    default :
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error : Role not found")) ;
                        roles.add(userRole) ;
                }
            });
        }

        user.setRoles(roles) ;
        userRepository.save(user) ;

        return new ResponseEntity<>(new MessageResponse("User registered successfully"), HttpStatus.CREATED) ;
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if(authentication != null)
            return authentication.getName() ;
        else
            return null ;
    }

    @GetMapping("/currentUser")
    public ResponseEntity<?> currentUser(Authentication authentication){

        if(authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .toList();

            String firstName = userRepository.findFirstNameByUserId(userDetails.getId()).orElse(null);
            String lastName = userRepository.findLastNameByUserId(userDetails.getId()).orElse(null);

            UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), firstName ,
                    lastName , roles);
            return ResponseEntity.ok().body(response) ;
        }

        return null ;
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE ,
                        cookie.toString())
                .body(new MessageResponse("You have been logged out")) ;
    }
}
