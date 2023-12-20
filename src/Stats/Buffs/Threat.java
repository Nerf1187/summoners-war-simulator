package Stats.Buffs;

/**
 * @author Anthony (Tony) Youssef
 * This class is used to create all Threats
 */
public class Threat extends Buff
{
    /**
     * Creates a new Threat buff
     *
     * @param numTurns The number of attacks it takes
     */
    public Threat(int numTurns)
    {
        super(Buff.THREAT, numTurns);
    }
    
    /**
     * Decreases the buff by the specified number
     *
     * @param turns The number to decrease
     */
    @Override
    public void decreaseTurn(int turns)
    {
        super.decreaseTurn(turns);
    }
    
    /**
     * Overrides the parent class so the time remaining does not decrease after a turn, only when attacked. This method does not execute any code
     */
    @Override
    public void decreaseTurn()
    {
    }
    
    
    /**
     * Formats the Threat into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return "Stats.Threat (" + getNumTurns() + " attacks left)";
    }
}
