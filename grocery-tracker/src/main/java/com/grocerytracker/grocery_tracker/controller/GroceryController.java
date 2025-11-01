package com.grocerytracker.grocery_tracker.controller;

import com.grocerytracker.grocery_tracker.model.GroceryItem;
import com.grocerytracker.grocery_tracker.model.User;
import com.grocerytracker.grocery_tracker.repository.GroceryRepository;
import com.grocerytracker.grocery_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GroceryController {

    @Autowired
    private GroceryRepository groceryRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Home page (user-specific dashboard)
    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            model.addAttribute("message", "Welcome to Grocery Tracker! Please log in.");
            return "index";
        }

        // Fetch logged-in user
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<GroceryItem> userItems = groceryRepository.findByUser(user);
        model.addAttribute("groceries", userItems);
        model.addAttribute("username", user.getUsername());

        // 🔔 Low-stock alert (fixed method name)
        List<GroceryItem> lowStockItems = userItems.stream()
                .filter(item -> item.getQuantity() <= item.getMinQuantity())
                .toList();

        model.addAttribute("lowStockItems", lowStockItems);
        return "index";
    }

    // ✅ Add new item (GET form)
    @GetMapping("/add")
    public String showAddItemForm(Model model) {
        model.addAttribute("groceryItem", new GroceryItem());
        return "add-item";
    }

    // ✅ Add new item (POST submit)
    @PostMapping("/add")
    public String addItem(@AuthenticationPrincipal UserDetails userDetails,
                          @ModelAttribute GroceryItem groceryItem) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        groceryItem.setUser(user);
        groceryRepository.save(groceryItem);
        return "redirect:/";
    }

    // ✅ Edit existing item (GET form)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        GroceryItem groceryItem = groceryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + id));
        model.addAttribute("groceryItem", groceryItem);
        return "edit-item";
    }

    // ✅ Handle edit submission
    @PostMapping("/edit/{id}")
    public String updateItem(@PathVariable("id") Long id, @ModelAttribute GroceryItem groceryItem) {
        groceryItem.setId(id);
        groceryRepository.save(groceryItem);
        return "redirect:/";
    }

    // ✅ Delete an item
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Long id) {
        GroceryItem groceryItem = groceryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + id));
        groceryRepository.delete(groceryItem);
        return "redirect:/";
    }

    // ✅ Show "Use Item" form
    @GetMapping("/use/{id}")
    public String showUseItemForm(@PathVariable Long id, Model model) {
        GroceryItem item = groceryRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/";
        }
        model.addAttribute("groceryItem", item);
        return "use-item"; // Loads use-item.html
    }

    // ✅ Handle "Use Item" submission
    @PostMapping("/use")
    public String useItem(@ModelAttribute GroceryItem groceryItem, @RequestParam("usedAmount") int usedAmount) {
        GroceryItem existing = groceryRepository.findById(groceryItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));

        int newQuantity = existing.getQuantity() - usedAmount;
        if (newQuantity < 0) newQuantity = 0;

        existing.setQuantity(newQuantity);
        groceryRepository.save(existing);
        return "redirect:/";
    }
 // ✅ Profile page
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // if not logged in
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch all grocery items for this user
        List<GroceryItem> userItems = groceryRepository.findByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("itemCount", userItems.size());

        return "profile"; // loads profile.html
    }

}
