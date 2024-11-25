package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Debuffs.*;
import Stats.*;
import java.util.*;

/**
 * Fire Grim Reaper
 */
public class Sath extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    
    private static int count = 1;
    
    private final int finalCritRate;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Sath()
    {
        super("Sath" + count, FIRE, 9_885, 505, 867, 91, 15, 50, 15, 0);
        setRunes(MonsterRunes.getRunesFromFile("Sath1.csv", this));
        setAbilities();
        finalCritRate = this.getCritRate();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Sath(String runeFileName)
    {
        this();
        super.setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        
        abilities.add(new Attack_Ability("Grim Scythe (1)", 4.4 * 1.2, 0, 1, "Reaps the life of the enemy with a deadly scythe. " +
                                                                             "Acquires an additional turn if the enemy dies.", 0, false, false, false));
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(Debuff.CONTINUOUS_DMG, 2, 0);
        ArrayList<Integer> ability2DebuffChances = abilityChances(100);
        abilities.add(new Attack_Ability("Deadly Swing (2)", 2.7 * 1.2, 0, 1, "Attacks all enemies with a deadly scythe and " +
                                                                              "inflicts Continuous Damage for 2 turns. Always lands a Critical Hit if the enemy's HP is 30% or lower.", ability2Debuffs, ability2DebuffChances, 3, false, false, true));
        
        //@Passive:Creation
        abilities.add(new Passive("Living Hell", "Increases the amount of damage all allies and enemies receive from Continuous Damage by two times. " +
                                                 "Disturbs the HP recovery for 2 turns with a 75% chance and inflicts Continuous Damage if you attack an enemy on your turn. If you attack an enemy who already has Continuous Damage, your attacks won't land as a Glancing Hit."));
        
        abilities.add(new Leader_Skill(Stat.ATK, 0.3, FIRE));
        
        
        super.setAbilities(abilities);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //@Passive
        //Prevent the attack from landing as a glancing hit if the target has continuous damage
        if (this.passiveCanActivate())
        {
            if (target.containsDebuff(Debuff.CONTINUOUS_DMG))
            {
                setAbilityGlancingRateChange(-999_999);
            }
        }
        
        if (abilityNum == 2 && abilities.get(1).getTurnsRemaining() == 0 && target.getHpRatio() < 30)
        {
            setCritRate(10_000);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        
        //@Passive
        //Reset crit rate and glancing rate change
        setAbilityGlancingRateChange(0);
        setCritRate(finalCritRate);
        
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 1)
        {
            //@Passive
            //Apply unrecoverable and DOT
            if (this.passiveCanActivate())
            {
                target.addAppliedDebuff(Debuff.UNRECOVERABLE, 75, 2, this);
                target.addAppliedDebuff(Debuff.CONTINUOUS_DMG, 100, 1, this);
            }
            
            //Gain an extra turn if the target died
            if (target.isDead())
            {
                if (isPrint())
                {
                    System.out.printf("%sExtra turn!%s%n", ConsoleColors.GREEN, ConsoleColors.RESET);
                }
                setAtkBar(2_000);
            }
        }
        
        super.afterTurnProtocol((abilityNum == 1) ? target : game.getOtherTeam(), true);
        
        //Reset crit rate and glancing rate change
        setCritRate(finalCritRate);
        setAbilityGlancingRateChange(0);
        return true;
    }
    
    public void attackTeam(Team target, Ability ability)
    {
        for (Monster m : target.getMonsters())
        {
            if (m.isDead())
            {
                continue;
            }
            
            //Reset crit rate and glancing rate change
            setCritRate(finalCritRate);
            setAbilityGlancingRateChange(0);
            
            //@Passive
            //Prevent attack from landing as a glancing hit if the target has continuous damage
            if (this.passiveCanActivate())
            {
                if (m.containsDebuff(Debuff.CONTINUOUS_DMG))
                {
                    setAbilityGlancingRateChange(-999_999);
                }
            }
            
            //Guarantee a crit if the target's HP ratio is less than 30%
            if (m.getHpRatio() < 30)
            {
                setCritRate(999_999);
            }
            attack(m, abilities.get(1), false);
            
            //@Passive
            //Apply unrecoverable and continuous damage
            if (this.passiveCanActivate())
            {
                m.addAppliedDebuff(Debuff.UNRECOVERABLE, 75, 2, this);
                m.addAppliedDebuff(Debuff.CONTINUOUS_DMG, 100, 1, this);
            }
        }
    }
}