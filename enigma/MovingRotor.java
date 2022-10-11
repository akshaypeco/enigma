package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Akshay Patel
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }


    @Override
    void advance() {
        _setting += 1;
        _ringstellung += 1;
    }

    @Override
    boolean atNotch() {
        if (_notches.equals("")) {
            return false;
        }
        for (int i = 0; i < _notches.length(); i += 1) {
            if (alphabet().toChar(permutation().wrap(_setting))
                    == _notches.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    /** Number of notches on rotor. */
    private final String _notches;
}
