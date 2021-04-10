package fr.antoninruan.mao.model;

import fr.antoninruan.mao.MainApp;
import javafx.scene.image.Image;

public class Card {

    public final static Card SPADE_AS = new Card(Suit.SPADE, Value.AS);
    public final static Card SPADE_TWO = new Card(Suit.SPADE, Value.TWO);
    public final static Card SPADE_THREE = new Card(Suit.SPADE, Value.THREE);
    public final static Card SPADE_FOUR = new Card(Suit.SPADE, Value.FOUR);
    public final static Card SPADE_FIVE = new Card(Suit.SPADE, Value.FIVE);
    public final static Card SPADE_SIX = new Card(Suit.SPADE, Value.SIX);
    public final static Card SPADE_SEVEN = new Card(Suit.SPADE, Value.SEVEN);
    public final static Card SPADE_EIGHT = new Card(Suit.SPADE, Value.EIGHT);
    public final static Card SPADE_NINE = new Card(Suit.SPADE, Value.NINE);
    public final static Card SPADE_TEN = new Card(Suit.SPADE, Value.TEN);
    public final static Card SPADE_JACK = new Card(Suit.SPADE, Value.JACK);
    public final static Card SPADE_QUEEN = new Card(Suit.SPADE, Value.QUEEN);
    public final static Card SPADE_KING = new Card(Suit.SPADE, Value.KING);

    public final static Card CLUB_AS = new Card(Suit.CLUB, Value.AS);
    public final static Card CLUB_TWO = new Card(Suit.CLUB, Value.TWO);
    public final static Card CLUB_THREE = new Card(Suit.CLUB, Value.THREE);
    public final static Card CLUB_FOUR = new Card(Suit.CLUB, Value.FOUR);
    public final static Card CLUB_FIVE = new Card(Suit.CLUB, Value.FIVE);
    public final static Card CLUB_SIX = new Card(Suit.CLUB, Value.SIX);
    public final static Card CLUB_SEVEN = new Card(Suit.CLUB, Value.SEVEN);
    public final static Card CLUB_EIGHT = new Card(Suit.CLUB, Value.EIGHT);
    public final static Card CLUB_NINE = new Card(Suit.CLUB, Value.NINE);
    public final static Card CLUB_TEN = new Card(Suit.CLUB, Value.TEN);
    public final static Card CLUB_JACK = new Card(Suit.CLUB, Value.JACK);
    public final static Card CLUB_QUEEN = new Card(Suit.CLUB, Value.QUEEN);
    public final static Card CLUB_KING = new Card(Suit.CLUB, Value.KING);

    public final static Card HEART_AS = new Card(Suit.HEART, Value.AS);
    public final static Card HEART_TWO = new Card(Suit.HEART, Value.TWO);
    public final static Card HEART_THREE = new Card(Suit.HEART, Value.THREE);
    public final static Card HEART_FOUR = new Card(Suit.HEART, Value.FOUR);
    public final static Card HEART_FIVE = new Card(Suit.HEART, Value.FIVE);
    public final static Card HEART_SIX = new Card(Suit.HEART, Value.SIX);
    public final static Card HEART_SEVEN = new Card(Suit.HEART, Value.SEVEN);
    public final static Card HEART_EIGHT = new Card(Suit.HEART, Value.EIGHT);
    public final static Card HEART_NINE = new Card(Suit.HEART, Value.NINE);
    public final static Card HEART_TEN = new Card(Suit.HEART, Value.TEN);
    public final static Card HEART_JACK = new Card(Suit.HEART, Value.JACK);
    public final static Card HEART_QUEEN = new Card(Suit.HEART, Value.QUEEN);
    public final static Card HEART_KING = new Card(Suit.HEART, Value.KING);

    public final static Card DIAMOND_AS = new Card(Suit.DIAMOND, Value.AS);
    public final static Card DIAMOND_TWO = new Card(Suit.DIAMOND, Value.TWO);
    public final static Card DIAMOND_THREE = new Card(Suit.DIAMOND, Value.THREE);
    public final static Card DIAMOND_FOUR = new Card(Suit.DIAMOND, Value.FOUR);
    public final static Card DIAMOND_FIVE = new Card(Suit.DIAMOND, Value.FIVE);
    public final static Card DIAMOND_SIX = new Card(Suit.DIAMOND, Value.SIX);
    public final static Card DIAMOND_SEVEN = new Card(Suit.DIAMOND, Value.SEVEN);
    public final static Card DIAMOND_EIGHT = new Card(Suit.DIAMOND, Value.EIGHT);
    public final static Card DIAMOND_NINE = new Card(Suit.DIAMOND, Value.NINE);
    public final static Card DIAMOND_TEN = new Card(Suit.DIAMOND, Value.TEN);
    public final static Card DIAMOND_JACK = new Card(Suit.DIAMOND, Value.JACK);
    public final static Card DIAMOND_QUEEN = new Card(Suit.DIAMOND, Value.QUEEN);
    public final static Card DIAMOND_KING = new Card(Suit.DIAMOND, Value.KING);

    private final Suit suit;
    private final Value value;
    private final Image image;

    public static Card getCard(Suit suit, Value value) {
        switch (suit) {
            case SPADE:
                switch (value) {

                    case AS:
                        return SPADE_AS;
                    case KING:
                        return SPADE_KING;
                    case QUEEN:
                        return SPADE_QUEEN;
                    case JACK :
                        return SPADE_JACK;
                    case TEN:
                        return SPADE_TEN;
                    case NINE:
                        return SPADE_NINE;
                    case EIGHT:
                        return SPADE_EIGHT;
                    case SEVEN:
                        return SPADE_SEVEN;
                    case SIX:
                        return SPADE_SIX;
                    case FIVE:
                        return SPADE_FIVE;
                    case FOUR:
                        return SPADE_FOUR;
                    case THREE:
                        return SPADE_THREE;
                    case TWO:
                        return SPADE_TWO;
                }
            case HEART:
                switch (value) {

                    case AS:
                        return HEART_AS;
                    case KING:
                        return HEART_KING;
                    case QUEEN:
                        return HEART_QUEEN;
                    case JACK :
                        return HEART_JACK;
                    case TEN:
                        return HEART_TEN;
                    case NINE:
                        return HEART_NINE;
                    case EIGHT:
                        return HEART_EIGHT;
                    case SEVEN:
                        return HEART_SEVEN;
                    case SIX:
                        return HEART_SIX;
                    case FIVE:
                        return HEART_FIVE;
                    case FOUR:
                        return HEART_FOUR;
                    case THREE:
                        return HEART_THREE;
                    case TWO:
                        return HEART_TWO;
                }
            case DIAMOND:
                switch (value) {

                    case AS:
                        return DIAMOND_AS;
                    case KING:
                        return DIAMOND_KING;
                    case QUEEN:
                        return DIAMOND_QUEEN;
                    case JACK :
                        return DIAMOND_JACK;
                    case TEN:
                        return DIAMOND_TEN;
                    case NINE:
                        return DIAMOND_NINE;
                    case EIGHT:
                        return DIAMOND_EIGHT;
                    case SEVEN:
                        return DIAMOND_SEVEN;
                    case SIX:
                        return DIAMOND_SIX;
                    case FIVE:
                        return DIAMOND_FIVE;
                    case FOUR:
                        return DIAMOND_FOUR;
                    case THREE:
                        return DIAMOND_THREE;
                    case TWO:
                        return DIAMOND_TWO;
                }
            case CLUB:
                switch (value) {
                    case AS:
                        return CLUB_AS;
                    case KING:
                        return CLUB_KING;
                    case QUEEN:
                        return CLUB_QUEEN;
                    case JACK :
                        return CLUB_JACK;
                    case TEN:
                        return CLUB_TEN;
                    case NINE:
                        return CLUB_NINE;
                    case EIGHT:
                        return CLUB_EIGHT;
                    case SEVEN:
                        return CLUB_SEVEN;
                    case SIX:
                        return CLUB_SIX;
                    case FIVE:
                        return CLUB_FIVE;
                    case FOUR:
                        return CLUB_FOUR;
                    case THREE:
                        return CLUB_THREE;
                    case TWO:
                        return CLUB_TWO;
                }
        }
        return null;
    }

    private Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
        this.image = new Image(MainApp.class.getClassLoader().getResource("card/" + suit.getId() + "-" + value.getId() + ".png").toString());
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Card{" +
                "suit=" + suit +
                ", value=" + value +
                '}';
    }

    public enum Suit {
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

    }

    public enum Value {
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

    }

}
