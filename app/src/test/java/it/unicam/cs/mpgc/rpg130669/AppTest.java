package it.unicam.cs.mpgc.rpg130669;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    
    @Test
    void testAppExists() {
        // Test semplice per verificare che la classe App esista
        assertNotNull(App.class, "La classe App dovrebbe esistere");
    }
    
    @Test
    void testMainMethodExists() {
        // Test per verificare che il metodo main esista
        assertDoesNotThrow(() -> {
            App.class.getDeclaredMethod("main", String[].class);
        }, "Il metodo main dovrebbe esistere");
    }
}