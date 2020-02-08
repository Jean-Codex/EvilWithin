package expansioncontent.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import expansioncontent.expansionContentMod;
import theHexaghost.util.CardIgnore;

@CardIgnore
public class OctoChoiceCard extends CustomCard {
    private static final int COST = -2;

    public OctoChoiceCard(String id, String name, String IMG, String description) {
        super(id, name, IMG, COST, description, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);

    }

    private static String makeID(String id) {
        return expansionContentMod.makeID("QuickStudy" + id);
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }

    @Override
    public void upgrade() {

    }
}