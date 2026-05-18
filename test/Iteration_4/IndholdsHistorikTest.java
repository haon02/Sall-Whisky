package Iteration_4;

import Iteration_5.controller.Controller;
import Iteration_5.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class IndholdsHistorikTest {

    private static final double RENT_DESTILLAT = 3000000.0;
    private static final double VAND_TILFØJET = 2000000.0;
    private static final double TOTAL_BEHOLDNING = RENT_DESTILLAT + VAND_TILFØJET; // 500.0
    private static final double FAD_STØRRELSE = 20000000.0;

    private Controller controller;
    private Destillat destillat;
    private Destillat destillat2;
    private Fad fad;
    private Leverandør leverandør;
    private Produktionslinje produktionslinje;
    private Produktionslinje produktionslinje2;
    private double FAD_MÆNGDE = 1000000;

    @BeforeEach
    void setUp() {
        controller = new Controller();
        leverandør = new Leverandør("Jens", 1232, "Ham der den høje fra novo");
        fad = new Fad(
                FAD_STØRRELSE,
                LocalDate.of(2020, 6, 1),
                "Bourbon-fad",
                true,   // erTom
                true,   // tidligereBrugt
                leverandør,
                null
        );

        produktionslinje = new Produktionslinje(
                new HashMap<>(), new HashMap<>(),
                1000.0, 90, new HashSet<>(), 2
        );
        destillat = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);

        produktionslinje2 = new Produktionslinje(
                new HashMap<>(), new HashMap<>(),
                1000.0, 90, new HashSet<>(), 3
        );
        destillat2 = produktionslinje2.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 55);
    }

    @Test
    void tilføjesTilFad() {
        assertEquals(0, fad.getIndholdshistorik().size());
        Indholdshistorik expected = fad.fyldFad(destillat, LocalDate.of(2002, 2, 3), 80);

        assertEquals(expected, fad.getIndholdshistorik().getFirst());
    }

    @Test
    void sortereIndholdshistorikDato() {
        // assign
        Destillat destillat1 = produktionslinje.createDestillat(100, 100, 65);

        Destillat destillat2 = produktionslinje2.createDestillat(200, 100, 45);
        // act
        Indholdshistorik indholdshistorik1 = fad.fyldFad( destillat1, LocalDate.of(2021, 1, 1), 5);
        fad.tømFad();
        Indholdshistorik indholdshistorik2 = fad.fyldFad(destillat1, LocalDate.of(2025, 1, 1), 4);
        fad.tømFad();
        Indholdshistorik indholdshistorik3 = fad.fyldFad(destillat2, LocalDate.of(2023, 1, 1), 3);
        // assert
        // Forventer sorteret rækkefølge: 2021 → 2023 → 2025

        assertTrue(fad.getIndholdshistorik().get(0).getPåfyldningsDato().isBefore(
                fad.getIndholdshistorik().get(1).getPåfyldningsDato())); // 2021 < 2023
        assertTrue(fad.getIndholdshistorik().get(1).getPåfyldningsDato().isBefore(
                fad.getIndholdshistorik().get(2).getPåfyldningsDato())); // 2023 < 2025
    }

    @Test
    void AflæsHistorikForFlaske() {
        // assign
        Indholdshistorik expected = fad.fyldFad(destillat, LocalDate.of(2001, 1, 1), 80);
        Regulering regulering = fad.createRegulering(10, 70, 10, 40);
        Flaske flaske = new Flaske("Monner", 0.7, false, regulering);
        Destillat destillat2 = produktionslinje.createDestillat(100, 10, 40);

        // act
        fad.tømFad();
        Indholdshistorik expected2 = fad.fyldFad(destillat2, LocalDate.of(2002, 1, 1), 80);

        // assert
        Indholdshistorik actual = flaske.getRegulering().getFad().getIndholdshistorik().get(0);
        Indholdshistorik actual2 = flaske.getRegulering().getFad().getIndholdshistorik().get(1);

        assertEquals(expected, actual);
        assertEquals(expected2, actual2);
    }

}
