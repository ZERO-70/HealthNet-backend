package com.server.HealthNet.Service;

import com.server.HealthNet.Model.MedicalRecord;
import com.server.HealthNet.Repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public int createMedicalRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    public int updateMedicalRecord(MedicalRecord record) {
        return medicalRecordRepository.update(record);
    }

    public int deleteMedicalRecord(Long id) {
        return medicalRecordRepository.deleteById(id);
    }

    public List<MedicalRecord> getmyMedicalRecords(Long id){
        return medicalRecordRepository.findByPatientId(id);
    }
}
