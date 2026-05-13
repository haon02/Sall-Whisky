package Iteration_3;

import Iteration_3.controller.Controller;
import Iteration_3.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ReguleringTest {
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

        produktionslinje = new Iteration_3.model.Produktionslinje(
                new HashMap<>(), new HashMap<>(),
                1000.0, 90, new HashSet<>(), 2
        );
        destillat = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);
    }

    @Test
    void kanIkkeTageMereEndFadMængde() {
        assertThrows(IllegalArgumentException.class,
                () -> fad.createRegulering(FAD_STØRRELSE + 1,40,40,40));
    }
}
