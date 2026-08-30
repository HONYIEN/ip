package kelore;

import java.io.IOException;
import java.nio.file.Path;

import kelore.exception.KeloreInputException;
import kelore.parser.Parser;
import kelore.storage.Storage;
import kelore.task.TaskList;
import kelore.ui.Ui;

/** Runs the Kelore task-tracking chatbot. */
public class Kelore {
    private static final Path DATA_FILE_PATH = Path.of("data", "kelore.txt");

    /** Creates a Kelore application entry point. */
    public Kelore() {
    }

    /**
     * Starts Kelore and processes commands until the user exits.
     *
     * @param args Command-line arguments, which Kelore does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        ui.showWelcome();
        Storage storage = new Storage(DATA_FILE_PATH);
        TaskList taskList;
        try {
            taskList = storage.load();
        } catch (IOException e) {
            ui.showError("I could not load your saved tasks.");
            ui.showIndentedLine(e.getMessage());
            taskList = new TaskList();
        }

        while (true) {
            String input = ui.readCommand();
            ui.showDivider();
            try {
                switch (parser.parseCommand(input)) {
                case BYE:
                    ui.showGoodbye();
                    ui.close();
                    return;
                case LIST:
                    ui.showMessage(taskList.display());
                    break;
                case MARK:
                    ui.showIndentedLine(taskList.mark(parser.parseTaskNumber(input)));
                    storage.save(taskList);
                    break;
                case UNMARK:
                    ui.showIndentedLine(taskList.unmark(parser.parseTaskNumber(input)));
                    storage.save(taskList);
                    break;
                case DELETE:
                    ui.showIndentedLine(taskList.delete(parser.parseTaskNumber(input)));
                    storage.save(taskList);
                    break;
                case TODO:
                    ui.showIndentedLine(taskList.addTodo(input));
                    storage.save(taskList);
                    break;
                case DEADLINE:
                    ui.showIndentedLine(taskList.addDeadline(input));
                    storage.save(taskList);
                    break;
                case EVENT:
                    ui.showIndentedLine(taskList.addEvent(input));
                    storage.save(taskList);
                    break;
                case ON:
                    ui.showMessage(taskList.displayTasksOn(input));
                    break;
                case FIND:
                    ui.showMessage(taskList.find(input));
                    break;
                default:
                    throw new AssertionError("Unhandled command");
                }
            } catch (KeloreInputException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("I could not save your tasks.");
                ui.showIndentedLine(e.getMessage());
            }
            ui.showDivider();
        }
    }

    /**
     * Returns the supplied string unchanged.
     *
     * @param input String to return.
     * @return The supplied string.
     */
    public static String echoString(String input) {
        return input;
    }
}
