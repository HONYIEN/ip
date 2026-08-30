package kelore.task;

import java.time.LocalDate;
import java.util.Locale;

import kelore.storage.Storage;

/** Represents a task and whether it has been completed. */
public class Task {
    /** Description of this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon representing whether this task is complete.
     *
     * @return {@code X} if complete; a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the completion status used by the data file.
     *
     * @return {@code 1} if complete; {@code 0} otherwise.
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the text format used by the data file.
     *
     * @return Serialized task record.
     */
    public String toStorageString() {
        return Storage.joinFields("T", getStorageStatus(), description);
    }

    /**
     * Returns whether this task occurs on the specified calendar date.
     *
     * @param date Calendar date to check.
     * @return False because a basic task has no associated date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether any word in this task's description is a close match for the keyword.
     * A small length-based edit-distance limit avoids overly broad matches for short keywords.
     */
    public boolean containsCloseKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        int allowedDistance = getAllowedDistance(normalizedKeyword.length());
        for (String word : description.toLowerCase(Locale.ENGLISH).split("\\s+")) {
            if (levenshteinDistance(word, normalizedKeyword) <= allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private int getAllowedDistance(int keywordLength) {
        if (keywordLength <= 2) {
            return 0;
        }
        if (keywordLength <= 7) {
            return 1;
        }
        return 2;
    }

    /** Calculates the minimum number of single-character edits between two strings. */
    private int levenshteinDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previousRow[j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                currentRow[j] = Math.min(
                        Math.min(currentRow[j - 1] + 1, previousRow[j] + 1),
                        previousRow[j - 1] + substitutionCost);
            }
            previousRow = currentRow;
        }
        return previousRow[second.length()];
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether any word in this task's description is a close match for the keyword.
     * A small length-based edit-distance limit avoids overly broad matches for short keywords.
     */
    public boolean containsCloseKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        int allowedDistance = getAllowedDistance(normalizedKeyword.length());
        for (String word : description.toLowerCase(Locale.ENGLISH).split("\\s+")) {
            if (levenshteinDistance(word, normalizedKeyword) <= allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private int getAllowedDistance(int keywordLength) {
        if (keywordLength <= 2) {
            return 0;
        }
        if (keywordLength <= 7) {
            return 1;
        }
        return 2;
    }

    /** Calculates the minimum number of single-character edits between two strings. */
    private int levenshteinDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previousRow[j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                currentRow[j] = Math.min(
                        Math.min(currentRow[j - 1] + 1, previousRow[j] + 1),
                        previousRow[j - 1] + substitutionCost);
            }
            previousRow = currentRow;
        }
        return previousRow[second.length()];
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns a displayable description of this task and its completion status. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
