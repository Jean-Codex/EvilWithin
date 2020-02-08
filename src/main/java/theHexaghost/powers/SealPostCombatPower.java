package theHexaghost.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import theHexaghost.HexaMod;
import theHexaghost.util.SealSealReward;
import theHexaghost.util.TextureLoader;

public class SealPostCombatPower extends AbstractPower implements CloneablePowerInterface, RemoveMeBabey {

    public static final String POWER_ID = HexaMod.makeID("SealPostCombatPower");

    private static final Texture tex84 = TextureLoader.getTexture(HexaMod.getModID() + "Resources/images/powers/SealAfterCombat84.png");
    private static final Texture tex32 = TextureLoader.getTexture(HexaMod.getModID() + "Resources/images/powers/SealAfterCombat32.png");

    public SealPostCombatPower(final int amount) {
        this.name = "Post-Combat Seal";
        this.ID = POWER_ID;
        this.owner = AbstractDungeon.player;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        if (amount > 1)
            description = "At the end of combat, obtain #b" + amount + " additional #ySeal cards.";
        else
            description = "At the end of combat, obtain #b" + amount + " additional #ySeal card.";
    }

    @Override
    public void onVictory() {
        AbstractDungeon.getCurrRoom().addCardReward(new SealSealReward(AbstractDungeon.player.getCardColor()));
    }

    @Override
    public AbstractPower makeCopy() {
        return new SealPostCombatPower(amount);
    }
}