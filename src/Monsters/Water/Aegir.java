package Monsters.Water;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Water Barbaric King
 */
public class Aegir extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    private final int normalSpd;
    private final int normalDef;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Aegir()
    {
        super("Aegir" + count, WATER, 10_215, 571, 725, 103, 15, 50, 15, 25);
        setRunes(MonsterRunes.getRunesFromFile("Aegir1.csv", this));
        //Set normal values
        normalSpd = getSpd();
        normalDef = getDef();
        setAbilities();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Aegir(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 2, 0, Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50, 50);
        abilities.add(new Attack_Ability("Penalty (1)", 3.8 * 1.3, 0, 1, "Attacks the enemy with an axe to inflict 2 continuous damage effects with a 50% chance", ability1Debuffs, ability1DebuffChances, 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.BRAND, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        ArrayList<Buff> ability2Buffs = new ArrayList<>();
        ArrayList<Integer> ability2BuffChances = abilityChances(100);
        ability2Buffs.add(new IncAtkBar(50));
        abilities.add(new Attack_Ability("Wrathful Attack (2)", 6.2 * 1.25, 0, 1,
                "Attacks an enemy with a wrathful axe, leaving a Branding Effect for 2 turns, and increasing your Attack Bar by 50%. The target with the Branding effect will receive 25% increased damage. While under [Berserk] state, additionally " +
                "decreases the enemy's Attack Bar by 50%.", ability2Debuffs, ability2DebuffChances, ability2Buffs, ability2BuffChances, 2, false, false, false));
        
        abilities.add(new Attack_Ability("Confiscate (3)", 4 * 1.25, 0, 2, "Attacks the enemy 2 times, with each strike having a 75% chance to steal 1 beneficial effect from the enemy. Absorbs the Attack Bar by 50% each if you attack the " +
                                                                           "enemy with no beneficial effects. Goes under [Berserk] state for 3 turns afterwards. Under Berserk state, the defense is decreased by 30%, damage dealt to enemies is "
                                                                           + "increased by 100%, Attack Speed is increased by 20% and HP is recovered by 10% of the damage dealt.", 3, false, false, false));
        
        abilities.getLast().addBeneficialEffectRemoversOverride(Buff.BUFF_STEAL);
        
        abilities.add(new Leader_Skill(Stat.HP, 0.33, ALL));
        
        super.setAbilities(abilities);
    }
    
    @Override
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Check for berserk before turn
        Stat berserk = new Stat(4);
        berserk.setStatNum(Stat.BERSERK);
        boolean hasBerserk = containsOtherStat(Stat.BERSERK);
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        //Decrease target's attack bar
        if (abilityNum == 2)
        {
            if (hasBerserk)
            {
                target.decreaseAtkBarByPercent(50);
            }
        }
        //Apply Berserk
        if (abilityNum == 3)
        {
            //Remove Berserk if it exists
            if (hasBerserk)
            {
                removeOtherStat(Stat.BERSERK);
            }
            //Add Berserk
            addOtherStat(berserk);
            
            //Add Berserk effects
            if (!hasBerserk)
            {
                setAtk(getAtk() * 2);
                setSpd((int) Math.ceil(getSpd() * 1.2));
                setDef(Math.ceil(getDef() * 0.7));
            }
        }
        
        //Life steal
        if (hasBerserk && !containsDebuff(Debuff.UNRECOVERABLE))
        {
            setCurrentHp(Math.max(getMaxHp(), (int) (getCurrentHp() + getDmgDealtThisTurn() * 0.1)));
        }
        
        //Remove Berserk if it's effect is over
        berserk.decreaseTurn();
        if (berserk.getNumTurns() <= 0)
        {
            removeBerserk();
        }
        
        super.afterTurnProtocol(target, true);
        return true;
    }
    
    /**
     * Removes the Berserk effect and resets the stats
     */
    private void removeBerserk()
    {
        this.removeOtherStat(Stat.BERSERK);
        this.setAtk(1.0 * getAtk() / 2);
        this.setSpd(normalSpd);
        this.setDef(normalDef);
    }
    
    /**
     * Resets Aegir
     */
    public void reset()
    {
        if (this.containsOtherStat(Stat.BERSERK))
        {
            this.removeBerserk();
        }
        super.reset();
    }
    
    /**
     * Attempt to remove the target's beneficial effect if ability 3 was chosen
     *
     * @param target     The target Monster of the attack
     * @param abilityNum The chosen ability number
     */
    public void selfAfterHitProtocol(Monster target, int abilityNum)
    {
        if (abilityNum == 3 && new Random().nextInt(101) <= 75)
        {
            if (!target.getAppliedDebuffs().isEmpty())
            {
                this.stealBuff(target);
            }
            else if (resistanceCheck(target))
            {
                target.decreaseAtkBarByPercent(50);
                this.increaseAtkBarByPercent(50);
            }
        }
        
        super.selfAfterHitProtocol(target, abilityNum);
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead() && this.containsOtherStat(Stat.BERSERK))
        {
            this.removeBerserk();
        }
    }
}