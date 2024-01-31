package xbrlcore.xlink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;

/**
 * This class represents an arc in a linkbase file. Its most important
 * attributes are a source and a target element as well as an arcrole indicating
 * the semantic meaning of the arc. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class Arc implements Serializable, Comparable {

	static final long serialVersionUID = -8032592797402796322L;

	private ExtendedLinkElement sourceElement;

	private ExtendedLinkElement targetElement;

	private String arcrole;

	private String extendedLinkRole;

	private String targetRole;

	private String contextElement;

	private List attributes;

	/* TODO: include order in equals() and hashCode()? */
	private float order;

	/* TODO: include weightAttribute in equals() and hashCode()? */
	private float weightAttribute; /* for calculation arcs */

	private String useAttribute; /* use attribute (Spec. 3.5.3.9.7.1) */

	private int priorityAttribute; /* priority attribute (Spec. 3.5.3.9.7.2) */

	/**
	 * Constructor.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the arc.
	 */
	public Arc(String extendedLinkRole) {
		this.extendedLinkRole = extendedLinkRole;
		useAttribute = "optional"; /*
									 * the default of the use attribute is
									 * "optional" (Spec. 3.5.3.9.7.1)
									 */
		priorityAttribute = 0; /*
								 * default of the priority attribute is "0"
								 * (Spec. 3.5.3.9.7.2)
								 */
		weightAttribute = .0F;
		order = .0F;
		attributes = new ArrayList();
	}

	/**
	 * Checks if two Arc objects are equal. This is true if and only if: <br/>-
	 * both have the same source element <br/>- both have the same target
	 * element <br/>- both are within the same extended link role <br/>- both
	 * have the same value in their "use" attribute <br/>- both have the same
	 * value in their "priority" attribute <br/>-the non-XLink attributes of
	 * both are s-equal
	 * 
	 * @param obj
	 *            The object the current Arc is checked against.
	 * @return True if both Arc objects are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Arc))
			return false;
		Arc otherArc = (Arc) obj;
		return sourceElement.equals(otherArc.getSourceElement())
				&& targetElement.equals(otherArc.getTargetElement())
				&& extendedLinkRole.equals(otherArc.getExtendedLinkRole())
				&& useAttribute.equals(otherArc.getUseAttribute())
				&& priorityAttribute == otherArc.getPriorityAttribute()
				&& hasSameAttributes(otherArc);
	}

	/**
	 * Checks if two Arc objects have the same source. This is true if and only
	 * if: <br/>- the source locators of both arcs refer to the same concept
	 * <br/>- both arcs are in the same extended link role
	 * 
	 * @param obj
	 *            The object the current Arc is checked against.
	 * @return True if the check succeeds, false otherwise.
	 */
	public boolean equalSource(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Arc))
			return false;
		Arc otherArc = (Arc) obj;

		if (sourceElement.isLocator()
				&& otherArc.getSourceElement().isLocator()) {
			Locator sourceLocator = (Locator) sourceElement;
			Locator otherSourceLoctator = (Locator) otherArc.getSourceElement();
			return sourceLocator.getConcept().equals(
					otherSourceLoctator.getConcept())
					&& extendedLinkRole.equals(otherArc.getExtendedLinkRole());
		}
		return false;
	}

	/**
	 * Checks if two Arc objects have the same target. This is true if and only
	 * if: <br/>- the target locators of both arcs refer to the same concept
	 * <br/>- both arcs are in the same extended link role
	 * 
	 * @param obj
	 *            The object the current Arc is checked against.
	 * @return True if the check succeeds, false otherwise.
	 */
	public boolean equalTarget(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Arc))
			return false;
		Arc otherArc = (Arc) obj;

		if (targetElement.isLocator()
				&& otherArc.getTargetElement().isLocator()) {
			Locator targetLocator = (Locator) sourceElement;
			Locator otherTargetLoctator = (Locator) otherArc.getSourceElement();
			return targetLocator.getConcept().equals(
					otherTargetLoctator.getConcept())
					&& extendedLinkRole.equals(otherArc.getExtendedLinkRole());
		} else {
			return targetElement.equals(otherArc.getTargetElement())
					&& extendedLinkRole.equals(otherArc.getExtendedLinkRole());
		}
	}

	/**
	 * Checks if two Arc objects have the same source and the same target
	 * element. This is true if and only if: <br/>- the source locators of both
	 * arcs refer to the same concept <br/>- the target locators of both arcs
	 * refer to the same concept <br/>- both arcs are in the same extended link
	 * role
	 * 
	 * @param obj
	 *            The object the current Arc is checked against.
	 * @return True if the check succeeds, false otherwise.
	 */
	public boolean equalSourceTarget(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Arc))
			return false;
		Arc otherArc = (Arc) obj;

		if (sourceElement.isLocator() && targetElement.isLocator()
				&& otherArc.getSourceElement().isLocator()
				&& otherArc.getTargetElement().isLocator()) {
			Locator sourceLocator = (Locator) sourceElement;
			Locator targetLocator = (Locator) targetElement;
			Locator otherSourceLocator = (Locator) otherArc.getSourceElement();
			Locator otherTargetLocator = (Locator) otherArc.getTargetElement();
			return sourceLocator.getConcept().equals(
					otherSourceLocator.getConcept())
					&& targetLocator.getConcept().equals(
							otherTargetLocator.getConcept())
					&& extendedLinkRole.equals(otherArc.getExtendedLinkRole());
		} else {
			return sourceElement.equals(otherArc.getSourceElement())
					&& targetElement.equals(otherArc.getTargetElement())
					&& extendedLinkRole.equals(otherArc.getExtendedLinkRole());
		}
	}

	/**
	 * @return Returns a hash code for this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + sourceElement.hashCode();
		hash = hash * 31 + targetElement.hashCode();
		hash = hash * 31 + extendedLinkRole.hashCode();
		hash = hash * 31 + useAttribute.hashCode();
		hash = hash * 31 + priorityAttribute;
		return hash;
	}

	/**
	 * This method checks if this arc is overridden by another arc.
	 * 
	 * @param overridingArc
	 *            Possible overriding arc.
	 * @return True if overriding arc overrides this arc, otherwise false. The
	 *         implementation follows the Spec. chapter 3.5.3.9.7.4 and
	 *         3.5.3.9.7.5
	 */
	public boolean isOverriddenByArc(Arc overridingArc) {
		if (overridingArc.getTargetElement().isLocator()
				&& targetElement.isLocator()) {
			/* a locator is overridden */
			if (overridingArc.getPriorityAttribute() >= priorityAttribute
					&& isEquivalentRelationship(overridingArc)) {
				return true;
			}
		} else if (overridingArc.getTargetElement().isResource()
				&& targetElement.isResource()) {
			/* TODO: a resource is overridden - this is not supported yet */
		}
		return false;
	}

	/**
	 * This method checks if this arc overrides another arc.
	 * 
	 * @param overriddenArc
	 *            Arc which is probably overridden by this arc.
	 * @return True if this arc overrides overridden arc, otherwise false. The
	 *         implementation follows the Spec. chapter 3.5.3.9.7.4 and
	 *         3.5.3.9.7.5
	 */
	public boolean overridesArc(Arc overriddenArc) {
		if (targetElement.isLocator()
				&& overriddenArc.getTargetElement().isLocator()) {
			/* a locator is overridden */
			if (priorityAttribute >= overriddenArc.getPriorityAttribute()
					&& isEquivalentRelationship(overriddenArc)) {
				return true;
			}
		} else if (targetElement.isResource()
				&& overriddenArc.getTargetElement().isResource()) {
			/* TODO: a resource is overridden - this is not supported yet */
		}
		return false;
	}

	/**
	 * This method checks if this arc is prohibited by another arc.
	 * 
	 * @param prohibitingArc
	 *            Possible prohibiting arc.
	 * @return True if prohibitingArc prohibits this arc, otherwise false. The
	 *         implementation follows the Spec. chapter 3.5.3.9.7.4 and
	 *         3.5.3.9.7.5
	 */
	public boolean isProhibitedByArc(Arc prohibitingArc) {
		if (prohibitingArc.getUseAttribute().equals("prohibited")
				&& isEquivalentRelationship(prohibitingArc)
				&& prohibitingArc.getPriorityAttribute() >= priorityAttribute) {
			return true;
		}
		return false;
	}

	/**
	 * This method checks if this arc prohibits another one.
	 * 
	 * @param prohibitedArc
	 *            Arc which is probably prohibited by this arc.
	 * @return True if this arc prohibits prohibitedArc, otherwise false. The
	 *         implementation follows the Spec. chapter 3.5.3.9.7.4 and
	 *         3.5.3.9.7.5
	 */
	public boolean prohibitsArc(Arc prohibitedArc) {
		if (useAttribute.equals("prohibited")
				&& isEquivalentRelationship(prohibitedArc)
				&& prohibitedArc.getPriorityAttribute() <= priorityAttribute) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two arcs form an "Equivalent Relationship" as described in
	 * the Spec. chapter 3.5.3.9.7.4.
	 * 
	 * @param equivalentArc
	 *            Arc against the relationship is checked.
	 * @return True if both arcs (this one and equivalentArc) from an
	 *         "Equivalent Relationship" as described in chapter 3.5.2.9.7.4 in
	 *         the Spec.
	 */
	public boolean isEquivalentRelationship(Arc equivalentArc) {
		return equalSourceTarget(equivalentArc)
				&& hasSameAttributes(equivalentArc);
	}

	/**
	 * Checks whether two arcs have s-equal non-XLink attributes (except "use"
	 * and "priority"), which is one requirement to be of equivalent
	 * relationship as described in Spec. chapter 3.5.3.9.7.4.
	 * 
	 * @param arcToCompare
	 *            Arc which attributes are compared to the attributes of this
	 *            arc.
	 * @return True if the value of any non-XLink attribute except "use" and
	 *         "priority" is s-equal to the same non-XLink attribute on the
	 *         other arc.
	 */
	public boolean hasSameAttributes(Arc arcToCompare) {
		boolean found = false;
		List attributeListToCompare = arcToCompare.getAttributes();
		nextAttribute: for (int i = 0; i < attributes.size(); i++) {
			Attribute currAttr = (Attribute) attributes.get(i);
			if (!currAttr.getName().equals("use")
					&& !currAttr.getName().equals("priority")
					&& !currAttr.getNamespaceURI().equals(
							"http://www.w3.org/1999/xlink")) {
				/*
				 * there must be an s-equal attribute in the other arc, see
				 * Spec. 3.5.3.9.7.4
				 */
				found = false;
				for (int j = 0; j < attributeListToCompare.size(); j++) {
					Attribute currAttrToCompare = (Attribute) attributeListToCompare
							.get(j);
					if (currAttr.getName().equals(currAttrToCompare.getName())
							&& currAttr.getValue().equals(
									currAttrToCompare.getValue())
							&& currAttr.getNamespace().equals(
									currAttrToCompare.getNamespace())) {
						continue nextAttribute;
					}
				}
				if (!found) {
					return false;
				}
			} else
				continue nextAttribute;
		}

		return true;
	}

	/**
	 * @return Arcrole of the arc.
	 */
	public String getArcrole() {
		return arcrole;
	}

	/**
	 * @return XLinkElement object from which this arc is linked from.
	 */
	public ExtendedLinkElement getSourceElement() {
		return sourceElement;
	}

	/**
	 * @return XLinkElement object to which this arc is linked to.
	 */
	public ExtendedLinkElement getTargetElement() {
		return targetElement;
	}

	/**
	 * @param string
	 *            Arcrole of the arc.
	 */
	public void setArcrole(String string) {
		arcrole = string;
	}

	/**
	 * @param element
	 *            XLinkElement object from which this arc is linked from.
	 */
	public void setSourceElement(ExtendedLinkElement element) {
		sourceElement = element;
	}

	/**
	 * @param element
	 *            XLinkElement object to which this arc is linked to.
	 */
	public void setTargetElement(ExtendedLinkElement element) {
		targetElement = element;
	}

	/**
	 * @return Extended link role of the arc.
	 */
	public String getExtendedLinkRole() {
		return extendedLinkRole;
	}

	/**
	 * @param string
	 *            Extended link role of the arc.
	 */
	public void setXbrlExtendedLinkRole(String string) {
		extendedLinkRole = string;
	}

	/**
	 * @return Target role attribute (if specified).
	 */
	public String getTargetRole() {
		return targetRole;
	}

	/**
	 * @param string
	 *            Target role attribute (if specified).
	 */
	public void setTargetRole(String string) {
		targetRole = string;
	}

	/**
	 * @return Order of the arc.
	 */
	public float getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            Order of the arc.
	 */
	public void setOrder(float order) {
		this.order = order;
	}

	/**
	 * @return Content of attribute "use".
	 */
	public String getUseAttribute() {
		return useAttribute;
	}

	/**
	 * @param string
	 *            Content of attribute "use".
	 */
	public void setUseAttribute(String string) {
		/*
		 * use can only be "optional" or "prohibited", otherwise it is ignored
		 * (Spec. 3.5.3.9.7.1)
		 */
		if (string != null
				&& (string.equals("optional") || string.equals("prohibited"))) {
			useAttribute = string;
		}
	}

	/**
	 * @return Attribute "priority" of this arc.
	 */
	public int getPriorityAttribute() {
		return priorityAttribute;
	}

	/**
	 * @param i
	 *            Value of the priority attribute of this arc.
	 */
	public void setPriorityAttribute(int i) {
		priorityAttribute = i;
	}

	public int compareTo(Object obj) {
		if (!(obj instanceof Arc))
			return 0;
		return (priorityAttribute > ((Arc) obj).getPriorityAttribute()) ? 1
				: -1;
	}

	/**
	 * @return Returns the weightAttribute.
	 */
	public float getWeightAttribute() {
		return weightAttribute;
	}

	/**
	 * @param weight
	 *            The weightAttribute to set.
	 */
	public void setWeightAttribute(float weight) {
		this.weightAttribute = weight;
	}

	/**
	 * @return Returns the contextElement.
	 */
	public String getContextElement() {
		return contextElement;
	}

	/**
	 * @param contextElement
	 *            The contextElement to set.
	 */
	public void setContextElement(String contextElement) {
		this.contextElement = contextElement;
	}

	/**
	 * @return Returns the attributes.
	 */
	public List getAttributes() {
		return attributes;
	}

	/**
	 * @param attributeList
	 *            The attributes to set.
	 */
	public void setAttributes(List attributeList) {
		this.attributes = attributeList;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
}
