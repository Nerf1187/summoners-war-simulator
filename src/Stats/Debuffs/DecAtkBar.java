package Stats.Debuffs;

/**
 * This class is used to create all decrease attack bar debuffs
 *
 * @author Anthony (Tony) Youssef
 */
public class DecAtkBar extends Debuff
{
    private final int amount;
    
    /**
     * Creates a new DecAtkBar Debuff
     *
     * @param amountPercent The amount (0-100) to decrease the attack bar
     */
    public DecAtkBar(int amountPercent)
    {
        super(Debuff.DEC_ATK_BAR, 0, 0);
        this.amount = amountPercent;
    }
    
    /**
     * Gets the amount to decrease the attack bar
     *
     * @return The amount to decrease the attack bar
     */
    public int getAmount()
    {
        return amount;
    }
}