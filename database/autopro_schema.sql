-- ============================================================================
-- SCRIPT MYSQL 8.0: AutoPro (autopro_db)
-- ============================================================================
CREATE DATABASE IF NOT EXISTS autopro_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE autopro_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    rol ENUM('ADMIN', 'ASESOR', 'MECANICO') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(15) NOT NULL UNIQUE,
    nombres VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    direccion VARCHAR(200)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vehiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    color VARCHAR(30),
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS repuestos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(60) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ordenes_trabajo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_ot VARCHAR(20) NOT NULL UNIQUE,
    fecha_ingreso DATETIME NOT NULL,
    kilometraje INT NOT NULL,
    nivel_combustible VARCHAR(20) NOT NULL,
    falla_reportada TEXT NOT NULL,
    diagnostico_tecnico TEXT,
    costo_mano_obra DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    igv DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    estado ENUM('EN_DIAGNOSTICO', 'EN_PROCESO', 'FINALIZADO', 'PAGADO') NOT NULL DEFAULT 'EN_DIAGNOSTICO',
    vehiculo_id BIGINT NOT NULL,
    mecanico_id BIGINT NOT NULL,
    asesor_id BIGINT NOT NULL,
    CONSTRAINT fk_ot_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id),
    CONSTRAINT fk_ot_mecanico FOREIGN KEY (mecanico_id) REFERENCES usuarios(id),
    CONSTRAINT fk_ot_asesor FOREIGN KEY (asesor_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_orden (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    repuesto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_orden FOREIGN KEY (orden_id) REFERENCES ordenes_trabajo(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_repuesto FOREIGN KEY (repuesto_id) REFERENCES repuestos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comprobantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_comprobante VARCHAR(25) NOT NULL UNIQUE,
    tipo_comprobante ENUM('BOLETA', 'FACTURA') NOT NULL,
    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'YAPE_PLIN', 'TRANSFERENCIA') NOT NULL,
    fecha_emision DATETIME NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    igv DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    orden_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_comprobante_orden FOREIGN KEY (orden_id) REFERENCES ordenes_trabajo(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS citas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    placa VARCHAR(10) NOT NULL,
    servicio_solicitado VARCHAR(120) NOT NULL,
    fecha_preferida VARCHAR(30) NOT NULL,
    observaciones TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB;
