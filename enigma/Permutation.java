package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Akshay Patel
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        if (cycleIsEmpty()) {
            throw EnigmaException
                    .error("Cycle cannot be ()");
        }
        if (cycleDuplicateParenthesis()) {
            throw EnigmaException
                    .error("Cycle contains duplicate parenthesis.");
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = cycle + _cycles;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char theChar = _alphabet.toChar(wrap(p));
        char newChar = permute(theChar);
        int newInt = _alphabet.toInt(newChar);
        return newInt;
    }


    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char theChar = _alphabet.toChar(wrap(c));
        char newChar = invert(theChar);
        int newInt = _alphabet.toInt(newChar);
        return newInt;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int thisIndex;
        int firstOfCycle;

        if (_cycles.equals("")) {
            return p;
        }

        thisIndex = _cycles.indexOf(p);

        if (thisIndex == -1) {
            return p;
        }

        if ((_cycles.charAt(thisIndex - 1) == '(')
                && (_cycles.charAt(thisIndex + 1) == ')')) {
            return _cycles.charAt(thisIndex);

        }

        if ((_cycles.charAt(thisIndex + 1) == ')')) {
            for (firstOfCycle = thisIndex; firstOfCycle > 0; --firstOfCycle) {
                if (_cycles.charAt(firstOfCycle - 1) == '(') {
                    return _cycles.charAt(firstOfCycle);
                }
            }
        }

        return _cycles.charAt(thisIndex + 1);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int thisIndex;
        int endOfCycle;

        if (_cycles.equals("")) {
            return c;
        }

        thisIndex = _cycles.indexOf(c);

        if (thisIndex == -1) {
            return c;
        }

        if ((_cycles.charAt(thisIndex - 1) == '(')
                && (_cycles.charAt(thisIndex + 1) == ')')) {
            return c;

        }

        if ((_cycles.charAt(thisIndex - 1) == '(')) {
            for (endOfCycle = thisIndex; endOfCycle
                    < (_cycles.length() - 1); endOfCycle += 1) {
                if (_cycles.charAt(endOfCycle + 1) == ')') {
                    return _cycles.charAt(endOfCycle);
                }
            }
        }

        return _cycles.charAt(thisIndex - 1);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int alph = 0; alph < _alphabet.size(); alph += 1) {
            int indexAt = _cycles.indexOf(_alphabet.toChar(alph));
            if (indexAt != -1) {
                if ((_cycles.charAt(indexAt - 1) != '(')
                        || (_cycles.charAt(indexAt + 1) != ')')) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String of cycles. */
    private String _cycles;

    /** Checks if a cycle is empty parenthesis only and @return boolean. */
    private boolean cycleIsEmpty() {
        if (_cycles.equals("()")) {
            return true;
        }
        return false;
    }

    /** Checks for duplicate parenthesis in cycle, and @return boolean. */
    private boolean cycleDuplicateParenthesis() {
        for (int i = 0; i  < (_cycles.length() - 2); i += 1) {
            Character thisChar = _cycles.charAt(i);
            Character nextChar = _cycles.charAt(i + 1);
            Character nextNextChar = _cycles.charAt(i + 2);
            if (thisChar.equals('(')) {
                if (nextChar.equals('(')) {
                    return true;
                }
            } else if (thisChar.equals(')')) {
                if (nextChar.equals(')')) {
                    return true;
                }
            }
        }
        return false;
    }
}


