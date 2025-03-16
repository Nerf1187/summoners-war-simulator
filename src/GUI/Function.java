package GUI;

/**
 * Creates a new Function interface. Used when closing a message
 */
@FunctionalInterface
public interface Function
{
    /**
     * The function to apply after the Message is closed
     */
    void apply();
}
