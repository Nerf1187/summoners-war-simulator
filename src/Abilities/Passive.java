package Abilities;

/**
 * The subclass for all passive abilities
 */
public class Passive extends Ability
{
    /**
     * Creates a new Passive ability with no cooldown
     *
     * @param name        The name of the Ability
     * @param description The description of the Ability
     */
    public Passive(String name, String description)
    {
        super(name, 0, 0, 0, description, 0, false, true, false, true, false);
        setToNumTurns(Integer.MAX_VALUE);
    }
    
    /**
     * Creates a new Passive ability
     *
     * @param name        The name of the Ability
     * @param description The description of the Ability
     * @param cooldown    The cooldown for the Ability
     */
    public Passive(String name, String description, int cooldown)
    {
        super(name, 0, 0, 0, description, cooldown, false, true, false, true, false);
    }
    
    /**
     * Formats the Passive ability object into a readable String
     *
     * @return the formatted String
     */
    public String toString()
    {
        return String.format("%s (Passive): %s", name, description);
    }
}
