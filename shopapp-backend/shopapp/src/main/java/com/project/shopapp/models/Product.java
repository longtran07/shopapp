package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@EntityListeners(ProductListener.class)
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false,length = 350)
    private String name;

    private Float price;

    @Column(name = "thumbnail" ,length = 300)
    private String thumbnail;

    @Column(name = "description")
    private String description;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List <ProductImage> productImages;
}
