package Iteration_5;

import Iteration_5.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UC5FyldfadTest {

    private Destillat destillat;
    private final double destillatLiter = 100;
    private final double vandLiter = 100;
    private Produktionslinje produktionslinje;
    private Leverandør leverandør;
    private Lager lager;
    private Fad fad1;
    private LocalDate testDato = LocalDate.of(2022,1,1);

    @BeforeEach
    void setUp() {
        produktionslinje = new Produktionslinje(null, null, 0, 0, null, 0);
        destillat = produktionslinje.createDestillat(destillatLiter,vandLiter,66);
        leverandør = new Leverandør("Noah",44,"Da goat");
        lager = new Lager("Hej");
        fad1 = new Fad(300, testDato,"Sherry",true,false, leverandør, lager);
    }

    @Test
    @DisplayName("TC1: Fadet er allerede fyldt")
    void test_fadErFyldt() {
        Fad fad = new Fad(100,LocalDate.now(),"Godt fad",false,true,leverandør, lager);
        assertThrows(IllegalArgumentException.class,
                () -> fad.fyldFad(destillat, LocalDate.now(),10));
    }

    //Denne test fejler, da vi ikke har lavet logik der sørger for at fadets produktions dato skal være før påfyldningdsdatoen
    @Test
    @DisplayName("TC2: Påfyldningsdato før fadets produktions dato")
    void test_fyldningsDatoFørOprettelseDato() {
        assertThrows(IllegalArgumentException.class,
                () -> fad1.fyldFad(destillat, testDato.minusYears(2),10));
    }

    @Test
    @DisplayName("TC3: Sunshine")
    void test_tilføjTilTomtFad(){
        fad1.fyldFad(destillat,LocalDate.now(),1);

        assertTrue(!fad1.erTom());
        assertEquals(1, fad1.getMængdeDestillatLiter());
    }

    @Test
    @DisplayName("TC4: Destillat > fadKapacitet EdgeCase")
    void test_tilføjForMegetDestillat(){
        assertThrows(IllegalArgumentException.class,
                () -> fad1.fyldFad(destillat,LocalDate.now(), 301));
    }

    @Test
    @DisplayName("TC5: Destillat = fadKapacitet EdgeCase")
    void test_tilføjLigeTilpasDestillat(){
        // assign
        // act
        fad1.fyldFad(destillat,LocalDate.now(),300);
        // assert
        assertTrue(!fad1.erTom());
        assertEquals(300,fad1.getMængdeDestillatLiter());
    }

    @Test
    @DisplayName("TC6: Destillat mængden er 0")
    void test_DestillatMængede0() {
        double destillatMængde = 0;
        Destillat destillat = produktionslinje.createDestillat(0,0,0);

        assertThrows(IllegalArgumentException.class,
                () -> fad1.fyldFad(destillat,LocalDate.now(),destillatMængde));
    }

    @Test
    @DisplayName("TC7: Destillat = null")
    void test_DestilatErNull(){
        Destillat destillat1 = null;

        assertThrows(IllegalArgumentException.class,
                () -> fad1.fyldFad(destillat1, LocalDate.now(), 100));

    }

    @Test
    @DisplayName("TC8: Får vi mån en Inholdshistorik?")
    void test_opretterIndholdshistorik() {
        Indholdshistorik expected = fad1.fyldFad(destillat, LocalDate.now(), destillatLiter);
        Indholdshistorik actual = fad1.getIndholdshistorik().getLast();
        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("TC9: double stuffed fad")
    void test_fyldEtNetopFyldtFad(){
        // assign
        // act
        fad1.fyldFad(destillat,LocalDate.now(),100);
        // assert
        assertTrue(!fad1.erTom());
        assertEquals(100,fad1.getMængdeDestillatLiter());

        assertThrows(IllegalArgumentException.class,
                () -> fad1.fyldFad(destillat,LocalDate.now(),100));
    }
}
