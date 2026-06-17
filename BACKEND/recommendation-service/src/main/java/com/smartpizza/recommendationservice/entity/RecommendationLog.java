package com.smartpizza.recommendationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private Long userId;

    private String recommendationType;

    @Column(length = 1000)
    private String recommendedItems;

    private String reason;

    private LocalDateTime createdAt;
}
