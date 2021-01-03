package sneckomod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import sneckomod.SneckoMod;
import sneckomod.cards.unknowns.UnknownClass;
import theHexaghost.util.TextureLoader;

public class SneckoBoss extends CustomRelic implements CustomSavable<String> {

    public static final String ID = SneckoMod.makeID("SneckoBoss");
    private static final Texture IMG = TextureLoader.getTexture(SneckoMod.makeRelicPath("SealOfApproval.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(SneckoMod.makeRelicOutlinePath("SealOfApproval.png"));

    public SneckoBoss() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    public static String chosenChar = "UNCHOSEN";
    public static AbstractCard.CardColor myColor = null;
    private boolean chosenInGeneral = true;

    @Override
    public void onEquip() {
        if (!chosenChar.equals("UNCHOSEN") && AbstractDungeon.player.hasRelic(SneckoCommon.ID)) { // Already got Seal of Approval
            for (AbstractCard c : AbstractDungeon.commonCardPool.group) { // This can only be accessed while the common card pool has exactly one UClass
                if (c instanceof UnknownClass) {
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.2F, Settings.HEIGHT / 2F));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.35F, Settings.HEIGHT / 2F));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.5F, Settings.HEIGHT / 2F));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.65F, Settings.HEIGHT / 2F));
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.8F, Settings.HEIGHT / 2F));
                }
            }
        } else {
            chosenInGeneral = false;
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }

            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            CardGroup c = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard q : AbstractDungeon.commonCardPool.group) {
                if (q instanceof UnknownClass) {
                    c.addToTop(q);
                }
            }
            AbstractDungeon.gridSelectScreen.open(c, 1, false, "Choose."); //TODO: Localize
        }
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && !chosenInGeneral) {
            chosenInGeneral = true;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            chosenChar = SneckoMod.getClassFromColor(((UnknownClass) c).myColor);
            myColor = ((UnknownClass) c).myColor;
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.2F, Settings.HEIGHT / 2F));
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.35F, Settings.HEIGHT / 2F));
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.5F, Settings.HEIGHT / 2F));
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.65F, Settings.HEIGHT / 2F));
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c.makeCopy(), Settings.WIDTH * 0.8F, Settings.HEIGHT / 2F));
            AbstractDungeon.commonCardPool.group.removeIf(q -> q instanceof UnknownClass && !q.cardID.equals(c.cardID));
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    public String getUpdatedDescription() {
        if (!SneckoBoss.chosenChar.equals("UNCHOSEN")) { //I sure hope no one makes a character called The UNCHOSEN.
            return DESCRIPTIONS[1] + SneckoBoss.chosenChar + DESCRIPTIONS[2] + SneckoBoss.chosenChar + DESCRIPTIONS[3];
        }
        return DESCRIPTIONS[0];
    }

    @Override
    public String onSave() {
        return chosenChar;
    }

    @Override
    public void onLoad(String s) {
        chosenChar = s;
    }

    @Override
    public void onVictory() {
        AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(myColor));
    }
}