package flashcards;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        FlashCards app = new FlashCards();
        app.start();
    }
}

class FlashCards {
    private FlashCardStorage storage = new FlashCardStorage(); // composition
    private List<String> logs = new ArrayList<>();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            log("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String userChoice = scanner.nextLine();

            switch (userChoice) {
                case "add": addCard(); break;
                case "remove": removeCard(); break;
                case "import": importCard(); break;
                case "export": exportCard(); break;
                case "ask": askCard(); break;
                case "reset stats": resetStats(); break;
                case "hardest card": hardestCard(); break;
                case "exit":
                    log("Bye bye!");
                    return;
                case "log": logSave(); break;
            }
        }
    }

    void logSave() {
        Scanner scanner = new Scanner(System.in);
        log("File name:");
        String filename = scanner.nextLine();

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (String line : logs) {
                writer.println(line);
            }
            log("The log has been saved.");
        } catch (IOException e) {
            log("Error saving the log.");
        }
    }

    void log(String message) {
        System.out.println(message);
        logs.add(message);
    }

    void resetStats() {
        storage.resetStats();
        log("Card statistics have been reset.");
    }

    void hardestCard() {
        List<String> hardest = storage.findHardestCards();
        if (hardest.isEmpty()) {
            log("There are no cards with errors.");
        } else if (hardest.size() == 1) {
            String term = hardest.get(0);
            int mistakes = storage.getMistakes(term);
            log("The hardest card is \"" + term + "\". You have " + mistakes + " errors answering it.");
        } else {
            StringJoiner sj = new StringJoiner(", ");
            for (String term : hardest) {
                sj.add("\"" + term + "\"");
            }
            int mistakes = storage.getMistakes(hardest.get(0)); // all hardest have same mistake count
            log("The hardest cards are " + sj.toString() + ". You have " + mistakes + " errors answering them.");
        }
    }

    void addCard() {
        Scanner scanner = new Scanner(System.in);
        log("The card:");
        String term = scanner.nextLine();

        if (storage.hasTerm(term)) {
            log("The term \"" + term + "\" already exists.");
            return;
        }

        log("The definition of the card:");
        String def = scanner.nextLine();

        if (storage.hasDefinition(def)) {
            log("The definition \"" + def + "\" already exists.");
            return;
        }

        storage.addCard(term, def);
        log("The pair (\"" + term + "\":\"" + def + "\") has been added.");
    }

    void removeCard() {
        Scanner scanner = new Scanner(System.in);
        log("Which card?");
        String term = scanner.nextLine();

        if (storage.removeCard(term)) {
            log("The card has been removed.");
        } else {
            log("Can't remove \"" + term + "\": there is no such card.");
        }
    }

    void importCard() {
        Scanner scanner = new Scanner(System.in);
        log("File name:");
        String fileName = scanner.nextLine();
        int count = storage.importFromFile(fileName);
        if (count == -1) {
            log("File not found.");
        } else {
            log(count + " cards have been loaded.");
        }
    }

    void exportCard() {
        Scanner scanner = new Scanner(System.in);
        log("File name:");
        String filename = scanner.nextLine();
        int count = storage.exportToFile(filename);
        if (count == -1) {
            log("Error saving file.");
        } else {
            log(count + " cards have been saved.");
        }
    }

    void askCard() {
        Scanner scanner = new Scanner(System.in);
        log("How many times to ask?");
        int n = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < n; i++) {
            String term = storage.getRandomTerm();
            log("Print the definition of \"" + term + "\":");
            String answer = scanner.nextLine();

            if (storage.isCorrectDefinition(term, answer)) {
                log("Correct!");
            } else if (storage.hasDefinition(answer)) {
                storage.incrementMistakes(term);
                String correctTerm = storage.getTermByDefinition(answer);
                log("Wrong. The right answer is \"" + storage.getDefinition(term) + "\", but your definition is correct for \"" + correctTerm + "\".");
            } else {
                log("Wrong. The right answer is \"" + storage.getDefinition(term) + "\".");
            }
        }
    }
}

class FlashCard {
    private String definition;
    private int mistakes;

    public FlashCard(String definition) {
        this.definition = definition;
        this.mistakes = 0;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void incrementMistakes() {
        mistakes++;
    }

    public void resetMistakes() {
        mistakes = 0;
    }
}

class FlashCardStorage {
    private HashMap<String, FlashCard> cards = new HashMap<>();

    public int getMistakes(String term) {
        return cards.get(term).getMistakes();
    }

    public void resetStats() {
        for (FlashCard card : cards.values()) {
            card.resetMistakes();
        }
    }

    public void incrementMistakes(String term) {
        if (cards.containsKey(term)) {
            cards.get(term).incrementMistakes();
        }
    }

    public String getRandomTerm() {
        ArrayList<String> terms = new ArrayList<>(cards.keySet());
        Random random = new Random();
        int index = random.nextInt(terms.size());
        return terms.get(index);
    }

    public boolean isCorrectDefinition(String term, String answer) {
        return cards.get(term).getDefinition().equals(answer);
    }

    public String getDefinition(String term) {
        return cards.get(term).getDefinition();
    }

    public String getTermByDefinition(String def) {
        for (Map.Entry<String, FlashCard> entry : cards.entrySet()) {
            if (entry.getValue().getDefinition().equals(def)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean hasTerm(String term) {
        return cards.containsKey(term);
    }

    public boolean hasDefinition(String def) {
        return cards.containsValue(def);
    }

    public void addCard(String term, String def) {
        cards.put(term, new FlashCard(def));
    }

    public boolean removeCard(String term) {
        if (cards.containsKey(term)) {
            cards.remove(term);
            return true;
        }
        return false;
    }

    public int importFromFile(String filename) {
        int count = 0;
        try {
            Scanner fileScanner = new Scanner(new File(filename));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("::", 3); // 3 parts now!

                if (parts.length == 3) {
                    String term = parts[0];
                    String def = parts[1];
                    int mistakes = Integer.parseInt(parts[2]);

                    FlashCard card = new FlashCard(def);
                    for (int i = 0; i < mistakes; i++) {
                        card.incrementMistakes();
                    }

                    cards.put(term, card);
                    count++;
                }
            }
            fileScanner.close();
            return count;
        } catch (FileNotFoundException e) {
            return -1;
        }
    }

    public int exportToFile(String filename) {
        int count = 0;
        try {
            PrintWriter writer = new PrintWriter(new File(filename));
            for (Map.Entry<String, FlashCard> entry : cards.entrySet()) {
                writer.println(entry.getKey() + "::" + entry.getValue().getDefinition() + "::" + entry.getValue().getMistakes());
                count++;
            }
            writer.close();
            return count;
        } catch (IOException e) {
            return -1;
        }
    }

    public List<String> findHardestCards() {
        List<String> hardest = new ArrayList<>();
        int maxMistakes = 0;

        for (Map.Entry<String, FlashCard> entry : cards.entrySet()) {
            int mistakes = entry.getValue().getMistakes();
            if (mistakes > maxMistakes) {
                maxMistakes = mistakes;
                hardest.clear();
                hardest.add(entry.getKey());
            } else if (mistakes == maxMistakes && mistakes != 0) {
                hardest.add(entry.getKey());
            }
        }

        return hardest;
    }
}
