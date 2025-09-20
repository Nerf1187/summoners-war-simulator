package Game;

import java.time.*;

/**
 * This is class is used to help time the {@link Auto_Play program}. The stopwatch can be paused and restarted at any point
 */
public class StopWatch
{
    /**
     * Time of last play
     */
    private Instant start;
    
    /**
     * Time since first start (in nanoseconds)
     */
    private long elapsedNanoseconds;
    
    /**
     * Flag for if the timer is paused
     */
    private boolean paused = true;
    
    /**
     * Creates a new StopWatch object
     *
     * @param start       True if the stopwatch should start on creation
     * @param initialTime The initial time to start at
     */
    public StopWatch(boolean start, long initialTime)
    {
        elapsedNanoseconds = initialTime;
        if (start)
        {
            //Start the timer immediately if needed
            play();
        }
    }
    
    /**
     * Starts the stopwatch if possible, does nothing otherwise
     *
     * @return True if the stopwatch was successfully started, false otherwise
     */
    public boolean play()
    {
        //Do nothing if already playing
        if (!paused)
        {
            return false;
        }
        //Set new start time
        start = Instant.now();
        paused = false;
        return true;
    }
    
    /**
     * Pauses the StopWatch if possible, does nothing otherwise
     *
     * @return True if the StopWatch was successfully paused, false otherwise
     */
    public boolean pause()
    {
        //Do nothing if already paused
        if (paused)
        {
            return false;
        }
        //Update elapsed time
        Instant end = Instant.now();
        elapsedNanoseconds += Duration.between(start, end).toNanos();
        paused = true;
        return true;
    }
    
    /**
     * Resets the stopwatch to its initial state.
     * <p>
     * This method sets the elapsed time to zero and pauses the stopwatch.
     * It can be used to prepare the stopwatch for reuse after it has been started or paused.
     */
    public void reset()
    {
        elapsedNanoseconds = 0;
        paused = true;
    }
    
    /**
     * Retrieves the total elapsed time recorded by the stopwatch in nanoseconds.
     * This method ensures the stopwatch is in a consistent state by pausing and
     * immediately restarting it before returning the elapsed time.
     *
     * @return The total elapsed time in nanoseconds.
     */
    public long getElapsedTime()
    {
        if (!paused)
        {
            pause();
            play();
        }
        return elapsedNanoseconds;
    }
}
