package kelore.parser;

/** Represents a command that Kelore can execute. */
public enum Command {
    BYE("bye", false), LIST("list", false), MARK("mark", true),
    UNMARK("unmark", true), DELETE("delete", true), TODO("todo", true),
    DEADLINE("deadline", true), EVENT("event", true), ON("on", true);

    private final String commandWord;
    private final boolean acceptsArguments;

    Command(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /** Returns whether the complete user input invokes this command. */
    boolean matches(String input) {
        return input.equals(commandWord)
                || acceptsArguments && input.startsWith(commandWord + " ");
    }
}
