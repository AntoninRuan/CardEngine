package fr.antoninruan.mao.model.cardcontainer;

import fr.antoninruan.mao.model.Card;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Objects;

public abstract class CardContainer {

    protected final ArrayList<Card> keys = new ArrayList<>();

    protected Pane container;

    public CardContainer(Pane container) {
        this.container = container;
    }

    public void setContainer(Pane container) {
        this.container = container;
    }

    public ArrayList<Card> getCards() {
        return keys;
    }

    public void add(Card card) {
        this.keys.add(card);
    }

    public void remove(Card card) {
        this.keys.remove(card);
    }

    public abstract void moveCardTo(Card card, CardContainer dest);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardContainer cardContainer = (CardContainer) o;
        return keys.equals(cardContainer.keys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }

}
