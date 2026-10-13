CREATE TABLE posicao (
    id            BIGSERIAL PRIMARY KEY,
    veiculo_id    BIGINT                     NOT NULL REFERENCES veiculo (id) ON DELETE CASCADE,
    ponto         geometry(Point, 4326)      NOT NULL,
    velocidade    NUMERIC(6, 2)              NOT NULL DEFAULT 0,
    registrado_em TIMESTAMPTZ                NOT NULL DEFAULT now()
);

-- índice espacial (GiST): essencial para consultas de contenção/proximidade
-- em qualquer volume de dados real
CREATE INDEX idx_posicao_ponto ON posicao USING GIST (ponto);
CREATE INDEX idx_posicao_veiculo_registrado ON posicao (veiculo_id, registrado_em DESC);
