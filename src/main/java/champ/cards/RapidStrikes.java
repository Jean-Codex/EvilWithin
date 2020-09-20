package champ.cards;

import champ.ChampMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RapidStrikes extends AbstractChampCard {

    public final static String ID = makeID("RapidStrikes");

    //stupid intellij stuff attack, enemy, uncommon

    private static final int DAMAGE = 3;

    public RapidStrikes() {
        super(ID, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        baseDamage = DAMAGE;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        finisher();
        int x = AbstractDungeon.cardRandomRng.random(0, 2);
        int q = ChampMod.techniquesThisTurn + 1;
        if (gcombo() && upgraded) q += 2;
        for (int i = 0; i < q; i++) {
            AbstractGameAction.AttackEffect r = null;
            switch (x) {
                case 0:
                    r = AbstractGameAction.AttackEffect.SLASH_DIAGONAL;
                    break;
                case 1:
                    r = AbstractGameAction.AttackEffect.SLASH_HORIZONTAL;
                    break;
                case 2:
                    r = AbstractGameAction.AttackEffect.SLASH_VERTICAL;
                    break;
            }
            dmg(m, r);
        }
    }

    public void upp() {
        rawDescription = UPGRADE_DESCRIPTION;
        initializeDescription();
    }
}