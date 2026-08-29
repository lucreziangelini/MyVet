-- ══════════════════════════════
--  MYVET DATABASE — SCHEMA
-- ══════════════════════════════

DROP SCHEMA IF EXISTS myvetdb;

CREATE SCHEMA myvetdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE myvetdb;

-- ══════════════════════════════
--  TABLES
-- ══════════════════════════════

CREATE TABLE myvetdb.user (
    id INT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM(
        'PET_OWNER',
        'VETERINARIAN',
        'ADMIN'
    ) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE myvetdb.veterinarian_detail (
    user_id INT NOT NULL,
    bio VARCHAR(500),
    specialization VARCHAR(150) NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.pet (
    id INT AUTO_INCREMENT,
    owner_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    birth_date DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.time_slot (
    id INT AUTO_INCREMENT,
    veterinarian_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    reserved_until DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_time_slot_interval
        CHECK (start_time < end_time)
) ENGINE=InnoDB;

CREATE TABLE myvetdb.booking (
    id INT AUTO_INCREMENT,
    pet_owner_id INT NOT NULL,
    veterinarian_id INT NOT NULL,
    pet_id INT NOT NULL,
    slot_id INT NOT NULL,
    status ENUM(
        'PENDING',
        'CONFIRMED',
        'CANCELLED',
    ) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (pet_owner_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (pet_id)
        REFERENCES pet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (slot_id)
        REFERENCES time_slot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.activity (
    id INT AUTO_INCREMENT,
    veterinarian_id INT NOT NULL,
    pet_id INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (pet_id)
        REFERENCES pet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.progress (
    id INT AUTO_INCREMENT,
    veterinarian_id INT NOT NULL,
    pet_id INT NOT NULL,
    notes VARCHAR(500) NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY unique_pet_veterinarian_progress (
        veterinarian_id,
        pet_id
    ),
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (pet_id)
        REFERENCES pet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.pet_owner_favourite_veterinarian (
    pet_owner_id INT NOT NULL,
    veterinarian_id INT NOT NULL,
    PRIMARY KEY (
        pet_owner_id,
        veterinarian_id
    ),
    FOREIGN KEY (pet_owner_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE myvetdb.medical_document (
    id INT AUTO_INCREMENT,
    pet_id INT NOT NULL,
    veterinarian_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    type ENUM(
        'MEDICAL_REPORT',
        'LAB_RESULT',
        'PRESCRIPTION',
        'VACCINATION_CERTIFICATE',
        'OTHER'
    ) NOT NULL,
    storage_reference VARCHAR(500) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (pet_id)
        REFERENCES pet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (veterinarian_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════
--  INDEXES
-- ══════════════════════════════

-- Il vincolo UNIQUE sull'email crea automaticamente il relativo indice.

CREATE INDEX idx_pet_owner
    ON myvetdb.pet (owner_id);

CREATE INDEX idx_booking_pet_owner
    ON myvetdb.booking (pet_owner_id);

CREATE INDEX idx_booking_veterinarian
    ON myvetdb.booking (veterinarian_id);

CREATE INDEX idx_booking_pet
    ON myvetdb.booking (pet_id);

CREATE INDEX idx_booking_slot
    ON myvetdb.booking (slot_id);

CREATE INDEX idx_timeslot_veterinarian_date
    ON myvetdb.time_slot (
        veterinarian_id,
        date
    );

CREATE INDEX idx_activity_pet
    ON myvetdb.activity (pet_id);

CREATE INDEX idx_activity_veterinarian
    ON myvetdb.activity (veterinarian_id);

CREATE INDEX idx_document_pet
    ON myvetdb.medical_document (pet_id);

-- ══════════════════════════════
--  STORED PROCEDURES
-- ══════════════════════════════

DELIMITER $$

DROP PROCEDURE IF EXISTS myvetdb.login$$

CREATE PROCEDURE myvetdb.login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(100),
    OUT p_id INT,
    OUT p_name VARCHAR(100),
    OUT p_surname VARCHAR(100),
    OUT p_role VARCHAR(30)
)
BEGIN
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET p_role = 'NOT_FOUND';

    SET p_id = NULL;
    SET p_name = NULL;
    SET p_surname = NULL;
    SET p_role = NULL;

    SELECT id, name, surname, role
    INTO p_id, p_name, p_surname, p_role
    FROM user
    WHERE email = p_email
      AND password = p_password;
END$$

DROP PROCEDURE IF EXISTS myvetdb.reserve_slot$$

CREATE PROCEDURE myvetdb.reserve_slot(
    IN p_slot_id INT,
    IN p_minutes INT,
    OUT p_success BOOLEAN
)
BEGIN
    UPDATE time_slot
    SET reserved_until = DATE_ADD(
        NOW(),
        INTERVAL p_minutes MINUTE
    )
    WHERE id = p_slot_id
      AND available = TRUE
      AND (
          reserved_until IS NULL
          OR reserved_until < NOW()
      );

    SET p_success = (ROW_COUNT() > 0);
END$$

DROP PROCEDURE IF EXISTS myvetdb.release_slot$$

CREATE PROCEDURE myvetdb.release_slot(
    IN p_slot_id INT
)
BEGIN
    UPDATE time_slot
    SET reserved_until = NULL
    WHERE id = p_slot_id
      AND available = TRUE;
END$$

DELIMITER ;

-- ══════════════════════════════
--  MYSQL USERS
-- ══════════════════════════════

-- LOGIN USER

DROP USER IF EXISTS 'myvet_login'@'localhost';

CREATE USER 'myvet_login'@'localhost'
IDENTIFIED BY 'myvet_login';

GRANT EXECUTE
ON PROCEDURE myvetdb.login
TO 'myvet_login'@'localhost';

GRANT SELECT, INSERT
ON myvetdb.user
TO 'myvet_login'@'localhost';

GRANT INSERT
ON myvetdb.veterinarian_detail
TO 'myvet_login'@'localhost';

-- PET OWNER

DROP USER IF EXISTS 'myvet_pet_owner'@'localhost';

CREATE USER 'myvet_pet_owner'@'localhost'
IDENTIFIED BY 'myvet_pet_owner';

GRANT EXECUTE
ON PROCEDURE myvetdb.login
TO 'myvet_pet_owner'@'localhost';

GRANT EXECUTE
ON PROCEDURE myvetdb.reserve_slot
TO 'myvet_pet_owner'@'localhost';

GRANT EXECUTE
ON PROCEDURE myvetdb.release_slot
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT
ON myvetdb.user
TO 'myvet_pet_owner'@'localhost';

GRANT UPDATE (email)
ON myvetdb.user
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT
ON myvetdb.veterinarian_detail
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT
ON myvetdb.pet
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT, UPDATE
ON myvetdb.time_slot
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT, INSERT, UPDATE
ON myvetdb.booking
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT, UPDATE
ON myvetdb.activity
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT
ON myvetdb.medical_document
TO 'myvet_pet_owner'@'localhost';

GRANT SELECT, INSERT, DELETE
ON myvetdb.pet_owner_favourite_veterinarian
TO 'myvet_pet_owner'@'localhost';

-- VETERINARIAN

DROP USER IF EXISTS 'myvet_veterinarian'@'localhost';

CREATE USER 'myvet_veterinarian'@'localhost'
IDENTIFIED BY 'myvet_veterinarian';

GRANT EXECUTE
ON PROCEDURE myvetdb.login
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT
ON myvetdb.user
TO 'myvet_veterinarian'@'localhost';

GRANT UPDATE (email)
ON myvetdb.user
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT
ON myvetdb.veterinarian_detail
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT
ON myvetdb.pet
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
ON myvetdb.time_slot
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT
ON myvetdb.booking
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT, INSERT, UPDATE
ON myvetdb.activity
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT, INSERT, UPDATE
ON myvetdb.progress
TO 'myvet_veterinarian'@'localhost';

GRANT SELECT, INSERT
ON myvetdb.medical_document
TO 'myvet_veterinarian'@'localhost';

-- ADMIN

DROP USER IF EXISTS 'myvet_admin'@'localhost';

CREATE USER 'myvet_admin'@'localhost'
IDENTIFIED BY 'myvet_admin';

GRANT ALL PRIVILEGES
ON myvetdb.*
TO 'myvet_admin'@'localhost';

FLUSH PRIVILEGES;

-- ══════════════════════════════
--  MYVET — TEST DATA
--  Password for all users: password123
-- ══════════════════════════════

USE myvetdb;

-- ══════════════════════════════
--  USERS
-- ══════════════════════════════

INSERT INTO user (
    name,
    surname,
    email,
    password,
    role
) VALUES
(
    'Anna',
    'Rossi',
    'anna@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'PET_OWNER'
),
(
    'Marco',
    'Bianchi',
    'marco@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'PET_OWNER'
),
(
    'Luca',
    'Verdi',
    'luca.vet@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'VETERINARIAN'
),
(
    'Giulia',
    'Romano',
    'giulia.vet@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'VETERINARIAN'
),
(
    'Sara',
    'Conti',
    'sara.vet@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'VETERINARIAN'
),
(
    'Admin',
    'MyVet',
    'admin@test.com',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'ADMIN'
);

-- ══════════════════════════════
--  VETERINARIAN DETAILS
-- ══════════════════════════════

INSERT INTO veterinarian_detail (
    user_id,
    bio,
    specialization
) VALUES
(
    3,
    'Veterinario con esperienza nella medicina degli animali domestici.',
    'Medicina generale'
),
(
    4,
    'Veterinaria specializzata nella prevenzione e nei controlli periodici.',
    'Medicina preventiva'
),
(
    5,
    'Veterinaria con esperienza nella diagnosi e nella cura degli animali.',
    'Medicina interna'
);

-- ══════════════════════════════
--  PETS
-- ══════════════════════════════

INSERT INTO pet (
    owner_id,
    name,
    species,
    breed,
    birth_date
) VALUES
(
    1,
    'Milo',
    'Dog',
    'Labrador',
    '2020-05-12'
),
(
    1,
    'Luna',
    'Cat',
    'European Shorthair',
    '2021-09-03'
),
(
    2,
    'Rocky',
    'Dog',
    'German Shepherd',
    '2019-02-18'
);

-- ══════════════════════════════
--  FAVOURITE VETERINARIANS
-- ══════════════════════════════

INSERT INTO pet_owner_favourite_veterinarian (
    pet_owner_id,
    veterinarian_id
) VALUES
(1, 3),
(2, 4);

-- ══════════════════════════════
--  FUTURE TIME SLOTS
-- ══════════════════════════════

INSERT INTO time_slot (
    veterinarian_id,
    date,
    start_time,
    end_time,
    available
) VALUES
(
    3,
    CURDATE() + INTERVAL 1 DAY,
    '09:00:00',
    '09:30:00',
    TRUE
),
(
    3,
    CURDATE() + INTERVAL 1 DAY,
    '10:00:00',
    '10:30:00',
    TRUE
),
(
    3,
    CURDATE() + INTERVAL 2 DAY,
    '15:00:00',
    '15:30:00',
    TRUE
),
(
    4,
    CURDATE() + INTERVAL 1 DAY,
    '11:00:00',
    '11:30:00',
    TRUE
),
(
    4,
    CURDATE() + INTERVAL 2 DAY,
    '16:00:00',
    '16:30:00',
    TRUE
),
(
    5,
    CURDATE() + INTERVAL 1 DAY,
    '14:00:00',
    '14:30:00',
    TRUE
);

-- ══════════════════════════════
--  CONFIRMED BOOKINGS
-- ══════════════════════════════

INSERT INTO booking (
    pet_owner_id,
    veterinarian_id,
    pet_id,
    slot_id,
    status
) VALUES
(1, 3, 1, 1, 'CONFIRMED'),
(2, 4, 3, 4, 'CONFIRMED');

UPDATE time_slot
SET available = FALSE,
    reserved_until = NULL
WHERE id IN (1, 4);

-- ══════════════════════════════
--  CARE ACTIVITIES
-- ══════════════════════════════

INSERT INTO activity (
    veterinarian_id,
    pet_id,
    description,
    completed,
    created_at
) VALUES
(
    3,
    1,
    'Somministrare una compressa dopo il pasto serale per cinque giorni.',
    FALSE,
    NOW() - INTERVAL 2 DAY
),
(
    3,
    1,
    'Controllare quotidianamente appetito e assunzione di acqua.',
    TRUE,
    NOW() - INTERVAL 5 DAY
),
(
    4,
    3,
    'Limitare l’attività fisica intensa per una settimana.',
    FALSE,
    NOW() - INTERVAL 1 DAY
);

-- ══════════════════════════════
--  PROGRESS
-- ══════════════════════════════

INSERT INTO progress (
    veterinarian_id,
    pet_id,
    notes
) VALUES
(
    3,
    1,
    'Milo risponde bene alla terapia. Appetito regolare e condizioni generali stabili.'
),
(
    4,
    3,
    'Rocky mostra un miglioramento della mobilità. Continuare il riposo controllato.'
);

-- ══════════════════════════════
--  MEDICAL DOCUMENTS
-- ══════════════════════════════

INSERT INTO medical_document (
    pet_id,
    veterinarian_id,
    title,
    type,
    storage_reference,
    uploaded_at
) VALUES
(
    1,
    3,
    'Referto visita generale',
    'MEDICAL_REPORT',
    'documents/milo-referto-generale.pdf',
    NOW() - INTERVAL 10 DAY
),
(
    1,
    3,
    'Prescrizione antibiotico',
    'PRESCRIPTION',
    'documents/milo-prescrizione.pdf',
    NOW() - INTERVAL 9 DAY
),
(
    3,
    4,
    'Certificato vaccinazione',
    'VACCINATION_CERTIFICATE',
    'documents/rocky-vaccinazione.pdf',
    NOW() - INTERVAL 5 DAY
);

-- ══════════════════════════════
--  PAST TIME SLOTS
-- ══════════════════════════════

INSERT INTO time_slot (
    veterinarian_id,
    date,
    start_time,
    end_time,
    available
) VALUES
(
    3,
    CURDATE() - INTERVAL 10 DAY,
    '09:00:00',
    '09:30:00',
    FALSE
),
(
    3,
    CURDATE() - INTERVAL 7 DAY,
    '14:00:00',
    '14:30:00',
    FALSE
),
(
    4,
    CURDATE() - INTERVAL 5 DAY,
    '10:00:00',
    '10:30:00',
    FALSE
),
(
    5,
    CURDATE() - INTERVAL 3 DAY,
    '15:00:00',
    '15:30:00',
    TRUE
);

-- ══════════════════════════════
--  PAST AND CANCELLED BOOKINGS
-- ══════════════════════════════

INSERT INTO booking (
    pet_owner_id,
    veterinarian_id,
    pet_id,
    slot_id,
    status,
    created_at
) VALUES
(
    1,
    3,
    1,
    7,
    'CONFIRMED',
    NOW() - INTERVAL 11 DAY
),
(
    1,
    3,
    2,
    8,
    'CANCELLED',
    NOW() - INTERVAL 8 DAY
),
(
    2,
    4,
    3,
    9,
    'CONFIRMED',
    NOW() - INTERVAL 6 DAY
);