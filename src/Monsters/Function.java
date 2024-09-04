package Monsters;

/**
 * Creates a new Function interface. Used when applying something to an entire team
 */
@FunctionalInterface
public interface Function
{
    /**
     * The function to apply to a Monster
     *
     * @param mon The monster to apply to
     */
    void apply(Monster mon);
}
