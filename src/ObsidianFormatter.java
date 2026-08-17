import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ObsidianFormatter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Obsidian Math Note Formatter ===");

        String inputFile;
        String outputFile;

        if (args.length >= 2) {
            inputFile = args[0];
            outputFile = args[1];
        } else {
            System.out.print("Inserisci il nome del file di input (es. input_appunti.txt): ");
            inputFile = scanner.nextLine().trim();
            if (inputFile.isEmpty()) inputFile = "input_appunti.txt";

            System.out.print("Inserisci il nome del file di output (es. appunti_formattati): ");
            outputFile = scanner.nextLine().trim();
            if (outputFile.isEmpty()) outputFile = "appunti_formattati.md";
        }

        if (!outputFile.toLowerCase().endsWith(".md")) {
            int lastDot = outputFile.lastIndexOf('.');
            if (lastDot != -1) {
                outputFile = outputFile.substring(0, lastDot) + ".md";
            } else {
                outputFile = outputFile + ".md";
            }
            System.out.println("-> Nota: L'estensione del file di output è stata corretta in: " + outputFile);
        }

        System.out.println("\nElaborazione del file " + inputFile + " in corso...");

        try {
            processFile(inputFile, outputFile);
            System.out.println("Successo! Il file formattato è salvato come: " + outputFile);
            generateReadme();
        } catch (IOException e) {
            System.err.println("Errore di I/O: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void processFile(String inputPath, String outputPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String formattedLine = formatLine(line);
                writer.write(formattedLine);
                writer.newLine();
            }
        }
    }

    private static String formatLine(String text) {
        if (text.trim().isEmpty()) {
            return text;
        }

        String processed = text;

        // 1. Sostituzioni di base e simboli matematici
        processed = processed.replace("sin(", "\\sin(");
        processed = processed.replace("cos(", "\\cos(");
        processed = processed.replace("ln|", "\\ln|");
        processed = processed.replace("L'integrale di", "\\int");
        processed = processed.replace("integrale di", "\\int");
        processed = processed.replace(" e' ", " è ");

        // NOVITÀ: Supporto per i limiti (converte "lim x->0" o "lim_{x->0}" in "\lim_{x \to 0}")
        // [^\\s}]+ significa "cattura qualsiasi carattere tranne gli spazi e la graffa chiusa"
        processed = processed.replaceAll("lim_?\\{?\\s*([a-zA-Z])\\s*->\\s*([^\\\\s}]+)\\}?", "\\\\lim_{$1 \\\\to $2}");

        // Converte le abbreviazioni "inf" in infinito LaTeX
        processed = processed.replace("\\to inf", "\\to \\infty");
        processed = processed.replace("\\to +inf", "\\to +\\infty");
        processed = processed.replace("\\to -inf", "\\to -\\infty");

        // 2. Conversione avanzata delle frazioni in LaTeX (\frac{}{}) con supporto a parentesi ed esponenti
        processed = processed.replaceAll("\\(([^)]+)\\)\\s*/\\s*\\(([^)]+)\\)(\\^[a-zA-Z0-9]+)?", "\\\\frac{$1}{($2)$3}");
        processed = processed.replaceAll("\\(([^)]+)\\)\\s*/\\s*([a-zA-Z0-9\\+\\-\\^\\*]+)", "\\\\frac{$1}{$2}");
        processed = processed.replaceAll("([a-zA-Z0-9\\+\\-\\^\\*]+)\\s*/\\s*\\(([^)]+)\\)(\\^[a-zA-Z0-9]+)?", "\\\\frac{$1}{($2)$3}");
        processed = processed.replaceAll("([a-zA-Z0-9\\\\\\(\\)]+)/([a-zA-Z0-9]+)", "\\\\frac{$1}{$2}");

        // 3. Rilevamento ed elaborazione delle equazioni/formule
        boolean isMathEquation = processed.contains("f(x)")
                || processed.contains("f'(x)")
                || processed.contains("\\int")
                || processed.contains("=")
                || processed.contains("\\lim");

        if (isMathEquation) {
            String mathContent = processed.trim();

            int markerIdx = -1;
            // Aggiungiamo \lim come indicatore principale di inizio formula
            if (mathContent.contains("\\lim")) {
                markerIdx = mathContent.indexOf("\\lim");
            } else if (mathContent.contains("\\int")) {
                markerIdx = mathContent.indexOf("\\int");
            } else if (mathContent.contains("f(x)")) {
                markerIdx = mathContent.indexOf("f(x)");
            } else if (mathContent.contains("f'(x)")) {
                markerIdx = mathContent.indexOf("f'(x)");
            }

            if (markerIdx > 0) {
                String prefix = mathContent.substring(0, markerIdx).trim();
                String remainder = mathContent.substring(markerIdx).trim();

                String formulaPart = remainder;
                String suffix = "";

                // Gestione di frasi descrittive dopo la variabile (es. "f(x) vale 1")
                if (remainder.startsWith("f(x)") && remainder.length() > 4 && remainder.charAt(4) == ' ') {
                    int equalsIdx = remainder.indexOf('=');
                    if (equalsIdx == -1 || equalsIdx > 15) {
                        String[] parts = remainder.split("\\s+", 2);
                        if (parts.length > 1 && !parts[1].startsWith("=")) {
                            formulaPart = parts[0];
                            suffix = parts[1];
                        }
                    }
                } else if (remainder.startsWith("f'(x)") && remainder.length() > 5 && remainder.charAt(5) == ' ') {
                    String[] parts = remainder.split("\\s+", 2);
                    if (parts.length > 1 && !parts[1].startsWith("=")) {
                        formulaPart = parts[0];
                        suffix = parts[1];
                    }
                }

                StringBuilder sb = new StringBuilder();
                if (!prefix.isEmpty()) {
                    sb.append("\\text{").append(prefix).append("} \\ ");
                }
                sb.append(formulaPart);
                if (!suffix.isEmpty()) {
                    sb.append(" \\ \\text{").append(suffix).append("}");
                }
                mathContent = sb.toString();
            }

            return "$$ " + mathContent + " $$";
        }

        // 4. Formattazione Titoli Markdown
        if (!processed.endsWith(".") && !processed.endsWith(":") && processed.length() > 5) {
            if (processed.startsWith("Appunti") || processed.startsWith("Analisi")) {
                return "# " + processed;
            }
            return "### " + processed;
        }

        return processed;
    }

    private static void generateReadme() throws IOException {
        String readmeContent = "# Obsidian Math Note Formatter\n\n"
                + "Utility in Java per la conversione e formattazione automatica di appunti matematici "
                + "grezzi (.txt) in file Markdown (.md) ottimizzati per il rendering LaTeX in Obsidian.\n\n"
                + "## Funzionalità\n"
                + "- Conversione automatica di titoli in sintassi Markdown (`#`, `###`).\n"
                + "- Riconoscimento intelligente delle formule e incapsulamento in blocchi LaTeX (`$$`).\n"
                + "- Mappatura dinamica del testo descrittivo e dei suffissi tramite `\\text{}`.\n"
                + "- Conversione automatica delle frazioni e gestione degli esponenti.\n"
                + "- Validazione e correzione automatica dell'estensione del file di output.\n\n"
                + "## Requisiti\n"
                + "- Java JDK 8 o superiore.\n\n"
                + "## Utilizzo\n"
                + "Eseguire il programma da terminale passando i parametri:\n"
                + "```bash\n"
                + "java ObsidianFormatter input_appunti.txt appunti_formattati.md\n"
                + "```\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("README.md"))) {
            writer.write(readmeContent);
        }
    }
}