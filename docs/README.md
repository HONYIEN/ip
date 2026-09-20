# Kelore User Guide

![Kelore's graphical user interface](Ui.png)

Kelore is a desktop task-tracking chatbot. Enter commands in the text field and
press Enter or select **Send**. Kelore stores successful changes automatically,
so your tasks remain available after restarting the application.

Commands and parameters are lowercase and case-sensitive. Dates use
`d/M/yyyy`, while date-times use `d/M/yyyy HHmm` in 24-hour time.

## Viewing tasks

Use `list` to display every saved task and its number. Task numbers are used by
the `mark`, `unmark`, and `delete` commands.

## Adding todos

Use `todo DESCRIPTION` to add a task without a date or time.

Example: `todo read a book`

## Adding deadlines

Use `deadline DESCRIPTION /by DATE_TIME` to add a task with a due date and
time.

Example: `deadline submit report /by 25/9/2026 1800`

## Adding events

Use `event DESCRIPTION /from START /to END` to add an event. The end must not
be earlier than the start.

Example: `event project meeting /from 25/9/2026 1400 /to 25/9/2026 1600`

## Updating tasks

- `mark NUMBER` marks a task as completed.
- `unmark NUMBER` marks a task as incomplete.
- `delete NUMBER` permanently removes a task.

Example: `mark 2`

## Viewing tasks on a date

Use `on DATE` to display deadlines and events occurring on a particular date.

Example: `on 25/9/2026`

## Finding tasks

Use `find KEYWORD` to search task descriptions. Kelore first looks for exact,
case-sensitive text. If there are no exact matches, it suggests descriptions
containing a similar word.

Example: `find report`

## Finding free times

Use `free HOURS` to find the earliest uninterrupted free slot of the requested
number of whole hours. Kelore searches every calendar day, including weekends,
between 8:00 AM and 6:00 PM. Only incomplete events occupy time; todos,
deadlines, completed events, and zero-duration events do not.

The search begins at the current time when considering today. Use
`/from d/M/yyyy` to begin on a specified date that is today or later.

Examples:

- `free 4`
- `free 2 /from 25/9/2026`

Kelore returns the earliest slot with exactly the requested duration:

```
The nearest 4-hour free slot is Sep 22 2026, 9:00 AM to 1:00 PM.
```

`HOURS` must be a whole number from 1 to 10. A `/from` date cannot be earlier
than today and must use the `d/M/yyyy` format. The command and its parameters
are lowercase and case-sensitive.

## Exiting Kelore

Use `bye` to end the conversation. Kelore disables further command entry for
that session.
