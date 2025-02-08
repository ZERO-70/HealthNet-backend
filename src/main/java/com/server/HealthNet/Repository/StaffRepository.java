package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class StaffRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Staff> staffRowMapper = new RowMapper<Staff>() {
        @Override
        public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
            Staff staff = new Staff();
            staff.setId(rs.getLong("person_id"));
            staff.setName(rs.getString("name"));
            staff.setGender(rs.getString("gender"));
            staff.setAge(rs.getInt("age"));
            staff.setBirthdate(rs.getDate("birthdate").toLocalDate());
            staff.setContact_info(rs.getString("contact_info"));
            staff.setAddress(rs.getString("address"));
            staff.setProffession(rs.getString("proffession"));
            staff.setImage(rs.getBytes("image")); // Map image
            staff.setImage_type(rs.getString("image_type")); // Map image type
            return staff;
        }
    };

    public Optional<Staff> findStaffById(Long staffId) {
        String sql = "SELECT p.person_id, p.name, p.gender, p.age, p.birthdate, p.contact_info, p.address, " +
                "p.image, p.image_type, s.proffession " +
                "FROM staff s " +
                "JOIN person p ON s.staff_id = p.person_id " +
                "WHERE s.staff_id = ?";
        return jdbcTemplate.query(sql, staffRowMapper, staffId).stream().findFirst();
    }

    public List<Staff> findAllStaff() {
        String sql = "SELECT p.person_id, p.name, p.gender, p.age, p.birthdate, p.contact_info, p.address, " +
                "p.image, p.image_type, s.proffession " +
                "FROM staff s " +
                "JOIN person p ON s.staff_id = p.person_id";
        return jdbcTemplate.query(sql, staffRowMapper);
    }

    public int deleteStaffById(Long staffId) {
        // First, delete the staff record from the staff table
        String deleteStaffSql = "DELETE FROM staff WHERE staff_id = ?";
        int rowsAffected = jdbcTemplate.update(deleteStaffSql, staffId);

        // Then, delete the associated record from the person table
        String deletePersonSql = "DELETE FROM person WHERE person_id = ?";
        rowsAffected += jdbcTemplate.update(deletePersonSql, staffId);

        return rowsAffected; // Return the total number of rows affected
    }

    public int updateStaff(Staff staff) {
        // Update the profession in the Staff table
        String staffSql = "UPDATE staff SET proffession = ? WHERE staff_id = ?";
        int rowsAffected = jdbcTemplate.update(staffSql, staff.getProffession(), staff.getId());

        // Update all fields in the Person table, including image and image_type
        String personSql = "UPDATE person SET name = ?, gender = ?, age = ?, birthdate = ?, " +
                "contact_info = ?, address = ?, image = ?, image_type = ? WHERE person_id = ?";
        rowsAffected += jdbcTemplate.update(personSql,
                staff.getName(),
                staff.getGender(),
                staff.getAge(),
                staff.getBirthdate(),
                staff.getContact_info(),
                staff.getAddress(),
                staff.getImage(), // Update image
                staff.getImage_type(), // Update image type
                staff.getId());

        return rowsAffected;
    }

    public Long saveStaff(Staff staff) {
        // Insert into Person table and retrieve the generated person_id
        String personSql = "INSERT INTO person (name, gender, age, birthdate, contact_info, address, image, image_type) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(personSql, new String[] { "person_id" });
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getGender());
            ps.setInt(3, staff.getAge());
            ps.setDate(4, java.sql.Date.valueOf(staff.getBirthdate()));
            ps.setString(5, staff.getContact_info());
            ps.setString(6, staff.getAddress());
            if (staff.getImage() != null) {
                ps.setBytes(7, staff.getImage());
            } else {
                ps.setNull(7, java.sql.Types.BLOB); // Handle case where no image is provided
            }
            if (staff.getImage_type() != null) {
                ps.setString(8, staff.getImage_type());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR); // Handle case where no image type is provided
            }
            return ps;
        }, keyHolder);

        // Retrieve the generated person_id to use as staff_id
        Long generatedStaffId = keyHolder.getKey().longValue();

        // Insert into Staff table using the generated staff_id
        String staffSql = "INSERT INTO staff (staff_id, proffession) VALUES (?, ?)";
        if (jdbcTemplate.update(staffSql, generatedStaffId, staff.getProffession()) > 0) {
            return generatedStaffId;
        } else {
            return 0L;
        }
    }
}
