/*
SCRIPT 02: LMD (Lenguaje de Manipulación de Datos)
Descripción: Inserción de datos de catálogo y prueba para el sistema del teatro.
*/

USE pia_teatro;

-- 1. POBLAR CATALOGOS (Tablas sin FK)
-- -------------------------------------------------

INSERT INTO Rol (nombre_rol) VALUES 
('Vendedor'), 
('Administrador'), 
('Director');

INSERT INTO MetodoPago (tipo) VALUES 
('Efectivo'), 
('Tarjeta de Crédito'), 
('Transferencia Bancaria');

INSERT INTO Sala (nombre_sala, capacidad) VALUES 
('Sala Principal', 200), 
('Sala Experimental', 100);

-- 2. POBLAR DATOS DEPENDIENTES (Tablas con FK de Catálogos)
-- -------------------------------------------------

-- *** CAMBIO AQUÍ: 'Orquesta' por 'Platea' ***
INSERT INTO Zona (nombre_zona, Sala_id_sala) VALUES 
('Platea', 1),       -- id_zona = 1 (se puede cambiar 'Platea' por otro nombre)
('Mezanine', 1),     -- id_zona = 2
('Balcón', 1),       -- id_zona = 3
('General', 2);      -- id_zona = 4

-- Clientes (Puedes cambiar estos nombres)
INSERT INTO Cliente (nombre_cliente, correo, telefono, rfc, razon_social, domicilio_fiscal) VALUES 
('Carlos Sánchez', 'carlos.sanchez@mail.com', '8112345678', NULL, NULL, NULL),
('Ana González', 'ana.gonzalez@mail.com', '8187654321', NULL, NULL, NULL),
('Empresa XYZ SA de CV', 'facturacion@empresa.com', '0000000000', 'EXY123456XYZ', 'Empresa XYZ SA de CV', 'Av. Siempre Viva 123, Col. Centro, MTY, NL');


-- Empleados (Depende de Rol)
INSERT INTO Empleado (nombre_e, apellido_e, nombre_usuario, contrasenia, Rol_id_rol) VALUES 
('Carlos', 'Gómez', 'cgomez', SHA2('pass123', 256), 1), -- Rol Vendedor (puedes cambiar 'Carlos', 'Gómez', 'cgomez')
('Ana', 'García', 'agarcia', SHA2('admin456', 256), 2), -- Rol Administrador
('Roberto', 'Mtz', 'rmartinez', SHA2('dir789', 256), 3); -- Rol Director

-- Funciones (Depende de Sala)
INSERT INTO Funcion (titulo_funcion, director, genero, duracion_minutos, fecha_fun, hora_fun, Sala_id_sala) VALUES
('Macbeth', 'W. Shakespeare', 'Tragedia', 180, '2025-12-10', '20:00:00', 1), -- id_funcion = 1 (puedes cambiar 'Macbeth', etc.)
('La Casa de Bernarda Alba', 'F. García Lorca', 'Drama', 120, '2025-12-11', '19:00:00', 1), -- id_funcion = 2
('Monólogo Experimental', 'Director Local', 'Experimental', 60, '2025-12-12', '21:00:00', 2); -- id_funcion = 3

-- Precios (Depende de Funcion y Zona)
-- Precios para Macbeth (Función 1)
INSERT INTO PrecioZona (precio, Funcion_id_funcion, Zona_id_zona) VALUES
(850.00, 1, 1), -- Macbeth, Platea
(600.00, 1, 2), -- Macbeth, Mezanine
(450.00, 1, 3); -- Macbeth, Balcón

-- Precios para Bernarda Alba (Función 2)
INSERT INTO PrecioZona (precio, Funcion_id_funcion, Zona_id_zona) VALUES
(700.00, 2, 1), -- Bernarda, Platea
(500.00, 2, 2), -- Bernarda, Mezanine
(350.00, 2, 3); -- Bernarda, Balcón

-- Precios para Monólogo (Función 3)
INSERT INTO PrecioZona (precio, Funcion_id_funcion, Zona_id_zona) VALUES
(250.00, 3, 4); -- Monólogo, General

-- Asientos (Depende de Zona)
INSERT INTO Asiento (fila, numero_asiento, Zona_id_zona) VALUES
('A', 10, 1), -- id_asiento = 1 (Platea)
('A', 11, 1), -- Agregado
('A', 12, 1), -- Agregado
('A', 13, 1), -- Agregado
('M', 5, 2),  -- id_asiento = 2 (Mezanine)
('M', 6, 2),  -- Agregado
('M', 7, 2),  -- Agregado
('G', 15, 4), -- id_asiento = 3 (General Experimental)
('G', 16, 4), -- Agregado
('G', 17, 4); -- Agregado

-- 3. POBLAR TRANSACCIONES (Datos de prueba de ventas)
-- -------------------------------------------------

-- VENTA 1 (Hecha por Vendedor 'cgomez', a Cliente 'Carlos Sánchez')
INSERT INTO Venta (fecha_venta, total_venta, Cliente_id_cliente, Empleado_id_empleado) VALUES
('2025-11-07 14:30:00', 0.00, 1, 1); -- id_venta = 1

-- Boletos para Venta 1 (Macbeth, Platea y Mezanine)
INSERT INTO Boleto (precio_final, Funcion_id_funcion, Asiento_id_asiento, Venta_id_venta) VALUES
(850.00, 1, 1, 1),
(600.00, 1, 2, 1);

-- Pago para Venta 1 (Total: 1450.00)
INSERT INTO Pago (monto, MetodoPago_id_metodoPago, Venta_id_venta) VALUES
(1450.00, 2, 1); -- Pagado con Tarjeta


-- VENTA 2 (Hecha por Admin 'agarcia', a Cliente 'Ana González')
INSERT INTO Venta (fecha_venta, total_venta, Cliente_id_cliente, Empleado_id_empleado) VALUES
('2025-11-07 16:15:00', 0.00, 2, 2); -- id_venta = 2

-- Boleto para Venta 2 (Monólogo Experimental)
INSERT INTO Boleto (precio_final, Funcion_id_funcion, Asiento_id_asiento, Venta_id_venta) VALUES
(250.00, 3, 3, 2);

-- Pago para Venta 2 (Total: 250.00)
INSERT INTO Pago (monto, MetodoPago_id_metodoPago, Venta_id_venta) VALUES
(250.00, 1, 2); -- Pagado con Efectivo

-- VENTA 3 (Genera Factura)
INSERT INTO Venta (fecha_venta, total_venta, Cliente_id_cliente, Empleado_id_empleado) VALUES
('2025-11-07 17:00:00', 0.00, 3, 2); -- id_venta = 3 (Cliente 'Empresa XYZ')

-- (Boleto para Venta 3)
INSERT INTO Boleto (precio_final, Funcion_id_funcion, Asiento_id_asiento, Venta_id_venta) VALUES 
(700.00, 2, 1, 3); -- (Bernarda, Asiento 1 -> Platea)

-- Factura para Venta 3
INSERT INTO Factura (fecha_emision, subtotal, impuestos, total, Venta_id_venta) VALUES
(NOW(), 603.45, 96.55, 700.00, 3); -- (Subtotal e IVA son calculados)

-- -------------------------------------------------
SELECT 'Datos insertados correctamente' AS Mensaje;

/*
-- 1. Verificar Encriptación
SELECT 
    nombre_usuario, 
    contrasenia 
FROM 
    Empleado;
    
    -- 2. Verificar Roles de Empleados
SELECT 
    e.nombre_e, 
    e.apellido_e, 
    e.nombre_usuario, 
    r.nombre_rol 
FROM 
    Empleado e
JOIN 
    Rol r ON e.Rol_id_rol = r.id_rol;
    
    -- 3. Verificar Precios de una Función
SELECT 
    f.titulo_funcion, 
    z.nombre_zona, 
    pz.precio 
FROM 
    PrecioZona pz
JOIN 
    Funcion f ON pz.Funcion_id_funcion = f.id_funcion
JOIN 
    Zona z ON pz.Zona_id_zona = z.id_zona
WHERE 
    f.id_funcion = 1; -- (Hamlet)
    
    -- 4. Ver el detalle de una Venta (Venta ID = 1)
SELECT 
    v.id_venta,
    v.fecha_venta,
    c.nombre_cliente,
    e.nombre_e AS vendido_por,
    b.precio_final,
    f.titulo_funcion,
    a.fila,
    a.numero_asiento
FROM 
    Venta v
JOIN 
    Cliente c ON v.Cliente_id_cliente = c.id_cliente
JOIN 
    Empleado e ON v.Empleado_id_empleado = e.id_empleado
JOIN 
    Boleto b ON b.Venta_id_venta = v.id_venta
JOIN 
    Funcion f ON b.Funcion_id_funcion = f.id_funcion
JOIN 
    Asiento a ON b.Asiento_id_asiento = a.id_asiento
WHERE 
    v.id_venta = 1;
    
-- comprobamos que trigger funcione
-- SELECT * FROM Venta; */

