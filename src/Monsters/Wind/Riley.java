package Monsters.Wind;

import Abilities.*;
import Game.*;
import Monsters.*;
import Effects.Buffs.*;
import Effects.*;
import Util.Util.*;
import java.util.*;

/**
 * Wind Totemist
 */
public class Riley extends Monster
{
    private static int count = 1;
    Effect totemCount = new Effect(999_999);
    
    /**
     * Creates the Monster with the default rune set
     */
    public Riley()
    {
        this("Riley1.csv");
    }
    
    /**
     * Creates the Monster with the given rune file
     *
     * @param runeFileName The name of the rune file to use
     */
    public Riley(String runeFileName)
    {
        super("Riley" + count, Element.WIND, 11_850, 714, 637, 97, 15, 50, 40, 0);
        setRunes(RUNES.getRunesFromFile(runeFileName, this));
        setAbilities();
        count++;
        totemCount.setEffect(OtherEffect.TOTEM);
        addOtherEffect(totemCount);
    }
    
    private void setAbilities()
    {
        
        Ability a1 = new Attack_Ability("Totem Magic (1)", 1.2 * 3.9, 0, 1, "Attacks the enemy target and recovers" +
                                                                             " the HP of an ally with the lowest HP ratio by 15%.", 0, false, false, false);
        
        ArrayList<Buff> ability2Buffs = MONSTERS.abilityBuffs(BuffEffect.IMMUNITY.getNum(), 1);
        ArrayList<Integer> ability2BuffChances = MONSTERS.abilityChances(100);
        Ability a2 = new Heal_Ability("Dance with the Elemental (2)", 0, 1, "Grants Immunity on all allies for 1 turn.", ability2Buffs,
                ability2BuffChances, 3, true);
        
        ArrayList<Buff> ability3Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_UP.getNum(), 2, BuffEffect.REMOVE_DEBUFF.getNum(), 0);
        ArrayList<Integer> ability3BuffChances = MONSTERS.abilityChances(100, 100);
        Ability a3 = new Heal_Ability("Valiant Boar Soul (3)", 1.15 * 0.3, 1, "Removes 1 harmful effect of all allies," +
                                                                               " recovers the HP by 30% and increases the Attack Power for 2 turns.", ability3Buffs, ability3BuffChances, 3, true);
        
        ArrayList<Buff> ability4Buffs = MONSTERS.abilityBuffs(BuffEffect.ATK_UP.getNum(), 2, BuffEffect.IMMUNITY.getNum(), 1);
        ArrayList<Integer> ability4BuffChances = MONSTERS.abilityChances(100, 100);
        Ability a4 = new Ability4(ability4Buffs, ability4BuffChances, this);
        
        super.setAbilities(a1, a2, a3, a4);
    }
    
    public boolean abilityIsValid(int abilityNum)
    {
        //Check if ability 4 is valid to use
        if (abilityNum == 4)
        {
            return totemCount.getNumOfSpecialEffects() >= 3;
        }
        return super.abilityIsValid(abilityNum);
    }
    
    public boolean abilityIsValid(Ability ability)
    {
        //Check if ability 4 is valid to use
        if (ability.equals(abilities.get(3)))
        {
            return totemCount.getNumOfSpecialEffects() >= 3;
        }
        return super.abilityIsValid(ability);
    }
    
    public boolean nextTurn(Monster target, int abilityNum)
    {
        //Prevent ability 4 from being used prematurely
        if (totemCount.getNumOfSpecialEffects() < 3 && abilityNum == 4)
        {
            System.out.println("Ability not ready yet");
            return false;
        }
        boolean b = super.nextTurn(target, abilityNum);
        if (!b)
        {
            return false;
        }
        
        switch (abilityNum)
        {
            //Heal the ally with the lowest HP ratio
            case 1 -> heal(game.getNextMonsTeam().getMonsterWithLowestHpRatio(), new Heal_Ability("", 1.15 * 0.15, 1, "", 0, false));
            //Reset totem count
            case 4 -> totemCount.setNumOfSpecialEffects(-1);
        }
        
        afterTurnProtocol(target, abilityNum == 1);
        return true;
    }
    
    public void afterTurnProtocol(Object o, boolean attack)
    {
        super.afterTurnProtocol(o, attack);
        //Increase the number of totems
        totemCount.setNumOfSpecialEffects(Math.min(totemCount.getNumOfSpecialEffects() + 1, 3));
    }
    
    public void attacked(Monster attacker)
    {
        //Increase the number of totems
        totemCount.setNumOfSpecialEffects(Math.min(totemCount.getNumOfSpecialEffects() + 1, 3));
        super.attacked(attacker);
    }
    
    public void reset()
    {
        //Reset number of totems
        totemCount.setNumOfSpecialEffects(0);
        totemCount.setNumTurns(999_999);
        super.reset();
    }
    
    public void kill()
    {
        super.kill();
        if (this.isDead())
        {
            totemCount.setNumOfSpecialEffects(0);
        }
    }
    
    public Monster copy()
    {
        Riley save = (Riley) super.copy();
        
        Effect s = new Effect(999_999);
        s.setEffect(OtherEffect.TOTEM);
        s.setNumOfSpecialEffects(this.totemCount.getNumOfSpecialEffects());
        save.totemCount = s;
        
        return save;
    }
    
    public void paste(Monster save)
    {
        if (!(save instanceof Riley))
        {
            return;
        }
        
        super.paste(save);
        
        this.totemCount = ((Riley) save).totemCount;
    }
}

/**
 * Implements Riley's ability 4
 */
class Ability4 extends Heal_Ability
{
    /**
     * The Riley who owns the ability
     */
    final Riley r;
    
    /**
     * Creates a new ability
     *
     * @param buffs       The buffs for the ability
     * @param buffChances The buff chances for the ability
     * @param r           The Riley who owns the ability
     */
    public Ability4(ArrayList<Buff> buffs, ArrayList<Integer> buffChances, Riley r)
    {
        super("Grassland Tribe Totem (4)", 1.2 * 0.2, 1, "Stacks 1 totem whenever you're attacked " +
                                                         "by a target during the enemy's turn or use a skill during your turn. Becomes able to use the [Grassland Tribe Totem] skill when you " +
                                                         "stack 3 totems. Recovers the HP of all allies by 20%, increases the Attack Power for 2 turns and grants Immunity for 1 turn when you use the skill.", buffs,
                buffChances, 0, true);
        this.r = r;
    }
    
    public String toString()
    {
        return (r.abilityIsValid(this)) ? super.toString() : name + ": " + ConsoleColor.BLACK + ConsoleColor.WHITE_BACKGROUND + description + ConsoleColor.RESET;
    }
}
