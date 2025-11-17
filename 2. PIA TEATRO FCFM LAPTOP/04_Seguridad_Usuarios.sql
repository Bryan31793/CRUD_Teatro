/*
SCRIPT 04: Seguridad (Usuarios y Permisos)
Descripción: Crea los usuarios del servidor MySQL y les
             asigna los permisos necesarios para operar.
*/

-- ---
-- 1. USUARIO DE LA APLICACIÓN (app_taquilla)
-- ---
-- Este es el usuario que tu programa Java usará para conectarse.
-- Tiene permisos MUY limitados por seguridad.

-- Primero, borramos al usuario si ya existe
DROP USER IF EXISTS 'app_taquilla'@'localhost';

-- Creamos el usuario
CREATE USER 'app_taquilla'@'localhost' IDENTIFIED BY 'JavaPass123';

-- Le damos permisos (SOLO lo que necesita)
-- 1. Permiso para "llamar" a los SP que hicimos:
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_BuscarFuncion TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_RegistrarVenta TO 'app_taquilla'@'localhost';

-- 2. Permiso para "llamar" a las Funciones que hicimos:
GRANT EXECUTE ON FUNCTION pia_teatro.fn_AsientosDisponibles TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON FUNCTION pia_teatro.fn_ObtenerPrecioBoleto TO 'app_taquilla'@'localhost';

-- 5. Permisos para el Módulo de Administrador
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_CrearFuncion TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_AsignarPrecio TO 'app_taquilla'@'localhost';
-- GRANT EXECUTE ON FUNCTION pia_teatro.fn_ValidarLogin TO 'app_taquilla'@'localhost'; 

-- (Script 04_Seguridad_Usuarios.sql)
-- ... (después de los GRANT EXECUTE) ...

-- 4. Permiso para LEER las tablas de catálogo
GRANT SELECT ON pia_teatro.Funcion TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.Cliente TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.MetodoPago TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.Asiento TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.Zona TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.Boleto TO 'app_taquilla'@'localhost'; -- (Necesario para ver asientos disponibles)

-- 5. Permiso para que el Login pueda LEER la tabla Empleado
GRANT SELECT ON pia_teatro.Empleado TO 'app_taquilla'@'localhost';

-- 3. Permiso para LEER las vistas (reportes):
GRANT SELECT ON pia_teatro.v_ReporteVentas TO 'app_taquilla'@'localhost';
GRANT SELECT ON pia_teatro.v_DisponibilidadFunciones TO 'app_taquilla'@'localhost';

-- ---
-- 2. USUARIO ADMINISTRADOR (admin_teatro)
-- ---
-- Este usuario es para un humano (como tú o el gerente).
-- Tiene control casi total SOBRE ESTA BASE DE DATOS, pero no sobre otras.

-- Borramos si ya existe
DROP USER IF EXISTS 'admin_teatro'@'localhost';

-- Creamos el usuario
CREATE USER 'admin_teatro'@'localhost' IDENTIFIED BY 'AdminKey456';

-- Le damos todos los permisos (SELECT, INSERT, UPDATE, DELETE, CREATE, etc.)
-- PERO solo en la base de datos 'pia_teatro'.
GRANT ALL PRIVILEGES ON pia_teatro.* TO 'admin_teatro'@'localhost';

-- -----------------------------------------------------
FLUSH PRIVILEGES; -- Aplica los cambios de permisos
SELECT 'Usuarios y permisos creados correctamente' AS Mensaje;

-- (En 04_Seguridad_Usuarios.sql)

-- 6. Permisos para la Gestión de Empleados (Director)
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_VerEmpleados TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_RegistrarEmpleado TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_EditarEmpleado TO 'app_taquilla'@'localhost';
GRANT EXECUTE ON PROCEDURE pia_teatro.sp_EliminarEmpleado TO 'app_taquilla'@'localhost';

-- También necesitamos permiso para leer la tabla Rol (para llenar el combo de roles)
GRANT SELECT ON pia_teatro.Rol TO 'app_taquilla'@'localhost';

CALL sp_VerEmpleados();