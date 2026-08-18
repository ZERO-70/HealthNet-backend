package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class MedicalRecordRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MedicalRecord mapRowToMedicalRecord(ResultSet rs, int rowNum) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(rs.getLong("record_id"));
        record.setPatientId(rs.getLong("patient_id"));
        record.setDoctorId(rs.getLong("doctor_id"));
        record.setDepartmentId(rs.getLong("department_id"));
        record.setTreatmentId(rs.getLong("treatment_id"));
        record.setRecordType(rs.getString("record_type"));
        record.setTitle(rs.getString("title"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setNotes(rs.getString("notes"));
        record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        record.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        record.setRecordDate(rs.getDate("record_date").toLocalDate());

        // Vital signs
        record.setBloodPressure(rs.getString("blood_pressure"));
        record.setHeartRate(rs.getInt("heart_rate"));
        record.setRespiratoryRate(rs.getInt("respiratory_rate"));
        record.setTemperature(rs.getDouble("temperature"));
        record.setOxygenSaturation(rs.getInt("oxygen_saturation"));
        record.setHeight(rs.getDouble("height"));
        record.setWeight(rs.getDouble("weight"));

        return record;
    }

    public List<MedicalRecord> findAll() {
        String sql = "SELECT * FROM medical_records";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord);
    }

    public MedicalRecord findById(Long id) {
        if (id == null) {
            return null;
        }
        // query(...) rather than queryForObject(...): a missing row is an ordinary
        // "not found" that callers already null-check, whereas queryForObject throws
        // EmptyResultDataAccessException, which surfaces to the client as an opaque
        // error rather than a 404.
        String sql = "SELECT * FROM medical_records WHERE record_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord, id).stream().findFirst().orElse(null);
    }

    public Long save(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, doctor_id, department_id, treatment_id, " +
                "record_type, title, diagnosis, notes, record_date, " +
                "blood_pressure, heart_rate, respiratory_rate, temperature, " +
                "oxygen_saturation, height, weight) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getPatientId());
            ps.setLong(2, record.getDoctorId());
            ps.setLong(3, record.getDepartmentId());

            if (record.getTreatmentId() != null) {
                ps.setLong(4, record.getTreatmentId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }

            ps.setString(5, record.getRecordType());
            ps.setString(6, record.getTitle());
            ps.setString(7, record.getDiagnosis());
            ps.setString(8, record.getNotes());
            ps.setObject(9, record.getRecordDate());
            ps.setString(10, record.getBloodPressure());

            if (record.getHeartRate() != null) {
                ps.setInt(11, record.getHeartRate());
            } else {
                ps.setNull(11, java.sql.Types.INTEGER);
            }

            if (record.getRespiratoryRate() != null) {
                ps.setInt(12, record.getRespiratoryRate());
            } else {
                ps.setNull(12, java.sql.Types.INTEGER);
            }

            if (record.getTemperature() != null) {
                ps.setDouble(13, record.getTemperature());
            } else {
                ps.setNull(13, java.sql.Types.DOUBLE);
            }

            if (record.getOxygenSaturation() != null) {
                ps.setInt(14, record.getOxygenSaturation());
            } else {
                ps.setNull(14, java.sql.Types.INTEGER);
            }

            if (record.getHeight() != null) {
                ps.setDouble(15, record.getHeight());
            } else {
                ps.setNull(15, java.sql.Types.DOUBLE);
            }

            if (record.getWeight() != null) {
                ps.setDouble(16, record.getWeight());
            } else {
                ps.setNull(16, java.sql.Types.DOUBLE);
            }

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(MedicalRecord record) {
        String sql = "UPDATE medical_records SET " +
                "patient_id = ?, doctor_id = ?, department_id = ?, treatment_id = ?, " +
                "record_type = ?, title = ?, diagnosis = ?, notes = ?, record_date = ?, " +
                "blood_pressure = ?, heart_rate = ?, respiratory_rate = ?, " +
                "temperature = ?, oxygen_saturation = ?, height = ?, weight = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE record_id = ?";

        return jdbcTemplate.update(sql,
                record.getPatientId(),
                record.getDoctorId(),
                record.getDepartmentId(),
                record.getTreatmentId(),
                record.getRecordType(),
                record.getTitle(),
                record.getDiagnosis(),
                record.getNotes(),
                record.getRecordDate(),
                record.getBloodPressure(),
                record.getHeartRate(),
                record.getRespiratoryRate(),
                record.getTemperature(),
                record.getOxygenSaturation(),
                record.getHeight(),
                record.getWeight(),
                record.getRecordId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<MedicalRecord> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM medical_records WHERE patient_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord, patientId);
    }

    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        String sql = "SELECT * FROM medical_records WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord, doctorId);
    }

    public List<MedicalRecord> findByPatientIdAndDateRange(Long patientId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM medical_records WHERE patient_id = ? AND record_date BETWEEN ? AND ? ORDER BY record_date DESC";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord, patientId, startDate, endDate);
    }
}
