package Monsters.Fire;

import Abilities.*;
import Game.*;
import Monsters.*;
import Runes.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

import static Util.Util.CONSOLE_INTERFACE.OUTPUT.printfWithColor;

/**
 * Fire Grim Reaper
 */
public class Sath extends Monster
{
    private static int count = 1;
    
    private final int finalCritRate;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Sath()
    {
        this("Sath1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Sath(String runeFileName)
    {
        super("Sath" + count, Element.FIRE, 9_885, 505, 867, 91, 15, 50, 15, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        finalCritRate = this.getCritRate();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Grim Scythe (1)", 4.4 * 1.2, 0, 1, "Reaps the life of the enemy with a deadly scythe. " +
                                                                             "Acquires an additional turn if the enemy dies.", 0, false, false, false);
        
        ArrayList<Debuff> ability2Debuffs = abilityDebuffs(DebuffEffect.CONTINUOUS_DMG.getNum(), 2, 0);
        ArrayList<Integer> ability2DebuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Attack_Ability("Deadly Swing (2)", 2.7 * 1.2, 0, 1, "Attacks all enemies with a deadly scythe and " +
                                                                              "inflicts Continuous Damage for 2 turns. The critical Rate increases to 100% if the enemy's HP is 30% or lower", ability2Debuffs, ability2DebuffChances, 3, false, false, true);
        
        //@Passive:Creation
        Ability a3 = new Passive("Living Hell", "Increases the amount of damage all allies and enemies receive from Continuous Damage by two times. " +
                                                 "Disturbs the HP recovery for 2 turns with a 75% chance and inflicts Continuous Damage if you attack an enemy on your turn. If you attack an enemy who already has Continuous Damage, your attacks won't land as a Glancing Hit.");
        
        Ability a4 = new Leader_Skill(RuneAttribute.ATK, 0.3, Element.FIRE);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //@Passive
        //Prevent the attack from landing as a glancing hit if the target has continuous damage
        if (this.passiveCanActivate())
        {
            if (target.containsDebuff(DebuffEffect.CONTINUOUS_DMG))
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
                target.addAppliedDebuff(DebuffEffect.UNRECOVERABLE, 75, 2, this);
                target.addAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 100, 1, this);
            }
            
            //Gain an extra turn if the target died
            if (target.isDead())
            {
                if (isPrint())
                {
                    printfWithColor("Extra turn!", ConsoleColor.GREEN);
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
                if (m.containsDebuff(DebuffEffect.CONTINUOUS_DMG))
                {
                    setAbilityGlancingRateChange(-999_999);
                }
            }
            
            //Guarantee a crit if the target's HP ratio is less than 30%
            if (m.getHpRatio() < 30)
            {
                setCritRate(999_999);
            }
            attack(m, abilities.get(1));
            
            //@Passive
            //Apply unrecoverable and continuous damage
            if (this.passiveCanActivate())
            {
                m.addAppliedDebuff(DebuffEffect.UNRECOVERABLE, 75, 2, this);
                m.addAppliedDebuff(DebuffEffect.CONTINUOUS_DMG, 100, 1, this);
            }
        }
    }
}
