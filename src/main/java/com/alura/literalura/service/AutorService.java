package com.alura.literalura.service;

import com.alura.literalura.model.Autor;
import com.alura.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional(readOnly = true)
    public List<Autor> listarTodosAutores() {
        return autorRepository.findAll().stream()
                .sorted(Comparator.comparing(Autor::getNome))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutoresVivosEmDeterminadoAno(Integer ano) {
        if (ano == null || ano <= 0) {
            throw new IllegalArgumentException("Ano deve ser um número positivo");
        }
        return autorRepository.buscarAutoresVivosEmDeterminadoAno(ano);
    }

    @Transactional(readOnly = true)
    public Optional<Autor> buscarAutorPorNomeExato(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return autorRepository.findFirstByNomeContainingIgnoreCase(nome);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> contarLivrosPorAutor() {
        return autorRepository.findAll().stream()
                .filter(autor -> !autor.getLivros().isEmpty())
                .collect(Collectors.toMap(
                        Autor::getNome,
                        autor -> (long) autor.getLivros().size()
                ));
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutoresComLivros() {
        return autorRepository.findAll().stream()
                .filter(autor -> !autor.getLivros().isEmpty())
                .sorted(Comparator.comparing(Autor::getNome))
                .collect(Collectors.toList());
    }

    public Autor getAutorIndefinido() {
        return autorRepository.findFirstByNomeContainingIgnoreCase("Indefinido")
                .orElseGet(() -> {
                    Autor autor = new Autor();
                    autor.setNome("Indefinido");
                    return autorRepository.save(autor);
                });
    }
}