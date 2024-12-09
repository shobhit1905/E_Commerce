package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId ;

    @Enumerated(EnumType.STRING) // to store the enum as String not as integers
    @Column(length = 20)
    private AppRoles roleName ;

    public Role(AppRoles appRoles) {
        this.roleName = appRoles;
    }
}
