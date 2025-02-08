package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Inventory;
import com.server.HealthNet.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryItemById(Long id) {
        return inventoryRepository.findById(id);
    }

    public int createInventoryItem(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public int updateInventoryItem(Inventory inventory) {
        return inventoryRepository.update(inventory);
    }

    public int deleteInventoryItem(Long id) {
        return inventoryRepository.deleteById(id);
    }
}
