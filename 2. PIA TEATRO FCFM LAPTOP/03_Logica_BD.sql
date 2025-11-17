/*
SCRIPT 03: Lógica Avanzada (Triggers, Vistas, Funciones, SP)
Descripción: Crea la lógica de negocio avanzada para el sistema del teatro.
*/

-- Usar la base de datos
USE pia_teatro;

-- -----------------------------------------------------
-- 1. TRIGGER (trg_ActualizarTotalVenta)
-- -----------------------------------------------------
-- Propósito: Actualiza automáticamente el total_venta en la tabla 'Venta'
--            cada vez que se inserta un nuevo boleto.
-- -----------------------------------------------------

DELIMITER $$

CREATE TRIGGER `trg_ActualizarTotalVenta`
AFTER INSERT ON `Boleto`
FOR EACH ROW
BEGIN
    -- Esta es la acción que se dispara:
    -- Actualiza el total_venta en la tabla Venta...
    UPDATE Venta
    
    -- ...calculando la suma de todos los precios_final de los boletos...
    SET total_venta = (
        SELECT SUM(precio_final) 
        FROM Boleto 
        WHERE Venta_id_venta = NEW.Venta_id_venta
    )
    
    -- ...solo para la venta que acaba de recibir el nuevo boleto.
    WHERE id_venta = NEW.Venta_id_venta;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- 2. TRIGGER (trg_ValidarCapacidad) 
-- -----------------------------------------------------
-- Propósito: Evita que se vendan más boletos que la capacidad de la sala.
-- Se ejecuta ANTES de insertar un boleto.
-- -----------------------------------------------------

DELIMITER $$
CREATE TRIGGER `trg_ValidarCapacidad`
BEFORE INSERT ON `Boleto`
FOR EACH ROW
BEGIN
    -- Declarar variables para guardar los datos
    DECLARE capacidad_sala INT;
    DECLARE boletos_vendidos INT;
    DECLARE id_sala_funcion INT;

    -- 1. Encontrar la sala de la función
    SELECT Sala_id_sala INTO id_sala_funcion 
    FROM Funcion 
    WHERE id_funcion = NEW.Funcion_id_funcion;

    -- 2. Obtener la capacidad de esa sala
    SELECT capacidad INTO capacidad_sala 
    FROM Sala 
    WHERE id_sala = id_sala_funcion;

    -- 3. Contar los boletos ya vendidos para esa función
    SELECT COUNT(id_boleto) INTO boletos_vendidos 
    FROM Boleto 
    WHERE Funcion_id_funcion = NEW.Funcion_id_funcion;

    -- 4. Comparar y lanzar un error si está lleno
    IF boletos_vendidos >= capacidad_sala THEN
        -- SIGNAL es el comando para detener la operación y enviar un error
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Función agotada. No se pueden vender más boletos.';
    END IF;
END$$
DELIMITER ;


-- -----------------------------------------------------
-- 3. TABLA DE AUDITORÍA (Necesaria para el Trigger 4)
-- -----------------------------------------------------
-- Esta tabla guardará el historial de cambios de precios.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`AuditoriaPrecios` (
  `id_auditoria` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATETIME NOT NULL,
  `usuario_db` VARCHAR(100) NOT NULL,
  `id_precio_zona` INT NOT NULL,
  `precio_anterior` DECIMAL(10,2) NOT NULL,
  `precio_nuevo` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id_auditoria`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- 4. TRIGGER (trg_AuditarCambioDePrecio) - (Automatiza Inserción)
-- -----------------------------------------------------
-- Propósito: Inserta automáticamente un registro en AuditoriaPrecios
--            cada vez que se actualiza (UPDATE) un precio.
-- -----------------------------------------------------

DELIMITER $$
CREATE TRIGGER `trg_AuditarCambioDePrecio`
AFTER UPDATE ON `PrecioZona`
FOR EACH ROW
BEGIN
    -- Solo si el precio realmente cambió
    IF OLD.precio <> NEW.precio THEN
        -- Inserta automáticamente un nuevo registro en la tabla de auditoría
        INSERT INTO AuditoriaPrecios (fecha, usuario_db, id_precio_zona, precio_anterior, precio_nuevo)
        VALUES (
            NOW(),         -- La fecha y hora actual
            USER(),        -- El usuario de la BD que hizo el cambio
            NEW.id_precio_zona, -- El ID del precio que cambió
            OLD.precio,    -- El precio ANTES del cambio
            NEW.precio     -- El precio DESPUÉS del cambio
        );
    END IF;
END$$
DELIMITER ;

-- -----------------------------------------------------
-- 5. VISTA (v_ReporteVentas)
-- -----------------------------------------------------
-- Propósito: Crea un reporte simple de ventas uniendo las tablas
--            Venta, Cliente, Empleado y Pago.
--            Ideal para el rol de Director.
-- -----------------------------------------------------

-- Primero borramos la vista si existe, para evitar errores
DROP VIEW IF EXISTS `v_ReporteVentas`;

CREATE VIEW `v_ReporteVentas` AS
SELECT 
    v.id_venta AS 'ID Venta',
    v.fecha_venta AS 'Fecha',
    v.total_venta AS 'Total',
    c.nombre_cliente AS 'Cliente',
    -- Concatenamos el nombre y apellido del empleado
    CONCAT(e.nombre_e, ' ', e.apellido_e) AS 'Vendido Por',
    mp.tipo AS 'Método de Pago'
FROM 
    Venta v
JOIN 
    Cliente c ON v.Cliente_id_cliente = c.id_cliente
JOIN 
    Empleado e ON v.Empleado_id_empleado = e.id_empleado
JOIN 
    Pago p ON v.id_venta = p.Venta_id_venta
JOIN 
    MetodoPago mp ON p.MetodoPago_id_metodoPago = mp.id_metodoPago;
    
-- PROBAMOS VIEW 

-- SELECT * FROM v_ReporteVentas;

-- -----------------------------------------------------
-- 6. VISTA (v_DisponibilidadFunciones)
-- -----------------------------------------------------
-- Propósito: Muestra un reporte operativo de la disponibilidad
--            de asientos para cada función.
-- -----------------------------------------------------

DROP VIEW IF EXISTS `v_DisponibilidadFunciones`;

CREATE VIEW `v_DisponibilidadFunciones` AS
SELECT 
    f.titulo_funcion AS 'Funcion',
    s.nombre_sala AS 'Sala',
    f.fecha_fun AS 'Fecha',
    f.hora_fun AS 'Hora',
    s.capacidad AS 'Capacidad Total',
    
    -- Contamos los boletos vendidos para esta función
    (SELECT COUNT(b.id_boleto) 
     FROM Boleto b 
     WHERE b.Funcion_id_funcion = f.id_funcion) AS 'Boletos Vendidos',
     
    -- Calculamos los asientos restantes
    (s.capacidad - (SELECT COUNT(b.id_boleto) 
                    FROM Boleto b 
                    WHERE b.Funcion_id_funcion = f.id_funcion)) AS 'Asientos Disponibles'
FROM 
    Funcion f
JOIN 
    Sala s ON f.Sala_id_sala = s.id_sala;
    
-- SELECT * FROM v_DisponibilidadFunciones;

-- -----------------------------------------------------
-- 7. FUNCIÓN (fn_AsientosDisponibles)
-- -----------------------------------------------------
-- Propósito: Calcula y retorna el número de asientos restantes
--            para una función específica.
-- -----------------------------------------------------

DELIMITER $$
CREATE FUNCTION `fn_AsientosDisponibles` (
    -- Parámetro de entrada: el ID de la función que queremos consultar
    funcionID INT
)
RETURNS INT -- Tipo de dato que va a retornar (un número entero)
DETERMINISTIC -- Es una buena práctica para optimización
BEGIN
    -- Declarar variables locales para los cálculos
    DECLARE capacidad_total INT;
    DECLARE asientos_vendidos INT;
    DECLARE id_sala_funcion INT;

    -- 1. Encontrar la sala de la función
    SELECT Sala_id_sala INTO id_sala_funcion 
    FROM Funcion 
    WHERE id_funcion = funcionID;
    
    -- 2. Obtener la capacidad de esa sala
    SELECT capacidad INTO capacidad_total 
    FROM Sala 
    WHERE id_sala = id_sala_funcion;

    -- 3. Contar los boletos ya vendidos para esa función
    SELECT COUNT(id_boleto) INTO asientos_vendidos 
    FROM Boleto 
    WHERE Funcion_id_funcion = funcionID;

    -- 4. Retornar el resultado del cálculo
    RETURN capacidad_total - asientos_vendidos;
END$$

-- Volvemos a poner el delimitador estándar
DELIMITER ;

-- -----------------------------------------------------
-- 8. FUNCIÓN (fn_ObtenerPrecioBoleto)
-- -----------------------------------------------------
-- Propósito: Calcula el precio correcto de un boleto,
--            dada la función y el asiento.
-- -----------------------------------------------------

DELIMITER $$
CREATE FUNCTION `fn_ObtenerPrecioBoleto` (
    funcionID INT,
    asientoID INT
)
RETURNS DECIMAL(10,2) -- Retorna un solo número (el precio)
DETERMINISTIC
BEGIN
    DECLARE zona_del_asiento INT;
    DECLARE precio_final DECIMAL(10,2);
    
    -- 1. Buscar a qué zona pertenece el asiento
    SELECT Zona_id_zona INTO zona_del_asiento
    FROM Asiento
    WHERE id_asiento = asientoID;
    
    -- 2. Buscar el precio para esa zona y esa función
    SELECT precio INTO precio_final
    FROM PrecioZona
    WHERE Funcion_id_funcion = funcionID AND Zona_id_zona = zona_del_asiento;
    
    -- 3. Retornar el precio
    RETURN precio_final;
END$$

-- Volvemos a poner el delimitador estándar
DELIMITER ;

-- Prueba de la función:
-- SELECT fn_ObtenerPrecioBoleto(1, 1) AS 'Precio del Boleto';

-- -----------------------------------------------------
-- 9. PROCEDIMIENTO ALMACENADO (sp_BuscarFuncion)
-- -----------------------------------------------------
-- Propósito: Busca funciones por título o por fecha.
--            Si un parámetro es NULL, lo ignora.
-- -----------------------------------------------------

DELIMITER $$
CREATE PROCEDURE `sp_BuscarFuncion` (
    -- Parámetros de entrada (pueden ser nulos)
    IN tituloBusqueda VARCHAR(100),
    IN fechaBusqueda DATE
)
BEGIN
    SELECT 
        f.id_funcion,
        f.titulo_funcion AS 'Funcion',
        f.genero,
        f.fecha_fun AS 'Fecha',
        f.hora_fun AS 'Hora',
        s.nombre_sala AS 'Sala',
        fn_AsientosDisponibles(f.id_funcion) AS 'Asientos Disponibles'
    FROM 
        Funcion f
    JOIN 
        Sala s ON f.Sala_id_sala = s.id_sala
    WHERE
        -- Lógica de búsqueda flexible
        -- Si tituloBusqueda no es nulo, filtra por título
        (tituloBusqueda IS NULL OR f.titulo_funcion LIKE CONCAT('%', tituloBusqueda, '%'))
        AND
        -- Si fechaBusqueda no es nula, filtra por fecha
        (fechaBusqueda IS NULL OR f.fecha_fun = fechaBusqueda);
END$$
DELIMITER ;

-- Prueba 1: Buscar por nombre "Hamlet"
-- CALL sp_BuscarFuncion('Hamlet', NULL);

-- Prueba 2: Buscar por fecha '2025-12-11'
-- CALL sp_BuscarFuncion(NULL, '2025-12-11');

-- -----------------------------------------------------
-- 10. PROCEDIMIENTO ALMACENADO (sp_RegistrarVenta)
-- -----------------------------------------------------
-- Propósito: Este es el SP principal de la aplicación.
--            Registra una venta completa con múltiples boletos
--            dentro de una transacción segura.
-- -----------------------------------------------------

DELIMITER $$
CREATE PROCEDURE `sp_RegistrarVenta` (
    -- PARÁMETROS DE ENTRADA (Lo que recibe de Java)
    IN p_clienteID INT,
    IN p_empleadoID INT,
    IN p_metodoID INT,
    IN p_funcionID INT,
    IN p_asientosCSV VARCHAR(1000) -- Ej: "1,2,3" (lista de IDs de asientos)
)
BEGIN
    -- Declarar variables
    DECLARE nueva_venta_id INT;
    DECLARE asiento_id_actual INT;
    DECLARE precio_del_boleto DECIMAL(10,2);
    DECLARE total_calculado DECIMAL(10,2) DEFAULT 0.00;
    
    -- Variables para el bucle de asientos
    DECLARE s_index INT DEFAULT 1;
    DECLARE s_length INT;
    DECLARE s_value VARCHAR(1000);

    -- 1. MANEJADOR DE ERRORES
    -- Si cualquier trigger (como el de capacidad) o INSERT falla,
    -- se ejecutará este 'Handler' que deshará todo (ROLLBACK).
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        -- Retorna el mensaje de error
        SELECT 'Error: No se pudo registrar la venta. Operación cancelada.' AS Mensaje;
    END;

    -- 2. INICIAR LA TRANSACCIÓN
    START TRANSACTION;
    
    -- 3. CREAR EL REGISTRO 'VENTA' (El ticket maestro)
    INSERT INTO Venta (fecha_venta, total_venta, Cliente_id_cliente, Empleado_id_empleado)
    VALUES (NOW(), 0.00, p_clienteID, p_empleadoID);
    
    -- Guardamos el ID de la venta que acabamos de crear
    SET nueva_venta_id = LAST_INSERT_ID();
    
    -- 4. BUCLE PARA INSERTAR LOS BOLETOS
    -- Este bucle procesa el string "1,2,3"
    SET s_value = p_asientosCSV;
    SET s_length = LENGTH(s_value);

    WHILE s_index <= s_length DO
        -- Obtiene el ID del asiento (ej: "1" o "2" o "3")
        SET asiento_id_actual = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(s_value, ',', s_index), ',', -1) AS UNSIGNED);
        
        -- Llamamos a nuestra Función para saber el precio
        SET precio_del_boleto = fn_ObtenerPrecioBoleto(p_funcionID, asiento_id_actual);
        
        -- Insertamos el boleto
        -- AQUI SE DISPARAN TUS TRIGGERS:
        -- 1. trg_ValidarCapacidad (podría fallar y activar el ROLLBACK)
        -- 2. trg_ActualizarTotalVenta (actualizará el total en Venta)
        INSERT INTO Boleto (precio_final, Funcion_id_funcion, Asiento_id_asiento, Venta_id_venta)
        VALUES (precio_del_boleto, p_funcionID, asiento_id_actual, nueva_venta_id);
        
        -- Sumamos el precio al total
        SET total_calculado = total_calculado + precio_del_boleto;
        
        -- Siguiente item en el bucle
        SET s_index = s_index + 1;
    END WHILE;
    
    -- 5. INSERTAR EL PAGO
    -- (Nota: El trigger ya actualizó el total_venta, pero usamos nuestro
    -- total_calculado aquí para confirmar el monto del pago)
    INSERT INTO Pago (monto, MetodoPago_id_metodoPago, Venta_id_venta)
    VALUES (total_calculado, p_metodoID, nueva_venta_id);
    
    -- 6. FINALIZAR LA TRANSACCIÓN
    COMMIT;
    
    -- 7. RETORNAR ÉXITO
    SELECT 
        'Venta registrada con éxito' AS Mensaje, 
        nueva_venta_id AS 'ID de Venta',
        total_calculado AS 'Monto Total';

END$$
DELIMITER ;

-- -----------------------------------------------------
-- 11. PROCEDIMIENTO ALMACENADO (sp_CrearFuncion)
-- -----------------------------------------------------
-- Propósito: Inserta una nueva función en el sistema.
-- -----------------------------------------------------

DELIMITER $$
CREATE PROCEDURE `sp_CrearFuncion` (
    IN p_titulo VARCHAR(45),
    IN p_director VARCHAR(45),
    IN p_genero VARCHAR(45),
    IN p_duracion INT,
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_salaID INT
)
BEGIN
    INSERT INTO Funcion 
        (titulo_funcion, director, genero, duracion_minutos, fecha_fun, hora_fun, Sala_id_sala)
    VALUES 
        (p_titulo, p_director, p_genero, p_duracion, p_fecha, p_hora, p_salaID);
END$$
DELIMITER ;


-- -----------------------------------------------------
-- 12. PROCEDIMIENTO ALMACENADO (sp_AsignarPrecio)
-- -----------------------------------------------------
-- Propósito: Asigna o actualiza el precio de una zona
--            para una función específica.
-- -----------------------------------------------------

DELIMITER $$
CREATE PROCEDURE `sp_AsignarPrecio` (
    IN p_funcionID INT,
    IN p_zonaID INT,
    IN p_precio DECIMAL(10,2)
)
BEGIN
    -- Inserta el precio. Si la combinación funcion/zona ya existe,
    -- actualiza el precio.
    INSERT INTO PrecioZona (Funcion_id_funcion, Zona_id_zona, precio)
    VALUES (p_funcionID, p_zonaID, p_precio)
    ON DUPLICATE KEY UPDATE
        precio = p_precio;
END$$
DELIMITER ;

/*
-----------------------------------------------------
-- GESTIÓN DE EMPLEADOS (Para el Rol de Director)
-----------------------------------------------------
*/

-- 13. SP: Ver lista de empleados
-- -----------------------------------------------------
DELIMITER $$
CREATE PROCEDURE `sp_VerEmpleados` ()
BEGIN
    SELECT 
        e.id_empleado,
        e.nombre_e,
        e.apellido_e,
        e.nombre_usuario,
        r.nombre_rol,
        r.id_rol -- Necesitamos el ID para saber qué es (1, 2 o 3)
    FROM 
        Empleado e
    JOIN 
        Rol r ON e.Rol_id_rol = r.id_rol;
END$$
DELIMITER ;


-- 14. SP: Registrar nuevo empleado (CONTRATAR)
-- -----------------------------------------------------
DELIMITER $$
CREATE PROCEDURE `sp_RegistrarEmpleado` (
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_usuario VARCHAR(45),
    IN p_password VARCHAR(100),
    IN p_rolID INT
)
BEGIN
    -- Insertamos encriptando la contraseña automáticamente
    INSERT INTO Empleado (nombre_e, apellido_e, nombre_usuario, contrasenia, Rol_id_rol)
    VALUES (p_nombre, p_apellido, p_usuario, SHA2(p_password, 256), p_rolID);
END$$
DELIMITER ;


-- 15. SP: Editar datos de empleado (CAMBIAR ROL/NOMBRE)
-- -----------------------------------------------------
-- Nota: Este SP NO cambia la contraseña por seguridad.
DELIMITER $$
CREATE PROCEDURE `sp_EditarEmpleado` (
    IN p_idEmpleado INT,
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_rolID INT
)
BEGIN
    UPDATE Empleado
    SET 
        nombre_e = p_nombre,
        apellido_e = p_apellido,
        Rol_id_rol = p_rolID
    WHERE id_empleado = p_idEmpleado;
END$$
DELIMITER ;


-- 16. SP: Eliminar empleado (DESPEDIR)
-- -----------------------------------------------------
DELIMITER $$
CREATE PROCEDURE `sp_EliminarEmpleado` (
    IN p_idEmpleado INT
)
BEGIN
    DELETE FROM Empleado WHERE id_empleado = p_idEmpleado;
END$$
DELIMITER ;


-- Prueba del SP de Venta:
-- (Cliente 1, Empleado 1, MetodoPago 2, Funcion 2, Asientos "1,2")
-- CALL sp_RegistrarVenta(1, 1, 2, 2, '1,2');

-- SELECT * FROM Venta WHERE id_venta = 4;

-- SELECT * FROM Boleto WHERE Venta_id_venta = 4;

-- SELECT * FROM Pago WHERE Venta_id_venta = 4;