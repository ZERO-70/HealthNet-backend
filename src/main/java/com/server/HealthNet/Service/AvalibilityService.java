package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Avalibility;
import com.server.HealthNet.Repository.AvalibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvalibilityService {

    @Autowired
    private AvalibilityRepository repository;

    public int addAvalibility(Avalibility avalibility) {
        return repository.save(avalibility);
    }

    public List<Avalibility> getAllAvalibilities() {
        return repository.findAll();
    }

    public Optional<Avalibility> getAvalibilityById(Long id) {
        return repository.findById(id);
    }

    public int deleteAvalibilityById(Long id) {
        return repository.deleteById(id);
    }

    public int updateAvalibility(Long id, Avalibility avalibility) {
        return repository.update(id, avalibility);
    }
}
