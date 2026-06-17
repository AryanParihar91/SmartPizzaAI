package com.smartpizza.menuservice.repository;

import com.smartpizza.menuservice.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    List<Pizza> findByAvailableTrue();

    List<Pizza> findByVeg(Boolean veg);

    List<Pizza> findByCategoryCategoryId(Long categoryId);
}
