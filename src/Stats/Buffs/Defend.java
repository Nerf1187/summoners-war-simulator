package Stats.Buffs;

import Monsters.*;

/**
 * @author Anthony (Tony) Youssef
 * This class is used to create all Defend buffs
 */
public class Defend extends Buff
{
    private final Monster caster;
    
    /**
     * Create a new Defend Buff
     *
     * @param numTurns The number of turns to apply
     * @param caster   The Monster who cast the buff
     */
    public Defend(int numTurns, Monster caster)
    {
        super(DEFEND, numTurns);
        this.caster = caster;
    }
    
    /**
     * @return The Monster who cast the buff
     */
    public Monster getCaster()
    {
        return caster;
    }
    
    /**
     * Compares two Defend buffs
     *
     * @param defend The other Defend buff to compare to
     * @return True if both Defend buffs have the same caster
     */
    public boolean equals(Defend defend)
    {
        return caster.equals(defend.caster);
    }
}
