package Effects.Debuffs;

import Errors.*;
import Monsters.*;
import Effects.*;

/**
 * The parent class for all Debuffs
 *
 * @author Anthony (Tony) Youssef
 */

public class Debuff extends Effect
{
    private final boolean goesThroughImmunity;
    private final DebuffEffect debuffEffect;
    private Monster caster = null;
    
    /**
     * Creates a new Debuff
     *
     * @param debuff              The debuff number
     * @param numTurns            The number of turns to apply
     * @param goesThroughImmunity 0 if the debuff ignores immunity, 1 otherwise
     */
    public Debuff(DebuffEffect debuff, int numTurns, int goesThroughImmunity)
    {
        super(numTurns);
        //Make sure the int is valid
        if (goesThroughImmunity != 1 && goesThroughImmunity != 0)
        {
            throw new ConflictingArguments("goesThroughImmunity must be 0 (false) or 1 (true)");
        }
        debuffEffect = debuff;
        this.goesThroughImmunity = goesThroughImmunity == 1;
    }
    
    /**
     * Creates a new Debuff that applies for one turn and does not go through immunity
     *
     * @param debuff The debuff number
     */
    public Debuff(DebuffEffect debuff)
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
        return "%s (%d turns remaining)".formatted(debuffEffect, getNumTurns());
    }
    
    /**
     * Gets the debuff number
     *
     * @return The debuff number
     */
    public DebuffEffect getDebuffEffect()
    {
        return debuffEffect;
    }
    
    /**
     * Compares two debuffs
     *
     * @param other The other debuff to compare to
     * @return True if the two debuffs have the same debuff number
     */
    public boolean equals(Debuff other)
    {
        return other.debuffEffect == this.debuffEffect;
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