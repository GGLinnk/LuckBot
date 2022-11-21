package fr.virtualworld.gglinnk.luckbot.exceptions;

/**
 * Indicates that the configuration file is invalid.
 * */
public class InvalidConfigurationFileException extends RuntimeException {

    /**
     * Constructs an <code>InvalidConfigurationFileException</code> with no detail message.
     */
    public InvalidConfigurationFileException()
    {
        super();
    }

    /**
     * Constructs an <code>InvalidConfigurationFileException</code> with the
     * specified detail message.
     *
     * @param  message
     *         The detail message.
     */
    public InvalidConfigurationFileException(String message)
    {
        super(message);
    }
}
