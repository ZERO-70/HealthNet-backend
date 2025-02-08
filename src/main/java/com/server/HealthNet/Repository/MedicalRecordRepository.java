package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MedicalRecordRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MedicalRecord mapRowToMedicalRecord(ResultSet rs, int rowNum) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecord_id(rs.getLong("record_id"));
        record.setDepartment_id(rs.getLong("department_id"));
        record.setPatient_id(rs.getLong("patient_id"));
        record.setTreatement_id(rs.getLong("treatement_id"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setBloodpressure(rs.getString("bloodpressure"));
        record.setDate(rs.getDate("date").toLocalDate());
        return record;
    }

    public List<MedicalRecord> findAll() {
        String sql = "SELECT * FROM medical_records";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord);
    }

    public MedicalRecord findById(Long id) {
        String sql = "SELECT * FROM medical_records WHERE record_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMedicalRecord, id);
    }

    public int save(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (department_id, patient_id, treatement_id, diagnosis, bloodpressure, date) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, record.getDepartment_id(), record.getPatient_id(), record.getTreatement_id(), record.getDiagnosis(), record.getBloodpressure(), record.getDate());
    }

    public int update(MedicalRecord record) {
        String sql = "UPDATE medical_records SET department_id = ?, patient_id = ?, treatement_id = ?, diagnosis = ?, bloodpressure = ?, date = ? WHERE record_id = ?";
        return jdbcTemplate.update(sql, record.getDepartment_id(), record.getPatient_id(), record.getTreatement_id(), record.getDiagnosis(), record.getBloodpressure(), record.getDate(), record.getRecord_id());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    public List<MedicalRecord> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM medical_records WHERE patient_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMedicalRecord, patientId);
    }
}
