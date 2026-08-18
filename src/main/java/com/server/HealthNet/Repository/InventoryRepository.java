package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InventoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Inventory mapRowToInventory(ResultSet rs, int rowNum) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventory_id(rs.getLong("inventory_id"));
        inventory.setName(rs.getString("name"));
        inventory.setQuantity(rs.getLong("quantity"));

        // Safely handle nullable expiry_date
        if (rs.getDate("expiry_date") != null) {
            inventory.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        } else {
            inventory.setExpiryDate(null); // or set a default LocalDate if needed
        }

        inventory.setDepartment_id(rs.getLong("department_id"));
        return inventory;
    }

    public List<Inventory> findAll() {
        String sql = "SELECT * FROM inventory";
        return jdbcTemplate.query(sql, this::mapRowToInventory);
    }

    public Inventory findById(Long id) {
        if (id == null) {
            return null;
        }
        // query(...) rather than queryForObject(...): a missing row is an ordinary
        // "not found" that callers already null-check, whereas queryForObject throws
        // EmptyResultDataAccessException, which surfaces to the client as an opaque
        // error rather than a 404.
        String sql = "SELECT * FROM inventory WHERE inventory_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToInventory, id).stream().findFirst().orElse(null);
    }

    public int save(Inventory inventory) {
        String sql = "INSERT INTO inventory (name, quantity, expiry_date, department_id) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, inventory.getName(), inventory.getQuantity(), inventory.getExpiryDate(),
                inventory.getDepartment_id());
    }

    public int update(Inventory inventory) {
        String sql = "UPDATE inventory SET name = ?, quantity = ?, expiry_date = ?, department_id = ? WHERE inventory_id = ?";
        return jdbcTemplate.update(sql, inventory.getName(), inventory.getQuantity(), inventory.getExpiryDate(),
                inventory.getDepartment_id(), inventory.getInventory_id());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
