package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public class SuggestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Map ResultSet to Suggestion object
    private Suggestion mapRowToSuggestion(ResultSet rs, int rowNum) throws SQLException {
        Suggestion suggestion = new Suggestion();
        suggestion.setSuggestionId(rs.getLong("suggestion_id"));
        suggestion.setPersonId(rs.getLong("person_id"));
        suggestion.setSuggestionText(rs.getString("suggestion_text"));
        suggestion.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return suggestion;
    }

    public List<Suggestion> findAll() {
        String sql = "SELECT * FROM suggestion";
        return jdbcTemplate.query(sql, this::mapRowToSuggestion);
    }

    public Suggestion findById(Long id) {
        String sql = "SELECT * FROM suggestion WHERE suggestion_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToSuggestion, id);
    }

    public List<Suggestion> findByPersonId(Long personId) {
        String sql = "SELECT * FROM suggestion WHERE person_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToSuggestion, personId);
    }

    /**
     * Find recent suggestions for a specific person within the specified minutes
     */
    public List<Suggestion> findRecentByPersonId(Long personId, int withinMinutes) {
        String sql = "SELECT * FROM suggestion WHERE person_id = ? AND created_at >= NOW() - INTERVAL ? MINUTE ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRowToSuggestion, personId, withinMinutes);
    }

    public int save(Suggestion suggestion) {
        String sql = "INSERT INTO suggestion (person_id, suggestion_text) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                suggestion.getPersonId(),
                suggestion.getSuggestionText());
    }

    public int update(Suggestion suggestion) {
        String sql = "UPDATE suggestion SET suggestion_text = ? WHERE suggestion_id = ?";
        return jdbcTemplate.update(sql,
                suggestion.getSuggestionText(),
                suggestion.getSuggestionId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM suggestion WHERE suggestion_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Delete all suggestions from the suggestion table
     * 
     * @return the number of rows affected
     */
    public int deleteAll() {
        String sql = "DELETE FROM suggestion";
        return jdbcTemplate.update(sql);
    }
}