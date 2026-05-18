package Iteration_3;

import Iteration_5.controller.Controller;
import Iteration_5.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FlaskeTest {
    private static final double RENT_DESTILLAT = 300.0;
    private static final double VAND_TILFØJET = 200.0;
    private static final double TOTAL_BEHOLDNING = RENT_DESTILLAT + VAND_TILFØJET; // 500.0
    private static final double FAD_STØRRELSE = 200.0;

    private Controller controller;
    private Destillat destillat;
    private Fad fad;
    private Lager lager;
    private Reol reol;
    private Hylde hylde;
    private Leverandør leverandør;
    private Produktionslinje produktionslinje;
    private double FAD_MÆNGDE = 100;
    private double VAND_MÆNGDE = 75;
    private Regulering regulering;

    @BeforeEach
    void setUp() {
        controller = new Controller();

        lager = new Lager("Testvej 1");
        reol = lager.createReol();
        hylde = reol.createHylde(3);
        leverandør = new Leverandør("Jens", 1232, "Ham der den høje fra novo");

        fad = new Fad(
                FAD_STØRRELSE,
                LocalDate.of(2020, 6, 1),
                "Bourbon-fad",
                true,   // erTom
                true,   // tidligereBrugt
                leverandør,
                lager
        );
        hylde.addFad(fad);

        produktionslinje = new Produktionslinje(
                new HashMap<>(), new HashMap<>(),
                1000.0, 90, new HashSet<>(), 2
        );
        destillat = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);
        fad.fyldFad(destillat,LocalDate.now(), FAD_MÆNGDE);
        regulering = fad.createRegulering(FAD_MÆNGDE, 70, VAND_MÆNGDE, 40);
    }

    @Test
    void flaskeAlleredeFyldt() {
        Flaske flaske = new Flaske("2020 collection", 0.75, false, regulering);
        assertThrows(IllegalArgumentException.class,
                () -> flaske.fyldFlaske(regulering));
    }

    @Test
    void flaskeIkkeFyldt() {
        Flaske flaske = new Flaske("2020 collection", 0.75, true, regulering);
        flaske.fyldFlaske(regulering);

        double expected = FAD_MÆNGDE + VAND_MÆNGDE - 0.75;
        double actual = regulering.getTotalMængde();

        assertEquals(expected,actual);
        assertFalse(flaske.erTom());
    }

    @Test
    void flereFlaskerEndMuligt() {
        Flaske flaske = new Flaske("2020 collection", regulering.getTotalMængde(), true, regulering);
        Flaske flaske2 = new Flaske("2020 collection", 1, true, regulering);

        flaske.fyldFlaske(regulering);

        assertThrows(IllegalArgumentException.class,
                () -> flaske2.fyldFlaske(regulering));
    }
}
