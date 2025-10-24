package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficioController.class)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private ObjectMapper objectMapper;

    private BeneficioDTO beneficioDTO;
    private List<BeneficioDTO> beneficios;

    @BeforeEach
    void setUp() {
        beneficioDTO = new BeneficioDTO();
        beneficioDTO.setId(1L);
        beneficioDTO.setNome("Beneficio Teste");
        beneficioDTO.setDescricao("Descrição Teste");
        beneficioDTO.setValor(new BigDecimal("1000.00"));
        beneficioDTO.setAtivo(true);

        beneficios = Arrays.asList(beneficioDTO);
    }

    @Test
    void testListarTodos() throws Exception {
        // Given
        when(beneficioService.findAll()).thenReturn(beneficios);

        // When & Then
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Beneficio Teste"))
                .andExpect(jsonPath("$[0].valor").value(1000.00));

        verify(beneficioService).findAll();
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Given
        when(beneficioService.findById(1L)).thenReturn(Optional.of(beneficioDTO));

        // When & Then
        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Beneficio Teste"));

        verify(beneficioService).findById(1L);
    }

    @Test
    void testBuscarPorIdNotFound() throws Exception {
        // Given
        when(beneficioService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/beneficios/999"))
                .andExpect(status().isNotFound());

        verify(beneficioService).findById(999L);
    }

    @Test
    void testCriar() throws Exception {
        // Given
        BeneficioDTO novoBeneficio = new BeneficioDTO();
        novoBeneficio.setNome("Novo Beneficio");
        novoBeneficio.setDescricao("Nova Descrição");
        novoBeneficio.setValor(new BigDecimal("500.00"));
        novoBeneficio.setAtivo(true);

        when(beneficioService.save(any(BeneficioDTO.class))).thenReturn(novoBeneficio);

        // When & Then
        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoBeneficio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Beneficio"));

        verify(beneficioService).save(any(BeneficioDTO.class));
    }

    @Test
    void testAtualizar() throws Exception {
        // Given
        when(beneficioService.update(1L, beneficioDTO)).thenReturn(Optional.of(beneficioDTO));

        // When & Then
        mockMvc.perform(put("/api/v1/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beneficioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Beneficio Teste"));

        verify(beneficioService).update(1L, beneficioDTO);
    }

    @Test
    void testAtualizarNotFound() throws Exception {
        // Given
        when(beneficioService.update(999L, beneficioDTO)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/v1/beneficios/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beneficioDTO)))
                .andExpect(status().isNotFound());

        verify(beneficioService).update(999L, beneficioDTO);
    }

    @Test
    void testExcluir() throws Exception {
        // Given
        when(beneficioService.delete(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isOk());

        verify(beneficioService).delete(1L);
    }

    @Test
    void testExcluirNotFound() throws Exception {
        // Given
        when(beneficioService.delete(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/beneficios/999"))
                .andExpect(status().isNotFound());

        verify(beneficioService).delete(999L);
    }

    @Test
    void testTransferir() throws Exception {
        // Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setValor(new BigDecimal("100.00"));

        doNothing().when(beneficioService).transferir(any(TransferenciaDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transferência realizada com sucesso"));

        verify(beneficioService).transferir(any(TransferenciaDTO.class));
    }

    @Test
    void testTransferirError() throws Exception {
        // Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(1L);
        transferencia.setToId(2L);
        transferencia.setValor(new BigDecimal("100.00"));

        doThrow(new IllegalStateException("Saldo insuficiente"))
                .when(beneficioService).transferir(any(TransferenciaDTO.class));

        // When & Then
        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Saldo insuficiente"));

        verify(beneficioService).transferir(any(TransferenciaDTO.class));
    }

    @Test
    void testBuscarPorNome() throws Exception {
        // Given
        when(beneficioService.searchByNome("Teste")).thenReturn(beneficios);

        // When & Then
        mockMvc.perform(get("/api/v1/beneficios/buscar?nome=Teste"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Beneficio Teste"));

        verify(beneficioService).searchByNome("Teste");
    }
}
