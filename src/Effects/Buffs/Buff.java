package Effects.Buffs;

import Effects.*;

/**
 * The parent class for all buffs
 *
 * @author Anthony (Tony) Youssef
 */
public class Buff extends Effect
{
    private final BuffEffect buffEffect;
    
    /**
     * Creates a new Buff
     *
     * @param buff     The Buff number
     * @param numTurns The number of turns to apply the buff
     */
    public Buff(BuffEffect buff, int numTurns)
    {
        super(numTurns);
        buffEffect = buff;
    }
    
    /**
     * Creates a new Buff that applies for one turn
     *
     * @param buff The Buff number
     */
    public Buff(BuffEffect buff)
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
        return "%s (%d turns remaining)".formatted(buffEffect, getNumTurns());
    }
    
    /**
     * Gets the Buff's number
     *
     * @return The Buff's number
     */
    public BuffEffect getBuffEffect()
    {
        return buffEffect;
    }
    
    /**
     * Compares two Buffs
     *
     * @param other The other Buff to compare to
     *
     * @return True if both buffs have the same Buff number, false otherwise
     */
    public boolean equals(Buff other)
    {
        return other.buffEffect == this.buffEffect;
    }
}