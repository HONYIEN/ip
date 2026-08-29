package kelore.parser;

import kelore.exception.KeloreInputException;

/** Interprets commands entered by the user. */
public class Parser {
    /** Returns the command represented by the user's full input. */
    public Command parseCommand(String input) throws KeloreInputException {
        for (Command command : Command.values()) {
            if (command.matches(input)) {
                return command;
            }
        }
        throw new KeloreInputException("I don't recognise that command.");
    }

    /** Parses the one-based task number following a command. */
    public int parseTaskNumber(String input) throws KeloreInputException {
        try {
            return Integer.parseInt(input.substring(input.indexOf(' ') + 1).trim());
        } catch (NumberFormatException e) {
            throw new KeloreInputException(
                    "Please provide a valid task number after the command.");
        }
    }
}
