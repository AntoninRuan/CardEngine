package fr.antoninruan.mao.model;

import fr.antoninruan.mao.MainApp;
import javafx.scene.image.Image;

import java.util.Random;

public class Card {

    private Suit suit;
    private Value value;
    private Image image;

    public static Card getRandomCard() {
        Random random = new Random();
        int s = random.nextInt(4), v = random.nextInt(13);
        return new Card(Suit.fromInt(s), Value.fromInt(v));
    }

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
        this.image = new Image(MainApp.class.getClassLoader().getResource("card/" + suit.getId() + "-" + value.getId() + ".png").toString());
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    public Image getImage() {
        return image;
    }

    public boolean is(Suit suit, Value value) {
        return (this.suit == suit) && (this.value == value);
    }

    public boolean isFaceCard() {
        return (this.value == Value.KING) || (this.value == Value.QUEEN) || (this.value == Value.JACK);
    }

    @Override
    public String toString() {
        return "Card{" +
                "suit=" + suit +
                ", value=" + value +
                '}';
    }

    protected enum Suit {
        SPADE("S", 0),
        CLUB("C", 1),
        HEART("H", 2),
        DIAMOND("D", 3);

        String id;
        int n;

        Suit(String id, int n) {
            this.id = id;
            this.n = n;
        }

        public String getId() {
            return id;
        }

        private int getN() {
            return n;
        }

        public static Suit fromInt(int i) {
            for (Suit s : Suit.values()) {
                if (i == s.getN())
                    return s;
            }
            return SPADE;
        }

    }

    protected enum Value {
        AS("01", 1),
        KING("K", 0),
        QUEEN("Q", 12),
        JACK("J", 11),
        TEN("10", 10),
        NINE("09", 9),
        EIGHT("08", 8),
        SEVEN("07", 7),
        SIX("06", 6),
        FIVE("05", 5),
        FOUR("04", 4),
        THREE("03", 3),
        TWO("02", 2);

        String id;
        int n;

        Value(String id, int n) {
            this.id = id;
            this.n = n;
        }

        public String getId() {
            return id;
        }

        private int getN() {
            return n;
        }

        public static Value fromInt(int i) {
            for (Value s : Value.values()) {
                if (i == s.getN())
                    return s;
            }
            return AS;
        }

    }

}
