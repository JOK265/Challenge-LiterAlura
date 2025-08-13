package com.alura.literalura.principal;

import com.alura.literalura.DTO.DadosLivroDTO;
import com.alura.literalura.DTO.ResultadosDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Livro;
import com.alura.literalura.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private final Scanner leitura = new Scanner(System.in);

    @Autowired
    private ConsumoAPI consumoApi;

    @Autowired
    private ConverteDados conversor;

    @Autowired
    private LivroService livroService;

    @Autowired
    private AutorService autorService;

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            System.out.println("""
                Escolha o número de sua opção:
                1- buscar livro pelo título
                2- listar livros registrados
                3- listar autores registrados
                4- listar autores vivos em um determinado ano
                5- listar livros em um determinado idioma
                0 - sair
                """);

            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEmAno();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Insira o nome do livro que você deseja procurar");
        var tituloLivro = leitura.nextLine();

        try {
            String json = consumoApi.obterDados(tituloLivro);

            ResultadosDTO resultados = conversor.obterDados(json, ResultadosDTO.class);

            if (resultados == null || resultados.results() == null || resultados.results().isEmpty()) {
                System.out.println("Nenhum livro encontrado com esse título.");
                return;
            }

            DadosLivroDTO primeiroLivro = resultados.results().get(0);
            livroService.salvarLivro(primeiroLivro);

            System.out.println("--- LIVRO ---");
            System.out.println("Título: " + primeiroLivro.title());
            System.out.println("Autor: " +
                    (primeiroLivro.authors() != null && !primeiroLivro.authors().isEmpty() ?
                            primeiroLivro.authors().get(0).name() : "Indefinido"));
            System.out.println("Idioma: " +
                    (primeiroLivro.languages() != null && !primeiroLivro.languages().isEmpty() ?
                            primeiroLivro.languages().get(0) : "Desconhecido"));
            System.out.println("Número de downloads: " + primeiroLivro.download_count());
            System.out.println("---");

        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao buscar o livro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = livroService.listarTodosLivros();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado no banco de dados.");
            return;
        }

        livros.forEach(livro -> {
            System.out.println("--- LIVRO ---");
            System.out.println("Título: " + livro.getTitulo());
            System.out.println("Autor: " + (livro.getAutor() != null ? livro.getAutor().getNome() : "Indefinido"));
            System.out.println("Idioma: " + livro.getIdioma().getDescricao());
            System.out.println("Número de downloads: " + livro.getNumeroDownloads());
            System.out.println("---");
        });
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorService.listarTodosAutores();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado no banco de dados.");
            return;
        }

        autores.forEach(autor -> {
            System.out.println("Autor: " + autor.getNome());
            System.out.println("Ano de nascimento: " + autor.getAnoNascimento());
            System.out.println("Ano de falecimento: " + autor.getAnoFalecimento());
            System.out.println("Livros: " + autor.getLivros().stream().map(Livro::getTitulo).toList());
            System.out.println("---");
        });
    }

    private void listarAutoresVivosEmAno() {
        System.out.println("Digite o ano para buscar autores vivos:");
        int ano = leitura.nextInt();
        leitura.nextLine();

        List<Autor> autores = autorService.listarAutoresVivosEmDeterminadoAno(ano);
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor vivo no ano " + ano);
            return;
        }

        autores.forEach(autor -> {
            System.out.println("Autor: " + autor.getNome());
            System.out.println("Ano de nascimento: " + autor.getAnoNascimento());
            System.out.println("Ano de falecimento: " + autor.getAnoFalecimento());
            System.out.println("Livros: " + autor.getLivros().stream().map(Livro::getTitulo).toList());
            System.out.println("---");
        });
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
        Escolha o idioma para filtrar os livros:
        es - espanhol
        en - inglês
        fr - francês
        pt - português
        """);
        String idioma = leitura.nextLine().toUpperCase();

        List<Livro> livros = livroService.listarLivrosPorIdioma(idioma);
        if (livros.isEmpty()) {
            System.out.println("Não existem livros nesse idioma no banco de dados.");
            return;
        }

        livros.forEach(livro -> {
            System.out.println("--- LIVRO ---");
            System.out.println("Título: " + livro.getTitulo());
            System.out.println("Autor: " + (livro.getAutor() != null ? livro.getAutor().getNome() : "Indefinido"));
            System.out.println("Idioma: " + livro.getIdioma().getDescricao());
            System.out.println("Número de downloads: " + livro.getNumeroDownloads());
            System.out.println("---");
        });
    }

}