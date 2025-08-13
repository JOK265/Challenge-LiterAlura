package com.alura.literalura.service;

import com.alura.literalura.DTO.AutorDTO;
import com.alura.literalura.DTO.DadosLivroDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Idioma;
import com.alura.literalura.model.Livro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LivroService {
    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void salvarLivro(DadosLivroDTO dadosLivro) {
        if (dadosLivro == null || dadosLivro.title() == null || dadosLivro.title().isBlank()) {
            throw new IllegalArgumentException("Dados do livro são inválidos");
        }

        livroRepository.findByTituloIgnoreCase(dadosLivro.title())
                .ifPresentOrElse(
                        livroExistente -> {},
                        () -> criarNovoLivro(dadosLivro)
                );


    }

    private void criarNovoLivro(DadosLivroDTO dadosLivro) {
        Livro livro = new Livro();
        livro.setTitulo(dadosLivro.title());
        livro.setNumeroDownloads(dadosLivro.download_count() != null ? dadosLivro.download_count() : 0.0);
        livro.setIdioma(Idioma.fromString(
                !dadosLivro.languages().isEmpty() ?
                        dadosLivro.languages().get(0) : "EN"
        ));

        if (!dadosLivro.authors().isEmpty()) {
            AutorDTO autorDTO = dadosLivro.authors().get(0);
            processarAutor(autorDTO, livro);
        }

        livroRepository.save(livro);
    }

    private void processarAutor(AutorDTO autorDTO, Livro livro) {
        Autor autor = autorRepository.findFirstByNomeContainingIgnoreCase(autorDTO.name())
                .orElseGet(() -> {
                    Autor novoAutor = new Autor();
                    novoAutor.setNome(autorDTO.name());
                    novoAutor.setAnoNascimento(autorDTO.birth_year());
                    novoAutor.setAnoFalecimento(autorDTO.death_year());
                    return autorRepository.save(novoAutor);
                });

        livro.setAutor(autor);
        autor.getLivros().add(livro);
    }


    public List<Livro> listarTodosLivros() {
        return livroRepository.findAllByOrderByTituloAsc();
    }

    public List<Livro> listarLivrosPorIdioma(String idiomaStr) {
        try {
            Idioma idioma = Idioma.valueOf(idiomaStr.toUpperCase());
            return livroRepository.findByIdioma(idioma);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    public Map<Idioma, Long> contarLivrosPorIdioma() {
        return livroRepository.contarLivrosPorIdioma()
                .stream()
                .collect(Collectors.toMap(
                        result -> (Idioma) result[0],
                        result -> (Long) result[1]
                ));
    }
}