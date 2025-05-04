package Monsters.Wind;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Panda Warrior
 */
public class Feng_Yan extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Feng_Yan()
    {
        this("Feng_Yan1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Feng_Yan(String runeFileName)
    {
        super("Feng Yan" + count, Element.WIND, 10_215, 801, 659, 96, 15, 50, 15, 25);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.DEC_DEF.getNum(), 2, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(50);
        Ability a1 = new Attack_Ability("Sequential Attack (1)", 1.3 * 1.25, 0, 3, "Attacks the enemy " +
                                                                                    "3 times with each strike having a 30% chance to decrease the target's Defense for 2 turns.", ability1Debuffs, ability1DebuffChances,
                0, false, false, false);
        
        ArrayList<Buff> ability2Buffs = MONSTERS.abilityBuffs(BuffEffect.RECOVERY.getNum(), 2);
        ArrayList<Integer> ability2BuffChances = MONSTERS.abilityChances(100);
        //Apply self effects in nextTurn() because this ability targets the entire team
        Ability a2 = new Heal_Ability("Calm Mind (2)", 0, 1, "Removes the harmful effects casted on yourself " +
                                                              "and counterattacks for 2 turns when attacked. Recovers 15% of the HP of all allies in each turn for the next 2 turns.", ability2Buffs, ability2BuffChances, 3, true);
        
        //@Passive:Creation
        Ability a3 = new Passive("Wind and Clouds", "Your Attack Bar increases by 20% whenever you are attacked. Your attacks will " +
                                                     "inflict additional damage that is proportionate to your Defense whenever you attack.");
        
        Ability a4 = new Leader_Skill(RuneAttribute.DEF, 0.44, Element.ALL);
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //@Passive
        //Increase damage based on defense
        if (this.passiveCanActivate())
        {
            abilities.getFirst().setDmgMultiplier(1.25 * (1.3 + (1.6 * getDef() * 1.35)) / getAtk());
        }
        else //Regular damage multiplier
        {
            abilities.getFirst().setDmgMultiplier(1.3 * 1.25);
        }
        
        //Cleanse Feng Yan and apply Counter
        if (abilityNum == 2 && abilityIsValid(2))
        {
            this.cleanse();
            this.addAppliedBuff(BuffEffect.COUNTER, 2, this);
        }
        
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public void attacked(Monster attacker)
    {
        super.attacked(attacker);
        
        //@Passive
        //Increase attack bar when attacked
        if (this.passiveCanActivate())
        {
            increaseAtkBarByPercent(20);
        }
    }
}
