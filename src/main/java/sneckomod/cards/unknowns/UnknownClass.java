
package sneckomod.cards.unknowns;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import downfall.util.CardIgnore;
import sneckomod.SneckoMod;

import java.util.ArrayList;
import java.util.function.Predicate;

@CardIgnore
public class UnknownClass extends AbstractUnknownCard {
    public final static String ID = makeID("UnknownClass");
    private static String[] unknownClass = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public CardColor myColor;

    public UnknownClass(CardColor cardColor) {
        super(ID + cardColor.name(), determineCardImg(cardColor), CardType.SKILL, CardRarity.COMMON);
        myColor = cardColor;
        name = unknownClass[0];
        originalName = unknownClass[0];
        if (CardCrawlGame.isInARun() || CardCrawlGame.loadingSave) {
            rawDescription = unknownClass[1] + getCharName(myColor)
                    + unknownClass[2];
        } else {
            rawDescription = unknownClass[1] + unknownClass[3]
                    + unknownClass[2];
        }

        initializeDescription();
    }

    private static String determineCardImg(CardColor myColor) {
        switch (myColor.name()) {
            case "RED":
                return "UnknownIronclad";
            case "BLUE":
                return "UnknownDefect";
            case "GREEN":
                return "UnknownSilent";
            case "PURPLE":
                return "UnknownWatcher";
            case "GUARDIAN":
                return "UnknownGuardian";
            case "SLIMEBOUND":
                return "UnknownSlimeBoss";
            case "HEXA_GHOST_PURPLE":
                return "UnknownHexaghost";
            case "THE_CHAMP_GRAY":
                return "UnknownChamp";
            case "THE_BRONZE_AUTOMATON":
                return "UnknownAutomaton";
            default:
                return "UnknownModded";
        }
    }

    private static String getCharName(CardColor myColor) {
        ArrayList<AbstractPlayer> theDudes = new ArrayList<AbstractPlayer>(CardCrawlGame.characterManager.getAllCharacters());
        for (AbstractPlayer p : theDudes) {
            if (p.getCardColor() == myColor)
                return p.getLocalizedCharacterName().replace(unknownClass[4], "");
        }
        return myColor.name();
    }

    @Override
    public AbstractCard makeCopy() {
        return new UnknownClass(myColor);
    }

    @Override
    public Predicate<AbstractCard> myNeeds() {
        return c -> c.color == myColor;
    }

    @Override
    public ArrayList<String> myList() {
        for (int i = 0; i < SneckoMod.unknownClasses.size(); i++) {
            if (SneckoMod.unknownClasses.get(i).myColor== myColor) {
                return AbstractUnknownCard.unknownClassReplacements.get(i);
            }
        }
        return null;
    }
}

