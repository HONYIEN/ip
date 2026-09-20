# Kelore User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Finding free times

Use `free HOURS` to find the earliest uninterrupted free slot of the requested
number of whole hours. Kelore searches every calendar day, including weekends,
between 8:00 AM and 6:00 PM. Only incomplete events occupy time; todos,
deadlines, completed events, and zero-duration events do not.

The search begins at the current time when considering today. Use
`/from d/M/yyyy` to begin on a specified date that is today or later.

Examples:

* `free 4`
* `free 2 /from 25/9/2026`

Kelore returns the earliest slot with exactly the requested duration:

```
The nearest 4-hour free slot is Sep 22 2026, 9:00 AM to 1:00 PM.
```

`HOURS` must be a whole number from 1 to 10. A `/from` date cannot be earlier
than today and must use the `d/M/yyyy` format. The command and its parameters
are lowercase and case-sensitive.

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
