package Runes;

import Util.Util.*;

/**
 * Enum representing various attributes of a rune, such as attack and defense. A NONE attribute
 * is included to handle invalid or undefined attributes.
 */
public enum RuneAttribute
{
    NONE(-1),
    ATK(1),
    ATK_PERCENT(2),
    DEF(3),
    DEF_PERCENT(4),
    HP(5),
    HP_PERCENT(6),
    SPD(7),
    CRIT_RATE(8),
    CRIT_DMG(9),
    RES(10),
    ACC(11);
    
    private final int num;
    
    /**
     * Constructs a RuneAttribute based on a numerical value.
     *
     * @param num The numerical value corresponding to a specific rune attribute.
     */
    RuneAttribute(int num)
    {
        this.num = num;
    }
    
    /**
     * Retrieves the numerical representation of this rune attribute.
     *
     * @return The numerical value associated with this rune attribute.
     */
    public int getNum()
    {
        return num;
    }
    
    /**
     * Converts the enumeration constant name to a formatted string.
     * The method replaces underscores with spaces and applies title case formatting.
     *
     * @return A formatted string representing the name of the enumeration constant
     *         in title case and with underscores replaced by spaces.
     */
    public String toString()
    {
        return STRINGS.toTitleCase(super.toString());
    }
    
    /**
     * Converts a numerical value to the corresponding RuneAttribute.
     * If no matching attribute is found, returns the NONE attribute.
     *
     * @param num The numerical value to be converted into a RuneAttribute.
     * @return The RuneAttribute corresponding to the provided numerical value,
     *         or NONE if no match is found.
     */
    public static RuneAttribute numToAttribute(int num)
    {
        for (RuneAttribute attribute : values())
        {
            if (attribute.getNum() == num)
            {
                return attribute;
            }
        }
        return NONE;
    }
    
    /**
     * Converts a string representation of a rune attribute to its corresponding RuneAttribute enum value.
     * If the input string does not match any valid attribute, the method returns the NONE attribute.
     *
     * @param att The string representation of the rune attribute to be converted. This string should match
     *            the name of the RuneAttribute constants (ignoring case differences).
     * @return The corresponding RuneAttribute enum value if a match is found, or NONE if no match is found.
     */
    public static RuneAttribute stringToAttribute(String att)
    {
        att = STRINGS.toEnumCase(att);
        try
        {
            return RuneAttribute.valueOf(att);
        }
        catch (IllegalArgumentException e)
        {
            return NONE;
        }
    }
}
