-- ============================================================
-- DATOS SEMILLA (SGI) - Pabellones y Aulas
-- ============================================================
-- TEMPORAL: este archivo solo se usa para la primera carga de
-- datos en la base de datos de Aiven. Una vez confirmado que
-- los datos cargaron correctamente:
--   1. Borra este archivo (o vacía su contenido), y
--   2. En application.properties, quita/pon en "never":
--        spring.sql.init.mode=never
--      (y opcionalmente elimina spring.jpa.defer-datasource-initialization)
-- De lo contrario, este script se re-ejecutará en cada redeploy
-- e insertará los mismos registros duplicados.
-- ============================================================

-- Primero los pabellones (la tabla aula depende de estos por la FK id_pabellon)
INSERT INTO pabellon (id_pabellon, nombre, sede) VALUES
(1, 'A', 'Tacna y Arica'),
(2, 'B', 'Tacna y Arica'),
(3, 'C', 'Parra 1'),
(4, 'D', 'Parra 1'),
(5, 'E', 'Parra 1'),
(6, 'F', 'Parra 1'),
(7, 'G', 'Parra 1'),
(8, 'H', 'Parra 2'),
(9, 'I', 'Parra 2'),
(10, 'J', 'Parra 2');

-- Luego las aulas
INSERT INTO aula (id_aula, nivel, numero, tipo, id_pabellon) VALUES
(1, '1', '101', 'Aula', 1),
(2, '1', '102', 'Aula', 1),
(3, '2', '201', 'Aula', 1),
(4, '2', '202', 'Aula', 1),
(5, '3', '301', 'Aula', 1),
(6, '3', '302', 'Aula', 1),
(7, '4', '401', 'Aula', 1),
(8, '4', '402', 'Aula', 1),
(9, 'S', '01', 'Aula', 2),
(10, 'S', '02', 'Aula', 2),
(11, '1', '101', 'Aula', 2),
(12, '1', '102', 'Aula', 2),
(13, '2', '201', 'Aula', 2),
(14, '2', '202', 'Aula', 2),
(15, '3', '301', 'Aula', 2),
(16, '4', '401', 'Aula', 2),
(17, 'S', '01', 'Aula', 3),
(18, '1', '101', 'Aula', 3),
(19, '1', '102', 'Aula', 3),
(20, '2', '201', 'Aula', 3),
(21, '3', '301', 'Aula', 3),
(22, '4', '401', 'Aula', 3),
(23, 'S', '01', 'Aula', 4),
(24, '1', '101', 'Aula', 4),
(25, '2', '201', 'Aula', 4),
(26, '3', '301', 'Aula', 4),
(27, '4', '401', 'Aula', 4),
(28, 'S', '01', 'Aula', 5),
(29, '1', '101', 'Aula', 5),
(30, '2', '201', 'Aula', 5),
(31, 'S', '01', 'Aula', 6),
(32, '1', '101', 'Aula', 6),
(33, 'S', '01', 'Aula', 7),
(34, '1', '101', 'Aula', 7),
(35, '4', '401', 'Aula', 7),
(36, 'S', '01', 'Aula', 8),
(37, 'S', '02', 'Aula', 8),
(38, '1', '101', 'Aula', 8),
(39, '1', '102', 'Aula', 8),
(40, '2', '201', 'Aula', 8),
(41, '3', '301', 'Aula', 8),
(42, '4', '401', 'Aula', 8),
(43, '5', '501', 'Aula', 8),
(44, '5', '502', 'Aula', 8),
(45, 'S', '01', 'Aula', 9),
(46, '1', '101', 'Aula', 9),
(47, '2', '201', 'Aula', 9),
(48, '5', '501', 'Aula', 9),
(49, 'S', '01', 'Aula', 10),
(50, '1', '101', 'Aula', 10),
(51, '3', '301', 'Aula', 10),
(52, '5', '501', 'Aula', 10);
