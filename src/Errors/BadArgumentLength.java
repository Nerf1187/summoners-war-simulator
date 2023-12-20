package Errors;

/**
 * The BadArgumentLength error is used when two or more arguments (ex. ArrayList, vararg) passed into a method are invalid lengths relative to each other
 */
public class BadArgumentLength extends RuntimeException
{
    /**
     * Creates a new error
     *
     * @param message the message to print when thrown
     */
    public BadArgumentLength(String message)
    {
        super(message);
    }
}
