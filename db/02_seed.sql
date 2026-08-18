-- Reference data. Users/people are NOT seeded here on purpose: passwords must be
-- BCrypt-hashed by the app, so accounts are created via /user_authentication/register
-- (see scripts/seed-users.sh).

INSERT INTO department (name) VALUES
    ('Cardiology'),
    ('Neurology'),
    ('Orthopedics'),
    ('Pediatrics'),
    ('Emergency'),
    ('Radiology'),
    ('General Medicine');

INSERT INTO inventory (name, quantity, expiry_date, department_id) VALUES
    ('Paracetamol 500mg', 500, '2027-06-30', 7),
    ('Amoxicillin 250mg', 300, '2026-12-31', 7),
    ('Surgical Gloves (box)', 150, '2028-01-31', 5),
    ('ECG Electrodes',      200, '2027-03-15', 1),
    ('Plaster Bandage',     120, '2027-09-30', 3);
