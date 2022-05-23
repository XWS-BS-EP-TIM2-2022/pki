package com.example.PKI.model.enumerations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "ROLE")
public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 1L;
    public static final String ADMIN_ROLE ="ROLE_ADMIN";
    public static final String INTERMEDIATE_ROLE="ROLE_INTERMEDIATE";
    public static final String END_USER_ROLE ="ROLE_END_USER";
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;


    public static Role of(String name){
        switch (name){
            case ADMIN_ROLE:return new Role(ADMIN_ROLE);
            case INTERMEDIATE_ROLE:return new Role(INTERMEDIATE_ROLE);
            case END_USER_ROLE:return new Role(END_USER_ROLE);
            default: return null;
        }
    }

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
