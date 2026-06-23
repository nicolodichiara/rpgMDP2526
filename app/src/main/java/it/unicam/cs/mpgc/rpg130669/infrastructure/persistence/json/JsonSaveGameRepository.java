package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg130669.domain.model.item.Item;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.GameMap;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.Position;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Player;
import it.unicam.cs.mpgc.rpg130669.domain.model.player.Stat;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.TimeOfDay;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.Weather;
import it.unicam.cs.mpgc.rpg130669.domain.model.world.WorldClock;
import it.unicam.cs.mpgc.rpg130669.domain.repository.MapRepository;
import it.unicam.cs.mpgc.rpg130669.domain.repository.SaveGameRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
  Persiste lo stato di gioco in JSON usando Gson.
  GameMap NON viene serializzata — viene ricaricata dall'XmlMapRepository
  usando il levelId salvato nel file del clock.
*/
public class JsonSaveGameRepository implements SaveGameRepository {

    // ── DTO — rappresentazione persistita, separata dal domain model ─────────

    private record PlayerDto(
            String              id,
            String              name,
            Position            position,
            Map<Stat, Integer>  stats,
            Map<Stat, Integer>  xpPerStat,
            List<ItemStackDto>  inventory
    ) {}

    private record ItemStackDto(Item item, int quantity) {}

    private record ClockDto(int day, TimeOfDay timeOfDay, Weather weather, int currentMapLevelId) {}

    // ──────────────────────────────────────────────────────────────────────────

    private final Gson          gson;
    private final Path          saveDir;
    private final MapRepository mapRepository;

    public JsonSaveGameRepository(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Item.class, new ItemTypeAdapter())
                .setPrettyPrinting()
                .create();
        this.saveDir = Path.of(System.getProperty("user.home"), ".fishingrpg", "saves");
        try {
            Files.createDirectories(saveDir);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare la directory saves", e);
        }
    }

    @Override
    public void save(Player player, GameMap currentMap, WorldClock clock) {
        try {
            PlayerDto playerDto = toPlayerDto(player);
            try (var writer = Files.newBufferedWriter(playerPath(player.getId()))) {
                gson.toJson(playerDto, writer);
            }

            ClockDto clockDto = new ClockDto(
                    clock.getDay(), clock.getTimeOfDay(), clock.getWeather(),
                    currentMap.getLevelId());
            try (var writer = Files.newBufferedWriter(clockPath(player.getId()))) {
                gson.toJson(clockDto, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore nel salvataggio JSON", e);
        }
    }

    @Override
    public Optional<SaveGameSnapshot> load(String playerId) {
        if (!hasSave(playerId)) return Optional.empty();
        try {
            PlayerDto playerDto;
            try (var reader = Files.newBufferedReader(playerPath(playerId))) {
                playerDto = gson.fromJson(reader, PlayerDto.class);
            }
            ClockDto clockDto;
            try (var reader = Files.newBufferedReader(clockPath(playerId))) {
                clockDto = gson.fromJson(reader, ClockDto.class);
            }

            Player     player = fromPlayerDto(playerDto);
            WorldClock clock  = new WorldClock(clockDto.day(), clockDto.weather(), clockDto.timeOfDay());
            GameMap    map    = mapRepository.loadById(clockDto.currentMapLevelId())
                    .orElseThrow(() -> new RuntimeException(
                            "Mappa " + clockDto.currentMapLevelId() + " non trovata"));

            return Optional.of(new SaveGameSnapshot(player, map, clock));
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del salvataggio", e);
        }
    }

    @Override
    public boolean hasSave(String playerId) {
        return Files.exists(playerPath(playerId)) && Files.exists(clockPath(playerId));
    }

    @Override
    public void deleteSave(String playerId) {
        try {
            Files.deleteIfExists(playerPath(playerId));
            Files.deleteIfExists(clockPath(playerId));
        } catch (IOException e) {
            throw new RuntimeException("Errore nella cancellazione del salvataggio", e);
        }
    }

    // ── mapping domain ↔ DTO ─────────────────────────────────────────────────

    private PlayerDto toPlayerDto(Player player) {
        List<ItemStackDto> items = new ArrayList<>();
        var inventory = player.getInventory();
        for (Item item : inventory.getItemSet())
            items.add(new ItemStackDto(item, inventory.getQuantity(item)));

        return new PlayerDto(
                player.getId(), player.getName(), player.getPosition(),
                player.getAllStats(), extractXp(player), items);
    }

    private Map<Stat, Integer> extractXp(Player player) {
        Map<Stat, Integer> xp = new EnumMap<>(Stat.class);
        for (Stat s : Stat.values()) xp.put(s, player.getXp(s));
        return xp;
    }

    private Player fromPlayerDto(PlayerDto dto) {
        Player player = new Player(dto.id(), dto.name(), dto.position(),
                dto.stats(), dto.xpPerStat());
        for (ItemStackDto stack : dto.inventory())
            player.getInventory().add(stack.item(), stack.quantity());
        return player;
    }

    private Path playerPath(String id) { return saveDir.resolve("player_" + id + ".json"); }
    private Path clockPath(String id)  { return saveDir.resolve("clock_"  + id + ".json"); }
}
