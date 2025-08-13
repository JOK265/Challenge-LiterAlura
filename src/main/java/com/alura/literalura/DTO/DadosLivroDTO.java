package com.alura.literalura.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivroDTO(
        String title,
        @JsonAlias("authors") List<AutorDTO> authors,
        @JsonAlias("languages") List<String> languages,
        @JsonAlias("download_count") Double download_count
) {}
