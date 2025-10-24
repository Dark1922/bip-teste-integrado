package com.example.backend.integration;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferenciaDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class BeneficioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        beneficioRepository.deleteAll();
        
        // Criar dados de teste
        Beneficio beneficio1 = new Beneficio();
        beneficio1.setNome("Beneficio Teste 1");
        beneficio1.setDescricao("Descrição 1");
        beneficio1.setValor(new BigDecimal("1000.00"));
        beneficio1.setAtivo(true);
        beneficioRepository.save(beneficio1);

        Beneficio beneficio2 = new Beneficio();
        beneficio2.setNome("Beneficio Teste 2");
        beneficio2.setDescricao("Descrição 2");
        beneficio2.setValor(new BigDecimal("500.00"));
        beneficio2.setAtivo(true);
        beneficioRepository.save(beneficio2);
    }

    @Test
    void testCriarBeneficio() throws Exception {
        BeneficioDTO novoBeneficio = new BeneficioDTO();
        novoBeneficio.setNome("Novo Beneficio");
        novoBeneficio.setDescricao("Nova Descrição");
        novoBeneficio.setValor(new BigDecimal("750.00"));
        novoBeneficio.setAtivo(true);

        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoBeneficio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Beneficio"))
                .andExpect(jsonPath("$.valor").value(750.00));
    }

    @Test
    void testListarBeneficios() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Beneficio Teste 1"))
                .andExpect(jsonPath("$[1].nome").value("Beneficio Teste 2"));
    }

    @Test
    void testBuscarBeneficioPorId() throws Exception {
        // Primeiro, buscar todos para obter um ID válido
        var beneficios = beneficioRepository.findByAtivoTrue();
        Long id = beneficios.get(0).getId();

        mockMvc.perform(get("/api/v1/beneficios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Beneficio Teste 1"));
    }

    @Test
    void testAtualizarBeneficio() throws Exception {
        var beneficios = beneficioRepository.findByAtivoTrue();
        Long id = beneficios.get(0).getId();

        BeneficioDTO beneficioAtualizado = new BeneficioDTO();
        beneficioAtualizado.setNome("Beneficio Atualizado");
        beneficioAtualizado.setDescricao("Descrição Atualizada");
        beneficioAtualizado.setValor(new BigDecimal("1200.00"));
        beneficioAtualizado.setAtivo(true);

        mockMvc.perform(put("/api/v1/beneficios/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beneficioAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Beneficio Atualizado"))
                .andExpect(jsonPath("$.valor").value(1200.00));
    }

    @Test
    void testTransferirEntreBeneficios() throws Exception {
        var beneficios = beneficioRepository.findByAtivoTrue();
        Long fromId = beneficios.get(0).getId();
        Long toId = beneficios.get(1).getId();

        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(fromId);
        transferencia.setToId(toId);
        transferencia.setValor(new BigDecimal("200.00"));

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transferência realizada com sucesso"));
    }

    @Test
    void testTransferirSaldoInsuficiente() throws Exception {
        var beneficios = beneficioRepository.findByAtivoTrue();
        Long fromId = beneficios.get(1).getId(); // Beneficio com saldo menor
        Long toId = beneficios.get(0).getId();

        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setFromId(fromId);
        transferencia.setToId(toId);
        transferencia.setValor(new BigDecimal("1000.00")); // Mais que o saldo disponível

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExcluirBeneficio() throws Exception {
        var beneficios = beneficioRepository.findByAtivoTrue();
        Long id = beneficios.get(0).getId();

        mockMvc.perform(delete("/api/v1/beneficios/" + id))
                .andExpect(status().isOk());

        // Verificar se o benefício foi marcado como inativo
        mockMvc.perform(get("/api/v1/beneficios/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarPorNome() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/buscar?nome=Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testValidacaoCamposObrigatorios() throws Exception {
        BeneficioDTO beneficioInvalido = new BeneficioDTO();
        // Campos obrigatórios não preenchidos

        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beneficioInvalido)))
                .andExpect(status().isBadRequest());
    }
}
