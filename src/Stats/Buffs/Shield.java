package Stats.Buffs;

/**
 * @author Anthony (Tony) Youssef
 * This class is used to create all shields
 */
public class Shield extends Buff
{
    private final int amount;
    
    /**
     * Creates a new Shield
     *
     * @param amount   The amount of health the Shield has
     * @param numTurns The number of turns it is active
     */
    public Shield(int amount, int numTurns)
    {
        super(SHIELD, numTurns);
        this.amount = amount;
    }
    
    /**
     * @return The amount of health the Shield has
     */
    public int getAmount()
    {
        return amount;
    }
}
