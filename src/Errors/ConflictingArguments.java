package Errors;

/**
 * The ConflictingArguments error is thrown when two or more arguments (ex. boolean) passed in a method conflict with each other
 */
public class ConflictingArguments extends RuntimeException
{
    /**
     * Creates a new error
     *
     * @param message The message to print when thrown
     */
    public ConflictingArguments(String message)
    {
        super(message);
    }
}
