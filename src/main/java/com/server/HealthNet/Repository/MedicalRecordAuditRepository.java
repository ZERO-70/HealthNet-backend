package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.MedicalRecordAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class MedicalRecordAuditRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MedicalRecordAudit mapRowToAudit(ResultSet rs, int rowNum) throws SQLException {
        MedicalRecordAudit audit = new MedicalRecordAudit();
        audit.setAuditId(rs.getLong("audit_id"));
        audit.setRecordId(rs.getLong("record_id"));
        audit.setUserId(rs.getLong("user_id"));
        audit.setActionType(rs.getString("action_type"));
        audit.setActionTimestamp(rs.getTimestamp("action_timestamp").toLocalDateTime());
        audit.setActionDetails(rs.getString("action_details"));
        return audit;
    }

    public List<MedicalRecordAudit> findByRecordId(Long recordId) {
        String sql = "SELECT * FROM medical_record_audit WHERE record_id = ? ORDER BY action_timestamp DESC";
        return jdbcTemplate.query(sql, this::mapRowToAudit, recordId);
    }

    public List<MedicalRecordAudit> findByUserId(Long userId) {
        String sql = "SELECT * FROM medical_record_audit WHERE user_id = ? ORDER BY action_timestamp DESC";
        return jdbcTemplate.query(sql, this::mapRowToAudit, userId);
    }

    public MedicalRecordAudit findById(Long auditId) {
        String sql = "SELECT * FROM medical_record_audit WHERE audit_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToAudit, auditId);
    }

    public Long save(MedicalRecordAudit audit) {
        String sql = "INSERT INTO medical_record_audit " +
                "(record_id, user_id, action_type, action_details) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, audit.getRecordId());
            ps.setLong(2, audit.getUserId());
            ps.setString(3, audit.getActionType());
            ps.setString(4, audit.getActionDetails());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int deleteByRecordId(Long recordId) {
        String sql = "DELETE FROM medical_record_audit WHERE record_id = ?";
        return jdbcTemplate.update(sql, recordId);
    }
}