-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema pia_teatro
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema pia_teatro
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS pia_teatro;
CREATE SCHEMA IF NOT EXISTS `pia_teatro` DEFAULT CHARACTER SET utf8 ;
USE `pia_teatro` ;

-- -----------------------------------------------------
-- Table `pia_teatro`.`Sala`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Sala` (
  `id_sala` INT NOT NULL AUTO_INCREMENT,
  `nombre_sala` VARCHAR(45) NOT NULL,
  `capacidad` INT NOT NULL,
  PRIMARY KEY (`id_sala`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Zona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Zona` (
  `id_zona` INT NOT NULL AUTO_INCREMENT,
  `nombre_zona` VARCHAR(45) NOT NULL,
  `Sala_id_sala` INT NOT NULL,
  PRIMARY KEY (`id_zona`),
  INDEX `fk_Zona_Sala_idx` (`Sala_id_sala` ASC) VISIBLE,
  CONSTRAINT `fk_Zona_Sala`
    FOREIGN KEY (`Sala_id_sala`)
    REFERENCES `pia_teatro`.`Sala` (`id_sala`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Asiento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Asiento` (
  `id_asiento` INT NOT NULL AUTO_INCREMENT,
  `fila` VARCHAR(5) NOT NULL,
  `numero_asiento` INT NOT NULL,
  `Zona_id_zona` INT NOT NULL,
  PRIMARY KEY (`id_asiento`),
  INDEX `fk_Asiento_Zona1_idx` (`Zona_id_zona` ASC) VISIBLE,
  CONSTRAINT `fk_Asiento_Zona1`
    FOREIGN KEY (`Zona_id_zona`)
    REFERENCES `pia_teatro`.`Zona` (`id_zona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Funcion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Funcion` (
  `id_funcion` INT NOT NULL AUTO_INCREMENT,
  `titulo_funcion` VARCHAR(45) NOT NULL,
  `director` VARCHAR(45) NULL,
  `genero` VARCHAR(45) NULL,
  `duracion_minutos` INT NULL,
  `fecha_fun` DATE NOT NULL,
  `hora_fun` TIME NOT NULL,
  `Sala_id_sala` INT NOT NULL,
  PRIMARY KEY (`id_funcion`),
  INDEX `fk_Funcion_Sala1_idx` (`Sala_id_sala` ASC) VISIBLE,
  CONSTRAINT `fk_Funcion_Sala1`
    FOREIGN KEY (`Sala_id_sala`)
    REFERENCES `pia_teatro`.`Sala` (`id_sala`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`PrecioZona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`PrecioZona` (
  `id_precio_zona` INT NOT NULL AUTO_INCREMENT,
  `precio` DECIMAL(10,2) NOT NULL,
  `Funcion_id_funcion` INT NOT NULL,
  `Zona_id_zona` INT NOT NULL,
  PRIMARY KEY (`id_precio_zona`),
  INDEX `fk_PrecioZona_Funcion1_idx` (`Funcion_id_funcion` ASC) VISIBLE,
  INDEX `fk_PrecioZona_Zona1_idx` (`Zona_id_zona` ASC) VISIBLE,
  CONSTRAINT `fk_PrecioZona_Funcion1`
    FOREIGN KEY (`Funcion_id_funcion`)
    REFERENCES `pia_teatro`.`Funcion` (`id_funcion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PrecioZona_Zona1`
    FOREIGN KEY (`Zona_id_zona`)
    REFERENCES `pia_teatro`.`Zona` (`id_zona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Cliente` (
  `id_cliente` INT NOT NULL AUTO_INCREMENT,
  `nombre_cliente` VARCHAR(100) NOT NULL,
  `correo` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(20) NOT NULL,
  `rfc` VARCHAR(13) NULL,
  `razon_social` VARCHAR(100) NULL,
  `domicilio_fiscal` VARCHAR(100) NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE INDEX `correo_UNIQUE` (`correo` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Rol` (
  `id_rol` INT NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Empleado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Empleado` (
  `id_empleado` INT NOT NULL AUTO_INCREMENT,
  `nombre_e` VARCHAR(100) NOT NULL,
  `apellido_e` VARCHAR(100) NOT NULL,
  `nombre_usuario` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(255) NOT NULL,
  `Rol_id_rol` INT NOT NULL,
  PRIMARY KEY (`id_empleado`),
  UNIQUE INDEX `nombre_usuario_UNIQUE` (`nombre_usuario` ASC) VISIBLE,
  INDEX `fk_Empleado_Rol1_idx` (`Rol_id_rol` ASC) VISIBLE,
  CONSTRAINT `fk_Empleado_Rol1`
    FOREIGN KEY (`Rol_id_rol`)
    REFERENCES `pia_teatro`.`Rol` (`id_rol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Venta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Venta` (
  `id_venta` INT NOT NULL AUTO_INCREMENT,
  `fecha_venta` DATETIME NOT NULL,
  `total_venta` DECIMAL(10,2) NOT NULL,
  `Cliente_id_cliente` INT NOT NULL,
  `Empleado_id_empleado` INT NOT NULL,
  PRIMARY KEY (`id_venta`),
  INDEX `fk_Venta_Cliente1_idx` (`Cliente_id_cliente` ASC) VISIBLE,
  INDEX `fk_Venta_Empleado1_idx` (`Empleado_id_empleado` ASC) VISIBLE,
  CONSTRAINT `fk_Venta_Cliente1`
    FOREIGN KEY (`Cliente_id_cliente`)
    REFERENCES `pia_teatro`.`Cliente` (`id_cliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Venta_Empleado1`
    FOREIGN KEY (`Empleado_id_empleado`)
    REFERENCES `pia_teatro`.`Empleado` (`id_empleado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Boleto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Boleto` (
  `id_boleto` INT NOT NULL AUTO_INCREMENT,
  `precio_final` DECIMAL(10,2) NOT NULL,
  `Venta_id_venta` INT NOT NULL,
  `Funcion_id_funcion` INT NOT NULL,
  `Asiento_id_asiento` INT NOT NULL,
  PRIMARY KEY (`id_boleto`),
  INDEX `fk_Boleto_Venta1_idx` (`Venta_id_venta` ASC) VISIBLE,
  INDEX `fk_Boleto_Funcion1_idx` (`Funcion_id_funcion` ASC) VISIBLE,
  INDEX `fk_Boleto_Asiento1_idx` (`Asiento_id_asiento` ASC) VISIBLE,
  CONSTRAINT `fk_Boleto_Venta1`
    FOREIGN KEY (`Venta_id_venta`)
    REFERENCES `pia_teatro`.`Venta` (`id_venta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Boleto_Funcion1`
    FOREIGN KEY (`Funcion_id_funcion`)
    REFERENCES `pia_teatro`.`Funcion` (`id_funcion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Boleto_Asiento1`
    FOREIGN KEY (`Asiento_id_asiento`)
    REFERENCES `pia_teatro`.`Asiento` (`id_asiento`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`MetodoPago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`MetodoPago` (
  `id_metodoPago` INT NOT NULL AUTO_INCREMENT,
  `tipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_metodoPago`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Pago` (
  `id_pago` INT NOT NULL AUTO_INCREMENT,
  `monto` DECIMAL(10,2) NOT NULL,
  `MetodoPago_id_metodoPago` INT NOT NULL,
  `Venta_id_venta` INT NOT NULL,
  PRIMARY KEY (`id_pago`),
  INDEX `fk_Pago_MetodoPago1_idx` (`MetodoPago_id_metodoPago` ASC) VISIBLE,
  INDEX `fk_Pago_Venta1_idx` (`Venta_id_venta` ASC) VISIBLE,
  CONSTRAINT `fk_Pago_MetodoPago1`
    FOREIGN KEY (`MetodoPago_id_metodoPago`)
    REFERENCES `pia_teatro`.`MetodoPago` (`id_metodoPago`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Pago_Venta1`
    FOREIGN KEY (`Venta_id_venta`)
    REFERENCES `pia_teatro`.`Venta` (`id_venta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pia_teatro`.`Factura`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pia_teatro`.`Factura` (
  `id_factura` INT NOT NULL AUTO_INCREMENT,
  `fecha_emision` DATETIME NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL,
  `impuestos` DECIMAL(10,2) NOT NULL,
  `total` DECIMAL(10,2) NOT NULL,
  `Venta_id_venta` INT NOT NULL,
  PRIMARY KEY (`id_factura`),
  INDEX `fk_Factura_Venta1_idx` (`Venta_id_venta` ASC) VISIBLE,
  CONSTRAINT `fk_Factura_Venta1`
    FOREIGN KEY (`Venta_id_venta`)
    REFERENCES `pia_teatro`.`Venta` (`id_venta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
