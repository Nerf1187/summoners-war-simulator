package Effects.Debuffs;

import Monsters.*;

/**
 * This class is used to create all Provoke debuffs
 *
 * @author Anthony (Tony) Youssef
 */
public class Provoke extends Debuff
{
    private final Monster caster;
    
    /**
     * Creates a new Provoke debuff
     *
     * @param numTurns The number of turns it is active
     * @param caster   The Monster who cast the debuff
     */
    public Provoke(int numTurns, Monster caster)
    {
        super(DebuffEffect.PROVOKE, numTurns, 0);
        this.caster = caster;
    }
    
    /**
     * Gets the Monster who cast the Debuff
     *
     * @return The Monster who cast the Debuff
     */
    public Monster getCaster()
    {
        return caster;
    }
    
    /**
     * Compares two Provokes
     *
     * @param provoke The other Provoke to compare to
     * @return True if both Provokes have the same caster, false otherwise
     */
    public boolean equals(Provoke provoke)
    {
        return caster.equals(provoke.caster);
    }
}