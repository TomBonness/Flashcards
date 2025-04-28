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

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            String userChoice = scanner.nextLine();

            switch (userChoice) {
                case "add": addCard(); break;
                case "remove": removeCard(); break;
                case "import": importCard(); break;
                case "export": exportCard(); break;
                case "ask": askCard(); break;
                case "exit":
                    System.out.println("Bye bye!");
                    return;
            }
        }
    }


    void addCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The card:");
        String term = scanner.nextLine();

        if (storage.hasTerm(term)) {
            System.out.println("The term \"" + term + "\" already exists.");
            return;
        }

        System.out.println("The definition of the card:");
        String def = scanner.nextLine();

        if (storage.hasDefinition(def)) {
            System.out.println("The definition \"" + def + "\" already exists.");
            return;
        }

        storage.addCard(term, def);
        System.out.println("The pair (\"" + term + "\":\"" + def + "\") has been added.");
    }

    void removeCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which card?");
        String term = scanner.nextLine();

        if (storage.removeCard(term)) {
            System.out.println("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + term + "\": there is no such card.");
        }
    }

    void importCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("File name:");
        String fileName = scanner.nextLine();
        int count = storage.importFromFile(fileName);
        if (count == -1) {
            System.out.println("File not found.");
        } else {
            System.out.println(count + " cards have been loaded.");
        }
    }

    void exportCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("File name:");
        String filename = scanner.nextLine();
        int count = storage.exportToFile(filename);
        if (count == -1) {
            System.out.println("Error saving file.");
        } else {
            System.out.println(count + " cards have been saved.");
        }
    }

    void askCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many times to ask?");
        int n = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < n; i++) {
            String term = storage.getRandomTerm();
            System.out.println("Print the definition of \"" + term + "\":");
            String answer = scanner.nextLine();

            if (storage.isCorrectDefinition(term, answer)) {
                System.out.println("Correct!");
            } else if (storage.hasDefinition(answer)) {
                String correctTerm = storage.getTermByDefinition(answer);
                System.out.println("Wrong. The right answer is \"" + storage.getDefinition(term) + "\", but your definition is correct for \"" + correctTerm + "\".");
            } else {
                System.out.println("Wrong. The right answer is \"" + storage.getDefinition(term) + "\".");
            }
        }
    }
}

class FlashCardStorage {
    private HashMap<String, String> cards = new HashMap<>();

    public String getRandomTerm() {
        ArrayList<String> terms = new ArrayList<>(cards.keySet());
        Random random = new Random();
        int index = random.nextInt(terms.size());
        return terms.get(index);
    }

    public boolean isCorrectDefinition(String term, String answer) {
        String correctDefinition = cards.get(term);
        return correctDefinition.equals(answer);
    }

    public String getDefinition(String term) {
        return cards.get(term);
    }

    public String getTermByDefinition(String def) {
        for (Map.Entry<String, String> entry : cards.entrySet()) {
            if (entry.getValue().equals(def)) {
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
        cards.put(term, def);
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
                String[] parts = line.split("::", 2);

                if (parts.length == 2) {
                    String term = parts[0];
                    String def = parts[1];
                    cards.put(term, def);
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
            for (Map.Entry<String, String> entry : cards.entrySet()) {
                writer.println(entry.getKey() + "::" + entry.getValue());
                count++;
            }
            writer.close();
            return count;
        } catch (IOException e) {
            return -1;
        }
    }
}
