package Stats.Buffs;

/**
 * @author Anthony (Tony) Youssef
 * This class is used to differentiate between rune shields and regular shields
 */
public class Rune_shield extends Shield
{
    /**
     * Creates a new Rune shield
     *
     * @param amount   The amount of health the shield has
     * @param numTurns The number of turns it is active for
     */
    public Rune_shield(int amount, int numTurns)
    {
        super(amount, numTurns);
    }
    
    /**
     * Formats the Rune shield into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        return "Rune " + super.toString();
    }
}
