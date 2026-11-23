

DROP DATABASE IF EXISTS tudentaria;
CREATE DATABASE tudentaria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tudentaria;


CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;


CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    genero VARCHAR(30),
    telefono VARCHAR(20),
    fecha_nacimiento DATE
) ENGINE=InnoDB;


CREATE TABLE usuarios_roles (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE especialidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
) ENGINE=InnoDB;


CREATE TABLE doctores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    imagen VARCHAR(255),
    especialidad_id INT,
    CONSTRAINT fk_doc_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidades(id) ON DELETE SET NULL
) ENGINE=InnoDB;


CREATE TABLE tratamientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL DEFAULT 0.00,
    costo DECIMAL(10,2) DEFAULT 0.00,
    duracion_minutos INT DEFAULT 30,
    descripcion TEXT
) ENGINE=InnoDB;


CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100)
) ENGINE=InnoDB;


CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(150) NOT NULL,
    notas TEXT,
    doctor_id INT,
    tratamiento_id INT,
    paciente_id INT,
    CONSTRAINT fk_cita_doctor FOREIGN KEY (doctor_id) REFERENCES doctores(id) ON DELETE SET NULL,
    CONSTRAINT fk_cita_tratamiento FOREIGN KEY (tratamiento_id) REFERENCES tratamientos(id) ON DELETE SET NULL,
    CONSTRAINT fk_cita_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE SET NULL
) ENGINE=InnoDB;


CREATE TABLE publicaciones_blog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    imagen_url VARCHAR(255),
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    autor_id INT,
    CONSTRAINT fk_blog_autor FOREIGN KEY (autor_id) REFERENCES doctores(id) ON DELETE SET NULL
) ENGINE=InnoDB;


INSERT INTO roles (id, nombre) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');


INSERT INTO usuarios (id, nombre, apellido, email, password, genero, telefono) VALUES 
(1, 'Admin', 'TuDentaria', 'admin@tudentaria.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'No especificado', '999888777');


INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (1, 1);


INSERT INTO especialidades (id, nombre, descripcion) VALUES
(1, 'Ortodoncia y Cirugía', 'Corrección de dientes y mandíbulas alineadas incorrectamente.'),
(2, 'Implantología y Estética', 'Reemplazo de piezas dentales perdidas y diseño de sonrisas.'),
(3, 'Endodoncia Avanzada', 'Tratamiento especializado de conductos radiculares.');


INSERT INTO doctores (id, nombre, especialidad, imagen, especialidad_id) VALUES
(1, 'Dra. Raquel Villa', 'Ortodoncia y Cirugía', 'doctor-1.jpg', 1),
(2, 'Dr. Carlos Mendoza', 'Implantología y Estética', 'doctor-2.jpg', 2);


INSERT INTO tratamientos (id, nombre, precio, costo, duracion_minutos, descripcion) VALUES
(1, 'Limpieza Dental Profunda (Profilaxis)', 80.00, 80.00, 45, 'Eliminación de sarro, placa bacteriana y pulido dental.'),
(2, 'Blanqueamiento Dental Láser', 250.00, 250.00, 60, 'Aclaramiento dental seguro de alta efectividad estética.'),
(3, 'Ortodoncia con Brackets Metálicos', 1500.00, 1500.00, 60, 'Alineación dental integral de arco completo.'),
(4, 'Implante Dental de Titanio', 2200.00, 2200.00, 90, 'Rehabilitación fija con perno de titanio biocompatible.');


INSERT INTO pacientes (id, nombre, apellido, dni, telefono, email) VALUES
(1, 'Juan', 'Pérez López', '72345678', '987654321', 'juan.perez@example.com'),
(2, 'María', 'Gómez Torres', '76543210', '912345678', 'maria.gomez@example.com');
