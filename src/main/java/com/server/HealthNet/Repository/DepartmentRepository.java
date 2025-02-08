package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DepartmentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Department mapRowToDepartment(ResultSet rs, int rowNum) throws SQLException {
        Department department = new Department();
        department.setDepartment_id(rs.getLong("department_id"));
        department.setName(rs.getString("name"));
        return department;
    }

    public List<Department> findAll() {
        String sql = "SELECT * FROM department";
        return jdbcTemplate.query(sql, this::mapRowToDepartment);
    }

    public Department findById(Long id) {
        String sql = "SELECT * FROM department WHERE department_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToDepartment, id);
    }

    public int save(Department department) {
        String sql = "INSERT INTO department (name) VALUES (?)";
        return jdbcTemplate.update(sql, department.getName());
    }

    public int update(Department department) {
        String sql = "UPDATE department SET name = ? WHERE department_id = ?";
        return jdbcTemplate.update(sql, department.getName(), department.getDepartment_id());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM department WHERE department_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
