package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Arrays;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Akshay Patel
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine thisMachine = readConfig();
        int counter = 0;
        while (_input.hasNextLine()) {
            if (_input.hasNext("\\*")) {
                String setting = _input.nextLine();
                while (setting.equals("")) {
                    if (counter > 0) {
                        _output.println();
                    }
                    setting = _input.nextLine();
                }
                setUp(thisMachine, setting);
                counter += 1;
                if (!_input.hasNext("\\*") && counter == 0) {
                    throw EnigmaException.error
                            ("No setting found on first line.");
                }
                if (_input.hasNext("\\*") && counter > 0) {
                    break;
                }
            }
            if (_input.hasNextLine() && !_input.hasNext("\\*")
                    && counter == 0) {
                throw EnigmaException.error("No setting found. ");
            }

            if (_input.hasNextLine()) {
                String message = _input.nextLine();
                Scanner messageScanner = new Scanner(message);
                String newMessage = "";
                while (messageScanner.hasNext()) {
                    newMessage += messageScanner.next();
                }
                message = thisMachine.convert(newMessage);
                printMessageLine(message);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numPawls = 0;
            int numRotors = 0;
            String rotorTypes = "";
            String rotorNotches = "";
            Collection<Rotor> allMachineRotors = new ArrayList<Rotor>();

            if (_config.hasNext("^[\\S]+")) {
                String currNext = _config.nextLine();
                _alphabet = new Alphabet(currNext);
                if (_alphabet.contains('*') || _alphabet.contains(')')
                        || _alphabet.contains('(')
                        || _alphabet.contains(' ')) {
                    throw EnigmaException
                            .error("Alphabet has *, ), or (, or space.");
                }
            }


            if (_config.hasNext("\\d+")) {
                try {
                    numRotors = _config.nextInt();
                } catch (NoSuchElementException excp) {
                    throw EnigmaException
                            .error("Number of rotors not correctly inputted.");
                }
            }

            if (!_config.hasNext("\\d+")) {
                throw EnigmaException.error("Alphabet or number of "
                        + "rotors not set properly.");
            }
            if (_config.hasNext("\\d+")) {
                numPawls = _config.nextInt();
            }
            if (numRotors == 0) {
                throw EnigmaException.error
                        ("Number of rotors has to be greater than 0.");
            }

            if (numPawls > numRotors) {
                throw EnigmaException.error
                        ("Can't have more pawls than rotors.");
            }
            while (_config.hasNext("[^\\(\\)\\s]+")) {
                allMachineRotors.add(readRotor());
            }
            return new Machine(_alphabet,
                    numRotors, numPawls, allMachineRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Character rotorType = ' ';
            String rotorNotches = "";
            String rotorPerm = "";

            String name = _config.next();

            if (_config.hasNext("[\\S]*")) {
                String currChars = _config.next();
                rotorType = currChars.charAt(0);
                for (int i = 1; i < currChars.length(); i += 1) {
                    rotorNotches += currChars.charAt(i);
                }
            }

            while (_config.hasNext("(\\(([\\S]+)\\))(\\s(\\(([\\S]+)\\)))*")) {
                rotorPerm += _config.next();
            }

            if (rotorType.equals('M')) {
                return new MovingRotor(name,
                        new Permutation(rotorPerm, _alphabet), rotorNotches);
            }
            if (rotorType.equals('N')) {
                return new FixedRotor(name,
                        new Permutation(rotorPerm, _alphabet));
            }
            if (rotorType.equals('R')) {
                return new Reflector(name,
                        new Permutation(rotorPerm, _alphabet));
            } else {
                throw EnigmaException.error("Rotor type is incorrect.");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotorNames = new String[M.numRotors()];
        Scanner settScanner = new Scanner(settings);
        String rings = "";

        int counter = 0;
        if (settScanner.hasNext("[^\\*]")) {
            throw EnigmaException
                    .error("Setting needs to have an * in beginning.");
        }
        String currString = settScanner.next();
        getDuplicates(settings, M);
        while (settScanner.hasNext("([\\S]+)")) {
            currString = settScanner.next();
            reflectorPosition(M, rotorNames, currString);

            if (counter < M.numRotors() - 1) {
                rotorExists(M, currString);
            }
            if (counter < M.numRotors()) {
                rotorNames[counter] = currString;
            }

            if (counter == M.numRotors()) {
                M.insertRotors(rotorNames);
                break;
            }
            counter += 1;
        }
        tooManyRotors(M, currString, settScanner);
        M.setRotors(currString);
        String plugPerm = "";
        if (settScanner.hasNext("[^\\(\\)]+")) {
            rings = settScanner.next();
            M.setRings(rings);
            M.setRingstellung();
        }
        getPerm(plugPerm, M, settScanner);
    }

    /** RETURNS VOID, errors if duplicates
     * are found in string SETTINGS and machine M. */
    void getDuplicates(String settings, Machine M) {
        Scanner loopScanner = new Scanner(settings);
        loopScanner.next();
        int loopCount = 0;
        while (loopScanner.hasNext()) {
            loopScanner.next();
            loopCount += 1;
        }
        if (loopCount < M.numRotors() + 1) {
            throw EnigmaException
                    .error("Settings has too few rotors for machine.");
        }
        String[] duplicates = new String[loopCount];
        Scanner newLoopScanner = new Scanner(settings);
        newLoopScanner.next();
        int duplicateCounter = 0;
        while (newLoopScanner.hasNext()) {
            duplicates[duplicateCounter] = newLoopScanner.next();
            duplicateCounter += 1;
        }
        Arrays.sort(duplicates);
        for (int i = 0; i < duplicates.length - 1; i += 1) {
            if (duplicates[i].equals(duplicates[i + 1])) {
                throw EnigmaException
                        .error("Duplicate rotor name.");
            }
        }
    }

    /** RETURNS VOID, takes in string PLUGPERM,
     * machine M, and scanner SETTSCANNER. */
    void getPerm(String plugPerm, Machine M, Scanner settScanner) {
        while (settScanner.hasNext()) {
            plugPerm += settScanner.next();
        }
        M.setPlugboard(new Permutation(plugPerm, _alphabet));
    }



    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String currOutput = "";
        int counter = 0;
        for (int i = 0; i < msg.length(); i += 1) {
            if (counter == 5) {
                currOutput = currOutput + " " + msg.charAt(i);
                counter = 1;
            } else {
                currOutput += msg.charAt(i);
                counter += 1;
            }
        }
        _output.println(currOutput);
    }

    /** Error if rotor doesn't exist. Returns VOID,
     * machine M, string CURRSTRING. */
    private void rotorExists(Machine M, String currString) {
        boolean doesNotExist = true;
        for (int i = 0; i < M.getAllRotors().size(); i += 1) {
            if (M.getAllRotors().get(i).name().equals(currString)) {
                doesNotExist = false;
                break;
            }
        }
        if (doesNotExist) {
            throw EnigmaException
                    .error("Rotor doesn't exist.");
        }
    }

    /** Error if too many rotors are inputted. Returns VOID,
     * machine M, string CURRSTRING, Scanner SETTSCANNER */
    private void tooManyRotors(Machine M, String currString,
                               Scanner settScanner) {
        for (int i = 0; i < M.getAllRotors().size(); i += 1) {
            if (M.getAllRotors().get(i).name().equals(currString)) {
                if (settScanner.hasNext("[^\\(\\)]")) {
                    break;
                } else {
                    throw EnigmaException
                            .error("Too many rotors inputted.");
                }
            }
        }
    }

    /** Error if reflector is not in first position. Returns VOID,
     * machine M, string array ROTORNAMES, string CURRSTRING. */
    private void reflectorPosition(Machine M, String[] rotorNames,
                                   String currString) {
        if (rotorNames[0] == null) {
            for (int i = 0; i < M.getAllRotors().size(); i += 1) {
                if (M.getAllRotors().get(i).name().equals(currString)) {
                    if (M.getAllRotors().get(i).reflecting()) {
                        break;
                    } else {
                        throw EnigmaException
                                .error("Reflector is "
                                        + "not in the first position.");
                    }
                }
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
