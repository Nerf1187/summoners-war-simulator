package Runes;

/**
 * This class contains the information for rune attributes
 *
 * @author Anthony (Tony) Youssef
 */
public class MainAttribute
{
    private final int num, amount;
    
    /**
     * Creates a new MainAttribute
     *
     * @param num    The attribute number. See {@link Rune} for attribute numbers
     * @param amount The attribute's amount
     */
    public MainAttribute(int num, int amount)
    {
        this.num = num;
        this.amount = amount;
    }
    
    /**
     * Gets the attribute's amount
     *
     * @return The attribute's amount
     */
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * Gets the attribute number
     *
     * @return The attribute number
     */
    public int getNum()
    {
        return num;
    }
    
    /**
     * Formats the attribute into a readable String
     *
     * @return The formatted String
     */
    public String toString()
    {
        //Get the affected stat
        String s = switch (num)
        {
            case 1, 2 -> "Attack ";
            case 3, 4 -> "Defense ";
            case 5, 6 -> "HP ";
            case 7 -> "Speed ";
            case 8 -> "Crit Rate ";
            case 9 -> "Crit Damage ";
            case 10 -> "Resistance ";
            case 11 -> "Accuracy ";
            default -> "";
        };
        
        s += amount;
        if ((num % 2 == 0 && num <= 6) || num >= 8)
        {
            s += "%";
        }
        return s;
    }
}