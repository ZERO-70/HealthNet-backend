-- HealthNet database schema
-- Reconstructed from the SQL in src/main/java/com/server/HealthNet/Repository/*.java
-- (the original database was lost; this recreates it from the queries the app actually runs).
--
-- Note: doctor / patient / staff use shared-primary-key inheritance off `person`
-- (doctor.doctor_id = person.person_id), matching the JOINs in the repositories.

CREATE TABLE person (
    person_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    gender       VARCHAR(50),
    age          INT,
    birthdate    DATE,
    contact_info VARCHAR(255),
    address      VARCHAR(255),
    image        LONGBLOB,
    image_type   VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE doctor (
    doctor_id      BIGINT PRIMARY KEY,
    specialization VARCHAR(255),
    CONSTRAINT fk_doctor_person FOREIGN KEY (doctor_id)
        REFERENCES person (person_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE patient (
    patient_id BIGINT PRIMARY KEY,
    weight     VARCHAR(50),
    height     VARCHAR(50),
    CONSTRAINT fk_patient_person FOREIGN KEY (patient_id)
        REFERENCES person (person_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE staff (
    staff_id   BIGINT PRIMARY KEY,
    profession VARCHAR(255),
    CONSTRAINT fk_staff_person FOREIGN KEY (staff_id)
        REFERENCES person (person_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- role: PATIENT | DOCTOR | STAFF | ADMIN     subscription: DEFAULT | PLUS
-- Stored as VARCHAR because the repository reads them with rs.getString(...).
CREATE TABLE user_authentication (
    username     VARCHAR(255) PRIMARY KEY,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(50)  NOT NULL,
    person_id    BIGINT,
    subscription VARCHAR(50) DEFAULT 'DEFAULT',
    CONSTRAINT fk_auth_person FOREIGN KEY (person_id)
        REFERENCES person (person_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE department (
    department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE availability (
    doctor_id     BIGINT PRIMARY KEY,
    Mon_startTime TIME, Mon_endTime TIME,
    Tue_startTime TIME, Tue_endTime TIME,
    Wed_startTime TIME, Wed_endTime TIME,
    Thu_startTime TIME, Thu_endTime TIME,
    Fri_startTime TIME, Fri_endTime TIME,
    Sat_startTime TIME, Sat_endTime TIME,
    Sun_startTime TIME, Sun_endTime TIME,
    CONSTRAINT fk_availability_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctor (doctor_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE treatment (
    treatment_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    doctor_id     BIGINT,
    department_id BIGINT,
    CONSTRAINT fk_treatment_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctor (doctor_id) ON DELETE SET NULL,
    CONSTRAINT fk_treatment_department FOREIGN KEY (department_id)
        REFERENCES department (department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE appointments (
    appointment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id     BIGINT,
    doctor_id      BIGINT,
    `date`         DATE,
    start_time     TIME,
    end_time       TIME,
    is_pending     BOOLEAN DEFAULT TRUE,
    is_approved    BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id)
        REFERENCES patient (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctor (doctor_id) ON DELETE CASCADE,
    INDEX idx_appointments_patient (patient_id),
    INDEX idx_appointments_doctor (doctor_id),
    INDEX idx_appointments_date (`date`)
) ENGINE=InnoDB;

CREATE TABLE medical_records (
    record_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id       BIGINT NOT NULL,
    doctor_id        BIGINT,
    department_id    BIGINT,
    treatment_id     BIGINT,
    record_type      VARCHAR(100),
    title            VARCHAR(255),
    diagnosis        TEXT,
    notes            TEXT,
    record_date      DATE,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- vitals
    blood_pressure   VARCHAR(50),
    heart_rate       INT,
    respiratory_rate INT,
    temperature      DOUBLE,
    oxygen_saturation INT,
    height           DOUBLE,
    weight           DOUBLE,
    CONSTRAINT fk_record_patient FOREIGN KEY (patient_id)
        REFERENCES patient (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctor (doctor_id) ON DELETE SET NULL,
    CONSTRAINT fk_record_department FOREIGN KEY (department_id)
        REFERENCES department (department_id) ON DELETE SET NULL,
    CONSTRAINT fk_record_treatment FOREIGN KEY (treatment_id)
        REFERENCES treatment (treatment_id) ON DELETE SET NULL,
    INDEX idx_records_patient (patient_id),
    INDEX idx_records_doctor (doctor_id),
    INDEX idx_records_date (record_date)
) ENGINE=InnoDB;

CREATE TABLE lab_results (
    result_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id       BIGINT NOT NULL,
    test_name       VARCHAR(255),
    test_value      VARCHAR(255),
    test_unit       VARCHAR(50),
    reference_range VARCHAR(255),
    is_abnormal     BOOLEAN DEFAULT FALSE,
    notes           TEXT,
    CONSTRAINT fk_lab_record FOREIGN KEY (record_id)
        REFERENCES medical_records (record_id) ON DELETE CASCADE,
    INDEX idx_lab_record (record_id)
) ENGINE=InnoDB;

CREATE TABLE medical_record_attachments (
    attachment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id     BIGINT NOT NULL,
    file_name     VARCHAR(255),
    file_type     VARCHAR(100),
    content_type  VARCHAR(100),
    file_size     BIGINT,
    file_data     LONGBLOB,
    file_path     VARCHAR(512),
    uploaded_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    description   TEXT,
    CONSTRAINT fk_attachment_record FOREIGN KEY (record_id)
        REFERENCES medical_records (record_id) ON DELETE CASCADE,
    INDEX idx_attachment_record (record_id)
) ENGINE=InnoDB;

CREATE TABLE medical_record_audit (
    audit_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_id        BIGINT NOT NULL,
    user_id          BIGINT,
    action_type      VARCHAR(100),
    action_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    action_details   TEXT,
    CONSTRAINT fk_audit_record FOREIGN KEY (record_id)
        REFERENCES medical_records (record_id) ON DELETE CASCADE,
    INDEX idx_audit_record (record_id)
) ENGINE=InnoDB;

CREATE TABLE inventory (
    inventory_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    quantity      BIGINT DEFAULT 0,
    expiry_date   DATE,
    department_id BIGINT,
    CONSTRAINT fk_inventory_department FOREIGN KEY (department_id)
        REFERENCES department (department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE chat (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id  BIGINT,
    request    TEXT,
    response   MEDIUMTEXT,
    `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_person FOREIGN KEY (person_id)
        REFERENCES person (person_id) ON DELETE CASCADE,
    INDEX idx_chat_person (person_id),
    INDEX idx_chat_timestamp (`timestamp`)
) ENGINE=InnoDB;

CREATE TABLE suggestion (
    suggestion_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id       BIGINT,
    suggestion_text TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_suggestion_person FOREIGN KEY (person_id)
        REFERENCES person (person_id) ON DELETE CASCADE
) ENGINE=InnoDB;
