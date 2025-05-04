package Runes;

/**
 * This class exists purely as an organizational tool, it functions exactly as a MainAttribute
 *
 * @author Anthony (Tony) Youssef
 */
public class SubAttribute extends MainAttribute
{
    /**
     * Creates a new SubAttribute
     *
     * @param attribute The attribute. See {@link RuneAttribute} for attributes
     * @param amount    The attribute's amount
     */
    public SubAttribute(RuneAttribute attribute, int amount)
    {
        super(attribute, amount);
    }
}
