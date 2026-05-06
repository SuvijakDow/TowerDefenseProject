package logic;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DamageTextTest {

    @Test
    void updateMovesUpAndFadesOverTime() {
        DamageText damageText = new DamageText("10", 100, 100, Color.WHITE);

        boolean removed = damageText.update(0.4);

        assertFalse(removed);
        assertEquals(88.0, damageText.getY());
        assertEquals(0.5, damageText.getOpacity(), 1e-9);
    }

    @Test
    void updateExpiresAfterLifetime() {
        DamageText damageText = new DamageText("10", 0, 0, Color.WHITE);

        assertTrue(damageText.update(0.8));
        assertEquals(0.0, damageText.getOpacity());
    }

    @Test
    void setOpacityClampsToZeroAndOne() {
        DamageText damageText = new DamageText("10", 0, 0, Color.WHITE);

        damageText.setOpacity(-1.0);
        assertEquals(0.0, damageText.getOpacity());

        damageText.setOpacity(2.0);
        assertEquals(1.0, damageText.getOpacity());
    }
}
