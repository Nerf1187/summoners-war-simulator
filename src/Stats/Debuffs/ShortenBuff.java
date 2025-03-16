package Stats.Debuffs;

/**
 * This class is used to create all shorten buff effects
 */
public class ShortenBuff extends Debuff
{
    private final int amount;
    
    /**
     * Creates a new Shorten_Buff Debuff
     *
     * @param numOfTurns The number of turns to decrease the buffs by
     */
    public ShortenBuff(int numOfTurns)
    {
        super(SHORTEN_BUFFS, 0, 1);
        this.amount = numOfTurns;
    }
    
    /**
     * Gets the number of turns to decrease the buffs by
     *
     * @return The number of turns to decrease the buffs by
     */
    public int getAmount()
    {
        return amount;
    }
}