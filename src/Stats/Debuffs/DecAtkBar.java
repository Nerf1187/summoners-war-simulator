package Stats.Debuffs;

/**
 * @author Anthony (Tony) Youssef
 * This class is used to create all decrease attack bar debuffs
 */
public class DecAtkBar extends Debuff
{
    private final int amount;
    
    /**
     * Creates a new DecAtkBar Debuff
     *
     * @param amountPercent The amount (as a percent) to decrease the attack bar
     */
    public DecAtkBar(int amountPercent)
    {
        super(Debuff.DEC_ATK_BAR, 0, 0);
        this.amount = amountPercent;
    }
    
    /**
     * @return The amount to decrease the attack bar
     */
    public int getAmount()
    {
        return amount;
    }
}
