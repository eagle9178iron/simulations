package xbrlcore.dimensions;

import java.io.Serializable;

import org.jdom.Element;

import xbrlcore.taxonomy.Concept;

/**
 * This class encapsulates a dimension and a specific element of that dimension.<br/><br/>
 * If it is an explicit dimension, the element of the dimension is a domain
 * member and therefore a Concept object itself. If it is a typed dimension, it
 * either has no element or this element is of type org.jdom.Element. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class SingleDimensionType implements Serializable, Cloneable {

	static final long serialVersionUID = -5800161623727773757L;

	private boolean typedDimension;

	private Concept dimensionConcept;

	private Concept domainMemberConcept;

	private Element typedDimensionElement;

	/**
	 * Constructor for a typed dimension without an element.
	 * 
	 * @param dimension
	 *            Concept specifying the current dimension.
	 */
	public SingleDimensionType(Concept dimension) {
		this.dimensionConcept = dimension;
		typedDimension = true;
	}

	/**
	 * Constructor for an explicit dimension.
	 * 
	 * @param dimension
	 *            Concept specifying the current dimension.
	 * @param nCurrDomain
	 *            Concept specifying the current domain.
	 */
	public SingleDimensionType(Concept dimension, Concept nCurrDomain) {
		this.dimensionConcept = dimension;
		/*
		 * TODO: nCurrDomain must not be null since it would conflict with the
		 * constructor SingleDimensionType(Concept, Document) - how to handle
		 * this?
		 */
		this.domainMemberConcept = nCurrDomain;
		typedDimension = false;
	}

	/**
	 * TODO: Bad practice: The method does not invoke super.clone(). This method
	 * creates a clone of the current SingleDimensionType object.
	 * 
	 * @return A 1:1 copy of the current SingleDimensionType.
	 */
	public Object clone() throws CloneNotSupportedException {
		SingleDimensionType clone = null;
		if (isTypedDimension()) {
			clone = new SingleDimensionType((Concept) dimensionConcept.clone(),
					(Element) typedDimensionElement.clone());
		} else {
			clone = new SingleDimensionType((Concept) dimensionConcept.clone(),
					(Concept) domainMemberConcept.clone());
		}
		return clone;
	}

	/**
	 * Constructor for a typed dimension with an element.
	 * 
	 * @param dimension
	 *            Concept specifying the current dimension.
	 * @param typedDimensionElement
	 *            JDOM Element specifying the element of this dimension.
	 */
	public SingleDimensionType(Concept dimension, Element typedDimensionElement) {
		this.dimensionConcept = dimension;
		this.typedDimensionElement = typedDimensionElement;
		typedDimension = true;
	}

	/**
	 * Checks whether two SingleDimensionType objects are equal. If both
	 * represent explicit dimensions, this is true if both refer to the same
	 * dimension and domain member concept. <br/>If both represent typed
	 * dimensions, this is true if both refer to the same dimension concept and
	 * if the typed domain member are equal. Currently only simple typed member
	 * are supported (no nested elements).
	 * 
	 * @return true if both objects are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SingleDimensionType))
			return false;
		SingleDimensionType otherType = (SingleDimensionType) obj;
		return dimensionConcept.equals(otherType.getDimensionConcept())
				&& typedDimension == otherType.isTypedDimension()
				&& (domainMemberConcept == null ? otherType
						.getDomainMemberConcept() == null : domainMemberConcept
						.equals(otherType.getDomainMemberConcept()))
				&&
				/*
				 * It is not possible to perform equals() on a JDom Document
				 * object, since this only checks for identity (==). Therefore
				 * equals() is performed on the String returned by
				 * Document.getRootElement().getValue().
				 */
				(typedDimensionElement == null ? otherType
						.getTypedDimensionElement() == null
						: typedDimensionElement.getValue()
								.equals(
										otherType.getTypedDimensionElement()
												.getValue()));
	}

	/**
	 * @return A hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (typedDimension ? 1 : 0);
		hash = hash * 31 + dimensionConcept.hashCode();
		hash = hash
				* 31
				+ (domainMemberConcept != null ? domainMemberConcept.hashCode()
						: 0);
		hash = hash
				* 31
				+ (typedDimensionElement != null ? typedDimensionElement
						.hashCode() : 0);
		return hash;
	}

	/**
	 * @return The domain member element.
	 */
	public Concept getDomainMemberConcept() {
		return domainMemberConcept;
	}

	/**
	 * @return The dimension element.
	 */
	public Concept getDimensionConcept() {
		return dimensionConcept;
	}

	/**
	 * @param element
	 *            The dimension element.
	 */
	public void setDimensionConcept(Concept element) {
		dimensionConcept = element;
	}

	/**
	 * @param element
	 *            The domain member element.
	 */
	public void setDomainMemberConcept(Concept element) {
		domainMemberConcept = element;
	}

	/**
	 * @return True if this dimension / domain combination belongs to a typed
	 *         dimension, otherwise false.
	 */
	public boolean isTypedDimension() {
		return typedDimension;
	}

	/**
	 * @return JDOM document representing the "domain member" of the typed
	 *         dimension, if this dimension is typed.
	 */
	public Element getTypedDimensionElement() {
		return typedDimensionElement;
	}

	/**
	 * 
	 * @param typedDimensionElement
	 *            JDOM Element representing the "domain member" of the typed
	 *            dimension, if this dimension is typed.
	 */
	public void setTypedDimensionElement(Element typedDimensionElement) {
		this.typedDimensionElement = typedDimensionElement;
	}
}
