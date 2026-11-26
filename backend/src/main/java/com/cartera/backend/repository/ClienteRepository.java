
package com.cartera.backend.repository;

import com.cartera.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    List<Cliente> findByActivoTrue();

    List<Cliente> findByNombreContainingIgnoreCase(
            String nombre);
}