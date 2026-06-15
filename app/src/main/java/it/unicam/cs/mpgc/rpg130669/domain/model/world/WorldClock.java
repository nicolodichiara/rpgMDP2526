package it.unicam.cs.mpgc.rpg130669.domain.model.world;

import java.util.Objects;

/**
 * Orologio del mondo di gioco. Mantiene giorno, fascia oraria e meteo correnti.
 * Persiste su JSON nel savegame.
 *
 * Il meteo è determinato esternamente (da WorldClockService nel layer application)
 * tramite un seed casuale — WorldClock si limita a tenerlo in stato.
 */

public class WorldClock {
    private TimeOfDay timeOfDay;
    private Weather   weather;
    private int       day;

    public WorldClock(int day, Weather weather, TimeOfDay timeOfDay){
        if (day <= 0) throw new IllegalArgumentException("il giorno non può essere minore o uguale a 0");
        this.day = day;
        this.weather = Objects.requireNonNull(weather, "oggetto weather non valido");
        this.timeOfDay = Objects.requireNonNull(timeOfDay, "oggetto timeOfDay non valido");
    }

    public static WorldClock newGame(){
        return new WorldClock(1, Weather.CLEAR, TimeOfDay.DAWN);
    }

    public void Advance(){
        TimeOfDay next = timeOfDay.next();
        if (next == TimeOfDay.DAWN) this.day++;
        timeOfDay = next;
    }

    public void setWeather(Weather weather){
        this.weather = Objects.requireNonNull(weather, "il weather non può essere null");
    }

    // parte di codice scritta con ai
    public int       getDay()       { return day;       }
    public TimeOfDay getTimeOfDay() { return timeOfDay; }
    public Weather   getWeather()   { return weather;   }
    @Override
    public String toString() {
        return "Giorno " + day + " — " + timeOfDay.getCode() + " (" + weather.getCode() + ")";
    }
    public boolean isNight()      { return timeOfDay == TimeOfDay.NIGHT; }

    public boolean isMapAccessible() { return weather.isMapAccessible(); }
}
