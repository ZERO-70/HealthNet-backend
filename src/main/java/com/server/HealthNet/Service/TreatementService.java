package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Treatement;
import com.server.HealthNet.Model.TreatmentSummaryDTO;
import com.server.HealthNet.Repository.TreatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Get all treatment summaries (ID and name only)
     * Lightweight method for dropdowns and selections
     */
    public List<TreatmentSummaryDTO> getAllTreatmentSummaries() {
        return treatmentRepository.findAll().stream()
                .map(treatment -> new TreatmentSummaryDTO(treatment.getTreatement_id(), treatment.getName()))
                .collect(Collectors.toList());
    }
}
