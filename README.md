# Obsidian Math Note Formatter

Utility in Java per la conversione e formattazione automatica di appunti matematici grezzi (.txt) in file Markdown (.md) ottimizzati per il rendering LaTeX in Obsidian.

## Funzionalità
- Conversione automatica di titoli in sintassi Markdown (`#`, `###`).
- Riconoscimento intelligente delle formule e incapsulamento in blocchi LaTeX (`$$`).
- Mappatura dinamica del testo descrittivo e dei suffissi tramite `\text{}`.
- Conversione automatica delle frazioni e gestione degli esponenti.
- Validazione e correzione automatica dell'estensione del file di output.
- Supporto avanzato per il riconoscimento di limiti e integrali definiti.

## Requisiti
- Java JDK 8 o superiore.

## Utilizzo
Eseguire il programma da terminale passando i parametri:
```bash
java -jar ObsidianFormatter.jar input_appunti.txt appunti_formattati.md
```
