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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId ;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    @NotBlank
    @Size(min = 1, max = 20)
    @Column(unique = true)
    private String userName ;

    @NotBlank
    @Size(min = 1, max = 20)
    @Email
    @Column(unique = true)
    private String email ;

    @NotBlank
    @Size(min = 8, max = 120)
    private String password ;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE} , fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "userId"),
    inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<Role> roles = new HashSet<>() ;


    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Product> products = new HashSet<>() ;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_address",
    joinColumns = @JoinColumn(name = "userId"),
    inverseJoinColumns = @JoinColumn(name = "addressId"))
    private List<Address> addresses = new ArrayList<>() ;
}
