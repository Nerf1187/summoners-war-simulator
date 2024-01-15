package Monsters.Water;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;


public class Aegir extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    public Aegir()
    {
        super("Aegir" + count, WATER, 10_215, 571, 725, 103, 15, 50, 15, 25);
        setRunes(MonsterRunes.getRunesFromFile("Aegir1.csv", this));
        setAbilities();
        count++;
    }
    
    public Aegir(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 2, 0, Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability1DebuffChances = abilityChances(50, 50);
        abilities.add(new Attack_Ability("Penalty (1)", 3.8 * 1.3, 0, 1, "Attacks the enemy with an axe to inflict 2 continuous " + "damage effects with a 50% chance", ability1Debuffs, ability1DebuffChances, 0, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.BRAND, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Wrathful Attack (2)", 6.2 * 1.25, 0, 1,
                "Attacks an enemy with a wrathful axe, leaving " + "a Branding Effect for 2 turns, and increasing your Attack Bar by 50%. The target with the Branding effect will " + "receive 25% increased " + "damage.", ability2Debuffs,
                ability2DebuffChances, 2, false, false));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.BUFF_STEAL, 0);
        ArrayList<Integer> ability3BuffsChances = abilityChances(75);
        abilities.add(new Attack_Ability("Confiscate (3)", 4 * 1.25, 0, 2, "Attacks the enemy 2 times, with each strike having " + "a 75% chance to steal 1 beneficial effect from the enemy. Absorbs the Attack Bar by 50% each if you attack the " +
                "enemy with no beneficial" + " effects. Goes under " + "[Berserk] state for 3 turns afterwards. Under Berserk state, the MAX HP is decreased by 30%, damage dealt to enemies is decreased by " + "30%, damage dealt to enemies " + "is "
                + "increased by 100%, Attack Speed is increased by 20% and HP is recovered by 10% of the damage dealt.", ability3Buffs, ability3BuffsChances, 3, false, false, 0));
        
        abilities.add(new Leader_Skill(Stat.HP, 0.33, ALL));
        
        super.setAbilities(abilities);
    }
    
    @Override
    public boolean nextTurn(Monster target, int abilityNum)
    {
        Stat berserk = new Stat(4);
        berserk.setStatNum(Stat.BERSERK);
        boolean hasBerserk = containsOtherStat(berserk);
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        if (abilityNum == 2)
        {
            increaseAtkBarByPercent(50);
        }
        if (abilityNum == 3)
        {
            if (hasBerserk)
            {
                removeOtherStat(berserk);
            }
            addOtherStat(berserk);
            
            if (!hasBerserk)
            {
                setAtk(getAtk() * 2);
                setSpd((int) (getSpd() * 1.2));
                setMaxHp((int) (getMaxHp() * 0.7));
                setCurrentHp((int) (getCurrentHp() * 0.7));
            }
        }
        
        if (hasBerserk && !containsDebuff(new Debuff(Debuff.UNRECOVERABLE, 1, 0)))
        {
            setCurrentHp(Math.max(getMaxHp(), (int) (getCurrentHp() + getDmgDealtThisTurn() * 0.1)));
        }
        
        
        berserk.decreaseTurn();
        if (berserk.getNumTurns() <= 0)
        {
            removeOtherStat(berserk);
            setAtk(1.0 * getAtk() / 2);
            setSpd((int) (getSpd() * 5.0 / 6));
            setMaxHp((int) ((getMaxHp() * 10.0 / 3) + 0.5));
            setCurrentHp((int) ((getCurrentHp() * 10 / 7) + 0.5));
        }
        super.afterTurnProtocol(target, true);
        return true;
    }
}