package Iteration_5;

import Iteration_5.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FadModelTest {
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

//    public double beregnNuværendeBeholdning() {
//        if (erTom || mængdeDestillatLiter <= 0.001) {
//            return 0;
//        }
//
//        int antalÅr = (int) ChronoUnit.YEARS.between(indholdshistorikker.getLast().getPåfyldningsDato(), LocalDate.now());
//        if (antalÅr < 0) {
//            throw new IllegalArgumentException("Kan ikke regne tilbage i tiden");
//        }
//        double devil = 0;
//        if (!tidligereBrugt) {
//            devil = this.devilsCut;
//        }
//        double førsteÅr = mængdeDestillatLiter * (1 - devil - angelsShare);
//
//        if (antalÅr == 1) {
//            return førsteÅr;
//        } else if (antalÅr > 1) {
//            double flereÅr = førsteÅr * Math.pow((1 - angelsShare),antalÅr - 1);
//            return flereÅr;
//        }
//
//        return mængdeDestillatLiter;
//    }

    @Test
    @DisplayName("Fadet er tomt")
    void test_fadetErTomt() {
        Fad tomtfad = new Fad(100,testDato,"Test fad",true, false, leverandør, lager);
        double expected = 0;
        double actual = tomtfad.beregnNuværendeBeholdning();

        assertEquals(expected, actual);
    }
}
