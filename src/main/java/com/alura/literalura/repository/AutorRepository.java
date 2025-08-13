package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findFirstByNomeContainingIgnoreCase(String nome);

    @Query("SELECT DISTINCT a FROM Autor a " +
            "LEFT JOIN FETCH a.livros " +
            "WHERE (:ano BETWEEN a.anoNascimento AND a.anoFalecimento) OR " +
            "(a.anoNascimento <= :ano AND a.anoFalecimento IS NULL)")
    List<Autor> buscarAutoresVivosEmDeterminadoAno(Integer ano);

    @Query("SELECT DISTINCT a FROM Autor a " +
            "LEFT JOIN FETCH a.livros " +
            "WHERE a.anoNascimento <= :ano AND " +
            "(a.anoFalecimento >= :ano OR a.anoFalecimento IS NULL)")
    List<Autor> buscarAutoresVivosNoAno(Integer ano);

    @Query("SELECT a FROM Autor a JOIN FETCH a.livros WHERE SIZE(a.livros) > 0")
    List<Autor> findAutoresComLivros();
}