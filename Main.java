package flashcards;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Card> cardsInUse = new ArrayList<>();
        System.out.println("Input the number of cards:");
        int numberOfCards = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numberOfCards; i++) {
            System.out.println("Card #" + (i + 1) + ":");
            String term = scanner.nextLine();
            System.out.println("The definition for card #" + (i + 1) + ":");
            String definition = scanner.nextLine();
            Card finishedCard = new Card(term, definition);
            cardsInUse.add(finishedCard);
        }

        for (int i = 0; i < numberOfCards; i++) {
            Card currentCard = cardsInUse.get(i);
            System.out.println("Print the definition of \"" + currentCard.term + "\":");
            if (scanner.nextLine().equals(currentCard.definition)) {
                System.out.println("Correct!");
            } else {
                System.out.println("Wrong. The right answer is \"" + currentCard.definition + "\".");
            }
        }
    }
}

class Card {
    String term;
    String definition;

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }
}