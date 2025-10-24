package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    
    List<Beneficio> findByAtivoTrue();
    
    @Query("SELECT b FROM Beneficio b WHERE b.ativo = true AND b.id = :id")
    Optional<Beneficio> findByIdAndAtivoTrue(@Param("id") Long id);
    
    @Query("SELECT b FROM Beneficio b WHERE b.nome LIKE %:nome% AND b.ativo = true")
    List<Beneficio> findByNomeContainingAndAtivoTrue(@Param("nome") String nome);
}
