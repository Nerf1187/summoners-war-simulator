package Abilities;

import Game.*;
import Monsters.*;
import Runes.*;

import static Runes.RuneAttribute.*;

/**
 * The subclass for all leader skills
 */
public class Leader_Skill extends Ability
{
    private boolean applied = false;
    private final RuneAttribute attribute;
    private final String statName;
    private final Element element;
    
    private final double amount;
    
    /**
     * Constructs a new Leader skill but does not apply it.
     *
     * @param attribute    The attribute that the skill affects.
     * @param amount  The percentage that the skill increases the attribute by (0-1)
     * @param element The element for which the skill applies to.
     */
    public Leader_Skill(RuneAttribute attribute, double amount, Element element)
    {
        this.attribute = attribute;
        this.amount = amount;
        this.element = element;
        
        //Set stat name
        statName = switch (attribute)
        {
            case HP -> "HP";
            case SPD -> "Speed";
            case DEF -> "Defense";
            case ATK -> "Attack";
            case CRIT_RATE -> "Crit Rate";
            case CRIT_DMG -> "Crit Damage";
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
            if (element != Element.ALL && element != mon.getElement())
            {
                continue;
            }
            switch (attribute)
            {
                case ATK -> mon.setAtk(mon.getAtk() + mon.getBaseAtk() * amount);
                case DEF -> mon.setDef(mon.getDef() + mon.getBaseDef() * amount);
                case HP ->
                {
                    mon.setMaxHp(mon.getMaxHp() + mon.getBaseMaxHp() * amount);
                    mon.setCurrentHp(mon.getMaxHp());
                }
                case SPD -> mon.setSpd(mon.getSpd() + mon.getBaseSpd() * amount);
                case CRIT_RATE -> mon.setCritRate(mon.getCritRate() + amount);
                case CRIT_DMG -> mon.setCritDmg(mon.getCritDmg() + amount);
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
        return String.format("Leader skill: Increases the %s of ally monsters%s by %d%%", statName,
                ((element.toString() != null) ? " with " + element +  " attribute" : ""),
                (int) ((attribute.getNum() <= SPD.getNum()) ? (amount * 100) : amount));
    }
}
