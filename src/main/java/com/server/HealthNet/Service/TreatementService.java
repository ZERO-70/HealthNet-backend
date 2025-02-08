package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Treatement;
import com.server.HealthNet.Repository.TreatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TreatementService {

    @Autowired
    private TreatementRepository treatmentRepository;

    public List<Treatement> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    public Treatement getTreatmentById(Long id) {
        return treatmentRepository.findById(id);
    }

    public int createTreatment(Treatement treatment) {
        return treatmentRepository.save(treatment);
    }

    public int updateTreatment(Treatement treatment) {
        return treatmentRepository.update(treatment);
    }

    public int deleteTreatment(Long id) {
        return treatmentRepository.deleteById(id);
    }
}
