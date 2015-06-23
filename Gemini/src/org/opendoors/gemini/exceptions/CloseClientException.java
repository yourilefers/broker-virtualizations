package org.opendoors.gemini.exceptions;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class CloseClientException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CloseClientException() {
        super("Close client exception.");
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CloseClientException(String message) {
        super(message);
    }
}
