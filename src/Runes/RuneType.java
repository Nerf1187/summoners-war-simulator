package Runes;

import Util.Util.*;

/**
 * Represents different types of Runes in the system. Each rune type has a corresponding numerical value
 * and a name that can be formatted or converted using utility methods.
 */
public enum RuneType
{
    NONE(-1),
    ENERGY(1),
    FATAL(2),
    BLADE(3),
    SWIFT(4),
    FOCUS(5),
    GUARD(6),
    ENDURE(7),
    SHIELD(8),
    REVENGE(9),
    WILL(10),
    NEMESIS(11),
    VAMPIRE(12),
    DESTROY(13),
    DESPAIR(14),
    VIOLENT(15),
    RAGE(16),
    FIGHT(17),
    DETERMINATION(18),
    ENHANCE(19),
    ACCURACY(20),
    TOLERANCE(21),
    ELEMENT_ARTIFACT(22),
    TYPE_ARTIFACT(23),
    SEAL(24);
    
    private final int num;
    
    /**
     * Constructs a new instance of a RuneType with the specified numerical value.
     *
     * @param num The numerical value associated with this RuneType.
     */
    RuneType(int num)
    {
        this.num = num;
    }
    
    /**
     * Returns the numerical value associated with this rune type.
     *
     * @return The numerical value of this rune type.
     */
    public int getNum()
    {
        return num;
    }
    
    public String toString()
    {
        return STRINGS.toTitleCase(super.toString().replaceAll("_", " "));
    }
    
    /**
     * Formats the rune type number into a String
     *
     * @param num The number to convert
     * @return The rune type or {@link #NONE} if the number doesn't correspond to an existing rune set
     */
    public static RuneType numToType(int num)
    {
        for (RuneType type : values())
        {
            if (type.getNum() == num)
            {
                return type;
            }
        }
        return NONE;
    }
    
    /**
     * Converts a given variable name into a set number
     *
     * @param type A String containing the variable name
     * @return The attribute number with the given variable name if possible, {@link RuneType#NONE} otherwise
     */
    public static RuneType stringToType(String type)
    {
        type = STRINGS.toEnumCase(type);
        
        try
        {
            return RuneType.valueOf(type);
        }
        catch (IllegalArgumentException e)
        {
            return NONE;
        }
    }
}