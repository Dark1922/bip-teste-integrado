package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeneficioService {

    @Autowired
    private BeneficioRepository beneficioRepository;

    @PersistenceContext
    private EntityManager em;

    // === CRUD ===

    public List<BeneficioDTO> findAll() {
        return beneficioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BeneficioDTO> findById(Long id) {
        return beneficioRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public Optional<BeneficioDTO> save(BeneficioDTO dto) {
        Beneficio beneficio = convertToEntity(dto);
        beneficio = beneficioRepository.save(beneficio);
        return Optional.of(convertToDTO(beneficio));
    }

    @Transactional
    public Optional<BeneficioDTO> update(Long id, BeneficioDTO dto) {
        return beneficioRepository.findById(id)
                .map(existing -> {
                    existing.setNome(dto.getNome());
                    existing.setDescricao(dto.getDescricao());
                    existing.setValor(dto.getValor());
                    existing.setAtivo(dto.getAtivo());
                    Beneficio updated = beneficioRepository.save(existing);
                    return convertToDTO(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        return beneficioRepository.findById(id)
                .map(b -> {
                    beneficioRepository.delete(b);
                    return true;
                })
                .orElse(false);
    }

    // === TRANSFERÊNCIA COM LOCK PESSIMISTA ===

    @Transactional
    public void transferir(TransferenciaDTO dto) {
        BigDecimal valor = dto.getValor();
        if (dto.getFromId().equals(dto.getToId())) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        Long a = Math.min(dto.getFromId(), dto.getToId());
        Long b = Math.max(dto.getFromId(), dto.getToId());
        boolean fromFirst = a.equals(dto.getFromId());

        Beneficio first = em.find(Beneficio.class, a, LockModeType.PESSIMISTIC_WRITE);
        Beneficio second = em.find(Beneficio.class, b, LockModeType.PESSIMISTIC_WRITE);

        Beneficio origem = fromFirst ? first : second;
        Beneficio destino = fromFirst ? second : first;

        if (origem == null || !Boolean.TRUE.equals(origem.getAtivo()))
            throw new IllegalArgumentException("Benefício origem inexistente/inativo");
        if (destino == null || !Boolean.TRUE.equals(destino.getAtivo()))
            throw new IllegalArgumentException("Benefício destino inexistente/inativo");
        if (origem.getValor().compareTo(valor) < 0)
            throw new IllegalStateException("Saldo insuficiente");

        origem.setValor(origem.getValor().subtract(valor));
        destino.setValor(destino.getValor().add(valor));
        em.flush();
    }

    // === CONSULTAS ===

    public List<BeneficioDTO> searchByNome(String nome) {
        return beneficioRepository.findByNomeContainingAndAtivoTrue(nome)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === CONVERSÕES ===

    private BeneficioDTO convertToDTO(Beneficio beneficio) {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setId(beneficio.getId());
        dto.setNome(beneficio.getNome());
        dto.setDescricao(beneficio.getDescricao());
        dto.setValor(beneficio.getValor());
        dto.setAtivo(beneficio.getAtivo());
        dto.setVersion(beneficio.getVersion());
        return dto;
    }

    private Beneficio convertToEntity(BeneficioDTO dto) {
        Beneficio beneficio = new Beneficio();
        beneficio.setId(dto.getId());
        beneficio.setNome(dto.getNome());
        beneficio.setDescricao(dto.getDescricao());
        beneficio.setValor(dto.getValor());
        beneficio.setAtivo(dto.getAtivo());
        beneficio.setVersion(dto.getVersion());
        return beneficio;
    }
}
