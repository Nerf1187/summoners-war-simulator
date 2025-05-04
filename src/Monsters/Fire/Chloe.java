package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.*;
import Effects.Buffs.*;
import Util.Util.*;
import java.util.*;

/**
 * Fire Epikion Priest
 */
public class Chloe extends Monster
{
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Chloe()
    {
        this("Chloe1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Chloe(String runeFileName)
    {
        super("Chloe" + count, Element.FIRE, 11_700, 648, 549, 111, 15, 50, 15, 0);
        super.setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
    }
    
    private void setAbilities()
    {
        Ability a1 = new Attack_Ability("Absorb Mana (1)", 1.35 * (1.8 + (0.12 * getMaxHp()) / getAtk()), 0.5, 1, "Attacks the " +
                                                                                                                   "enemy and recovers the HP by 50% of the damage dealt. This attack will deal more damage according to your MAX HP.", 0, false,
                false, false);
        
        Ability a2 = new Heal_Ability("Fill (2)", 1.3 * 1.2, 1, "Removes 1 harmful effect from all allies " +
                                                                 "(excluding yourself) and recovers the HP of all allies (including yourself) by 20% each.", 3, true);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.INVINCIBILITY.getNum(), 1, BuffEffect.IMMUNITY.getNum(), 3);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100, 100);
        Ability a3 = new Heal_Ability("Fanatic (3)", 0, 1, "All allies are invincible for 1 turn and gain Immunity for 3 turns.",
                ability3Buffs, ability3BuffChances, 5, true);
        
        Ability a4 = new Leader_Skill(RuneAttribute.SPD, 0.2, Element.ALL);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        if (abilityNum == 2)
        {
            //Remove a debuff for all team members except self
            applyToTeam(game.getNextMonsTeam(), m -> {
                if (!m.equals(this))
                {
                    addAppliedBuff(BuffEffect.REMOVE_DEBUFF, 0, this);
                }
            });
        }
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}
