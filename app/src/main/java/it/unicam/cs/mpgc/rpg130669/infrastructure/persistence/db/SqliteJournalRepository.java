package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.db;

import it.unicam.cs.mpgc.rpg130669.domain.repository.JournalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteJournalRepository implements JournalRepository {

    private final Connection conn;

    public SqliteJournalRepository(DatabaseManager db) {
        this.conn = db.getConnection();
    }

    @Override
    public void recordCatch(String playerId, String fishId, int weight) {
        String sql = "INSERT INTO catch_log(player_id, fish_id, weight) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerId);
            ps.setString(2, fishId);
            ps.setInt(3, weight);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio della cattura", e);
        }
    }

    @Override
    public int getCatchCount(String playerId, String fishId) {
        String sql = "SELECT COUNT(*) FROM catch_log WHERE player_id=? AND fish_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerId);
            ps.setString(2, fishId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel conteggio catture", e);
        }
    }

    @Override
    public Optional<Integer> getRecord(String playerId, String fishId) {
        String sql = "SELECT MAX(weight) FROM catch_log WHERE player_id=? AND fish_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerId);
            ps.setString(2, fishId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getObject(1) != null)
                return Optional.of(rs.getInt(1));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del record", e);
        }
    }

    @Override
    public List<String> getDiscoveredFishIds(String playerId) {
        String sql = "SELECT DISTINCT fish_id FROM catch_log WHERE player_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerId);
            ResultSet rs = ps.executeQuery();
            List<String> ids = new ArrayList<>();
            while (rs.next()) ids.add(rs.getString(1));
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero specie scoperte", e);
        }
    }

    @Override
    public boolean hasDiscovered(String playerId, String fishId) {
        return getCatchCount(playerId, fishId) > 0;
    }
}
