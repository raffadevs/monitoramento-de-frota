package dev.raffadevs.frota.cerca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CercaRepository extends JpaRepository<Cerca, Long> {

    List<Cerca> findByAtivaTrue();
}
