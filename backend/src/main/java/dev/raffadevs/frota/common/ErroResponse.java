package dev.raffadevs.frota.common;

import java.time.Instant;
import java.util.List;

public record ErroResponse(Instant timestamp, int status, String erro, List<String> detalhes) {

    public static ErroResponse de(int status, String erro) {
        return new ErroResponse(Instant.now(), status, erro, List.of());
    }

    public static ErroResponse de(int status, String erro, List<String> detalhes) {
        return new ErroResponse(Instant.now(), status, erro, detalhes);
    }
}
