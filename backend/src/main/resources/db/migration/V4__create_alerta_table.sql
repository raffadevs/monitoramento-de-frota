CREATE TABLE alerta (
    id          BIGSERIAL PRIMARY KEY,
    veiculo_id  BIGINT      NOT NULL REFERENCES veiculo (id) ON DELETE CASCADE,
    cerca_id    BIGINT      NOT NULL REFERENCES cerca (id) ON DELETE CASCADE,
    tipo        VARCHAR(20) NOT NULL CHECK (tipo IN ('ENTROU', 'SAIU', 'EXCESSO_VELOCIDADE')),
    ocorrido_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    lido        BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_alerta_veiculo_id ON alerta (veiculo_id);
CREATE INDEX idx_alerta_lido ON alerta (lido) WHERE NOT lido;
