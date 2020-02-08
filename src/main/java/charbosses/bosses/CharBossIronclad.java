package charbosses.bosses;

import charbosses.cards.AbstractBossCard;
import charbosses.cards.AbstractBossDeckArchetype;
import charbosses.cards.EnemyCardGroup;
import charbosses.cards.red.*;
import charbosses.core.EnemyEnergyManager;
import charbosses.relics.*;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed;

import java.util.ArrayList;

public class CharBossIronclad extends AbstractCharBoss {

    public static final String ARCHETYPE_IC_STRIKE = "IC_STRIKE_ARCHETYPE";
    public static final String ARCHETYPE_IC_STRENGTH = "IC_STRENGTH_ARCHETYPE";
    public static final String ARCHETYPE_IC_BLOCK = "IC_BLOCK_ARCHETYPE";
    public static final String ARCHETYPE_IC_RAMPAGE = "IC_RAMPAGE_ARCHETYPE";
    public static ArrayList<AbstractBossCard> generallyGoodCards;
    public static ArrayList<AbstractBossCard> generallyEhCards;

    static {
        generallyGoodCards = new ArrayList<AbstractBossCard>();
        generallyGoodCards.add(new EnArmaments());
        generallyGoodCards.add(new EnTwinStrike());
        generallyGoodCards.add(new EnHeadbutt());
        generallyGoodCards.add(new EnIronWave());
        generallyGoodCards.add(new EnFlex());
        generallyGoodCards.add(new EnGhostlyArmor());
        generallyGoodCards.add(new EnSeeingRed());
        generallyGoodCards.add(new EnFlameBarrier());
        generallyGoodCards.add(new EnDisarm());
        generallyEhCards = new ArrayList<AbstractBossCard>();
        generallyEhCards.add(new EnAnger());
        generallyEhCards.add(new EnSentinel());
        generallyEhCards.add(new EnAnger());
        generallyEhCards.add(new EnSentinel());
        generallyEhCards.add(new EnCleave());
    }
    public CharBossIronclad() {
        super("Ironclad", "EvilWithin:Ironclad", 80, -4.0f, -16.0f, 220.0f, 290.0f, null, 0.0f, 0.0f, PlayerClass.IRONCLAD);
        this.energyOrb = new EnergyOrbRed();
        this.energy = new EnemyEnergyManager(3);
        this.loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0f);
        final AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Hit", "Idle", 0.1f);
        this.flipHorizontal = true;
        e.setTimeScale(0.6f);
        this.relicPool.add(new CBR_MagicFlower());
        this.relicPool.add(new CBR_RedSkull());
        this.relicPool.add(new CBR_StrikeDummy());
        this.startingRelic = new CBR_BurningBlood();
    }

    @Override
    public void generateDeck() {
        for (int i = 0; i < 5; i++) {
            this.masterDeck.addToTop(new EnStrikeRed());
            this.masterDeck.addToTop(new EnDefendRed());
        }
        this.masterDeck.addToTop(new EnBash());
        ArrayList<AbstractBossDeckArchetype> archetypes = new ArrayList<AbstractBossDeckArchetype>();
        archetypes.add(new ArchetypeIcStrike());
        archetypes.add(new ArchetypeIcStrength());
        archetypes.add(new ArchetypeIcRampage());
        archetypes.add(new ArchetypeIcBlock());
        this.chosenArchetype = archetypes.get(AbstractDungeon.monsterRng.random(archetypes.size() - 1));
        for (AbstractBossCard c : this.chosenArchetype.buildCardList()) {
            this.masterDeck.addToTop(c);
        }
        if (this.chosenArchetype.ID == ARCHETYPE_IC_RAMPAGE) {
            for (int i = 0; i < (AbstractDungeon.actNum - 1) * 2; i++) {
                this.masterDeck.group.remove(0);
            }
        }
        if (AbstractDungeon.actNum > 1) {
            ((EnemyCardGroup) this.masterDeck).getHighestUpgradeValueCard().upgrade();
        }
        for (int i = 0; i < AbstractDungeon.actNum; i++) {
            AbstractCard c = this.masterDeck.getRandomCard(false);
            if (c.canUpgrade()) {
                c.upgrade();
            } else {
                i -= 1;
            }
        }

    }

    public static class ArchetypeIcStrike extends AbstractBossDeckArchetype {

        public ArchetypeIcStrike() {
            super(ARCHETYPE_IC_STRIKE);
            this.allCards.add(new EnPerfectedStrike());
            this.allCards.add(new EnWildStrike());
            this.allCards.add(new EnTwinStrike());
            this.allCards.add(new EnHeadbutt());
            this.allCards.add(new EnDoubleTap());

            this.synergyRelics.add(new CBR_StrikeDummy());
        }

        @Override
        public ArrayList<AbstractBossCard> buildCardList() {
            ArrayList<AbstractBossCard> cards = new ArrayList<AbstractBossCard>();
            cards.add(new EnPerfectedStrike());
            for (int i = 0; i < 2 * AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards));
            }
            for (int i = 0; i < 4; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.COMMON));
            }
            for (int i = 0; i < AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.UNCOMMON));
            }
            return cards;
        }

    }

    public static class ArchetypeIcStrength extends AbstractBossDeckArchetype {

        public ArchetypeIcStrength() {
            super(ARCHETYPE_IC_STRENGTH);
            this.allCards.add(new EnTwinStrike());
            this.allCards.add(new EnInflame());
            this.allCards.add(new EnFlex());
            this.allCards.add(new EnSwordBoomerang());
            this.allCards.add(new EnDemonForm());
            this.allCards.add(new EnLimitBreak());

            this.synergyRelics.add(new CBR_Vajra());
            this.synergyRelics.add(new CBR_Girya());
            this.synergyRelics.add(new CBR_RedSkull());
        }

        @Override
        public ArrayList<AbstractBossCard> buildCardList() {
            ArrayList<AbstractBossCard> cards = new ArrayList<AbstractBossCard>();
            for (int i = 0; i < 2 + AbstractDungeon.actNum * 2; i++) {
                AbstractBossCard c = this.getRandomCard(cards);
                if (c.rarity != CardRarity.RARE || i % (5 - AbstractDungeon.actNum) == 0) {
                    cards.add(c);
                } else {
                    i -= 1;
                }
            }
            for (int i = 0; i < 3 - AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards, generallyEhCards));
            }
            for (int i = 0; i < 2 + AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.COMMON));
            }
            for (int i = 0; i < AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.UNCOMMON));
            }
            return cards;
        }

    }

    public static class ArchetypeIcRampage extends AbstractBossDeckArchetype {

        public ArchetypeIcRampage() {
            super(ARCHETYPE_IC_RAMPAGE);
            this.allCards.add(new EnRampage());
            this.allCards.add(new EnDoubleTap());
            this.allCards.add(new EnHeadbutt());
            this.allCards.add(new EnTrueGrit());
            this.allCards.add(new EnGhostlyArmor());

            this.synergyRelics.add(new CBR_Shuriken());
        }

        @Override
        public ArrayList<AbstractBossCard> buildCardList() {
            ArrayList<AbstractBossCard> cards = new ArrayList<AbstractBossCard>();
            cards.add(new EnRampage());
            cards.add(new EnHeadbutt());
            if (AbstractDungeon.actNum > 1) {
                cards.get(0).upgrade();
            }
            for (int i = 0; i < 1 + AbstractDungeon.actNum * 2; i++) {
                AbstractBossCard c = this.getRandomCard(cards);
                if (c.rarity != CardRarity.RARE || i % (5 - AbstractDungeon.actNum) == 0) {
                    cards.add(c);
                } else {
                    i -= 1;
                }
            }
            for (int i = 0; i < 3; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.COMMON));
            }
            for (int i = 0; i < AbstractDungeon.actNum - 1; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards, CardRarity.UNCOMMON));
            }
            return cards;
        }
    }

    public static class ArchetypeIcBlock extends AbstractBossDeckArchetype {

        public ArchetypeIcBlock() {
            super(ARCHETYPE_IC_BLOCK);
            this.allCards.add(new EnArmaments());
            this.allCards.add(new EnBodySlam());
            this.allCards.add(new EnIronWave());
            this.allCards.add(new EnGhostlyArmor());
            this.allCards.add(new EnImpervious());
            this.allCards.add(new EnEntrench());
            this.allCards.add(new EnMetallicize());
            this.allCards.add(new EnJuggernaut());

            this.synergyRelics.add(new CBR_SelfFormingClay());
            this.synergyRelics.add(new CBR_SmoothStone());
            this.synergyRelics.add(new CBR_CaptainsWheel());
        }

        @Override
        public ArrayList<AbstractBossCard> buildCardList() {
            ArrayList<AbstractBossCard> cards = new ArrayList<AbstractBossCard>();
            if (AbstractDungeon.actNum > 1) {
                cards.add(new EnBarricade());
                if (AbstractDungeon.actNum > 2) {
                    cards.get(0).upgrade();
                }
            } else {
                cards.add(new EnJuggernaut());
            }
            for (int i = 0; i < 2; i++) {
                cards.add(this.getRandomCard(cards, CardRarity.COMMON));
            }
            for (int i = 0; i < AbstractDungeon.actNum * 2; i++) {
                cards.add(this.getRandomCard(cards, CardRarity.RARE, true));
            }
            for (int i = 0; i < AbstractDungeon.actNum - 1; i++) {
                cards.add(this.getRandomCard(cards, CardRarity.RARE));
            }
            for (int i = 0; i < 3 - AbstractDungeon.actNum; i++) {
                cards.add(this.getRandomCard(cards, generallyGoodCards));
            }
            return cards;
        }
    }
}
