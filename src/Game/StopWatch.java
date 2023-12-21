package Game;

import java.time.*;

/**
 * This is class is used to help time the {@link Auto_Play program}. The stopwatch can be paused and restarted at any point
 */
public class StopWatch
{
    private Instant start;
    private long elapsedMilliseconds = 0;
    private boolean paused = true;
    
    /**
     * Creates a new StopWatch object
     *
     * @param start True if the stopwatch should start on creation
     */
    public StopWatch (boolean start)
    {
        if (start)
        {
            paused = false;
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
        if (!paused)
        {
            return false;
        }
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
        if (paused)
        {
            return false;
        }
        Instant end = Instant.now();
        elapsedMilliseconds += Duration.between(start, end).toMillis();
        paused = true;
        return true;
    }
    
    /**
     * @return The amount of time that elapsed while the StopWatch was played
     */
    public long getElapsedTime()
    {
        return elapsedMilliseconds;
    }
}
