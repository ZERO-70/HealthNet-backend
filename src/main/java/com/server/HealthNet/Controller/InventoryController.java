package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Inventory;
import com.server.HealthNet.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAllInventoryItems() {
        return inventoryService.getAllInventoryItems();
    }

    @GetMapping("/{id}")
    public Inventory getInventoryItemById(@PathVariable Long id) {
        return inventoryService.getInventoryItemById(id);
    }

    @PostMapping
    public ResponseEntity<String> createInventoryItem(@RequestBody Inventory inventory) {
        return inventoryService.createInventoryItem(inventory) > 0
                ? new ResponseEntity<>("Inventory item created successfully", HttpStatus.OK)
                : new ResponseEntity<>("Inventory item creation failed", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateInventoryItem(@PathVariable Long id, @RequestBody Inventory inventory) {
        inventory.setInventory_id(id);
        return inventoryService.updateInventoryItem(inventory) > 0
                ? new ResponseEntity<>("Inventory item updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Inventory item update failed", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventoryItem(@PathVariable Long id) {
        return inventoryService.deleteInventoryItem(id) > 0
                ? new ResponseEntity<>("Inventory item deleted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Inventory item deletion failed", HttpStatus.NOT_FOUND);
    }
}
