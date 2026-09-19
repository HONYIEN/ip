package kelore;

import java.io.IOException;
import java.nio.file.Path;

import kelore.exception.KeloreInputException;
import kelore.parser.Parser;
import kelore.storage.Storage;
import kelore.task.TaskList;

/** Processes commands for the Kelore task-tracking chatbot. */
public class Kelore {
    private static final Path DATA_FILE_PATH = Path.of("data", "kelore.txt");
    private static final String WELCOME_MESSAGE = "Hello! I'm Kelore.\nWhat can I do for you?";

    private final Parser parser = new Parser();
    private final Storage storage;
    private final TaskList taskList;
    private final String loadMessage;

    /** Creates a Kelore chatbot that stores tasks in the default data file. */
    public Kelore() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Kelore chatbot that stores tasks in the specified data file.
     *
     * @param dataFilePath Path of the data file.
     */
    public Kelore(Path dataFilePath) {
        storage = new Storage(dataFilePath);
        TaskList loadedTasks;
        String loadingError = "";
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadingError = "\nOops! I could not load your saved tasks.\n" + e.getMessage();
        }
        taskList = loadedTasks;
        loadMessage = loadingError;
    }

    /**
     * Returns the greeting shown when the chatbot starts.
     *
     * @return Greeting and any data-loading error.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE + loadMessage;
    }

    /**
     * Executes a user command and returns Kelore's response.
     *
     * @param input Complete command entered by the user.
     * @return Displayable response to the command.
     */
    public String getResponse(String input) {
        try {
            switch (parser.parseCommand(input)) {
                case BYE:
                    return "Bye. Hope to see you again soon!";
                case LIST:
                    return taskList.display();
                case MARK:
                    return saveAfter(taskList.mark(parser.parseTaskNumber(input)));
                case UNMARK:
                    return saveAfter(taskList.unmark(parser.parseTaskNumber(input)));
                case DELETE:
                    return saveAfter(taskList.delete(parser.parseTaskNumber(input)));
                case TODO:
                    return saveAfter(taskList.addTodo(input));
                case DEADLINE:
                    return saveAfter(taskList.addDeadline(input));
                case EVENT:
                    return saveAfter(taskList.addEvent(input));
                case ON:
                    return taskList.displayTasksOn(input);
                case FIND:
                    return taskList.find(input);
                default:
                    throw new AssertionError("Unhandled command");
            }
        } catch (KeloreInputException e) {
            return "Oops! " + e.getMessage();
        } catch (IOException e) {
            return "Oops! I could not save your tasks.\n" + e.getMessage();
        }
    }

    private String saveAfter(String response) throws IOException {
        storage.save(taskList);
        return response;
    }

}
