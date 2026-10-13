CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE veiculo (
    id            BIGSERIAL PRIMARY KEY,
    placa         VARCHAR(10)  NOT NULL UNIQUE,
    descricao     VARCHAR(120) NOT NULL,
    dispositivo_id VARCHAR(60) NOT NULL UNIQUE,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE
);
