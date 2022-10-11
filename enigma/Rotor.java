package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Akshay Patel
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ringstellung = setting();
        _ring = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
        _ringstellung = _setting;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
        _ringstellung = _setting;
    }

    /** Set ring to character CPOSN. */
    void setRing(char cposn) {
        _ring = alphabet().toInt(cposn);
    }

    /** Set ringstellung to character CPOSN. */
    void setRingstellung() {
        _ringstellung = _setting - _ring;
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int contactExited = _permutation.permute(p + _ringstellung);
        return _permutation.wrap(contactExited - _ringstellung);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int contactExited = _permutation.invert(e + _ringstellung);
        int positionExited = _permutation.wrap(contactExited - _ringstellung);
        return positionExited;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Setting of this rotor. */
    protected int _setting;

    /** Ringstellung setting of this rotor. */
    protected int _ringstellung;

    /** Ring setting of this rotor. */
    protected int _ring;
}
