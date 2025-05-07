package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public class ChatRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Map ResultSet to Chat object
    private Chat mapRowToChat(ResultSet rs, int rowNum) throws SQLException {
        Chat chat = new Chat();
        chat.setMessageId(rs.getLong("message_id"));
        chat.setPersonId(rs.getLong("person_id"));
        chat.setMessageText(rs.getString("message_text"));
        chat.setTimestamp(rs.getObject("timestamp", LocalDateTime.class));
        return chat;
    }

    public List<Chat> findAll() {
        String sql = "SELECT * FROM chat";
        return jdbcTemplate.query(sql, this::mapRowToChat);
    }

    public Chat findById(Long id) {
        String sql = "SELECT * FROM chat WHERE message_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToChat, id);
    }

    public List<Chat> findByPersonId(Long personId) {
        String sql = "SELECT * FROM chat WHERE person_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToChat, personId);
    }

    public int save(Chat chat) {
        String sql = "INSERT INTO chat (person_id, message_text, timestamp) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                chat.getPersonId(),
                chat.getMessageText(),
                chat.getTimestamp());
    }

    public int update(Chat chat) {
        String sql = "UPDATE chat SET message_text = ?, timestamp = ? WHERE message_id = ?";
        return jdbcTemplate.update(sql,
                chat.getMessageText(),
                chat.getTimestamp(),
                chat.getMessageId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM chat WHERE message_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}