-- =====================================================================
-- SCRIPT SQL - PostgreSQL
-- Modelo: Usuarios, Autenticación JWT (Access stateless + Refresh en BD),
--         Categorías, Productos, Locales, Pedidos y Detalle de Pedido
-- =====================================================================

-- =====================================================================
-- TABLA: usuario
-- =====================================================================
CREATE TABLE usuario (
                         id_usuario          BIGSERIAL PRIMARY KEY,
                         nombre              VARCHAR(150) NOT NULL,
                         correo              VARCHAR(150) NOT NULL UNIQUE,
                         password            VARCHAR(255) NOT NULL,
                         validado            BOOLEAN NOT NULL DEFAULT FALSE,
                         codigo_verificacion VARCHAR(10) NULL,
                         creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuario_codigo_verificacion ON usuario(codigo_verificacion);

-- =====================================================================
-- TABLA: token
-- Solo se persiste el REFRESH TOKEN (hasheado). El access token es JWT
-- stateless, de vida corta, y se valida solo con la firma, sin ir a BD.
-- =====================================================================
CREATE TABLE token (
                       id_token        BIGSERIAL PRIMARY KEY,
                       id_usuario      BIGINT NOT NULL REFERENCES usuario(id_usuario),
                       refresh_token   TEXT NOT NULL,                  -- hash del refresh token (no el valor plano)
                       user_agent      VARCHAR(255),                   -- para identificar el dispositivo/navegador
                       ip_origen       VARCHAR(45),                     -- soporta IPv4 e IPv6
                       expira_en       TIMESTAMP NOT NULL,
                       revocado        BOOLEAN NOT NULL DEFAULT FALSE,  -- permite invalidar manualmente (logout)
                       creado_en       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_token_usuario ON token(id_usuario);
CREATE INDEX idx_token_refresh ON token(refresh_token);

-- =====================================================================
-- TABLA: categoria
-- =====================================================================
CREATE TABLE categoria (
                           id_categoria    SERIAL PRIMARY KEY,
                           nombre          VARCHAR(100) NOT NULL UNIQUE
);

-- =====================================================================
-- TABLA: producto  (muchos productos -> una categoría)
-- =====================================================================
CREATE TABLE producto (
                          id_producto     BIGSERIAL PRIMARY KEY,
                          id_categoria    INTEGER NOT NULL REFERENCES categoria(id_categoria),
                          nombre          VARCHAR(150) NOT NULL,
                          descripcion     TEXT,
                          precio          NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
                          nuevo           BOOLEAN NOT NULL DEFAULT FALSE,
                          favorito        BOOLEAN NOT NULL DEFAULT FALSE,
                          opciones        JSONB NULL                       -- ej: {"tallas": ["S","M"], "colores": ["rojo"]}
);

CREATE INDEX idx_producto_categoria ON producto(id_categoria);

-- =====================================================================
-- TABLA: local
-- =====================================================================
CREATE TABLE local (
                       id_local        SERIAL PRIMARY KEY,
                       razon_social    VARCHAR(150) NOT NULL,
                       direccion       VARCHAR(255) NOT NULL
);

-- =====================================================================
-- TABLA: pedido
-- =====================================================================
CREATE TABLE pedido (
                        id_pedido       BIGSERIAL PRIMARY KEY,
                        id_usuario      BIGINT NOT NULL REFERENCES usuario(id_usuario),
                        id_local        INTEGER NOT NULL REFERENCES local(id_local),
                        fecha           TIMESTAMP NOT NULL DEFAULT NOW(),
                        total_general   NUMERIC(10,2) NOT NULL CHECK (total_general >= 0),
                        item            INTEGER NOT NULL CHECK (item >= 0)  -- cantidad total de productos en el pedido
);

CREATE INDEX idx_pedido_usuario ON pedido(id_usuario);
CREATE INDEX idx_pedido_local ON pedido(id_local);

-- =====================================================================
-- TABLA: detalle_pedido (detalleProducto)
-- =====================================================================
CREATE TABLE detalle_pedido (
                                id_detalle      BIGSERIAL PRIMARY KEY,
                                id_pedido       BIGINT NOT NULL REFERENCES pedido(id_pedido),
                                id_producto     BIGINT NOT NULL REFERENCES producto(id_producto),
                                cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
                                precio          NUMERIC(10,2) NOT NULL CHECK (precio >= 0),  -- precio unitario al momento de la compra
                                total           NUMERIC(10,2) NOT NULL CHECK (total >= 0)     -- cantidad * precio
);

CREATE INDEX idx_detalle_pedido ON detalle_pedido(id_pedido);
CREATE INDEX idx_detalle_producto ON detalle_pedido(id_producto);