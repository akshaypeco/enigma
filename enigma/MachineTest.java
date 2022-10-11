package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MachineTest {
    Alphabet testAlph
            = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    Reflector B = new Reflector("B",
            new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                    + "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", testAlph));
    FixedRotor beta = new FixedRotor("Beta",
            new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", testAlph));
    MovingRotor rotIII = new MovingRotor("III",
            new Permutation(
                    "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)",
                    testAlph), "V");
    MovingRotor rotIV = new MovingRotor("IV",
            new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", testAlph),
            "J");
    MovingRotor rotI = new MovingRotor("I",
            new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) "
                    + "(IV) (JZ) (S)",
                    testAlph),
            "Q");

    Permutation plugboardPerm = new Permutation("(HQ) (EX) (IP) (TR) (BY)",
            testAlph);

    @Test public void checkMachine() {
        assertEquals(testAlph.toInt('E'),
                B.convertForward(testAlph.toInt('A')));
        assertEquals(testAlph.toInt('L'),
                beta.convertForward(testAlph.toInt('A')));
        assertEquals(testAlph.toInt('B'),
                rotIII.convertForward(testAlph.toInt('A')));
        assertEquals(testAlph.toInt('E'),
                rotIV.convertForward(testAlph.toInt('A')));
        assertEquals(testAlph.toInt('E'),
                rotI.convertForward(testAlph.toInt('A')));
        assertEquals(testAlph.toInt('T'),
                B.convertForward(testAlph.toInt('V')));
        assertEquals(testAlph.toInt('J'),
                beta.convertForward(testAlph.toInt('D')));
        assertEquals(testAlph.toInt('P'),
                rotIII.convertForward(testAlph.toInt('H')));
        assertEquals(testAlph.toInt('U'),
                rotIV.convertForward(testAlph.toInt('K')));
        assertEquals(testAlph.toInt('O'),
                rotI.convertForward(testAlph.toInt('M')));
        assertEquals(testAlph.toInt('L'),
                rotIII.convertBackward(testAlph.toInt('V')));

        List<Rotor> testRotors = new ArrayList<Rotor>();
        testRotors.add(B);
        testRotors.add(beta);
        testRotors.add(rotIII);
        testRotors.add(rotIV);
        testRotors.add(rotI);

        Machine testMachine = new Machine(testAlph, 5, 3, testRotors);
        testMachine.setPlugboard(plugboardPerm);
        testMachine.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});
        testMachine.setRotors("AXLE");


        assertEquals(5, testMachine.numRotors());
        assertEquals(3, testMachine.numPawls());
        assertEquals(0, B.setting());
        assertEquals(testAlph.toInt('A'), beta.setting());
        assertEquals(testAlph.toInt('X'), rotIII.setting());
        assertEquals(testAlph.toInt('L'), rotIV.setting());
        assertEquals(testAlph.toInt('E'), rotI.setting());



        assertEquals("QVPQSOKOILPUBKJZPISF"
                        + "XDWBHCNSCXNUOAAT"
                        + "ZXSRCFYDGUFLPNXG"
                        + "XIXTYJUJRCAUGEUNCFMKUF",
                testMachine.convert("FROMHISSHOULDERHIAWA"
                        + "THATOOKTHECAMERAOFRO"
                        + "SEWOODMADEOFSLIDINGF"
                        + "OLDINGROSEWOOD"));

    }
}

