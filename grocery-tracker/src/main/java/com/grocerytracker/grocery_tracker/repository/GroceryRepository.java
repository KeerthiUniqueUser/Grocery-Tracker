package com.grocerytracker.grocery_tracker.repository;

import com.grocerytracker.grocery_tracker.model.GroceryItem;
import com.grocerytracker.grocery_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findByUser(User user);
}
