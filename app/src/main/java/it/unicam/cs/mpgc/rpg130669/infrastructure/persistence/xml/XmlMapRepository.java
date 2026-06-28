package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.xml;

import it.unicam.cs.mpgc.rpg130669.domain.model.fish.*;
import it.unicam.cs.mpgc.rpg130669.domain.model.map.*;
import it.unicam.cs.mpgc.rpg130669.domain.repository.MapRepository;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

/**
 * Loads GameMap from an XML file within the classpath.
 * Each map is a separate file: maps/map_001.xml, maps/map_002.xml, ...
 * Maps are immutable — they are cached after the first load.
 */
public class XmlMapRepository implements MapRepository {

    private static final String MAPS_PATH = "/it/unicam/cs/mpgc/rpg130669/data/maps/";
    private final Map<Integer, GameMap> cache = new HashMap<>();
    private final Map<String, FishTemplate> fishTemplates;

    public XmlMapRepository(Map<String, FishTemplate> fishTemplates) {
        this.fishTemplates = fishTemplates;
    }

    @Override
    public Optional<GameMap> loadById(int levelId) {
        if (cache.containsKey(levelId))
            return Optional.of(cache.get(levelId));
        try {
            String path = MAPS_PATH + "map_" + String.format("%03d", levelId) + ".xml";
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) return Optional.empty();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new XmlErrorHandler());
            Document doc = builder.parse(is);

            GameMap map   = parseMap(doc.getDocumentElement());
            cache.put(levelId, map);
            return Optional.of(map);
        } catch (Exception e) {
            throw new RuntimeException("Errore nel parsing della mappa " + levelId, e);
        }
    }

    @Override
    public List<GameMap> loadAll() {
        List<GameMap> maps = new ArrayList<>();
        for (int i = 1; i <= 99; i++)
            loadById(i).ifPresent(maps::add);
        return maps;
    }

    // parsing

    private GameMap parseMap(Element root) {
        int    levelId       = Integer.parseInt(root.getAttribute("id"));
        String name          = root.getAttribute("name");
        int    requiredLevel = Integer.parseInt(root.getAttribute("requiredLevel"));

        TileGrid grid        = parseGrid(getChild(root, "grid"));
        List<SpawnZone> zones = parseSpawnZones(getChild(root, "spawnZones"));

        return new GameMap(levelId, name, grid, zones, requiredLevel);
    }

    private TileGrid parseGrid(Element gridEl) {
        int rows = Integer.parseInt(gridEl.getAttribute("rows"));
        int cols = Integer.parseInt(gridEl.getAttribute("cols"));

        Tile[][] tiles = new Tile[rows][cols];
        NodeList rowNodes = gridEl.getElementsByTagName("row");
        for (int r = 0; r < rowNodes.getLength(); r++) {
            String[] codes = rowNodes.item(r).getTextContent().trim().split(",");
            for (int c = 0; c < codes.length; c++)
                tiles[r][c] = new Tile(TileType.fromCode(codes[c].trim()));
        }
        return new TileGrid(tiles);
    }

    private List<SpawnZone> parseSpawnZones(Element zonesEl) {
        List<SpawnZone> zones = new ArrayList<>();
        if (zonesEl == null) return zones;

        NodeList zoneNodes = zonesEl.getElementsByTagName("zone");
        for (int i = 0; i < zoneNodes.getLength(); i++)
            zones.add(parseZone((Element) zoneNodes.item(i)));
        return zones;
    }

    private SpawnZone parseZone(Element zoneEl) {
        Position topLeft     = parsePosition(zoneEl.getAttribute("topLeft"));
        Position bottomRight = parsePosition(zoneEl.getAttribute("bottomRight"));
        int maxFish          = Integer.parseInt(zoneEl.getAttribute("maxFish"));

        List<SpawnZone.WeightedFish> pool = new ArrayList<>();
        NodeList fishNodes = zoneEl.getElementsByTagName("fish");
        for (int i = 0; i < fishNodes.getLength(); i++) {
            Element fishEl    = (Element) fishNodes.item(i);
            String  templateId = fishEl.getAttribute("template");
            float   weight    = Float.parseFloat(fishEl.getAttribute("weight"));
            FishTemplate template = fishTemplates.get(templateId);
            if (template == null)
                throw new RuntimeException("FishTemplate non trovato: " + templateId);
            pool.add(new SpawnZone.WeightedFish(template, weight));
        }
        return new SpawnZone(topLeft, bottomRight, pool, maxFish);
    }

    /** Formato atteso: "row,col" — es. "2,3" */
    private Position parsePosition(String raw) {
        String[] parts = raw.split(",");
        return new Position(Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()));
    }

    private Element getChild(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? (Element) nl.item(0) : null;
    }
}
