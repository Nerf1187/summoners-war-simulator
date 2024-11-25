package Stats.Debuffs;

import Errors.*;
import Monsters.*;
import Stats.*;

/**
 * The parent class for all Debuffs
 *
 * @author Anthony (Tony) Youssef
 */

public class Debuff extends Stat
{
    public static final int GLANCING_HIT_UP = 0, DEC_ATK = 1, DEC_DEF = 2, DEC_ATK_SPD = 3, BLOCK_BENEFICIAL_EFFECTS = 4, BOMB = 5, PROVOKE = 6, SLEEP = 7;
    public final static int CONTINUOUS_DMG = 8, FREEZE = 9, STUN = 10, UNRECOVERABLE = 11, SILENCE = 12, BRAND = 13, OBLIVION = 14, DEC_ATK_BAR = 15,
            REMOVE_BENEFICIAL_EFFECT = 16, STRIP = 17, SEAL = 18, SHORTEN_BUFFS = 19;
    private final boolean goesThroughImmunity;
    int debuffNum;
    private Monster caster = null;
    
    /**
     * Creates a new Debuff
     *
     * @param debuff              The debuff number
     * @param numTurns            The number of turns to apply
     * @param goesThroughImmunity 0 if the debuff ignores immunity, 1 otherwise
     */
    public Debuff(int debuff, int numTurns, int goesThroughImmunity)
    {
        super(numTurns);
        //Make sure the int is valid
        if (goesThroughImmunity != 1 && goesThroughImmunity != 0)
        {
            throw new ConflictingArguments("goesThroughImmunity must be 0 (false) or 1 (true)");
        }
        debuffNum = debuff;
        this.goesThroughImmunity = goesThroughImmunity == 1;
    }
    
    /**
     * Creates a new Debuff that applies for one turn and does not go through immunity
     *
     * @param debuff The debuff number
     */
    public Debuff(int debuff)
    {
        this(debuff, 1, 0);
    }
    
    /**
     * Formats the Debuff into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return "%s (%d turns remaining)".formatted(numToDebuff(debuffNum), getNumTurns());
    }
    
    /**
     * Converts the provided debuff number into a String
     *
     * @param num The number to convert
     * @return The debuff number as a String
     */
    public static String numToDebuff(int num)
    {
        return switch (num)
        {
            case 0 -> "Glancing Hit Rate up";
            case 1 -> "Dec Atk";
            case 2 -> "Dec Def";
            case 3 -> "Dec Atk Spd";
            case 4 -> "Block Beneficial Effects";
            case 5 -> "Bomb";
            case 6 -> "Provoke";
            case 7 -> "Sleep";
            case 8 -> "Continuous Dmg";
            case 9 -> "Freeze";
            case 10 -> "Stun";
            case 11 -> "Unrecoverable";
            case 12 -> "Silence";
            case 13 -> "Brand";
            case 14 -> "Oblivion";
            case 15 -> "Dec Atk Bar";
            case 16 -> "Remove Beneficial Effect";
            case 17 -> "Strip";
            case 18 -> "Seal";
            case 19 -> "Shorten Buff";
            default -> "";
        };
    }
    
    /**
     * Gets the debuff number
     *
     * @return The debuff number
     */
    public int getDebuffNum()
    {
        return debuffNum;
    }
    
    /**
     * Compares two debuffs
     *
     * @param other The other debuff to compare to
     * @return True if the two debuffs have the same debuff number
     */
    public boolean equals(Debuff other)
    {
        return other.debuffNum == this.debuffNum;
    }
    
    /**
     * Checks if the debuff can be applied through immunity
     *
     * @return True if the debuff ignores immunity, false otherwise
     */
    public boolean goesThroughImmunity()
    {
        return goesThroughImmunity;
    }
    
    /**
     * Sets the Monster who cast the Debuff
     *
     * @param caster The Monster who cast the Debuff
     */
    public void setCaster(Monster caster)
    {
        this.caster = caster;
    }
    
    /**
     * Gets the Monster who cast the Debuff if one has been set
     *
     * @return The Monster who cast the Debuff if one has been set
     */
    public Monster getCaster()
    {
        return caster;
    }
}