package Iteration_2;

import Iteration_2.controller.Controller;
import Iteration_2.model.Fad;
import Iteration_2.model.Hylde;
import Iteration_2.model.Lager;
import Iteration_2.model.Reol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class ControllerTest {

    Controller controller;
    Fad fad;

    @BeforeEach
    void setup(){
        Lager lager = new Lager("SixSeven SkibidiVej 420");
        controller = new Controller();
        fad = new Fad(100.00, LocalDate.now(),"Godt fad",true,false,null,null);
    }

    @Test
    void test_FjernFraLager(){
        // assign

        // act
        // assert
    }

    @Test
    void test_TildelLagerPladsSpecifik(){
        // assign
        int pladser = 5;
        Lager lager = new Lager("Sønderhøjvej 67");
        Reol reol = lager.createReol();
        Hylde hylde = reol.createHylde(pladser);
        // act
        controller.sætPåLager(lager,reol,hylde,3,fad);
        Fad expected = hylde.getFade()[3];
        // assert
        assertEquals(expected,fad);
    }

    @Test
    void test_TildelLagerPladsNæste(){
        // assign

        // act
        // assert
    }

    @Test
    void test_flytLagerPlads(){

    }
}