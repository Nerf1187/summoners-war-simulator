package Abilities;

import Game.*;
import Monsters.*;
import Stats.*;

import static Monsters.Monster.*;
import static Runes.Rune.*;

@FunctionalInterface
interface Function
{
    void apply(Monster mon);
}

/**
 * The subclass for all leader skills
 */
public class Leader_Skill extends Ability
{
    private boolean applied = false;
    private String statName, elementName;
    private final int stat;
    private final double amount;
    private final Function func;
    
    /**
     * Constructs a new Leader skill but does not apply it.
     *
     * @param stat    the stat that the skill affects. See {@link Stat} for stat numbers
     * @param amount  the percentage that the skill increases the stat by
     * @param element the element for which the skill applies to. See {@link Monster} for element numbers
     */
    public Leader_Skill(int stat, double amount, int element)
    {
        this.stat = stat;
        this.amount = amount;
        
        switch (element)
        {
            case FIRE -> elementName = "Fire";
            case WATER -> elementName = "Water";
            case WIND -> elementName = "Wind";
            case LIGHT -> elementName = "Light";
            case DARK -> elementName = "Dark";
        }
        switch (stat)
        {
            case HP -> statName = "HP";
            case SPD -> statName = "Speed";
            case DEF -> statName = "Defense";
            case ATK -> statName = "Attack";
            case CRITRATE -> statName = "Crit Rate";
            case CRITDMG -> statName = "Crit Damage";
            case RES -> statName = " Resistance";
            case ACC -> statName = "Accuracy";
        }
        func = (Monster mon) -> {
            if (element != Monster.ALL && element != mon.getElement())
            {
                return;
            }
            switch (stat)
            {
                case ATK -> mon.setAtk(mon.getAtk() + mon.getBaseAtk() * amount);
                case DEF -> mon.setDef(mon.getDef() + mon.getBaseDef() * amount);
                case HP ->
                {
                    mon.setMaxHp(mon.getMaxHp() + mon.getBaseMaxHp() * amount);
                    mon.setCurrentHp(mon.getMaxHp());
                }
                case SPD -> mon.setSpd(mon.getSpd() + mon.getBaseSpd() * amount);
                case CRITRATE -> mon.setCritRate(mon.getCritRate() + amount);
                case CRITDMG -> mon.setCritDmg(mon.getCritDmg() + amount);
                case RES -> mon.setResistance(mon.getResistance() + amount);
                case ACC -> mon.setAccuracy(mon.getAccuracy() + amount);
            }
        };
    }
    
    /**
     * Applies the leader skill to a given Team
     *
     * @param applyToTeam the team to apply the skill to
     */
    public void apply(Team applyToTeam)
    {
        if (applied)
        {
            return;
        }
        applied = true;
        for (Monster mon : applyToTeam.getMonsters())
        {
            func.apply(mon);
        }
    }
    
    /**
     * Formats the leader skill object into a readable String
     *
     * @return the formatted String
     */
    public String toString()
    {
        return String.format("Leader skill: Increases the %s of ally monsters%s by %d%%", statName, ((elementName != null) ? " with " + elementName + " " +
                "attribute" : ""), (int) ((stat <= SPD) ? (amount * 100) : amount));
    }
}
