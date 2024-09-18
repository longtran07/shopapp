package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "provider" ,length = 20,nullable = false)
    private String provider;

    @Column(name = "provider_id" ,length = 20,nullable = false)
    private String providerId;

    @Column(name = "name" ,length = 150,nullable = false)
    private String name;

    @Column(name = "email" ,length = 20,nullable = false)
    private String email;

}
