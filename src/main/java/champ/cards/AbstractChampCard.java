package champ.cards;

import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import champ.ChampChar;
import champ.ChampMod;
import champ.actions.OpenerReduceCostAction;
import champ.stances.AbstractChampStance;
import champ.stances.BerserkerStance;
import champ.stances.DefensiveStance;
import champ.stances.GladiatorStance;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.KeywordStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.NeutralStance;
import slimebound.SlimeboundMod;

import java.util.ArrayList;
import java.util.List;

import static champ.ChampMod.getModID;
import static champ.ChampMod.makeCardPath;


public abstract class AbstractChampCard extends CustomCard {

    protected final CardStrings cardStrings;
    protected final String NAME;
    protected String DESCRIPTION;
    protected String UPGRADE_DESCRIPTION;
    protected String[] EXTENDED_DESCRIPTION;

    public int cool;
    public int baseCool;
    public boolean upgradedCool;
    public boolean isCoolModified;

    public int myHpLossCost;

    public void resetAttributes() {
        super.resetAttributes();
        cool = baseCool;
        isCoolModified = false;
    }

    public void displayUpgrades() {
        super.displayUpgrades();
        if (upgradedCool) {
            cool = baseCool;
            isCoolModified = true;
        }
    }

    void upgradeCool(int amount) {
        baseCool += amount;
        cool = baseCool;
        upgradedCool = true;
    }


    public AbstractChampCard(final String id, final int cost, final CardType type, final CardRarity rarity, final CardTarget target) {
        super(id, "ERROR", getCorrectPlaceholderImage(id),
                cost, "ERROR", type, ChampChar.Enums.CHAMP_GRAY, rarity, target);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(id);
        name = NAME = cardStrings.NAME;
        originalName = NAME;
        rawDescription = DESCRIPTION = cardStrings.DESCRIPTION;
        UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
        EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
        initializeTitle();
        initializeDescription();
    }

    public AbstractChampCard(final String id, final int cost, final CardType type, final CardRarity rarity, final CardTarget target, final CardColor color) {
        super(id, "ERROR", getCorrectPlaceholderImage(id),
                cost, "ERROR", type, color, rarity, target);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(id);
        name = NAME = cardStrings.NAME;
        originalName = NAME;
        rawDescription = DESCRIPTION = cardStrings.DESCRIPTION;
        UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
        EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
        initializeTitle();
        initializeDescription();
    }

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> tips = new ArrayList<>();
        if (this.rawDescription.contains("champ:Fatigue")) { //TODO: FIX THIS FOR LOCALIZATION!!!
            tips.add(new TooltipInfo("Resolve", "You have #b1 #yStrength for every #b5 Resolve.")); //TODO: FIX
        }
        return tips;
    }

    private static String getCorrectPlaceholderImage(String id) {
        return makeCardPath(id.replaceAll((getModID() + ":"), "")) + ".png";
    }

    public static String makeID(String blah) {
        return getModID() + ":" + blah;
    }

    public static boolean gcombo() {
        return (AbstractDungeon.player.stance.ID.equals(GladiatorStance.STANCE_ID));
    }

    public static boolean bcombo() {
        return (AbstractDungeon.player.stance.ID.equals(BerserkerStance.STANCE_ID));
    }

    public static boolean dcombo() {
        return (AbstractDungeon.player.stance.ID.equals(DefensiveStance.STANCE_ID));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upp();
        }
    }

    public abstract void upp();

    protected void atb(AbstractGameAction action) {
        addToBot(action);
    }

    protected void att(AbstractGameAction action) {
        addToTop(action);
    }

    protected DamageInfo makeInfo() {
        return makeInfo(damageTypeForTurn);
    }

    private DamageInfo makeInfo(DamageInfo.DamageType type) {
        return new DamageInfo(AbstractDungeon.player, damage, type);
    }

    public void dmg(AbstractMonster m, AbstractGameAction.AttackEffect fx) {
        atb(new DamageAction(m, makeInfo(), fx));
    }

    public void allDmg(AbstractGameAction.AttackEffect fx) {
        atb(new DamageAllEnemiesAction(AbstractDungeon.player, multiDamage, damageTypeForTurn, fx));
    }

    public void blck() {
        atb(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, block));
    }

    private void makeInHand(AbstractCard c, int i) {
        atb(new MakeTempCardInHandAction(c, i));
    }

    public void makeInHand(AbstractCard c) {
        makeInHand(c, 1);
    }

    void shuffleIn(AbstractCard c, int i) {
        atb(new MakeTempCardInDrawPileAction(c, i, false, true));
    }

    public void shuffleIn(AbstractCard c) {
        shuffleIn(c, 1);
    }

    public ArrayList<AbstractMonster> monsterList() {
        ArrayList<AbstractMonster> q = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDying && !m.isDead) q.add(m);
        }
        return q;
    }

    public void applyToEnemy(AbstractMonster m, AbstractPower po) {
        atb(new ApplyPowerAction(m, AbstractDungeon.player, po, po.amount));
    }

    public void applyToSelf(AbstractPower po) {
        atb(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, po, po.amount));
    }

    public void loseHP(int amount) {
        atb(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, amount));
    }

    WeakPower autoWeak(AbstractMonster m, int i) {
        return new WeakPower(m, i, false);
    }

    VulnerablePower autoVuln(AbstractMonster m, int i) {
        return new VulnerablePower(m, i, false);
    }

    public void berserkOpen() {
        berserkerStance();
        if (AbstractDungeon.player.stance.ID.equals(NeutralStance.STANCE_ID)) {
            atb(new OpenerReduceCostAction());
        }
    }

    public void gladOpen() {
        gladiatorStance();
        if (AbstractDungeon.player.stance.ID.equals(NeutralStance.STANCE_ID)) {
            atb(new OpenerReduceCostAction());
        }
    }

    public void defenseOpen() {
        defensiveStance();
        if (AbstractDungeon.player.stance.ID.equals(NeutralStance.STANCE_ID)) {
            atb(new OpenerReduceCostAction());
        }
    }

    private void berserkerStance() {
        SlimeboundMod.logger.info("Switching to Berserker (Abstract)");
        atb(new ChangeStanceAction(BerserkerStance.STANCE_ID));
    }

    private void gladiatorStance() {
        SlimeboundMod.logger.info("Switching to Gladiator (Abstract)");
        atb(new ChangeStanceAction(GladiatorStance.STANCE_ID));
    }

    private void defensiveStance() {
        SlimeboundMod.logger.info("Switching to Defensive (Abstract)");
        atb(new ChangeStanceAction(DefensiveStance.STANCE_ID));
    }

    public void exitStance() {
        SlimeboundMod.logger.info("Switching to Neutral (Abstract)");
        atb(new ChangeStanceAction(NeutralStance.STANCE_ID));
    }

    public void techique() {
        if (AbstractDungeon.player.stance instanceof AbstractChampStance)
            ((AbstractChampStance) AbstractDungeon.player.stance).techique();
    }

    public void finisher() {
        if (AbstractDungeon.player.stance instanceof AbstractChampStance) {
            exitStance();
            ((AbstractChampStance) AbstractDungeon.player.stance).finisher();
            if (!(AbstractDungeon.player.stance instanceof GladiatorStance)) {
                addToBot(new PressEndTurnButtonAction());
            }
        }
    }
}