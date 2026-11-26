
package com.cartera.backend.repository;

import com.cartera.backend.entity.Cobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CobroRepository
        extends JpaRepository<Cobro, Long> {

    List<Cobro> findByClienteId(Long clienteId);

    List<Cobro> findByUsuarioId(Long usuarioId);

    List<Cobro> findByEstado(String estado);

    List<Cobro> findByFechaPagoBetween(
            LocalDate fechaInicio,
            LocalDate fechaFin);

    List<Cobro> findByMetodoPago(String metodoPago);

    List<Cobro> findAllByOrderByFechaRegistroDesc();

    long countByEstado(String estado);
}
