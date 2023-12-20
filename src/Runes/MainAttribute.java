package Runes;

/**
 * @author Anthony (Tony) Youssef
 * This class contains the information for rune attributes
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
     * @return The attribute's amount
     */
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * @return The attribute number
     */
    public int getNum()
    {
        return num;
    }
    
    /**
     * Formats the attribute into a readable String
     *
     * @return the formatted String
     */
    public String toString()
    {
        String s = "";
        switch (num)
        {
            case 1, 2 -> s += "Attack ";
            case 3, 4 -> s += "Defense ";
            case 5, 6 -> s += "HP ";
            case 7 -> s += "Speed ";
            case 8 -> s += "Crit Rate ";
            case 9 -> s += "Crit Damage ";
            case 10 -> s += "Resistance ";
            case 11 -> s += "Accuracy ";
        }
        
        s += amount;
        if ((num % 2 == 0 && num <= 6) || num >= 8)
        {
            s += "%";
        }
        return s;
    }
}
