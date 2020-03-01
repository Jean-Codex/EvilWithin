package charbosses.relics.EventRelics;

import charbosses.bosses.AbstractBossDeckArchetype;
import charbosses.bosses.AbstractCharBoss;
import charbosses.cards.AbstractBossCard;
import charbosses.relics.AbstractCharbossRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import evilWithin.EvilWithinMod;

import java.util.ArrayList;


public class CBR_WeMeetAgain extends AbstractCharbossRelic {
    public static String ID = EvilWithinMod.makeID("Transmogrifier");
    private static RelicTier tier = RelicTier.SPECIAL;
    private static LandingSound sound = LandingSound.MAGICAL;
    private String removedName;
    private String addedName;

    public CBR_WeMeetAgain() {
        super(ID, tier, sound, new Texture(EvilWithinMod.assetPath("images/relics/transmogrifier.png")));
    }

    @Override
    public void modifyCardsOnCollect(ArrayList<AbstractBossCard> list, int actIndex) {
        AbstractBossCard cardToRemove;
        ArrayList<AbstractBossCard> nonRareCards = new ArrayList<>();

        for (AbstractBossCard c : list) {
            if (c.rarity != AbstractCard.CardRarity.RARE && c.rarity != AbstractCard.CardRarity.CURSE) {
                nonRareCards.add(c);
            }
        }
        cardToRemove = nonRareCards.get(AbstractDungeon.cardRng.random(0, nonRareCards.size() - 1));
        AbstractBossDeckArchetype.logger.info("We Meet Again event removed 1 " + cardToRemove.name + ".");

        removedName = cardToRemove.name;
        list.remove(cardToRemove);


        addedName = AbstractCharBoss.boss.chosenArchetype.addRandomGlobalRelic(actIndex, AbstractCharBoss.boss, list);


        this.description = getUpdatedDescription();
        refreshDescription();
    }

    @Override
    public String getUpdatedDescription() {
        return this.removedName + this.DESCRIPTIONS[0] + this.addedName + ".";
    }

    @Override
    public AbstractRelic makeCopy() {
        return new CBR_WeMeetAgain();
    }
}