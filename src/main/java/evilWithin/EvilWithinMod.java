package evilWithin;

/*

This package should contain all content additions strictly related to the
Evil Mode alternate gameplay run.  This includes Bosses, Events,
Event Override patches, and other things that only appear during Evil Runs.

 */

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import charbosses.actions.util.CharBossMonsterGroup;
import charbosses.bosses.Defect.CharBossDefect;
import charbosses.bosses.Ironclad.CharBossIronclad;
import charbosses.bosses.Merchant.CharBossMerchant;
import charbosses.bosses.Silent.CharBossSilent;
import charbosses.bosses.Watcher.CharBossWatcher;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import eventUtil.EventUtils;
import evilWithin.cards.KnowingSkullWish;
import evilWithin.events.*;
import evilWithin.monsters.*;
import evilWithin.potions.CursedFountainPotion;
import evilWithin.relics.KnowingSkull;
import evilWithin.relics.*;
import evilWithin.util.ReplaceData;
import expansioncontent.expansionContentMod;
import expansioncontent.patches.CenterGridCardSelectScreen;
import guardian.GuardianMod;
import slimebound.SlimeboundMod;
import sneckomod.SneckoMod;
import theHexaghost.HexaMod;

import javax.smartcardio.Card;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static evilWithin.patches.EvilModeCharacterSelect.evilMode;

@SpireInitializer
public class EvilWithinMod implements
        EditStringsSubscriber, EditKeywordsSubscriber, PostInitializeSubscriber, EditRelicsSubscriber, EditCardsSubscriber, PostUpdateSubscriber, StartGameSubscriber, StartActSubscriber {
    public static final String modID = "evil-within";

    public static boolean choosingBossRelic = false;
    public static boolean choosingRemoveCard = false;
    public static boolean choosingUpgradeCard = false;
    public static boolean choosingTransformCard = false;

    public static boolean replaceMenuColor = true;
    public static boolean tempAscensionHack = false;
    public static int tempAscensionOriginalValue = 0;

    @SpireEnum
    public static AbstractCard.CardTags CHARBOSS_ATTACK;
    @SpireEnum
    public static AbstractCard.CardTags CHARBOSS_SETUP;

    public static final boolean EXPERIMENTAL_FLIP = false;
    public static Settings.GameLanguage[] SupportedLanguages = {
            // Insert other languages here
            Settings.GameLanguage.ENG,
            Settings.GameLanguage.ZHS
    };
    public static ReplaceData[] wordReplacements;
    public static SpireConfig bruhData = null;

    public EvilWithinMod() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new EvilWithinMod();
    }

    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static String assetPath(String path) {
        return "evilWithinResources/" + path;
    }

    public static String assetPath(String path, otherPackagePaths otherPath) {
        switch (otherPath) {
            case PACKAGE_GUARDIAN: return "guardianResources/" + path;
            case PACKAGE_SLIME: return "slimeboundResources/" + path;
            case PACKAGE_SNECKO: return "sneckomodResources/" + path;
            case PACKAGE_HEXAGHOST: return "hexamodResources/" + path;
            case PACKAGE_EXPANSION: return "expansioncontentResources/" + path;
        }
        return "evilWithinResources/" + path;
    }

    public static void saveData() {
        try {
            if (bruhData == null) {
                bruhData = new SpireConfig("EvilWithin", "TrapSaveData");
            }
            GoldenIdol_Evil.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadOtherData() {
        try {
            bruhData = new SpireConfig("EvilWithin", "TrapSaveData");
            GoldenIdol_Evil.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makeLocalizationPath(Settings.GameLanguage language, String filename) {
        String langPath = getLangString();
        return assetPath("localization/" + langPath + "/" + filename + ".json");
    }

    private String makeLocalizationPath(Settings.GameLanguage language, String filename, otherPackagePaths otherPackage) {
        String langPath = getLangString();
        return assetPath("localization/" + langPath + "/" + filename + ".json", otherPackage);
    }

    private String getLangString() {
        for (Settings.GameLanguage lang : SupportedLanguages) {
            if (lang.equals(Settings.language)) {
                return Settings.language.name().toLowerCase();
            }
        }
        return "eng";
    }

    private void loadLocalization(Settings.GameLanguage language, Class<?> stringType) {
        SlimeboundMod.logger.info("loading loc:" + language + " evilWithin" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName()));

        SlimeboundMod.logger.info("loading loc:" + language + " PACKAGE_EXPANSION" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName(), otherPackagePaths.PACKAGE_EXPANSION));

        SlimeboundMod.logger.info("loading loc:" + language + " PACKAGE_GUARDIAN" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName(), otherPackagePaths.PACKAGE_GUARDIAN));

        SlimeboundMod.logger.info("loading loc:" + language + " PACKAGE_HEXAGHOST" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName(), otherPackagePaths.PACKAGE_HEXAGHOST));

        SlimeboundMod.logger.info("loading loc:" + language + " PACKAGE_SLIME" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName(), otherPackagePaths.PACKAGE_SLIME));

        SlimeboundMod.logger.info("loading loc:" + language + " PACKAGE_SNECKO" + stringType);
        BaseMod.loadCustomStringsFile(stringType, makeLocalizationPath(language, stringType.getSimpleName(), otherPackagePaths.PACKAGE_SNECKO));
    }

    private void loadLocalization(Settings.GameLanguage language) {

        loadLocalization(language, UIStrings.class);
        loadLocalization(language, EventStrings.class);
        loadLocalization(language, RelicStrings.class);
        loadLocalization(language, MonsterStrings.class);
        loadLocalization(language, PotionStrings.class);
        loadLocalization(language, CharacterStrings.class);
        loadLocalization(language, CardStrings.class);
        //loadLocalization(language, KeywordStrings.class);
        loadLocalization(language, OrbStrings.class);
        loadLocalization(language, RunModStrings.class);
        loadLocalization(language, PowerStrings.class);
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addCard(new KnowingSkullWish());
    }

    @Override
    public void receiveEditStrings() {
        loadLocalization(Settings.GameLanguage.ENG);
        if (Settings.language != Settings.GameLanguage.ENG) {
            loadLocalization(Settings.language);
        }

        try {
            String lang = getLangString();

            Gson gson = new Gson();
            String json = Gdx.files.internal(assetPath("localization/" + lang + "/replacementStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
            wordReplacements = gson.fromJson(json, ReplaceData[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadModKeywords(String modID, otherPackagePaths otherPath) {

        String lang = getLangString();
        SlimeboundMod.logger.info("loading loc:" + lang + " " + otherPath + " keywords");

        Gson gson = new Gson();
        String json = Gdx.files.internal(assetPath("localization/" + lang + "/KeywordStrings.json", otherPath)).readString(String.valueOf(StandardCharsets.UTF_8));

        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID + "", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditKeywords() {
        loadModKeywords(HexaMod.getModID(), otherPackagePaths.PACKAGE_HEXAGHOST);
        loadModKeywords(expansionContentMod.getModID(), otherPackagePaths.PACKAGE_EXPANSION);
        loadModKeywords(SneckoMod.getModID(), otherPackagePaths.PACKAGE_SNECKO);
        loadModKeywords(SlimeboundMod.getModID(), otherPackagePaths.PACKAGE_SLIME);
        loadModKeywords(GuardianMod.getModID(), otherPackagePaths.PACKAGE_GUARDIAN);

    }

    public void receivePostInitialize() {

        loadOtherData();

        this.initializeMonsters();
        this.addPotions();
        this.initializeEvents();

    }

    private void initializeEvents() {
        EventUtils.registerEvent(
                //Event ID//
                GremlinMatchGame_Evil.ID, GremlinMatchGame_Evil.class, true,
                //Event ID to Override//
                GremlinMatchGame.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                GremlinWheelGame_Evil.ID, GremlinWheelGame_Evil.class, true,
                //Event ID to Override//
                GremlinWheelGame.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event only used in Gremlin Wheel relic.  Is not initialized into any Act.
                GremlinWheelGame_Rest.ID, GremlinWheelGame_Rest.class, new String[]{""});

        EventUtils.registerEvent(
                //Event ID//
                WomanInBlue_Evil.ID, WomanInBlue_Evil.class, true,
                //Event ID to Override//
                WomanInBlue.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                LivingWall_Evil.ID, LivingWall_Evil.class, true,
                //Event ID to Override//
                LivingWall.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Augmenter_Evil.ID, Augmenter_Evil.class, true,
                //Event ID to Override//
                DrugDealer.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                BonfireSpirits_Evil.ID, BonfireSpirits_Evil.class, true,
                //Event ID to Override//
                Bonfire.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                GoldenShrine_Evil.ID, GoldenShrine_Evil.class, true,
                //Event ID to Override//
                GoldShrine.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                FaceTrader_Evil.ID, FaceTrader_Evil.class, true,
                //Event ID to Override//
                FaceTrader.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                CursedFountain.ID, CursedFountain.class, true,
                //Event ID to Override//
                FountainOfCurseRemoval.ID,
                //Other predicates//
                AbstractPlayer::isCursed,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                WeMeetAgain_Evil.ID, WeMeetAgain_Evil.class, true,
                //Event ID to Override//
                WeMeetAgain.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Designer_Evil.ID, Designer_Evil.class, true,
                //Event ID to Override//
                Designer.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);


        EventUtils.registerEvent(
                //Event ID//
                DeadGuy_Evil.ID, DeadGuy_Evil.class, true,
                //Event ID to Override//
                DeadAdventurer.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                ShiningLight_Evil.ID, ShiningLight_Evil.class, true,
                //Event ID to Override//
                ShiningLight.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                WorldOfGoop_Evil.ID, WorldOfGoop_Evil.class, true,
                //Event ID to Override//
                GoopPuddle.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Serpent_Evil.ID, Serpent_Evil.class, true,
                //Event ID to Override//
                Sssserpent.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                WingStatue_Evil.ID, WingStatue_Evil.class, true,
                //Event ID to Override//
                GoldenWing.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                GoldenIdol_Evil.ID, GoldenIdol_Evil.class, true,
                //Event ID to Override//
                GoldenIdol.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Cleric_Evil.ID, Cleric_Evil.class, true,
                //Event ID to Override//
                Cleric.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                CouncilOfGhosts_Evil.ID, CouncilOfGhosts_Evil.class, true,
                //Event ID to Override//
                Ghosts.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                CursedTome_Evil.ID, CursedTome_Evil.class, true,
                //Event ID to Override//
                CursedTome.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                ForgottenAltar_Evil.ID, ForgottenAltar_Evil.class, true,
                //Event ID to Override//
                ForgottenAltar.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Bandits_Evil.ID, Bandits_Evil.class, true,
                //Event ID to Override//
                MaskedBandits.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                KnowingSkull_Evil.ID, KnowingSkull_Evil.class, true,
                //Event ID to Override//
                KnowingSkull.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Vagrant_Evil.ID, Vagrant_Evil.class, true,
                //Event ID to Override//
                Addict.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Mausoleum_Evil.ID, Mausoleum_Evil.class, true,
                //Event ID to Override//
                TheMausoleum.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Beggar_Evil.ID, Beggar_Evil.class, true,
                //Event ID to Override//
                Beggar.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                TheNest_Evil.ID, TheNest_Evil.class, true,
                //Event ID to Override//
                Nest.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Colosseum_Evil.ID, Colosseum_Evil.class, true,
                //Event ID to Override//
                Colosseum.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                MindBloom_Evil.ID, MindBloom_Evil.class, true,
                //Event ID to Override//
                MindBloom.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                MoaiHead_Evil.ID, MoaiHead_Evil.class, true,
                //Event ID to Override//
                MoaiHead.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Nloth_Evil.ID, Nloth_Evil.class, true,
                //Event ID to Override//
                Nloth.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                SensoryStone_Evil.ID, SensoryStone_Evil.class, true,
                //Event ID to Override//
                SensoryStone.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Portal_Evil.ID, Portal_Evil.class, true,
                //Event ID to Override//
                SecretPortal.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                TombRedMask_Evil.ID, TombRedMask_Evil.class, true,
                //Event ID to Override//
                TombRedMask.ID,
                //Other predicates//
                (c) -> c.hasRelic(RedIOU.ID),
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                WindingHalls_Evil.ID, WindingHalls_Evil.class, true,
                //Event ID to Override//
                WindingHalls.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(
                //Event ID//
                Joust_Evil.ID, Joust_Evil.class, true,
                //Event ID to Override//
                TheJoust.ID,
                //Event Type//
                EventUtils.EventType.FULL_REPLACE);

        EventUtils.registerEvent(BossTester.ID, BossTester.class, new String[]{""});
    }

    public static ArrayList<String> possEncounterList = new ArrayList<>();

    private void initializeMonsters() {

        BaseMod.addMonster(LadyInBlue.ID, LadyInBlue::new);

        BaseMod.addMonster(Augmenter.ID, Augmenter::new);

        BaseMod.addMonster(FleeingMerchant.ID, FleeingMerchant::new);

        BaseMod.addMonster("EvilWithin:CharBossMerchant", () -> new CharBossMonsterGroup(new AbstractMonster[]{new CharBossMerchant()}));

        BaseMod.addMonster(evilWithin.monsters.FaceTrader.ID, evilWithin.monsters.FaceTrader::new);

        BaseMod.addMonster("EvilWithin:Heads", "Living Wall Heads", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new ChangingTotem(),
                        new ForgetfulTotem(),
                        new GrowingTotem(),
                }));

        BaseMod.addMonster("EvilWithin:CharBossIronclad", () -> new CharBossMonsterGroup(new AbstractMonster[]{new CharBossIronclad()}));
        BaseMod.addMonster("EvilWithin:CharBossSilent", () -> new CharBossMonsterGroup(new AbstractMonster[] { new CharBossSilent() }));
        BaseMod.addMonster("EvilWithin:CharBossDefect", () -> new CharBossMonsterGroup(new AbstractMonster[]{new CharBossDefect()}));
        BaseMod.addMonster("EvilWithin:CharBossWatcher", () -> new CharBossMonsterGroup(new AbstractMonster[]{new CharBossWatcher()}));
    }

    public void addPotions() {

        BaseMod.addPotion(CursedFountainPotion.class, Color.PURPLE, Color.MAROON, Color.BLACK, CursedFountainPotion.POTION_ID);

    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new ShatteredFragment(), RelicType.SHARED);
        BaseMod.addRelic(new BrokenWingStatue(), RelicType.SHARED);
        BaseMod.addRelic(new CloakOfManyFaces(), RelicType.SHARED);
        BaseMod.addRelic(new GremlinSack(), RelicType.SHARED);
        BaseMod.addRelic(new GremlinWheel(), RelicType.SHARED);
        BaseMod.addRelic(new RedIOU(), RelicType.SHARED);
        BaseMod.addRelic(new RedIOUUpgrade(), RelicType.SHARED);
        BaseMod.addRelic(new KnowingSkull(), RelicType.SHARED);
        BaseMod.addRelic(new HeartBlessingBlue(), RelicType.SHARED);
        BaseMod.addRelic(new HeartBlessingGreen(), RelicType.SHARED);
        BaseMod.addRelic(new HeartBlessingRed(), RelicType.SHARED);
        BaseMod.addRelic(new TeleportStone(), RelicType.SHARED);
        BaseMod.addRelic(new HeartsMalice(), RelicType.SHARED);
    }

    @Override
    public void receivePostUpdate() {
        if (choosingBossRelic && AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2F, Settings.HEIGHT / 2F, RelicLibrary.getRelic(AbstractDungeon.gridSelectScreen.selectedCards.get(0).cardID));
            choosingBossRelic = false;
            CenterGridCardSelectScreen.centerGridSelect = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (choosingUpgradeCard && AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));// 54
            card.upgrade();
            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));// 59
            choosingUpgradeCard = false;
            CenterGridCardSelectScreen.centerGridSelect = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (choosingRemoveCard && AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            card.untip();// 73
            card.unhover();// 74
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2.0F));// 75
            AbstractDungeon.player.masterDeck.removeCard(card);// 78
            choosingRemoveCard = false;
            CenterGridCardSelectScreen.centerGridSelect = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        if (choosingTransformCard && AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.player.masterDeck.removeCard(c);// 79
            AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);// 80
            AbstractCard transCard = AbstractDungeon.getTransformedCard();// 81
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));// 82
            choosingTransformCard = false;
            CenterGridCardSelectScreen.centerGridSelect = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
    }

    @Override
    public void receiveStartGame() {
        if (!CardCrawlGame.loadingSave) {
            possEncounterList.clear();
            possEncounterList.add("EvilWithin:CharBossIronclad");
            possEncounterList.add("EvilWithin:CharBossSilent");
            possEncounterList.add("EvilWithin:CharBossDefect");
            possEncounterList.add("EvilWithin:CharBossWatcher");
        }
    }

    @Override
    public void receiveStartAct() {
        if (evilMode) {
            Method setBoss = null;
            try {
                AbstractDungeon.bossKey = possEncounterList.remove(AbstractDungeon.cardRandomRng.random(possEncounterList.size() - 1));
                setBoss = AbstractDungeon.class.getDeclaredMethod("setBoss", String.class);
                setBoss.setAccessible(true);
                setBoss.invoke(CardCrawlGame.dungeon, AbstractDungeon.bossKey);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public enum otherPackagePaths {
        PACKAGE_SLIME,
        PACKAGE_GUARDIAN,
        PACKAGE_HEXAGHOST,
        PACKAGE_SNECKO,
        PACKAGE_EXPANSION;

        otherPackagePaths() {
        }
    }
}
