package xbrlcore.instance;

import java.io.Serializable;

import xbrlcore.exception.InstanceException;
import xbrlcore.taxonomy.Concept;

/**
 * This class represents a Fact within an XBRL instance document as it is
 * described by the XBRL 2.1 Specification, which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author Daniel Hamm
 */

public class Fact implements Serializable {

    static final long serialVersionUID = -8027402116197248526L;

    private Concept concept;

    private InstanceContext instanceContext;

    private InstanceUnit instanceUnit;

    private String decimals;

    private String precision;

    private String value;

    /**
     * Constructor.
     * 
     * @param concept
     *            Concept this fact refers to.
     * @exception InstanceException
     *                This exception is thrown if the concept is NULL or
     *                abstract.
     */
    public Fact(Concept concept) throws InstanceException {
        if (concept == null || concept.isAbstract()) {
            throw new InstanceException(
                    "Fact cannot be created: Concept must be not null and not abstract");
        }
        this.concept = concept;
    }

    /**
     * Constructor.
     * 
     * @param concept
     *            Concept this fact refers to.
     * @param value
     *            Value of the fact.
     * @exception InstanceException
     *                This exception is thrown if the concept is NULL or
     *                abstract.
     *  
     */
    /*
     * TODO: It is not checked whether the value is compliant to the data type
     * of the concept!
     */
    public Fact(Concept concept, String value) throws InstanceException {
        this(concept);
        this.value = value;
    }

    /**
     * 
     * Returns whether two Fact objects are equal or not. They are equal if and
     * only if: <br/>- the XBRL concepts they refer to are equal <br/>- the
     * units they refer to are equal <br/>- the instance contexts they refer to
     * are equal <br/>
     * 
     * @return True if both objects are the same, false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Fact))
            return false;
        Fact otherFact = (Fact) obj;
        return concept.equals(otherFact.getConcept())
                && (instanceContext == null ? otherFact.getInstanceContext() == null
                        : instanceContext
                                .equals(otherFact.getInstanceContext()))
                && (instanceUnit == null ? otherFact.getInstanceUnit() == null
                        : instanceUnit.equals(otherFact.getInstanceUnit()));
    }

    /**
     * @return Returns a hash code of this object.
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + concept.hashCode();
        hash = hash * 31
                + (instanceContext != null ? instanceContext.hashCode() : 0);
        hash = hash * 31 + (instanceUnit != null ? instanceUnit.hashCode() : 0);
        return hash;
    }

    /**
     * 
     * @return Concept this fact refers to.
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     * 
     * @return Value of the attribute "decimals" of this fact.
     */
    public String getDecimals() {
        return decimals;
    }

    /**
     * Sets the value of the "decimals" attribute of this fact. According to the
     * XBRL 2.1 Spec., a decimal attribute can only be set for numeric items. It
     * must be an integer or the String "INF".
     * 
     * @param decimals
     *            Value of the attribute "decimals" of this fact.
     * @throws InstanceException
     *             Thrown if the type of the attribute is not compliant to the
     *             XBRL 2.1 Spec.
     */
    public void setDecimals(String decimals) throws InstanceException {
        if (!concept.isNumericItem()) {
            throw new InstanceException(
                    "Decimals attribute can only be set for numeric items");
        }
        try {
            new Integer(decimals);
            this.decimals = decimals;
            precision = null;
        } catch (NumberFormatException ex) {
            if (decimals != null && decimals.equals("INF")) {
                this.decimals = decimals;
                precision = null;
            } else {
                throw new InstanceException(
                        "Invalid value for decimals attribute: " + decimals);
            }
        }
    }

    /**
     * 
     * @return Value of this fact.
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *            Value of this fact.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return The context this fact refers to.
     */
    public InstanceContext getInstanceContext() {
        return instanceContext;
    }

    /**
     * 
     * @param instanceContext
     *            The context this fact refers to.
     */
    public void setInstanceContext(InstanceContext instanceContext) {
        this.instanceContext = instanceContext;
    }

    /**
     * 
     * @return ID of the context this fact refers to.
     */
    public String getContextRef() {
        return (instanceContext != null ? instanceContext.getId() : null);
    }

    /**
     * @return Unit this fact refers to.
     */
    public InstanceUnit getInstanceUnit() {
        return instanceUnit;
    }

    /**
     * According to the XBRL 2.1 Spec., a unit can only be set to a numeric
     * item.
     * 
     * @param unit
     *            Unit this fact refers to.
     * @throws InstanceException
     *             Thrown if a unit is set to a not numeric item.
     */
    public void setInstanceUnit(InstanceUnit unit) throws InstanceException {
        if (!concept.isNumericItem()) {
            throw new InstanceException(
                    "A unit can only be set for numeric items: "
                            + concept.getId() + " is not numeric");
        }
        instanceUnit = unit;
    }

    /**
     * @return Returns the precision.
     */
    public String getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the "precision" attribute. According to the XBRL 2.1
     * Spec., this attribute can only be set for numeric items and must be an
     * integer greater than or equal 0. The String "INF" is valid as well.
     * 
     * @param precision
     *            The precision to set.
     * @throws InstanceException
     *             Thrown if the precision attribute is not compliant to the
     *             XBRL 2.1 Spec.
     */
    public void setPrecision(String precision) throws InstanceException {
        if (!concept.isNumericItem()) {
            throw new InstanceException(
                    "Precision attribute can only be set for numeric items: "
                            + concept.getId() + " is not numeric");
        }
        if (precision != null && precision.equals("INF")) {
            this.precision = precision;
            decimals = null;
            return;
        }
        try {
            Integer intValue = new Integer(precision);
            if (intValue.intValue() < 0)
                throw new InstanceException("");
            this.precision = precision;
            decimals = null;
        } catch (NumberFormatException ex) {
            throw new InstanceException(
                    "Invalid format for precision attribute: " + precision);
        } catch (InstanceException ex) {
            throw new InstanceException(
                    "Invalid value for precision attribute: " + precision);
        }
    }
}
