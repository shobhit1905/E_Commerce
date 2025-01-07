package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole roleName ;

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }

    @CreationTimestamp
    private LocalDateTime creationDate ;

    @UpdateTimestamp
    private LocalDateTime modificationDate ;
}
