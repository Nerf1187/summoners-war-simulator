package Runes;

import static Runes.RuneAttribute.CRIT_RATE;

/**
 * This class contains the information for rune attributes
 *
 * @author Anthony (Tony) Youssef
 */
public class MainAttribute
{
    private final RuneAttribute attribute;
    private final int amount;
    
    /**
     * Creates a new MainAttribute
     *
     * @param attribute The attribute. See {@link RuneAttribute} for attribute numbers
     * @param amount    The attribute's amount
     */
    public MainAttribute(RuneAttribute attribute, int amount)
    {
        this.attribute = attribute;
        this.amount = amount;
    }
    
    /**
     * Gets the attribute's amount
     *
     * @return The attribute's amount
     */
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * Gets the attribute number
     *
     * @return The attribute number
     */
    public RuneAttribute getAttribute()
    {
        return attribute;
    }
    
    /**
     * Formats the attribute into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        //Get the affected stat
        String s = switch (attribute)
        {
            case ATK, ATK_PERCENT -> "Attack ";
            case DEF, DEF_PERCENT -> "Defense ";
            case HP, HP_PERCENT -> "HP ";
            case SPD -> "Speed ";
            case CRIT_RATE -> "Crit Rate ";
            case CRIT_DMG -> "Crit Damage ";
            case RES -> "Resistance ";
            case ACC -> "Accuracy ";
            default -> "";
        };
        
        s += amount;
        return (attribute.toString().toLowerCase().endsWith("percent") || attribute.getNum() >= CRIT_RATE.getNum()) ? s + "%" : s;
    }
}