package kelore.task;

import java.time.LocalDate;

import kelore.storage.Storage;

/** Represents a task and whether it has been completed. */
public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns {@code 1} when completed and {@code 0} otherwise for file storage. */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /** Converts this task into the text format used in the data file. */
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /** Returns whether this task occurs on the specified calendar date. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
