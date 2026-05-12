package Iteration_2;

import Iteration_2.controller.Controller;
import Iteration_2.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC5 – Påfyldning af fad
 * <p>
 * Lager-hierarki: Lager → Reol → Hylde → Fad[]
 * <p>
 * UC4 er IKKE en klasse – det er blot logikken der:
 * - trin 1: fjerner fadet fra sin hylde (sætter plads til null)
 * - trin 3: placerer fadet på en hylde igen
 * Det udtrykkes her som to private hjælpemetoder i controlleren.
 */
class PåfyldningControllerTest {
    // ── Fixtures ──────────────────────────────────────────────────────────────

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
    private DestillatType destillatType;
    private Produktionslinje produktionslinje;

    @BeforeEach
    void setUp() {
        controller = new Controller();

        // Lager-hierarki: Lager → Reol → Hylde (3 pladser)
        lager = new Lager("Testvej 1");
        reol = lager.createReol();
        hylde = reol.createHylde(3);

        // Leverandør til Fad-konstruktøren
        leverandør = new Leverandør("Jens", 1232, "Ham der den høje fra novo");

        // Tomt fad placeret på hylden
        fad = new Fad(
                FAD_STØRRELSE,
                LocalDate.of(2020, 6, 1),
                "Bourbon-fad",
                true,       // erTom = true
                true,       // tidligereBrugt
                leverandør,
                lager
        );
        hylde.addFad(fad);

        // Destillat oprettet via Produktionslinje
        produktionslinje = new Produktionslinje(
                new HashMap<>(),
                new HashMap<>(),
                1000.0,
                90,
                new HashSet<>(),
                2
        );
        destillat = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);
        destillatType = new SingleCask(100, destillat);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 1 – Vælg fad (UC4: fjern fra lager)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 1: vælgFad returnerer det tomme fad")
    void vælgFad_returnerer_tomt_fad() {
        //assign - setup

        // act
        Fad valgt = controller.vælgFad(lager);

        // assert
        assertSame(fad, valgt);
    }

    @Test
    @DisplayName("Trin 1: fad har ikke længere lager-reference efter valg (UC4 fjerner det)")
    void vælgFad_fjerner_lager_reference() {
        // assign - setup

        // act
        controller.vælgFad(lager);

        //assert
        assertNull(fad.getLager(), "Fadet skal ikke pege på et lager mens det påfyldes");
    }

    @Test
    @DisplayName("Trin 1 – alt: ingen tomme fade → IllegalStateException")
    void vælgFad_ingen_tomme_fade_giver_exception() {
        // assign
        Lager tomtLager = new Lager("Tomt lager");
        // act

        // assert
        assertThrows(IllegalStateException.class,
                () -> controller.vælgFad(tomtLager));
    }

    @Test
    @DisplayName("Trin 1 – alt: kun fyldte fade → IllegalStateException")
    void vælgFad_kun_fyldte_fade_giver_exception() {
        // Fyld fadet manuelt så det ikke er tomt
        // assign
        double mængde = 150;
        //act
        controller.fyldFad(fad, destillatType, mængde);

        // assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.fyldFad(fad, destillatType, mængde));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 2 – Tilføj destillat (sammenhørende mængder)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 2: reducer() returnerer korrekt restmængde")
    void påfyldning_reducerer_destillat_korrekt() {
        // assign
        double restmængde = controller.tilføjDestillat(fad, destillat, destillatType, 150.0);

        // act & assert
        assertEquals(TOTAL_BEHOLDNING - 150.0, restmængde, 1e-9);
    }

    @Test
    @DisplayName("Trin 2: fad er ikke tomt efter påfyldning")
    void påfyldning_fad_markeres_som_fyldt() {
        // assert

        // act
        controller.tilføjDestillat(fad, destillat, destillatType, 150.0);

        // assert
        assertFalse(fad.erTom());
    }

    @Test
    @DisplayName("Trin 2: massebalance – fad-mængde + restmængde = original beholdning")
    void påfyldning_massebalance_er_bevaret() {
        // assign
        double påfyldt = 150.0;
        double restmængde = controller.tilføjDestillat(fad, destillat, destillatType, påfyldt);

        //act & assert
        assertEquals(TOTAL_BEHOLDNING, påfyldt + restmængde, 1e-9,
                "Påfyldt mængde + restmængde skal svare til original beholdning");
    }

    @Test
    @DisplayName("Trin 2 – edge: præcis fadets størrelse er lovlig")
    void påfyldning_præcis_max_er_gyldig() {
        // test af edgecase
        assertDoesNotThrow(
                () -> controller.tilføjDestillat(fad, destillat, destillatType, FAD_STØRRELSE));
    }

    @Test
    @DisplayName("Trin 2 – alt: mængde over fadets størrelse → IllegalArgumentException")
    void påfyldning_over_kapacitet_giver_exception() {
        // assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.tilføjDestillat(fad, destillat, destillatType, FAD_STØRRELSE + 0.001));
    }

    @Test
    @DisplayName("Trin 2 – alt: ved kapacitetsfejl forbliver fadet tomt")
    void kapacitetsfejl_ændrer_ikke_fad() {
        // assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.tilføjDestillat(fad, destillat, destillatType, FAD_STØRRELSE + 1));

        assertTrue(fad.erTom(), "Fadet skal stadig være tomt når påfyldning afvises");
    }

    @Test
    @DisplayName("Trin 2 – alt: allerede fyldt fad kaster exception")
    void påfyldning_af_fyldt_fad_kaster_exception() {
        // assert

        //act
        fad.fyldFad(destillatType, 1); // fyld fadet manuelt

        // assert
        // fyldFad() kaster selv IllegalArgumentException når fadet ikke er tomt
        assertThrows(IllegalArgumentException.class,
                () -> controller.tilføjDestillat(fad, destillat, destillatType, 100.0));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 3 – Sæt fad på lager (UC4: tildel plads)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 3: fad peger på det korrekte lager efter returnering")
    void sætPåLager_lager_reference_sat() {
        // assign
        // act
        controller.vælgFad(lager);
        controller.tilføjDestillat(fad, destillat, destillatType, 100.0);
        controller.sætPåLager(lager,fad);

        //assert
        assertSame(lager, fad.getLager());
    }

    @Test
    @DisplayName("Trin 3 – alt: fuld hylde → exception fra Hylde.placerFad()")
    void sætPåLager_fuld_hylde_giver_exception() {
        // assign
        // Opret en hylde med 0 pladser
        Hylde fuldHylde = reol.createHylde(0);

        // act
        controller.vælgFad(lager);
        controller.tilføjDestillat(fad, destillat, destillatType, 100.0);

        //assert
       assertThrows(Exception.class,
               () -> controller.sætPåLager(lager, fad));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // FULDT FLOW – end-to-end
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Fuldt flow: fad valgt, påfyldt og sat tilbage på lager")
    void fuldtFlow_korrekt_sluttilstand() {
        double påfyldt = 120.0;

        // Trin 1 – UC4: fjern fra lager
        Fad valgt = controller.vælgFad(lager);
        assertNull(valgt.getLager(), "Fad skal ikke have lager-reference mens det påfyldes");

        // Trin 2 – påfyld
        double restmængde = controller.tilføjDestillat(valgt, destillat, destillatType, påfyldt);

        // Trin 3 – UC4: sæt på lager igen
       controller.sætPåLager(lager, valgt);

        assertAll("Slutkontrol",
                () -> assertFalse(valgt.erTom(), "Fad skal ikke være tomt"),
                () -> assertSame(lager, valgt.getLager(), "Fad skal pege på lageret"),
                () -> assertSame(destillatType, valgt.getDestillatType(), "Fad skal have korrekt destillatType"),
                () -> assertEquals(TOTAL_BEHOLDNING - påfyldt, restmængde, 1e-9,
                        "Restmængde skal svare til beholdning minus påfyldt"),
                () -> assertEquals(TOTAL_BEHOLDNING, påfyldt + restmængde, 1e-9,
                        "Massebalance skal holde")
        );
    }
}