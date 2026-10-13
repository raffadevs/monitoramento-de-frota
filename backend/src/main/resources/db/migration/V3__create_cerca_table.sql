CREATE TABLE cerca (
    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(120)            NOT NULL,
    area              geometry(Polygon, 4326) NOT NULL,
    tipo_alerta       VARCHAR(10)             NOT NULL CHECK (tipo_alerta IN ('ENTRADA', 'SAIDA', 'AMBOS')),
    velocidade_maxima NUMERIC(6, 2),
    ativa             BOOLEAN                 NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_cerca_area ON cerca USING GIST (area);

-- Duas cercas de demonstração em torno da Av. Paulista (São Paulo), só para
-- o mapa não começar vazio.
INSERT INTO cerca (nome, area, tipo_alerta, velocidade_maxima) VALUES
    ('Região Central', ST_GeomFromText(
        'POLYGON((-46.6563 -23.5600, -46.6450 -23.5600, -46.6450 -23.5670, -46.6563 -23.5670, -46.6563 -23.5600))',
        4326), 'AMBOS', 60),
    ('Zona de Entrega', ST_GeomFromText(
        'POLYGON((-46.6700 -23.5550, -46.6600 -23.5550, -46.6600 -23.5620, -46.6700 -23.5620, -46.6700 -23.5550))',
        4326), 'ENTRADA', NULL);
