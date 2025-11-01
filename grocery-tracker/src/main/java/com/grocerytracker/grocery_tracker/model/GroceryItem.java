package com.grocerytracker.grocery_tracker.model;

import jakarta.persistence.*;

@Entity
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;
    private String category;
    private int minQuantity; // For low-stock alert
    private String unit;     // ✅ Added field

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getMinQuantity() { return minQuantity; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }

    public String getUnit() { return unit; }       // ✅ Getter
    public void setUnit(String unit) { this.unit = unit; } // ✅ Setter

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
