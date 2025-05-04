package Util;

/**
 * A generic class that holds a pair of objects of potentially different types.
 * It provides methods to access and modify the pair's elements.
 *
 * @param <I> the type of the first object in the pair
 * @param <J> the type of the second object in the pair
 */
public class Pair<I, J>
{
    private I obj1;
    private J obj2;
    
    /**
     * Constructs a Pair with the specified objects.
     *
     * @param obj1 the first object in the pair
     * @param obj2 the second object in the pair
     */
    public Pair(I obj1, J obj2)
    {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }
    
    /**
     * Retrieves the first object in the pair.
     *
     * @return the first object of type I in the pair
     */
    public I getFirst()
    {
        return obj1;
    }
    
    /**
     * Updates the first object in the pair with the specified value.
     *
     * @param obj1 the new value to be set as the first object in the pair
     */
    public void setFirst(I obj1)
    {
        this.obj1 = obj1;
    }
    
    /**
     * Retrieves the second object in the pair.
     *
     * @return the second object of type J in the pair
     */
    public J getSecond()
    {
        return obj2;
    }
    
    /**
     * Updates the second object in the pair with the specified value.
     *
     * @param obj2 the new value to be set as the second object in the pair
     */
    public void setSecond(J obj2)
    {
        this.obj2 = obj2;
    }
}