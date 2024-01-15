package Stats.Debuffs;

import Errors.*;
import Monsters.*;
import Stats.*;

/**
 * @author Anthony (Tony) Youssef
 * The parent class for all Debuffs
 */

public class Debuff extends Stat
{
    public static final int GLANCING_HIT_UP = 0, DEC_ATK = 1, DEC_DEF = 2, DEC_ATK_SPD = 3, BLOCK_BENEFICIAL_EFFECTS = 4, BOMB = 5, PROVOKE = 6, SLEEP = 7;
    public final static int CONTINUOUS_DMG = 8, FREEZE = 9, STUN = 10, UNRECOVERABLE = 11, SILENCE = 12, BRAND = 13, OBLIVION = 14, DEC_ATK_BAR = 15,
            REMOVE_BENEFICIAL_EFFECT = 16, STRIP = 17, SEAL = 18;
    private final boolean goesThroughImmunity;
    int debuffNum;
    private Monster caster = null;
    
    /**
     * Creates a new Debuff
     *
     * @param debuff              The debuff number
     * @param numTurns            The number of turns to apply
     * @param goesThroughImmunity True if the debuff ignores immunity, false otherwise
     */
    public Debuff(int debuff, int numTurns, int goesThroughImmunity)
    {
        super(numTurns);
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
        return numToDebuff(debuffNum) + " (" + getNumTurns() + " turns remaining)";
    }
    
    /**
     * Converts the provided debuff number into a String
     *
     * @param num The number to convert
     * @return The debuff number as a String
     */
    public static String numToDebuff(int num)
    {
        String debuff = "";
        switch (num)
        {
            case 0 -> debuff = "Glancing Hit Chance up";
            case 1 -> debuff = "Dec atk";
            case 2 -> debuff = "Dec def";
            case 3 -> debuff = "Dec Atk Spd";
            case 4 -> debuff = "Block Beneficial Effects";
            case 5 -> debuff = "Bomb";
            case 6 -> debuff = "Stats.Provoke";
            case 7 -> debuff = "Sleep";
            case 8 -> debuff = "Continuous Dmg";
            case 9 -> debuff = "Freeze";
            case 10 -> debuff = "Stun";
            case 11 -> debuff = "Unrecoverable";
            case 12 -> debuff = "Silence";
            case 13 -> debuff = "Brand";
            case 14 -> debuff = "Oblivion";
            case 15 -> debuff = "Dec atk bar";
            case 16 -> debuff = "Remove Beneficial Effect";
            case 17 -> debuff = "Strip";
            case 18 -> debuff = "Seal";
        }
        return debuff;
    }
    
    /**
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
     * @return True if the debuff ignores immunity, false otherwise
     */
    public boolean goesThroughImmunity()
    {
        return goesThroughImmunity;
    }
    
    /**
     * Set the Monster who cast the Debuff
     *
     * @param caster The Monster who cast the Debuff
     */
    public void setCaster(Monster caster)
    {
        this.caster = caster;
    }
    
    /**
     * @return The Monster who cast the Debuff if one has been set
     */
    public Monster getCaster()
    {
        return caster;
    }
}
