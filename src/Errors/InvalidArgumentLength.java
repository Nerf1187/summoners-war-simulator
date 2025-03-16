package Errors;

/**
 * The InvalidArgumentLength error is used when two or more arguments (ex. ArrayList, vararg) passed into a method are invalid lengths relative to each other
 */
public class InvalidArgumentLength extends RuntimeException
{
    /**
     * Creates a new error
     *
     * @param message The message to print when thrown
     */
    public InvalidArgumentLength(String message)
    {
        super(message);
    }
}
