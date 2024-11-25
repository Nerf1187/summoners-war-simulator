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
    private long elapsedNanoseconds = 0;
    /**
     * Flag for if the timer is paused
     */
    private boolean paused = true;
    
    /**
     * Creates a new StopWatch object
     *
     * @param start True if the stopwatch should start on creation
     */
    public StopWatch(boolean start)
    {
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
     * @return The amount of time that elapsed while the StopWatch was played (in nanoseconds)
     */
    public long getElapsedTime()
    {
        return elapsedNanoseconds;
    }
}
