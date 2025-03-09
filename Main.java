package flashcards;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, String> termToDefinition = new HashMap<>();
        HashMap<String, String> definitionToTerm = new HashMap<>();
        System.out.println("Input the number of cards:");
        int numberOfCards = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numberOfCards; i++) {
            String term;
            do {
                System.out.println("Card #" + (i + 1) + ":");
                term = scanner.nextLine();
                System.out.println("The definition for card #" + (i + 1) + ":");
                if (termToDefinition.containsKey(term)) {
                    System.out.println("The term \"" + term + "\" already exists. Try again:");
                }
            } while (termToDefinition.containsKey(term));

            String definition;
            do {
                System.out.println("The definition for card #" + (i + 1) + ":");
                definition = scanner.nextLine();
                if (definitionToTerm.containsKey(definition)) {
                    System.out.println("The definition \"" + definition + "\" already exists. Try again:");
                }
            } while(definitionToTerm.containsKey(definition));

            termToDefinition.put(term, definition);
            definitionToTerm.put(definition, term);
        }

        for (String currentTerm : termToDefinition.keySet()) {
            String correctDefinition = termToDefinition.get(currentTerm);

            System.out.println("Print the definition of \"" + currentTerm + "\":");
            String userAnswer = scanner.nextLine();

            if (userAnswer.equals(correctDefinition)) {
                System.out.println("correct!");
            } else if (definitionToTerm.containsKey(userAnswer)) {
                String correctTerm = definitionToTerm.get(userAnswer);
                System.out.println("Wrong. The right answer is \"" + correctDefinition + "\", but your definition is correct for \"" + correctTerm + "\".");
            } else {
                System.out.println("Wrong. The right answer is \"" + correctDefinition + "\".");
            }
        }
    }
}
