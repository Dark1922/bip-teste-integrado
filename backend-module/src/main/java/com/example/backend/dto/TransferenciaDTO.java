package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferenciaDTO {
    
    @NotNull(message = "ID do benefício origem é obrigatório")
    private Long fromId;
    
    @NotNull(message = "ID do benefício destino é obrigatório")
    private Long toId;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valor;
    
    // Construtores
    public TransferenciaDTO() {}
    
    public TransferenciaDTO(Long fromId, Long toId, BigDecimal valor) {
        this.fromId = fromId;
        this.toId = toId;
        this.valor = valor;
    }
    
    // Getters e Setters
    public Long getFromId() {
        return fromId;
    }
    
    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }
    
    public Long getToId() {
        return toId;
    }
    
    public void setToId(Long toId) {
        this.toId = toId;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
