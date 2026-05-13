package Iteration_2Test;

import Iteration_3.controller.Controller;
import Iteration_3.model.Fad;
import Iteration_3.model.Hylde;
import Iteration_3.model.Lager;
import Iteration_3.model.Reol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class ControllerTest {

    Controller controller;
    Fad fad;
    Lager lager;

    @BeforeEach
    void setup() {
        lager = new Lager("SixSeven SkibidiVej 420");
        controller = new Controller();
        fad = new Fad(100.00, LocalDate.now(), "Godt fad", true, false, null, null);
    }

    @Test
    void test_FjernFraLager() {
        // assign
        int pladser = 5;
        Reol reol = lager.createReol();
        Hylde hylde = reol.createHylde(pladser);
        // act
        controller.sætPåLager(lager, reol, hylde, fad, 3);

//        Fad expected = hylde.getFade()[2]; Ved ikke om vi vil have dette check med også?
//        assertEquals(expected,fad);

        controller.fjernFraLager(fad);
        Fad expected = null;
        Fad actual = hylde.getFade()[2];
        // assert
        assertEquals(expected, actual);
    }

    @Test
    void test_TildelLagerPladsSpecifik() {
        // assign
        int pladser = 5;
        Reol reol = lager.createReol();
        Hylde hylde = reol.createHylde(pladser);
        // act
        controller.sætPåLager(lager, reol, hylde, fad, 3);
        Fad expected = hylde.getFade()[2];
        // assert
        assertEquals(expected, fad);
    }

    @Test
    void test_TildelLagerPladsNæste() {
        // assign
        int pladser = 5;
        Reol reol = lager.createReol();
        Hylde hylde = reol.createHylde(pladser);
        Fad fad2 = new Fad(0, LocalDate.now(), "", true, false, null, null);
        // act
        controller.sætPåLager(lager, fad);
        controller.sætPåLager(lager, fad2);
        Fad expected = hylde.getFade()[0];
        Fad expected2 = hylde.getFade()[1];

        // assert
        assertEquals(expected, fad);
        assertEquals(expected2,fad2);
    }

    @Test
    void test_flytLagerPlads() {
        // assign
        int pladser = 5;
        Reol reol = lager.createReol();
        Hylde hylde = reol.createHylde(pladser);
        // act
        controller.sætPåLager(lager, reol, hylde, fad, 3);
        controller.sætPåLager(lager, reol, hylde, fad, 1);
        Fad expected = hylde.getFade()[0];
        Fad notExpected = hylde.getFade()[2];
        // assert
        assertNotEquals(notExpected, fad);
        assertEquals(expected, fad);
    }


}