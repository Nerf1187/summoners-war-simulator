package Effects.Buffs;

/**
 * This class is used to differentiate between rune shields and regular shields
 *
 * @author Anthony (Tony) Youssef
 */
public class RuneShield extends Shield
{
    /**
     * Creates a new Rune shield
     *
     * @param amount   The amount of health the shield has
     * @param numTurns The number of turns it is active for
     */
    public RuneShield(int amount, int numTurns)
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