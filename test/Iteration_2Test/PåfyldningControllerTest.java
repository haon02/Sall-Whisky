package Iteration_2Test;

import Iteration_3.controller.Controller;
import Iteration_3.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC5 – Påfyldning af fad
 *
 * Flow:
 *  Trin 1 – vælgFad()         : find et tomt fad
 *  Trin 1b – fjernFraLager()  : tag fadet af hylden (separat ansvar)
 *  Trin 2 – tilføjDestillat() : påfyld atomisk
 *  Trin 3 – sætPåLager()      : sæt fadet tilbage
 */
class PåfyldningControllerTest {

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private static final double RENT_DESTILLAT  = 300.0;
    private static final double VAND_TILFØJET   = 200.0;
    private static final double TOTAL_BEHOLDNING = RENT_DESTILLAT + VAND_TILFØJET; // 500.0
    private static final double FAD_STØRRELSE   = 200.0;

    private Controller       controller;
    private Destillat        destillat;
    private Fad              fad;
    private Lager            lager;
    private Reol             reol;
    private Hylde            hylde;
    private Leverandør       leverandør;
    private DestillatType    destillatType;
    private Produktionslinje produktionslinje;

    @BeforeEach
    void setUp() {
        controller = new Controller();

        lager = new Lager("Testvej 1");
        reol  = lager.createReol();
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
        destillat     = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);
        destillatType = new SingleCask(100, destillat);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 1 – Vælg fad
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 1: vælgFad returnerer det tomme fad")
    void vælgFad_returnerer_tomt_fad() {
        Fad valgt = controller.vælgFad(lager);
        assertSame(fad, valgt);
    }

    @Test
    @DisplayName("Trin 1: vælgFad fjerner IKKE lager-referencen – det gør fjernFraLager()")
    void vælgFad_fjerner_ikke_lager_reference_selv() {
        // vælgFad is now a pure read — it does NOT set lager=null as a side effect.
        // Call fjernFraLager() explicitly if you want to take the fad off the shelf.
        controller.vælgFad(lager);
        assertNotNull(fad.getLager(), "vælgFad must not touch the lager reference");
    }

    @Test
    @DisplayName("Trin 1b: fjernFraLager sætter lager-reference til null")
    void fjernFraLager_nulstiller_lager_reference() {
        controller.fjernFraLager(fad);
        assertNull(fad.getLager());
    }

    @Test
    @DisplayName("Trin 1 – alt: ingen tomme fade → IllegalStateException")
    void vælgFad_ingen_tomme_fade_giver_exception() {
        Lager tomtLager = new Lager("Tomt lager");
        assertThrows(IllegalStateException.class,
                () -> controller.vælgFad(tomtLager));
    }

    @Test
    @DisplayName("Trin 1 – alt: kun fyldte fade → IllegalStateException fra vælgFad")
    void vælgFad_kun_fyldte_fade_giver_exception() {
        // Fill the fad so it is no longer empty
        controller.påfyldFad(fad, destillat, null,150.0);

        // Now vælgFad should find no empty fad in the lager
        assertThrows(IllegalStateException.class,
                () -> controller.vælgFad(lager));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 2 – Tilføj destillat (atomisk)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 2: tilføjDestillat reducerer destillat korrekt")
    void påfyldning_reducerer_destillat_korrekt() {
        double restmængde = controller.påfyldFad(fad, destillat, null, 150.0);
        assertEquals(TOTAL_BEHOLDNING - 150.0, restmængde, 1e-9);
    }

    @Test
    @DisplayName("Trin 2: fad er ikke tomt efter påfyldning")
    void påfyldning_fad_markeres_som_fyldt() {
        controller.påfyldFad(fad, destillat, null, 150.0);
        assertFalse(fad.erTom());
    }

    @Test
    @DisplayName("Trin 2: massebalance – påfyldt + restmængde = original beholdning")
    void påfyldning_massebalance_er_bevaret() {
        double påfyldt    = 150.0;
        double restmængde = controller.påfyldFad(fad, destillat, null, påfyldt);
        assertEquals(TOTAL_BEHOLDNING, påfyldt + restmængde, 1e-9);
    }

    @Test
    @DisplayName("Trin 2 – edge: præcis fadets størrelse er lovlig")
    void påfyldning_præcis_max_er_gyldig() {
        assertDoesNotThrow(
                () -> controller.påfyldFad(fad, destillat, null, FAD_STØRRELSE));
    }

    @Test
    @DisplayName("Trin 2 – alt: mængde over fadets størrelse → IllegalArgumentException")
    void påfyldning_over_kapacitet_giver_exception() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.påfyldFad(fad, destillat, null, FAD_STØRRELSE + 0.001));
    }

    @Test
    @DisplayName("Trin 2 – alt: kapacitetsfejl ændrer hverken fad eller destillat (atomicitet)")
    void kapacitetsfejl_ændrer_hverken_fad_eller_destillat() {
        double beholdningFør = destillat.getResterendeMængde();

        assertThrows(IllegalArgumentException.class,
                () -> controller.påfyldFad(fad, destillat, null, FAD_STØRRELSE + 1));

        // FIX: we now also verify the destillat was NOT reduced — this was the
        // silent data-corruption bug in the original code.
        assertTrue(fad.erTom(),               "Fadet skal stadig være tomt");
        assertEquals(beholdningFør, destillat.getResterendeMængde(), 1e-9,
                "Destillat må ikke være reduceret når påfyldning fejler");
    }

    @Test
    @DisplayName("Trin 2 – alt: allerede fyldt fad kaster IllegalArgumentException")
    void påfyldning_af_fyldt_fad_kaster_exception() {
        fad.fyldFad(destillat, null,1); // mark the fad as full

        assertThrows(IllegalArgumentException.class,
                () -> controller.påfyldFad(fad, destillat, null, 100.0));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TRIN 3 – Sæt fad på lager
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Trin 3: fad peger på det korrekte lager efter returnering")
    void sætPåLager_lager_reference_sat() {
        controller.fjernFraLager(fad);
        controller.påfyldFad(fad, destillat, null, 100.0);
        controller.sætPåLager(lager, fad);

        assertSame(lager, fad.getLager());
    }

    @Test
    @DisplayName("Trin 3 – alt: fuld hylde → IllegalStateException fra sætPåLager")
    void sætPåLager_fuld_lager_giver_exception() {
        // Create a new lager with zero slots so it is immediately full
        Lager fuldtLager = new Lager("Fuldt lager");
        fuldtLager.createReol().createHylde(0);

        controller.fjernFraLager(fad);
        controller.påfyldFad(fad, destillat, null, 100.0);

        // FIX: sætPåLager now throws instead of silently printing to stdout.
        // The test therefore uses the correct specific exception type.
        assertThrows(IllegalStateException.class,
                () -> controller.sætPåLager(fuldtLager, fad));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // FULDT FLOW – end-to-end
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Fuldt flow: fad valgt, fjernet fra hylde, påfyldt og sat tilbage")
    void fuldtFlow_korrekt_sluttilstand() {
        double påfyldt = 120.0;

        // Trin 1 – find tomt fad
        Fad valgt = controller.vælgFad(lager);

        // Trin 1b – fjern fra hylde (explicit, not a hidden side effect)
        controller.fjernFraLager(valgt);
        assertNull(valgt.getLager(), "Fad skal ikke have lager-reference mens det påfyldes");

        // Trin 2 – påfyld atomisk
        double restmængde = controller.påfyldFad(valgt, destillat, null, påfyldt);

        // Trin 3 – sæt tilbage
        controller.sætPåLager(lager, valgt);

        assertAll("Slutkontrol",
                () -> assertFalse(valgt.erTom(),                         "Fad skal ikke være tomt"),
                () -> assertSame(lager, valgt.getLager(),                 "Fad skal pege på lageret"),
                () -> assertSame(destillatType, valgt.getDestillat(), "Fad skal have korrekt destillatType"),
                () -> assertEquals(TOTAL_BEHOLDNING - påfyldt, restmængde, 1e-9, "Restmængde forkert"),
                () -> assertEquals(TOTAL_BEHOLDNING, påfyldt + restmængde, 1e-9,  "Massebalance holder ikke")
        );
    }
}