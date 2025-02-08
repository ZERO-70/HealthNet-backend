package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Treatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TreatementRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Treatement mapRowToTreatment(ResultSet rs, int rowNum) throws SQLException {
        Treatement treatment = new Treatement();
        treatment.setTreatement_id(rs.getLong("treatement_id"));
        treatment.setName(rs.getString("name"));
        treatment.setDoctor_id(rs.getLong("doctor_id"));
        treatment.setDepartment_id(rs.getLong("department_id"));
        return treatment;
    }

    public List<Treatement> findAll() {
        String sql = "SELECT * FROM treatement";
        return jdbcTemplate.query(sql, this::mapRowToTreatment);
    }

    public Treatement findById(Long id) {
        String sql = "SELECT * FROM treatement WHERE treatement_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToTreatment, id);
    }

    public int save(Treatement treatment) {
        String sql = "INSERT INTO treatement (name, doctor_id, department_id) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, treatment.getName(), treatment.getDoctor_id(), treatment.getDepartment_id());
    }

    public int update(Treatement treatment) {
        String sql = "UPDATE treatement SET name = ?, doctor_id = ?, department_id = ? WHERE treatement_id = ?";
        return jdbcTemplate.update(sql, treatment.getName(), treatment.getDoctor_id(), treatment.getDepartment_id(), treatment.getTreatement_id());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM treatement WHERE treatement_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
