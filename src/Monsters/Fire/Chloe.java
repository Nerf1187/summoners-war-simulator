package Monsters.Fire;

import Abilities.*;
import Monsters.*;
import Runes.Monster_Runes.*;
import Stats.Buffs.*;
import Stats.*;
import java.util.*;

/**
 * Fire Epikion Priest
 */
public class Chloe extends Monster
{
    private final ArrayList<Ability> abilities = new ArrayList<>();
    private static int count = 1;
    
    /**
     * Creates the Monster with the default rune set
     */
    public Chloe()
    {
        super("Chloe" + count, FIRE, 11_700, 648, 549, 111, 15, 50, 15, 0);
        super.setRunes(MonsterRunes.getRunesFromFile("Chloe1.csv", this));
        setAbilities();
        count++;
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Chloe(String runeFileName)
    {
        this();
        setRunes(MonsterRunes.getRunesFromFile(runeFileName, this));
    }
    
    private void setAbilities()
    {
        abilities.add(new Attack_Ability("Absorb Mana (1)", 1.35 * (1.8 + (0.12 * getMaxHp()) / getAtk()), 0.5, 1, "Attacks the " +
                                                                                                                   "enemy and recovers the HP by 50% of the damage dealt. This attack will deal more damage according to your MAX HP.", 0, false,
                false, false));
        
        abilities.add(new Heal_Ability("Fill (2)", 1.3 * 1.2, 1, "Removes 1 harmful effect from all allies " +
                                                                 "(excluding yourself) and recovers the HP of all allies (including yourself) by 20% each.", 3, true));
        
        ArrayList<Buff> ability3Buffs = abilityBuffs(Buff.INVINCIBILITY, 1, Buff.IMMUNITY, 3);
        ArrayList<Integer> ability3BuffChances = abilityChances(100, 100);
        abilities.add(new Heal_Ability("Fanatic (3)", 0, 1, "All allies are invincible for 1 turn and gain Immunity for 3 turns.",
                ability3Buffs, ability3BuffChances, 5, true));
        
        abilities.add(new Leader_Skill(Stat.SPD, 0.2, ALL));
        
        super.setAbilities(abilities);
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
                    addAppliedBuff(Buff.REMOVE_DEBUFF, 0, this);
                }
            });
        }
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
}