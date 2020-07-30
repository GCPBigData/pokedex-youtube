package com.anabneri.pokedex.controller;

import com.anabneri.pokedex.model.Pokemon;
import com.anabneri.pokedex.model.PokemonEvent;
import com.anabneri.pokedex.repository.PokedexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(value = "pokemons")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PokemonController {

    private PokedexRepository pokedexRepository;
    public PokemonController(PokedexRepository repository) { this.pokedexRepository = repository; }

    @GetMapping
    public Flux<Pokemon> getAllPokemons() {return pokedexRepository.findAll();}

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Pokemon>> getPokemon(@PathVariable String id) {
        return pokedexRepository.findById(id)
                .map(pokemon -> ResponseEntity.ok(pokemon))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Pokemon> savePokemon(@RequestBody Pokemon pokemon) {
        return pokedexRepository.save(pokemon);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Pokemon>> updatePokemon(@PathVariable(value="id")
                                                       String id,
                                                       @RequestBody Pokemon pokemon) {
        return pokedexRepository.findById(id)
                .flatMap(existingPokemon -> {
                    existingPokemon.setNome(pokemon.getNome());
                    existingPokemon.setCategoria(pokemon.getCategoria());
                    existingPokemon.setHabilidade(pokemon.getHabilidade());
                    existingPokemon.setPeso(pokemon.getPeso());
                    return pokedexRepository.save(existingPokemon);
                })
                .map(updatePokemon -> ResponseEntity.ok(updatePokemon))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deletePokemon(@PathVariable(value = "id") String id) {
        return pokedexRepository.findById(id)
                .flatMap(existingPokemon -> pokedexRepository.delete(existingPokemon)
                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                        )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllPokemons() {
        return pokedexRepository.deleteAll();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PokemonEvent> getPokemonEvents() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(val ->
                        new PokemonEvent(val, "Evento de pokemonssss")
                );
    }
}
