package graph;

/**
 * Interface for collecting metrics: timing and operation counters.
 */
public interface Metrics {
    /**
     * Starts timing.
     */
    void startTiming();

    /**
     * Stops timing and returns elapsed nanoseconds.
     * @return elapsed time in nanoseconds
     */
    long stopTiming();

    /**
     * Increments a named counter.
     * @param name counter name
     */
    void incrementCounter(String name);

    /**
     * Gets the value of a named counter.
     * @param name counter name
     * @return counter value
     */
    long getCounter(String name);

    /**
     * Resets all counters and timing.
     */
    void reset();
}