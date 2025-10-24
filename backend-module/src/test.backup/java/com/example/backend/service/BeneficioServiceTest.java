package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository beneficioRepository;

    @InjectMocks
    private BeneficioService beneficioService;

    private Beneficio beneficio1;
    private Beneficio beneficio2;
    private BeneficioDTO beneficioDTO1;

    @BeforeEach
    void setUp() {
        beneficio1 = new Beneficio();
        beneficio1.setId(1L);
        beneficio1.setNome("Beneficio A");
        beneficio1.setDescricao("Descrição A");
        beneficio1.setValor(new BigDecimal("1000.00"));
        beneficio1.setAtivo(true);

        beneficio2 = new Beneficio();
        beneficio2.setId(2L);
        beneficio2.setNome("Beneficio B");
        beneficio2.setDescricao("Descrição B");
        beneficio2.setValor(new BigDecimal("500.00"));
        beneficio2.setAtivo(true);

        beneficioDTO1 = new BeneficioDTO();
        beneficioDTO1.setNome("Novo Beneficio");
        beneficioDTO1.setDescricao("Nova Descrição");
        beneficioDTO1.setValor(new BigDecimal("750.00"));
        beneficioDTO1.setAtivo(true);
    }

    @Test
    void testFindAll() {
        // Given
        List<Beneficio> beneficios = Arrays.asList(beneficio1, beneficio2);
        when(beneficioRepository.findByAtivoTrue()).thenReturn(beneficios);

        // When
        List<BeneficioDTO> result = beneficioService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("Beneficio A", result.get(0).getNome());
        assertEquals("Beneficio B", result.get(1).getNome());
        verify(beneficioRepository).findByAtivoTrue();
    }

    @Test
    void testFindById() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficio1));

        // When
        Optional<BeneficioDTO> result = beneficioService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Beneficio A", result.get().getNome());
        verify(beneficioRepository).findByIdAndAtivoTrue(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        // When
        Optional<BeneficioDTO> result = beneficioService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(beneficioRepository).findByIdAndAtivoTrue(999L);
    }

    @Test
    void testSave() {
        // Given
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(beneficio1);

        // When
        BeneficioDTO result = beneficioService.save(beneficioDTO1);

        // Then
        assertNotNull(result);
        assertEquals("Beneficio A", result.getNome());
        verify(beneficioRepository).save(any(Beneficio.class));
    }

    @Test
    void testUpdate() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficio1));
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(beneficio1);

        // When
        Optional<BeneficioDTO> result = beneficioService.update(1L, beneficioDTO1);

        // Then
        assertTrue(result.isPresent());
        verify(beneficioRepository).findByIdAndAtivoTrue(1L);
        verify(beneficioRepository).save(any(Beneficio.class));
    }

    @Test
    void testUpdateNotFound() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        // When
        Optional<BeneficioDTO> result = beneficioService.update(999L, beneficioDTO1);

        // Then
        assertFalse(result.isPresent());
        verify(beneficioRepository).findByIdAndAtivoTrue(999L);
        verify(beneficioRepository, never()).save(any(Beneficio.class));
    }

    @Test
    void testDelete() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficio1));
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(beneficio1);

        // When
        boolean result = beneficioService.delete(1L);

        // Then
        assertTrue(result);
        verify(beneficioRepository).findByIdAndAtivoTrue(1L);
        verify(beneficioRepository).save(any(Beneficio.class));
    }

    @Test
    void testDeleteNotFound() {
        // Given
        when(beneficioRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        // When
        boolean result = beneficioService.delete(999L);

        // Then
        assertFalse(result);
        verify(beneficioRepository).findByIdAndAtivoTrue(999L);
        verify(beneficioRepository, never()).save(any(Beneficio.class));
    }

    @Test
    void testTransferirSuccess() {
        // Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setValor(new BigDecimal("100.00"));

        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficio1));
        when(beneficioRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(beneficio2));
        when(beneficioRepository.save(any(Beneficio.class))).thenReturn(beneficio1);

        // When & Then
        assertDoesNotThrow(() -> beneficioService.transferir(transferencia));
        
        verify(beneficioRepository).findByIdAndAtivoTrue(1L);
        verify(beneficioRepository).findByIdAndAtivoTrue(2L);
        verify(beneficioRepository, times(2)).save(any(Beneficio.class));
    }

    @Test
    void testTransferirSameBeneficio() {
        // Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(1L);
        transferencia.setValor(new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> beneficioService.transferir(transferencia));
        verify(beneficioRepository, never()).findByIdAndAtivoTrue(anyLong());
    }

    @Test
    void testTransferirInsufficientFunds() {
        // Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setValor(new BigDecimal("2000.00")); // Mais que o saldo disponível

        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficio1));
        when(beneficioRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(beneficio2));

        // When & Then
        assertThrows(IllegalStateException.class, () -> beneficioService.transferir(transferencia));
        verify(beneficioRepository).findByIdAndAtivoTrue(1L);
        verify(beneficioRepository).findByIdAndAtivoTrue(2L);
        verify(beneficioRepository, never()).save(any(Beneficio.class));
    }

    @Test
    void testSearchByNome() {
        // Given
        List<Beneficio> beneficios = Arrays.asList(beneficio1);
        when(beneficioRepository.findByNomeContainingAndAtivoTrue("Beneficio")).thenReturn(beneficios);

        // When
        List<BeneficioDTO> result = beneficioService.searchByNome("Beneficio");

        // Then
        assertEquals(1, result.size());
        assertEquals("Beneficio A", result.get(0).getNome());
        verify(beneficioRepository).findByNomeContainingAndAtivoTrue("Beneficio");
    }
}
