package Util;

/**
 * A generic class that holds a triplet of objects, each potentially of a different type.
 * It provides methods to access and modify the triplet's components.
 *
 * @param <I> the type of the first object in the triplet
 * @param <J> the type of the second object in the triplet
 * @param <K> the type of the third object in the triplet
 */
public class Triplet<I, J, K>
{
    private I obj1;
    private J obj2;
    private K obj3;
    
    /**
     * Constructs a Triplet with the specified objects.
     *
     * @param obj1 the first object in the triplet of type I
     * @param obj2 the second object in the triplet of type J
     * @param obj3 the third object in the triplet of type K
     */
    public Triplet(I obj1, J obj2, K obj3)
    {
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }
    
    /**
     * Retrieves the first object in the triplet.
     *
     * @return the first object of type I in the triplet
     */
    public I getFirst()
    {
        return obj1;
    }
    
    /**
     * Updates the first object in the triplet with the specified value.
     *
     * @param obj1 the new value to be set as the first object in the triplet
     */
    public void setFirst(I obj1)
    {
        this.obj1 = obj1;
    }
    
    /**
     * Retrieves the second object in the triplet.
     *
     * @return the second object of type J in the triplet
     */
    public J getSecond()
    {
        return obj2;
    }
    
    /**
     * Updates the second object in the triplet with the specified value.
     *
     * @param obj2 the new value to be set as the second object in the triplet
     */
    public void setSecond(J obj2)
    {
        this.obj2 = obj2;
    }
    
    /**
     * Retrieves the third object in the triplet.
     *
     * @return the third object of type K in the triplet
     */
    public K getThird()
    {
        return obj3;
    }
    
    /**
     * Updates the third object in the triplet with the specified value.
     *
     * @param obj3 the new value to be set as the third object in the triplet
     */
    public void setThird(K obj3)
    {
        this.obj3 = obj3;
    }
}
