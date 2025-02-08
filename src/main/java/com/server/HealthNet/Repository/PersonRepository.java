package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository {

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Define a RowMapper for Person
    private final RowMapper<Person> personRowMapper = (rs, rowNum) -> {
        Person person = new Person();
        person.setId(rs.getLong("person_id"));
        person.setName(rs.getString("name"));
        person.setGender(rs.getString("gender"));
        person.setAge(rs.getInt("age"));
        person.setBirthdate(rs.getDate("birthdate").toLocalDate());
        person.setContact_info(rs.getString("contact_info"));
        person.setAddress(rs.getString("address"));
        person.setImage(rs.getBytes("image")); // Handle image as byte array
        person.setImage_type(rs.getString("image_type")); // Handle image type
        return person;
    };

    // Find all persons
    public List<Person> findAll() {
        String sql = "SELECT * FROM person";
        return jdbcTemplate.query(sql, personRowMapper);
    }

    // Find person by ID
    public Person findById(Long id) {
        String sql = "SELECT * FROM person WHERE person_id = ?";
        return jdbcTemplate.queryForObject(sql, personRowMapper, id);
    }

    // Save a new person
    public int save(Person person) {
        String sql = "INSERT INTO person (name, gender, age, birthdate, contact_info, address, image, image_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                person.getName(),
                person.getGender(),
                person.getAge(),
                person.getBirthdate(),
                person.getContact_info(),
                person.getAddress(),
                person.getImage() != null ? person.getImage() : null, // Handle null image
                person.getImage_type() != null ? person.getImage_type() : null); // Handle null image type
    }

    // Update an existing person
    public int update(Person person) {
        String sql = "UPDATE person SET name = ?, gender = ?, age = ?, birthdate = ?, contact_info = ?, address = ?, " +
                "image = ?, image_type = ? WHERE person_id = ?";
        return jdbcTemplate.update(sql,
                person.getName(),
                person.getGender(),
                person.getAge(),
                person.getBirthdate(),
                person.getContact_info(),
                person.getAddress(),
                person.getImage() != null ? person.getImage() : null, // Handle null image
                person.getImage_type() != null ? person.getImage_type() : null, // Handle null image type
                person.getId());
    }

    // Delete a person by ID
    public int deleteById(Long id) {
        String sql = "DELETE FROM person WHERE person_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
