-- ========================================
-- 🧪 DATOS DE PRUEBA PARA INTEGRATION TESTS
-- ========================================

-- Limpiar datos existentes (en orden correcto por dependencias)
TRUNCATE payments CASCADE;
TRUNCATE parking_sessions CASCADE;
TRUNCATE vehicles CASCADE;
TRUNCATE rate_configs CASCADE;
TRUNCATE parking_spaces CASCADE;
TRUNCATE vehicle_types CASCADE;

-- Reset sequences
ALTER SEQUENCE vehicle_types_id_seq RESTART WITH 1;
ALTER SEQUENCE parking_spaces_id_seq RESTART WITH 1;
ALTER SEQUENCE rate_configs_id_seq RESTART WITH 1;
ALTER SEQUENCE vehicles_id_seq RESTART WITH 1;
ALTER SEQUENCE parking_sessions_id_seq RESTART WITH 1;
ALTER SEQUENCE payments_id_seq RESTART WITH 1;

-- ========================================
-- TIPOS DE VEHÍCULOS
-- ========================================
INSERT INTO vehicle_types (id, name, description, is_active, created_at) VALUES 
(1, 'CAR', 'Automóviles y sedanes', true, CURRENT_TIMESTAMP),
(2, 'MOTORCYCLE', 'Motocicletas y ciclomotores', true, CURRENT_TIMESTAMP),
(3, 'TRUCK', 'Camiones y vehículos de carga', true, CURRENT_TIMESTAMP),
(4, 'VAN', 'Camionetas y vehículos familiares', true, CURRENT_TIMESTAMP);

-- ========================================
-- CONFIGURACIÓN DE TARIFAS
-- ========================================
INSERT INTO rate_configs (id, vehicle_type_id, rate_per_hour, minimum_charge_hours, maximum_daily_rate, is_active, created_at, updated_at) VALUES 
(1, 1, 2000.00, 1, 15000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Carros: $2,000/hora
(2, 2, 1000.00, 1, 8000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Motos: $1,000/hora
(3, 3, 3000.00, 1, 25000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- Camiones: $3,000/hora
(4, 4, 2500.00, 1, 20000.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- Camionetas: $2,500/hora

-- ========================================
-- ESPACIOS DE PARQUEO
-- ========================================
INSERT INTO parking_spaces (id, space_number, floor_level, vehicle_type_id, is_occupied, is_active, created_at, updated_at) VALUES 
(1, 'A-001', 1, 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'A-002', 1, 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'A-003', 1, 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'M-001', 1, 2, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'M-002', 1, 2, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'T-001', 0, 3, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'V-001', 2, 4, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- USUARIO DE PRUEBA (OPERATOR)
-- ========================================
-- Nota: Asumimos que el usuario con ID 1 ya existe del sistema base
-- Si no existe, crearlo aquí
INSERT INTO users (id, firstname, lastname, email, password, role, is_active, created_at, updated_at) 
VALUES (1, 'Test', 'Operator', 'test@operator.com', '$2a$10$obirjZMfHwFQpk1giGHaJenIIrS6n1oTA//GL.PcZPRqy0w0y23Em', 'OPERATOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
