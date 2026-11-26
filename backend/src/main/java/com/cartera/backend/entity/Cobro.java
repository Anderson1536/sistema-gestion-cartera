
package com.cartera.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cobros")
public class Cobro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "fecha_registro", nullable = false,
            updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "metodo_pago", nullable = false,
            length = 30)
    private String metodoPago;

    @Column(length = 100)
    private String referencia;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(length = 500)
    private String observaciones;

    // Constructor vacío
    public Cobro() {
    }

    // Constructor con parámetros
    public Cobro(Cliente cliente, Usuario usuario,
                 BigDecimal valor, LocalDate fechaPago,
                 String metodoPago, String referencia,
                 String observaciones) {
        this.cliente = cliente;
        this.usuario = usuario;
        this.valor = valor;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
        this.observaciones = observaciones;
    }

    // Asignación automática de la fecha de registro
    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    // Métodos getter y setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
