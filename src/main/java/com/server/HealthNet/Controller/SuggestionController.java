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

import java.util.List;

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
}