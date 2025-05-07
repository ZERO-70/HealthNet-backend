package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Suggestion;
import com.server.HealthNet.Repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    public Suggestion getSuggestionById(Long id) {
        return suggestionRepository.findById(id);
    }

    public List<Suggestion> getSuggestionsByPersonId(Long personId) {
        return suggestionRepository.findByPersonId(personId);
    }

    public int createSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public int updateSuggestion(Suggestion suggestion) {
        return suggestionRepository.update(suggestion);
    }

    public int deleteSuggestion(Long id) {
        return suggestionRepository.deleteById(id);
    }
}