package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId ;

    @NotBlank(message = "User name should not be blank")
    @Size(min = 3 , message = "User name must contains atleast 3 characters")
    @Column(unique = true)
    private String userName ;

    @Email
    @NotBlank(message = "User email should not be blank")
    @Column(unique = true)
    private String userEmail ;

    @Size(min = 8 , message = "Password must contains atleast 8 characters")
    private String password ;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "user_roles" ,
    joinColumns = @JoinColumn(name = "userId") ,
    inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<Role> roles = new HashSet<>() ;

    @ToString.Exclude
    @OneToMany(mappedBy = "user" , fetch = FetchType.EAGER ,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST} ,
            orphanRemoval = true)
    private Set<Product> products = new HashSet<>() ;

    public User(String userName, String userEmail, String password) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
    }

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "user_address" ,
    joinColumns = @JoinColumn(name = "userId") ,
    inverseJoinColumns = @JoinColumn(name = "addressId"))
    private List<Address> addresses = new ArrayList<>() ;


    @OneToOne(mappedBy = "user", cascade = {CascadeType.MERGE , CascadeType.PERSIST} , orphanRemoval = true)
    @ToString.Exclude
    private Cart cart ;
}
