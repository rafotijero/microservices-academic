-- Script de inicialización de la base de datos

-- Crear tabla de roles si no existe
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Crear tabla de usuarios si no existe
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Crear tabla intermedia user_roles si no existe
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insertar roles por defecto
INSERT INTO roles (name) VALUES ('ROLE_STUDENT')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('ROLE_TEACHER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuarios de ejemplo
-- Contraseña para todos: password123
-- Hash BCrypt: $2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG

-- Limpiar datos existentes solo si existen
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE username IN ('juan.perez', 'maria.gonzalez', 'carlos.rodriguez', 'ana.martinez', 'luis.sanchez', 'prof.garcia', 'admin'));
DELETE FROM users WHERE username IN ('juan.perez', 'maria.gonzalez', 'carlos.rodriguez', 'ana.martinez', 'luis.sanchez', 'prof.garcia', 'admin');

-- ESTUDIANTES (coinciden con ms-academic-management)
INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('juan.perez', 'juan.perez@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('maria.gonzalez', 'maria.gonzalez@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('carlos.rodriguez', 'carlos.rodriguez@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('ana.martinez', 'ana.martinez@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('luis.sanchez', 'luis.sanchez@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

-- PROFESOR
INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('prof.garcia', 'profesor.garcia@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

-- ADMINISTRADOR
INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('admin', 'admin@universidad.edu', '$2a$10$WMB21QiVhhSzGjhh/QSg6uI5Vf8aLXOQREa9bWKvfi3f2.2wf5zvG', true, CURRENT_TIMESTAMP);

-- Asignar roles a usuarios
-- Estudiantes -> ROLE_STUDENT
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'juan.perez' AND r.name = 'ROLE_STUDENT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'maria.gonzalez' AND r.name = 'ROLE_STUDENT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'carlos.rodriguez' AND r.name = 'ROLE_STUDENT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'ana.martinez' AND r.name = 'ROLE_STUDENT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'luis.sanchez' AND r.name = 'ROLE_STUDENT';

-- Profesor -> ROLE_TEACHER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'prof.garcia' AND r.name = 'ROLE_TEACHER';

-- Admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
