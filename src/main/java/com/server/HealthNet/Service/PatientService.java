package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Patient;
import com.server.HealthNet.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Optional<Patient> getPatientById(Long patientId) {
        return patientRepository.findPatientById(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAllPatients();
    }

    public int deletePatientById(Long patientId) {
        return patientRepository.deletePatientById(patientId);
    }

    public int updatePatient(Patient patient) {
        return patientRepository.updatePatient(patient);
    }

    public Long addPatient(Patient patient) {
        return patientRepository.savePatient(patient);
    }
}
