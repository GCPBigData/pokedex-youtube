package com.anabneri.pokedex.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "pokemon")
public class Pokemon {

    @Id
    String id;
    String nome;
    String categoria;
    String habilidade;
    Double peso;

 }
