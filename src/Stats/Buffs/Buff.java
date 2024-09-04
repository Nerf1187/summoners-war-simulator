package Stats.Buffs;

import Stats.*;

/**
 * @author Anthony (Tony) Youssef
 * The parent class for all buffs
 */
public class Buff extends Stat
{
    //Buff numbers
    public static final int ATK_UP = 0, DEF_UP = 1, CRIT_RATE_UP = 2, CRIT_RESIST_UP = 3, ATK_SPD_UP = 4, RECOVERY = 5, COUNTER = 6, IMMUNITY = 7,
            INVINCIBILITY = 8, REFLECT = 9, SHIELD = 10, ENDURE = 11, DEFEND = 12, SOUL_PROTECTION = 13, RUNE_SHIELD = 14, THREAT = 15, INCREASE_ATK_BAR = 16, CLEANSE = 17,
            BUFF_STEAL = 18, VAMPIRE = 19, REMOVE_DEBUFF = 20, EXTEND_BUFF = 21, SHORTEN_DEBUFF = 22;
    
    private final int buffNum;
    
    /**
     * Creates a new Buff
     *
     * @param buff     The Buff number
     * @param numTurns The number of turns to apply the buff
     */
    public Buff(int buff, int numTurns)
    {
        super(numTurns);
        buffNum = buff;
    }
    
    /**
     * Creates a new Buff that applies for one turn
     *
     * @param buff the Buff number
     */
    public Buff(int buff)
    {
        this(buff, 1);
    }
    
    /**
     * Formats the Buff into a readable String
     *
     * @return The Formatted String
     */
    public String toString()
    {
        return numToBuff(buffNum) + " (" + getNumTurns() + " turns remaining)";
    }
    
    /**
     * Converts the Buff number into a String
     *
     * @param num The buff number to convert
     * @return The Buff number as a String
     */
    public static String numToBuff(int num)
    {
        String buff = "";
        switch (num)
        {
            case 0 -> buff = "Atk up";
            case 1 -> buff = "Def up";
            case 2 -> buff = "Crit Rate up";
            case 3 -> buff = "Crit Resist Rate up";
            case 4 -> buff = "Atk Spd up";
            case 5 -> buff = "Recovery";
            case 6 -> buff = "Counter";
            case 7 -> buff = "Immunity";
            case 8 -> buff = "Invincibility";
            case 9 -> buff = "Reflect";
            case 10 -> buff = "Shield";
            case 11 -> buff = "Endure";
            case 12 -> buff = "Defend";
            case 13 -> buff = "Soul Protection";
            case 15 -> buff = "Threat";
            case 16 -> buff = "Increase Atk Bar";
            case 17 -> buff = "Cleanse";
            case 18 -> buff = "Steal buff";
            case 19 -> buff = "Vampire";
            case 14 -> buff = "Rune shield";
        }
        return buff;
    }
    
    /**
     * @return The Buff's number
     */
    public int getBuffNum()
    {
        return buffNum;
    }
    
    /**
     * Compares two Buffs
     *
     * @param other The other Buff to compare to
     * @return True if both buffs have the same Buff number, false otherwise
     */
    public boolean equals(Buff other)
    {
        return other.buffNum == this.buffNum;
    }
}
