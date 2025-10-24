package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Validações de entrada
        if (fromId == null || toId == null || amount == null) {
            throw new IllegalArgumentException("IDs e valor não podem ser nulos");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        // Busca com locking otimista para evitar lost updates
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC);

        if (from == null) {
            throw new IllegalArgumentException("Benefício origem não encontrado: " + fromId);
        }
        
        if (to == null) {
            throw new IllegalArgumentException("Benefício destino não encontrado: " + toId);
        }
        
        if (!from.getAtivo()) {
            throw new IllegalStateException("Benefício origem está inativo");
        }
        
        if (!to.getAtivo()) {
            throw new IllegalStateException("Benefício destino está inativo");
        }

        // Validação de saldo suficiente
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente. Saldo atual: " + from.getValor() + ", Valor solicitado: " + amount);
        }

        try {
            // Operação de transferência com controle de concorrência
            BigDecimal novoValorFrom = from.getValor().subtract(amount);
            BigDecimal novoValorTo = to.getValor().add(amount);
            
            from.setValor(novoValorFrom);
            to.setValor(novoValorTo);

            // Merge com controle de versão (optimistic locking)
            em.merge(from);
            em.merge(to);
            
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Transferência falhou devido a conflito de concorrência. Tente novamente.", e);
        }
    }
    
    public Beneficio findById(Long id) {
        return em.find(Beneficio.class, id);
    }
    
    public Beneficio save(Beneficio beneficio) {
        if (beneficio.getId() == null) {
            em.persist(beneficio);
        } else {
            beneficio = em.merge(beneficio);
        }
        return beneficio;
    }
    
    public void delete(Long id) {
        Beneficio beneficio = em.find(Beneficio.class, id);
        if (beneficio != null) {
            em.remove(beneficio);
        }
    }
}
