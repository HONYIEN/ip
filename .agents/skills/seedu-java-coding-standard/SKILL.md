---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for all Java code created or modified in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to every Java change in this repository. Use the Google Java Style Guide only for topics that SE-EDU does not cover.

## Required conventions

- Use lowercase package names; PascalCase noun names for classes and enums; camelCase verb names for methods; camelCase variable names; and SCREAMING_SNAKE_CASE for constants.
- Keep acronyms lowercase within identifiers, use English names, make boolean names read as booleans, and use plural names for collections.
- Use four spaces, never tabs. Indent wrapped lines eight spaces beyond their parent line.
- Prefer lines below 110 characters and never exceed 120 characters.
- Use K&R braces. Always use braces for loops and conditionals, including single-statement bodies, and put conditional bodies on separate lines.
- Put spaces around operators and after keywords, commas, and semicolons where the standard requires them. Separate logical units with one blank line.
- Put every class in a package. Keep imports consistently ordered, explicit, minimal, and free of wildcards.
- Attach array brackets to the type. Initialize variables at declaration when possible and declare them in the smallest practical scope.
- Do not expose mutable class fields publicly. Constants and behavior-free data classes are the stated exceptions.
- Indent `case` and `default` one level inside `switch`; indent their statements one further level. Mark intentional fallthrough with `// Fallthrough`.
- Write English comments using American spelling and align comments with the code they describe.
- Add descriptive Javadocs to every class and public method, except test code, straightforward getters/setters, and overrides whose inherited documentation applies exactly. Start method summaries with a third-person verb such as `Returns`, `Adds`, or `Sends` and document non-obvious parameters, returns, and exceptions.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`.

## Workflow

When creating or editing Java, inspect the surrounding file for affected violations, apply these rules without changing unrelated behavior, and run the relevant JUnit tests. Before finishing, check changed Java files for tabs, wildcard imports, lines over 120 characters, missing braces, inconsistent switch indentation, and missing required Javadocs.
