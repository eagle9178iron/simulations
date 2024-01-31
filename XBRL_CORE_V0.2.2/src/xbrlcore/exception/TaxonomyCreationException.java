package xbrlcore.exception;

/**
 * An exception of this class is thrown if an error occurs during the creation
 * of a taxonomy. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class TaxonomyCreationException extends XBRLException {

    static final long serialVersionUID = 7031774969275247698L;
	
    public TaxonomyCreationException(String message) {
        super(message);
    }
}
