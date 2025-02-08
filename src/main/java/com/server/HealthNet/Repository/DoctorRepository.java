package com.server.HealthNet.Repository;

import com.server.HealthNet.Model.Appointment;
import com.server.HealthNet.Model.Avalibility;
import com.server.HealthNet.Model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AvalibilityRepository avalibilityRepository;

    private final RowMapper<Doctor> doctorRowMapper = (ResultSet rs, int rowNum) -> {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getLong("person_id"));
        doctor.setName(rs.getString("name"));
        doctor.setGender(rs.getString("gender"));
        doctor.setAge(rs.getInt("age"));
        doctor.setBirthdate(rs.getDate("birthdate").toLocalDate());
        doctor.setContact_info(rs.getString("contact_info"));
        doctor.setAddress(rs.getString("address"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setImage(rs.getBytes("image")); // Handle image
        doctor.setImage_type(rs.getString("image_type")); // Handle image type
        return doctor;
    };

    public Optional<Doctor> findDoctorById(Long doctorId) {
        String sql = "SELECT p.person_id, p.name, p.gender, p.age, p.birthdate, p.contact_info, p.address, " +
                "p.image, p.image_type, d.specialization " +
                "FROM doctor d " +
                "JOIN person p ON d.doctor_id = p.person_id " +
                "WHERE d.doctor_id = ?";
        return jdbcTemplate.query(sql, doctorRowMapper, doctorId).stream().findFirst();
    }

    public List<Doctor> findAllDoctors() {
        String sql = "SELECT p.person_id, p.name, p.gender, p.age, p.birthdate, p.contact_info, p.address, " +
                "p.image, p.image_type, d.specialization " +
                "FROM doctor d " +
                "JOIN person p ON d.doctor_id = p.person_id";
        return jdbcTemplate.query(sql, doctorRowMapper);
    }

    public int deleteDoctorById(Long doctorId) {
        String deleteDoctorSql = "DELETE FROM doctor WHERE doctor_id = ?";
        int rowsAffected = jdbcTemplate.update(deleteDoctorSql, doctorId);

        String deletePersonSql = "DELETE FROM person WHERE person_id = ?";
        rowsAffected += jdbcTemplate.update(deletePersonSql, doctorId);

        return rowsAffected;
    }

    public int updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctor SET specialization = ? WHERE doctor_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                doctor.getSpecialization(),
                doctor.getId());

        String personSql = "UPDATE person SET name = ?, gender = ?, age = ?, birthdate = ?, " +
                "contact_info = ?, address = ?, image = ?, image_type = ? WHERE person_id = ?";
        rowsAffected += jdbcTemplate.update(personSql,
                doctor.getName(),
                doctor.getGender(),
                doctor.getAge(),
                doctor.getBirthdate(),
                doctor.getContact_info(),
                doctor.getAddress(),
                doctor.getImage(), // Update image
                doctor.getImage_type(), // Update image type
                doctor.getId());

        return rowsAffected;
    }

    public Long saveDoctor(Doctor doctor) {
        String personSql = "INSERT INTO person (name, gender, age, birthdate, contact_info, address, image, image_type) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(personSql, new String[] { "person_id" });
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getGender());
            ps.setInt(3, doctor.getAge());
            ps.setDate(4, java.sql.Date.valueOf(doctor.getBirthdate()));
            ps.setString(5, doctor.getContact_info());
            ps.setString(6, doctor.getAddress());
            if (doctor.getImage() != null) {
                ps.setBytes(7, doctor.getImage());
            } else {
                ps.setNull(7, java.sql.Types.BLOB);
            }
            if (doctor.getImage_type() != null) {
                ps.setString(8, doctor.getImage_type());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }
            return ps;
        }, keyHolder);

        Long generatedDoctorId = keyHolder.getKey().longValue();

        String doctorSql = "INSERT INTO doctor (doctor_id, specialization) VALUES (?, ?)";
        if (jdbcTemplate.update(doctorSql, generatedDoctorId, doctor.getSpecialization()) > 0) {
            return generatedDoctorId;
        } else {
            return 0L;
        }
    }

    public List<String> getAvailableAppointmentTimes(Long doctorId, LocalDate date) {
        Optional<Avalibility> availabilityOpt = avalibilityRepository.findById(doctorId);
        if (availabilityOpt.isEmpty()) {
            return List.of();
        }

        Avalibility availability = availabilityOpt.get();

        String appointmentsSql = "SELECT start_time, end_time FROM appointments WHERE doctor_id = ? AND date = ?";
        List<Appointment> appointments = jdbcTemplate.query(appointmentsSql, (rs, rowNum) -> {
            Appointment appointment = new Appointment();
            appointment.setStartTime(rs.getObject("start_time", LocalTime.class));
            appointment.setEndTime(rs.getObject("end_time", LocalTime.class));
            return appointment;
        }, doctorId, date);

        LocalTime dayStart, dayEnd;
        switch (date.getDayOfWeek()) {
            case MONDAY -> {
                dayStart = availability.getMon_startTime();
                dayEnd = availability.getMon_endTime();
            }
            case TUESDAY -> {
                dayStart = availability.getTue_startTime();
                dayEnd = availability.getTue_endTime();
            }
            case WEDNESDAY -> {
                dayStart = availability.getWed_startTime();
                dayEnd = availability.getWed_endTime();
            }
            case THURSDAY -> {
                dayStart = availability.getThu_startTime();
                dayEnd = availability.getThu_endTime();
            }
            case FRIDAY -> {
                dayStart = availability.getFri_startTime();
                dayEnd = availability.getFri_endTime();
            }
            case SATURDAY -> {
                dayStart = availability.getSat_startTime();
                dayEnd = availability.getSat_endTime();
            }
            case SUNDAY -> {
                dayStart = availability.getSun_startTime();
                dayEnd = availability.getSun_endTime();
            }
            default -> {
                return List.of();
            }
        }

        if (dayStart == null || dayEnd == null) {
            return List.of();
        }

        appointments.sort(Comparator.comparing(Appointment::getStartTime));

        List<String> availableTimes = new ArrayList<>();
        LocalTime currentStart = dayStart;

        for (Appointment appt : appointments) {
            if (!appt.getStartTime().isBefore(currentStart)) {
                if (appt.getStartTime().isAfter(currentStart)) {
                    availableTimes.add(currentStart + " - " + appt.getStartTime());
                }
                currentStart = appt.getEndTime();
            } else if (appt.getEndTime().isAfter(currentStart)) {
                currentStart = appt.getEndTime();
            }
        }

        if (currentStart.isBefore(dayEnd)) {
            availableTimes.add(currentStart + " - " + dayEnd);
        }

        return availableTimes;
    }
}
