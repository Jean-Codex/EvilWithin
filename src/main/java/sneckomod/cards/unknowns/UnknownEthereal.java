package sneckomod.cards.unknowns;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.function.Predicate;

public class UnknownEthereal extends AbstractUnknownCard {
    public final static String ID = makeID("UnknownEthereal");

    public UnknownEthereal() {
        super(ID, CardType.SKILL, CardRarity.UNCOMMON);
    }

    @Override
    public Predicate<AbstractCard> myNeeds() {
        return c -> c.isEthereal;
    }
}
