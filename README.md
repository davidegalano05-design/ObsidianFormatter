# Obsidian Math Note Formatter

A Java utility for the automatic conversion and formatting of raw mathematical notes (.txt) into Markdown files (.md) optimized for LaTeX rendering in Obsidian.

## Features
- Automatic conversion of titles into Markdown syntax (`#`, `###`).
- Smart recognition of formulas and encapsulation within LaTeX blocks (`$$`).
- Dynamic mapping of descriptive text and suffixes using `\text{}`.
- Automatic conversion of fractions and exponent handling.
- Automatic validation and correction of the output file extension.
- Advanced support for recognizing limits and definite integrals.

## Requirements
- Java JDK 8 or higher.

## Usage
Run the program from the terminal by passing the following parameters:
```bash
java -jar ObsidianFormatter.jar input_notes.txt formatted_notes.md
