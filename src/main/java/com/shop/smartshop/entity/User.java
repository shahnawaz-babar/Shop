package com.shop.smartshop.entity;


import com.shop.smartshop.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name="users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(name = "mobile_verified", nullable = false)
    private boolean mobileVerified = false;
    private String mobileNumber;
    private String email;
    private String password;
    private String address;
    private String imageUrl;
    private String authProvider;
    @Column(length = 512)
    private String profileImageUrl;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<UserSession> userSessions;
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Cart cart;

}
