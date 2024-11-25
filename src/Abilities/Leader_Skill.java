package Abilities;

import Game.*;
import Monsters.*;
import Stats.*;

import static Monsters.Monster.*;
import static Runes.Rune.*;

/**
 * The subclass for all leader skills
 */
public class Leader_Skill extends Ability
{
    private boolean applied = false;
    private final String statName, elementName;
    private final int statNum, elementNum;
    private final double amount;
    
    /**
     * Constructs a new Leader skill but does not apply it.
     *
     * @param stat    The stat that the skill affects. See {@link Stat} for stat numbers
     * @param amount  The percentage that the skill increases the stat by (0-1)
     * @param element The element for which the skill applies to. See {@link Monster} for element numbers
     */
    public Leader_Skill(int stat, double amount, int element)
    {
        this.statNum = stat;
        this.amount = amount;
        this.elementNum = element;
        
        //Set element name
        elementName = switch (element)
        {
            case FIRE -> "Fire";
            case WATER -> "Water";
            case WIND -> "Wind";
            case LIGHT -> "Light";
            case DARK -> "Dark";
            default -> "";
        };
        //Set stat name
        statName = switch (stat)
        {
            case HP -> "HP";
            case SPD -> "Speed";
            case DEF -> "Defense";
            case ATK -> "Attack";
            case CRITRATE -> "Crit Rate";
            case CRITDMG -> "Crit Damage";
            case RES -> " Resistance";
            case ACC -> "Accuracy";
            default -> "";
        };
    }
    
    /**
     * Applies the leader skill to a given Team if it hasn't already
     *
     * @param applyToTeam The team to apply the skill to
     */
    public void apply(Team applyToTeam)
    {
        //Do nothing if the skill has already been applied
        if (applied)
        {
            return;
        }
        applied = true;
        //Apply the skill to each Monster on the team
        for (Monster mon : applyToTeam.getMonsters())
        {
            if (elementNum != Monster.ALL && elementNum != mon.getElement())
            {
                continue;
            }
            switch (statNum)
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
        }
    }
    
    /**
     * Formats the leader skill object into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return String.format("Leader skill: Increases the %s of ally monsters%s by %d%%", statName, ((elementName != null) ? " with " + elementName + " " +
                                                                                                                             "attribute" : ""), (int) ((statNum <= SPD) ? (amount * 100) : amount));
    }
}
