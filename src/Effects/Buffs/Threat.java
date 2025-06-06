package Effects.Buffs;

/**
 * This class is used to create all Threats
 *
 * @author Anthony (Tony) Youssef
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
        super(BuffEffect.THREAT, numTurns);
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
        return "Effects.Threat (" + getNumTurns() + " attacks left)";
    }
}