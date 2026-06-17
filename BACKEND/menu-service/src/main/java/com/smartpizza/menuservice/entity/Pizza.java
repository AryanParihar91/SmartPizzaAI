package com.smartpizza.menuservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pizzaId;

    @Column(nullable = false)
    private String pizzaName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    private String size;

    private String imageUrl;

    private Boolean available;

    private Boolean veg;

    private Integer preparationTimeMinutes;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}