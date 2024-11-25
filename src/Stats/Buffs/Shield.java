package Stats.Buffs;

/**
 * This class is used to create all shields
 *
 * @author Anthony (Tony) Youssef
 */
public class Shield extends Buff
{
    private int amount;
    
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
     * Gets the amount of health the Shield has
     *
     * @return The amount of health the Shield has
     */
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * Decreases the amount of shield health left
     *
     * @param amount The amount to decrease by
     */
    public void decreaseShieldHealth(int amount)
    {
        this.amount -= amount;
    }
}