package charbosses.relics;

import charbosses.bosses.AbstractCharBoss;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;

public class CBR_CursedKey extends AbstractCharbossRelic {
    public static final String ID = "Cursed Key";

    public CBR_CursedKey() {
        super("Cursed Key", "cursedKey.png", RelicTier.BOSS, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        if (AbstractCharBoss.boss != null) {
            return this.setDescription(AbstractCharBoss.boss.chosenClass);
        }
        return this.setDescription(null);
    }

    @Override
    public void justEnteredRoom(final AbstractRoom room) {
        if (room instanceof TreasureRoom) {
            this.flash();
            this.pulse = true;
        } else {
            this.pulse = false;
        }
    }

    private String setDescription(final AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }

    @Override
    public void updateDescription(final AbstractPlayer.PlayerClass c) {
        this.description = this.setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onEquip() {
        final EnergyManager energy = AbstractCharBoss.boss.energy;
        ++energy.energyMaster;
        AbstractCharBoss.boss.masterDeck.addToTop(AbstractCharBoss.getRandomCurse());
        if (AbstractDungeon.actNum > 2) {
            AbstractCharBoss.boss.masterDeck.addToTop(AbstractCharBoss.getRandomCurse());
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new CBR_CursedKey();
    }
}
