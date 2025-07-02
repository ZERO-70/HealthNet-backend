package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Suggestion;
import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.SuggestionService;
import com.server.HealthNet.Service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/suggestion")
@CrossOrigin(origins = "*")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private UserAuthenticationService userAuthService;

    // Admin: get all suggestions
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Suggestion> getAllSuggestions() {
        return suggestionService.getAllSuggestions();
    }

    // Admin or owner: get suggestion by id
    @GetMapping("/{id}")
    public ResponseEntity<Suggestion> getSuggestionById(@PathVariable Long id) {
        Suggestion suggestion = suggestionService.getSuggestionById(id);
        if (suggestion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (user.getRole().equals(Role.ADMIN) || user.getPersonId().equals(suggestion.getPersonId())) {
            return new ResponseEntity<>(suggestion, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Create suggestion: any authenticated user
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createSuggestion(@RequestBody Suggestion suggestion) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        suggestion.setPersonId(user.getPersonId());
        int res = suggestionService.createSuggestion(suggestion);
        return res > 0
                ? new ResponseEntity<>("Suggestion created", HttpStatus.OK)
                : new ResponseEntity<>("Failed to create suggestion", HttpStatus.BAD_REQUEST);
    }

    // Update suggestion: admin or owner
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateSuggestion(@RequestBody Suggestion suggestion) {
        Suggestion existing = suggestionService.getSuggestionById(suggestion.getSuggestionId());
        if (existing == null) {
            return new ResponseEntity<>("Suggestion not found", HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (!user.getRole().equals(Role.ADMIN) && !user.getPersonId().equals(existing.getPersonId())) {
            return new ResponseEntity<>("Not authorized", HttpStatus.FORBIDDEN);
        }
        int res = suggestionService.updateSuggestion(suggestion);
        return res > 0
                ? new ResponseEntity<>("Suggestion updated", HttpStatus.OK)
                : new ResponseEntity<>("Update failed", HttpStatus.BAD_REQUEST);
    }

    // Delete suggestion: admin or owner
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSuggestion(@PathVariable Long id) {
        Suggestion existing = suggestionService.getSuggestionById(id);
        if (existing == null) {
            return new ResponseEntity<>("Suggestion not found", HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (!user.getRole().equals(Role.ADMIN) && !user.getPersonId().equals(existing.getPersonId())) {
            return new ResponseEntity<>("Not authorized", HttpStatus.FORBIDDEN);
        }
        int res = suggestionService.deleteSuggestion(id);
        return res > 0
                ? new ResponseEntity<>("Suggestion deleted", HttpStatus.OK)
                : new ResponseEntity<>("Deletion failed", HttpStatus.BAD_REQUEST);
    }

    // Get suggestions for current user
    @GetMapping("/getmine")
    @PreAuthorize("isAuthenticated()")
    public List<Suggestion> getMySuggestions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        return suggestionService.getSuggestionsByPersonId(user.getPersonId());
    }

    // Get the date of the last suggestion update
    @GetMapping("/lastupdated")
    public ResponseEntity<Map<String, Object>> getLastUpdateDate() {
        LocalDate lastUpdateDate = suggestionService.getLastSuggestionUpdateDate();
        Map<String, Object> response = new HashMap<>();

        if (lastUpdateDate != null) {
            response.put("lastUpdated", lastUpdateDate);
            response.put("isUpdatedToday", lastUpdateDate.equals(LocalDate.now()));
        } else {
            response.put("lastUpdated", null);
            response.put("isUpdatedToday", false);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get only the last suggestion update date for authenticated users
    @GetMapping("/lastUpdateDate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LocalDate> getLastSuggestionUpdateDate() {
        LocalDate lastUpdateDate = suggestionService.getLastSuggestionUpdateDate();
        return new ResponseEntity<>(lastUpdateDate, HttpStatus.OK);
    }

    // Force update suggestions (admin only)
    @PostMapping("/forceupdate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> forceUpdateSuggestions() {
        suggestionService.fetchDailySuggestions();
        return new ResponseEntity<>("Suggestions updated successfully", HttpStatus.OK);
    }

    // Generate advice for current patient only (PATIENT role only)
    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> generateAdviceForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        
        // Only allow patients to generate advice
        if (!user.getRole().equals(Role.PATIENT)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Only patients can generate medical advice");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        
        // Delete only this patient's existing suggestions before generating new ones
        List<Suggestion> existingSuggestions = suggestionService.getSuggestionsByPersonId(user.getPersonId());
        for (Suggestion suggestion : existingSuggestions) {
            suggestionService.deleteSuggestion(suggestion.getSuggestionId());
        }
        
        // Start async advice generation for this patient only
        CompletableFuture.supplyAsync(() -> {
            return suggestionService.fetchAndSaveMedicalAdvice(user.getPersonId());
        });
        
        // Return immediate response with status
        Map<String, Object> response = new HashMap<>();
        response.put("status", "generating");
        response.put("message", "Medical advice generation started for your profile");
        response.put("patientId", user.getPersonId());
        response.put("timestamp", LocalDateTime.now());
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    // Check generation status for current patient
    @GetMapping("/generation-status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkGenerationStatus() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        
        // Only allow patients to check status
        if (!user.getRole().equals(Role.PATIENT)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Only patients can check advice generation status");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        
        // Get recent suggestions for this patient (within last 10 minutes)
        List<Suggestion> recentSuggestions = suggestionService.getRecentSuggestionsByPersonId(user.getPersonId(), 10);
        
        Map<String, Object> response = new HashMap<>();
        if (!recentSuggestions.isEmpty()) {
            response.put("status", "completed");
            response.put("suggestion", recentSuggestions.get(0));
            response.put("message", "Your personalized medical advice is ready!");
        } else {
            response.put("status", "generating");
            response.put("message", "AI is analyzing your medical data to generate personalized advice...");
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}