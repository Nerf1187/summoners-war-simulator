package Monsters.Light;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Effects.Debuffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Light Martial Cat (2A)
 */
public class Xiao_Ling extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Xiao_Ling()
    {
        this("Xiao_Ling1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Xiao_Ling(String runeFileName)
    {
        super("Xiao Ling" + count, Element.LIGHT, 12_180, 527, 692, 104, 15, 50, 40, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        ArrayList<Debuff> ability1Debuffs = abilityDebuffs(DebuffEffect.STUN.getNum(), 1, 0);
        ArrayList<Integer> ability1DebuffChances = MONSTERS.abilityChances(100);
        Ability a1 = new Attack_Ability("Energy Punch (1)", 1.2 * (2.0 + ((0.2 * getMaxHp())) / getAtk()), 0, 1, "Attacks " +
                                                                                                                  "with a spinning punch and stuns the enemy for 1 turn with a 50% chance. The damage of this attack increases according to your MAX HP.",
                ability1Debuffs, ability1DebuffChances, 0, false, false, false);
        
        ArrayList<Buff> ability2Buffs = MONSTERS.abilityBuffs(BuffEffect.CRIT_RATE_UP.getNum(), 2, BuffEffect.COUNTER.getNum(), 2);
        ability2Buffs.add(new IncAtkBar(70));
        ArrayList<Integer> ability2BuffChances = MONSTERS.abilityChances(100, 100, 100);
        Ability a2 = new Ability("Counterattack (2)", 0, 0, 1, "Increases the critical rate for 2 turns and counterattacks " +
                                                                "when attacked. The Attack Bar is increased by 70%, and the damage you receive will be reduced by half when you get attacked while this " +
                                                                "skill is on cooldown.", ability2Buffs, ability2BuffChances, 3, false, false, false, true, false, false, 0);
        
        //@Passive:Creation
        Ability a3 = new Passive("Lonely Fight", "Absorbs the Attack Bar by 50% if you attack a monster with same or lower HP status compared to yours." +
                                                  " Recovers your HP by 50% of the damage dealt if you attack a monster that has better HP status than yours.");
        
        Ability a4 = new Leader_Skill(RuneAttribute.ATK, 0.21, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        if (!target.equals(this) && abilityNum == 2)
        {
            System.out.println("Ability can only be used on self!");
            return false;
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        //@Passive
        if (this.passiveCanActivate())
        {
            //Steal a portion of the attack bar if the target has a worse HP ratio
            if (target.getHpRatio() <= this.getHpRatio())
            {
                if (!target.containsBuff(BuffEffect.IMMUNITY))
                {
                    this.increaseAtkBar((int) Math.ceil((target.getAtkBar() * 0.5)));
                    target.setAtkBar((int) Math.ceil((target.getAtkBar() * 0.5)));
                }
            }
            else if (!this.containsDebuff(DebuffEffect.UNRECOVERABLE)) //Increase HP by half the damage done
            {
                setCurrentHp(getCurrentHp() + (int) (0.5 * getDmgDealtThisTurn()));
            }
        }
        
        super.afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public double dmgReductionProtocol(double num, boolean self)
    {
        if (!self)
        {
            return num;
        }
        //Halve the damage if ability 2 is on cooldown
        return (abilities.get(1).getTurnsRemaining() == 0) ? num : num / 2;
    }
}
