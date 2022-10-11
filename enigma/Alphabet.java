package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Akshay Patel
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        if (checkDuplicates()) {
            throw EnigmaException.error("Alphabet has duplicate character(s).");
        }
        if (_chars.length() == 1) {
            throw EnigmaException.error("Alphabet incorrectly inputted.");
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int x = 0; x < size(); x += 1) {
            if (_chars.charAt(x) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if ((0 <= index) && (index <= size())) {
            return _chars.charAt(index);
        } else {
            throw EnigmaException.error("Index not in alphabet.");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int index = 0; index < size(); index += 1) {
            char currChar = toChar(index);
            if (currChar == ch) {
                return index;
            }
        }
        throw EnigmaException.error("Character not in alphabet.");
    }


    /** number of characters in alphabet. */
    private final String _chars;


    /** checks for duplicate letters and @return boolean. */
    private boolean checkDuplicates() {
        int count;
        for (int first = 0; first < size(); ++first) {
            count = 0;
            for (int second = 0; second < size(); ++second) {
                if (toChar(first) == toChar(second)) {
                    count += 1;
                }
            }
            if (count > 1) {
                return true;
            }
        }
        return false;
    }

}
