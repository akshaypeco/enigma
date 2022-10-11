package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Class that represents a complete enigma machine.
 *  @author Akshay Patel
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _plugboard = null;
        _allRotors = new ArrayList<Rotor>(allRotors);
        _machineRotors = new ArrayList<Rotor>();
    }

    /** Returns all rotors in machine. */
    List<Rotor> getAllRotors() {
        return _allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _machineRotors.clear();
        for (int str = 0; str < rotors.length; str += 1) {
            for (int rotor = 0; rotor < _allRotors.size(); rotor += 1) {
                if (rotors[str].equals(_allRotors.get(rotor).name())) {
                    if (str == 0 && !_allRotors.get(rotor).reflecting()) {
                        throw EnigmaException.error
                                ("First rotor has to be reflector");
                    }
                    if (!_machineRotors.contains(_allRotors.get(rotor))) {
                        _machineRotors.add(_allRotors.get(rotor));
                    }
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int counter = 0;
        if (setting.length() > (_machineRotors.size() - 1)) {
            throw EnigmaException.error("Wheel settings too long.");
        }
        if (setting.length() < (_machineRotors.size() - 1)) {
            throw EnigmaException.error("Wheel settings too short.");
        }
        for (int i = 1; i < _machineRotors.size(); i += 1) {
            if (counter < setting.length()) {
                _machineRotors.get(i).set(setting.charAt(counter));
                counter += 1;
            }
        }
    }

    /** Set my rotors according to RINGS, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRings(String rings) {
        int counter = 0;
        if (rings.length() > (_machineRotors.size() - 1)) {
            throw EnigmaException.error("Wheel settings too long.");
        }
        if (rings.length() < (_machineRotors.size() - 1)) {
            throw EnigmaException.error("Wheel settings too short.");
        }
        for (int i = 1; i < _machineRotors.size(); i += 1) {
            if (counter < rings.length()) {
                _machineRotors.get(i).setRing(rings.charAt(counter));
                counter += 1;
            }
        }
    }

    /** RETURN VOID, sets RINGSTELLUNG. */
    void setRingstellung() {
        int counter = 0;
        for (int i = 1; i < _machineRotors.size(); i += 1) {
            _machineRotors.get(i).setRingstellung();
        }
    }


    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int[] didAdvance = new int[_machineRotors.size()];

        for (int rotor = (numPawls() - 1);
             rotor < (_machineRotors.size() - 1); rotor += 1) {
            if (_machineRotors.get(rotor + 1).atNotch()) {
                didAdvance[rotor + 1] = 1;
                didAdvance[rotor] = 1;
            }
        }
        didAdvance[_machineRotors.size() - 1] = 1;

        for (int i = 0; i < _machineRotors.size(); i += 1) {
            if (didAdvance[i] == 1) {
                _machineRotors.get(i).advance();
            }
        }

        int plugboardPermed = _plugboard.permute(c);
        for (int rotor = (_machineRotors.size() - 1); rotor > -1; rotor -= 1) {
            plugboardPermed
                    = _machineRotors.get(rotor).convertForward(plugboardPermed);
        }

        for (int rotor = 1; rotor < _machineRotors.size(); rotor += 1) {
            plugboardPermed
                    = _machineRotors.get(rotor).
                    convertBackward(plugboardPermed);
        }

        plugboardPermed = _plugboard.invert(plugboardPermed);

        return plugboardPermed;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String output = "";
        int decoded;
        for (int i = 0; i < msg.length(); i += 1) {
            decoded = convert(_alphabet.toInt(msg.charAt(i)));
            output += _alphabet.toChar(decoded);
        }
        return output;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in machine. */
    private final int _numRotors;

    /** Number of pawls in machine. */
    private final int _pawls;

    /** Plugboard permutation. */
    private Permutation _plugboard;

    /** List of rotors available to machine. */
    private List<Rotor> _allRotors;

    /** List of rotors in machine. */
    private List<Rotor> _machineRotors;
}
