package Stats.Buffs;

/**
 * This class is used to create all Increase Attack Bar buffs
 */
public class IncAtkBar extends Buff
{
    private final int amount;
    
    /**
     * Creates the buff
     *
     * @param amount The amount to increase the attack bar as a percentage (0-100)
     */
    public IncAtkBar(int amount)
    {
        super(INCREASE_ATK_BAR, 0);
        this.amount = amount;
    }
    
    /**
     * @return The amount to increase the attack bar as a percentage (0-100)
     */
    public int getAmount()
    {
        return amount;
    }
}