package xbrlcore.exception;

/**
 * An exception that is thrown if something with an instance goes wrong. <br/>
 * <br/>
 * 
 * @author Daniel Hamm
 */
public class InstanceException extends XBRLException {
	
    static final long serialVersionUID = 3219000168674937922L;

    public InstanceException(String message) {
        super(message);
    }
}
