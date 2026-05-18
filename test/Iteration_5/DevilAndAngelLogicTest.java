package Iteration_5;

import Iteration_5.controller.Controller;
import Iteration_5.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class DevilAndAngelLogicTest {

    private static final double RENT_DESTILLAT = 100;
    private static final double VAND_TILFØJET = 100;
    private static final double TOTAL_BEHOLDNING = RENT_DESTILLAT + VAND_TILFØJET; // 200
    private static final double FAD_STØRRELSE = 200;

    private Controller controller;
    private Destillat destillat;
    private Destillat destillat2;
    private Fad fad;
    private Leverandør leverandør;
    private Produktionslinje produktionslinje;
    private Produktionslinje produktionslinje2;
    private Lager lager;
    private double FAD_MÆNGDE = 100;

    @BeforeEach
    void setUp() {
        controller = new Controller();
        leverandør = new Leverandør("Jens", 1232, "Ham der den høje fra novo");
        lager = new Lager("PetersvejLager");

        produktionslinje = new Produktionslinje(new HashMap<>(), new HashMap<>(), 1000.0, 90, new HashSet<>(), 2);
        destillat = produktionslinje.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 63.5);

        produktionslinje2 = new Produktionslinje(new HashMap<>(), new HashMap<>(), 1000.0, 90, new HashSet<>(), 3);
        destillat2 = produktionslinje2.createDestillat(RENT_DESTILLAT, VAND_TILFØJET, 55);
    }

    @Test
    void test_devilCutDag1_tideligereBrugt() {
        //assign
        fad = new Fad(FAD_STØRRELSE, LocalDate.of(2020, 6, 1), "Bourbon-fad", true, true, leverandør, null);
        fad.fyldFad(destillat, LocalDate.now(), FAD_MÆNGDE);
        // act
        double aktuelBeholdning = fad.beregnNuværendeBeholdning();
        // assert
        assertEquals(100.0, aktuelBeholdning, 0.001);
    }

    @Test
    void test_devilCutDag1_ejBrugt() {
        // assign
        fad = new Fad(FAD_STØRRELSE, LocalDate.now(), "Nyt fad", true, false, leverandør, null);
        fad.fyldFad(destillat, LocalDate.now(), FAD_MÆNGDE);
        // act
        double aktuelBeholdning = fad.beregnNuværendeBeholdning();
        // assert
        assertEquals(100.0, aktuelBeholdning, 0.001);
    }

    @Test
    void test_nuværendeBeholdningEfter3År_Brugt() {
        // assign
        fad = new Fad(FAD_STØRRELSE, LocalDate.now().minusYears(5), "Bourbon-fad", true, true, leverandør, lager);
        LocalDate påfyldningsDato = LocalDate.now().minusYears(3);
        fad.fyldFad(destillat, påfyldningsDato, FAD_MÆNGDE);

        double aktuelBeholdning = fad.beregnNuværendeBeholdning();

        //assert
        // År 1 Angel's share: 100 * 0.975 = 97.5L
        // År 2 Angel's share: 97,5L * 0.975 = 95.0625L
        // År 3 Angel's share: 95.0625 * 0.975 = 92.6859L
        double expected = 92.6859;
        assertEquals(expected, aktuelBeholdning, 0.001);
    }

    @Test
    void test_BeregnNuværendeBeholdning_Efter3År_ubrugt() {
        //assign
        fad = new Fad(FAD_STØRRELSE, LocalDate.now().minusYears(5), "Bourbon-fad", true,   // erTom
                false,   // tidligereBrugt
                leverandør, lager);
        LocalDate påfyldningsDato = LocalDate.now().minusYears(3);
        fad.fyldFad(destillat, påfyldningsDato, FAD_MÆNGDE);
        // act
        double aktuelBeholdning = fad.beregnNuværendeBeholdning();

        // assert
        // Matematikken bag (med devil = 2.5% og angel = 2.5% årligt)
        // start: 200L
        // Efter Devil's cut og angelshare (år 1): 100 * 0.950 = 95L
        // År 2 Angel's share: 95 * 0.975 = 92.625L
        // År 3 Angel's share: 92.625L * 0.975 = 90,3093L

        // aka 90,3093L
        double forventetMængde = 100 * 0.95 * 0.975 * 0.975;
        assertEquals(forventetMængde, aktuelBeholdning, 0.001);
    }

    @Test
    void testUdragOgFremtidigBeholdning() {
        // assign
        Fad fad = new Fad(FAD_STØRRELSE, LocalDate.now().minusYears(5), "Bourbon-fad", true, false, leverandør, lager);

        LocalDate oprindeligeDato = LocalDate.now().minusYears(2);
        //Fad mænge = 100
        fad.fyldFad(destillat, oprindeligeDato, FAD_MÆNGDE);

        // act
        // bare så man får præcis 70L i fadet tilbage efter angel og devil sam tapning fra fad.
        fad.aftapFraFad(22.625);
        // nuværende beholdning 70L

        // assert
        assertEquals(70.0, fad.getMængdeDestillatLiter(), 0.001);
        assertEquals(70.0, fad.beregnNuværendeBeholdning(), 0.001);

        // ser 1 år ud i fremtiden
        LocalDate etÅrFrem = LocalDate.now().plusYears(1);
        assertEquals(70*0.975, fad.beregnFremtidigBeholdning(etÅrFrem), 0.001);
        }
    }

