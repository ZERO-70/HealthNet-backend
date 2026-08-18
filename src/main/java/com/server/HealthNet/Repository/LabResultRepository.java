package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.LabResult;
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
public class LabResultRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LabResult mapRowToLabResult(ResultSet rs, int rowNum) throws SQLException {
        LabResult labResult = new LabResult();
        labResult.setResultId(rs.getLong("result_id"));
        labResult.setRecordId(rs.getLong("record_id"));
        labResult.setTestName(rs.getString("test_name"));
        labResult.setTestValue(rs.getString("test_value"));
        labResult.setTestUnit(rs.getString("test_unit"));
        labResult.setReferenceRange(rs.getString("reference_range"));
        labResult.setIsAbnormal(rs.getBoolean("is_abnormal"));
        labResult.setNotes(rs.getString("notes"));
        return labResult;
    }

    public List<LabResult> findByRecordId(Long recordId) {
        String sql = "SELECT * FROM lab_results WHERE record_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToLabResult, recordId);
    }

    public LabResult findById(Long resultId) {
        if (resultId == null) {
            return null;
        }
        // query(...) rather than queryForObject(...): a missing row is an ordinary
        // "not found" that callers already null-check, whereas queryForObject throws
        // EmptyResultDataAccessException, which surfaces to the client as an opaque
        // error rather than a 404.
        String sql = "SELECT * FROM lab_results WHERE result_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToLabResult, resultId).stream().findFirst().orElse(null);
    }

    public Long save(LabResult labResult) {
        String sql = "INSERT INTO lab_results " +
                "(record_id, test_name, test_value, test_unit, reference_range, is_abnormal, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, labResult.getRecordId());
            ps.setString(2, labResult.getTestName());
            ps.setString(3, labResult.getTestValue());
            ps.setString(4, labResult.getTestUnit());
            ps.setString(5, labResult.getReferenceRange());
            ps.setBoolean(6, labResult.getIsAbnormal() != null ? labResult.getIsAbnormal() : false);
            ps.setString(7, labResult.getNotes());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(LabResult labResult) {
        String sql = "UPDATE lab_results SET " +
                "test_name = ?, test_value = ?, test_unit = ?, " +
                "reference_range = ?, is_abnormal = ?, notes = ? " +
                "WHERE result_id = ?";

        return jdbcTemplate.update(sql,
                labResult.getTestName(),
                labResult.getTestValue(),
                labResult.getTestUnit(),
                labResult.getReferenceRange(),
                labResult.getIsAbnormal(),
                labResult.getNotes(),
                labResult.getResultId());
    }

    public int deleteById(Long resultId) {
        String sql = "DELETE FROM lab_results WHERE result_id = ?";
        return jdbcTemplate.update(sql, resultId);
    }

    public int deleteByRecordId(Long recordId) {
        String sql = "DELETE FROM lab_results WHERE record_id = ?";
        return jdbcTemplate.update(sql, recordId);
    }

    public List<LabResult> findAbnormalResults(Long patientId) {
        String sql = "SELECT lr.* FROM lab_results lr " +
                "JOIN medical_records mr ON lr.record_id = mr.record_id " +
                "WHERE mr.patient_id = ? AND lr.is_abnormal = true " +
                "ORDER BY mr.record_date DESC";
        return jdbcTemplate.query(sql, this::mapRowToLabResult, patientId);
    }
}