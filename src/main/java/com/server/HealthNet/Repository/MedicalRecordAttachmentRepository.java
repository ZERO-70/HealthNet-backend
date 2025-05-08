package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.MedicalRecordAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class MedicalRecordAttachmentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MedicalRecordAttachment mapRowToAttachment(ResultSet rs, int rowNum) throws SQLException {
        MedicalRecordAttachment attachment = new MedicalRecordAttachment();
        attachment.setAttachmentId(rs.getLong("attachment_id"));
        attachment.setRecordId(rs.getLong("record_id"));
        attachment.setFileName(rs.getString("file_name"));
        attachment.setFileType(rs.getString("file_type"));
        attachment.setContentType(rs.getString("content_type"));
        attachment.setFileSize(rs.getLong("file_size"));
        attachment.setFileData(rs.getBytes("file_data"));
        attachment.setFilePath(rs.getString("file_path"));
        attachment.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        attachment.setDescription(rs.getString("description"));
        return attachment;
    }

    public List<MedicalRecordAttachment> findByRecordId(Long recordId) {
        String sql = "SELECT * FROM medical_record_attachments WHERE record_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToAttachment, recordId);
    }

    public MedicalRecordAttachment findById(Long attachmentId) {
        String sql = "SELECT * FROM medical_record_attachments WHERE attachment_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToAttachment, attachmentId);
    }

    public Long save(MedicalRecordAttachment attachment) {
        String sql = "INSERT INTO medical_record_attachments " +
                "(record_id, file_name, file_type, content_type, file_size, file_data, file_path, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, attachment.getRecordId());
            ps.setString(2, attachment.getFileName());
            ps.setString(3, attachment.getFileType());
            ps.setString(4, attachment.getContentType());
            ps.setLong(5, attachment.getFileSize());

            if (attachment.getFileData() != null) {
                ps.setBytes(6, attachment.getFileData());
            } else {
                ps.setNull(6, java.sql.Types.BLOB);
            }

            ps.setString(7, attachment.getFilePath());
            ps.setString(8, attachment.getDescription());

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(MedicalRecordAttachment attachment) {
        String sql = "UPDATE medical_record_attachments SET " +
                "file_name = ?, file_type = ?, content_type = ?, " +
                "file_size = ?, file_data = ?, file_path = ?, description = ? " +
                "WHERE attachment_id = ?";

        return jdbcTemplate.update(sql,
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getFileData(),
                attachment.getFilePath(),
                attachment.getDescription(),
                attachment.getAttachmentId());
    }

    public int deleteById(Long attachmentId) {
        String sql = "DELETE FROM medical_record_attachments WHERE attachment_id = ?";
        return jdbcTemplate.update(sql, attachmentId);
    }

    public int deleteByRecordId(Long recordId) {
        String sql = "DELETE FROM medical_record_attachments WHERE record_id = ?";
        return jdbcTemplate.update(sql, recordId);
    }
}