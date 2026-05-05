package Iteration1;

import Iteration1.model.Gær;
import Iteration1.model.Korn;
import Iteration1.model.Medarbejder;
import Iteration1.model.Produktionslinje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProduktionslinjeTest {

    Korn korn;
    Gær gær;
    Medarbejder medarbejder;
    HashSet<Medarbejder> medarbejdere;
    Produktionslinje produktionslinje1;

    @BeforeEach
    void setUp() {
        korn = new Korn("Byg", "mark 67", "Et fyldig og god sommerfrisk basse", 2024, "Arla",true);
        gær = new Gær("Saccharomyces cerevisiae", "35", true,15,18,50,"Hej");
        medarbejder = new Medarbejder("Rodi", "Gellerupparken 1", "12345678");
        produktionslinje1 = new Produktionslinje(new HashMap<>(), new HashMap<>(), 0.00, 60, medarbejdere);
        medarbejdere.add(medarbejder);
        produktionslinje1.addKorn(korn, 0.00);
        produktionslinje1.addGær(gær, 0.00);
    }

    @Test
    void test_tilføjMedarbejderTilProduktionslinje() {
        //assign
        Medarbejder medarbejder2 = new Medarbejder("Henrik", "Kelvingade 176", "34354433");
        // act
        produktionslinje1.addMedarbejder(medarbejder2);

        // assign
        Set expected = new HashSet<>();
        expected.add(medarbejder);
        expected.add(medarbejder2);
        Set actual = produktionslinje1.getMedarbejderSet();
        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }

    @Test
    void test_ProduktlinjeIdOptælling() {
        //arrange
        //act
        Produktionslinje produktionslinje2 = new Produktionslinje(new HashMap<>(), new HashMap<>(), 0.00, 60, medarbejdere);
        Produktionslinje produktionslinje3 = new Produktionslinje(new HashMap<>(), new HashMap<>(), 0.00, 60, medarbejdere);
        //assert

        assertEquals(1,produktionslinje1.getIdProduktionslinje());
        assertEquals(2, produktionslinje2.getIdProduktionslinje());
        assertEquals(3, produktionslinje3.getIdProduktionslinje());
    }

}