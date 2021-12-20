package io.github.nickid2018.chemistrylab.util.pool;

/**
 * Objects implementing this interface will have {@link #reset()} called when
 * passed to .
 */
public interface Poolable {
    /**
     * Resets the object for reuse. Object references should be nulled and fields
     * may be set to default values.
     */
    void reset();
}