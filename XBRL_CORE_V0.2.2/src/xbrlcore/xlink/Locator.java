package xbrlcore.xlink;

import xbrlcore.taxonomy.Concept;

/**
 * This class represents a locator within a linkbase.<br/><br/> A locator can
 * either refer to a concept of a taxonomy or to a resource within another
 * linkbase. <br/> <br/>
 * 
 * @author Daniel Hamm
 */
public class Locator extends ExtendedLinkElement {

	static final long serialVersionUID = 3488018552010465175L;

	private Concept concept;

	private Resource resource;

	/* for the definition linkbase */
	private boolean usable;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label of the locator.
	 */
	public Locator(String label, String linkbaseSource) {
		super(label, linkbaseSource);
		usable = true;
	}

	/**
	 * Checks if two locators are equal. This is true if and only if: <br/>-
	 * their label attributes are equal <br/>- they are in the same extended
	 * link role <br/>- they both refer either to the same concept or the same
	 * resource
	 * 
	 * @param obj
	 *            The object the current Locator is checked against.
	 * @return True if both locators are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Locator))
			return false;
		Locator otherLocator = (Locator) obj;
		return getLabel().equals(otherLocator.getLabel())
				&& (getExtendedLinkRole() == null ? otherLocator
						.getExtendedLinkRole() == null : getExtendedLinkRole()
						.equals(otherLocator.getExtendedLinkRole()))
				&& (concept == null ? otherLocator.getConcept() == null
						: concept.equals(otherLocator.getConcept()))
				&& (resource == null ? otherLocator.getResource() == null
						: resource.equals(otherLocator.getResource()))
				&& (getLinkbaseSource() == null ? otherLocator
						.getLinkbaseSource() == null : getLinkbaseSource()
						.equals(otherLocator.getLinkbaseSource()));
	}

	/**
	 * @return Returns a hash code for this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getLabel().hashCode();
		hash = hash
				* 31
				+ (getExtendedLinkRole() != null ? getExtendedLinkRole()
						.hashCode() : 0);
		hash = hash * 31 + (concept != null ? concept.hashCode() : 0);
		hash = hash * 31 + (resource != null ? resource.hashCode() : 0);
		hash = hash
				* 31
				+ (getLinkbaseSource() != null ? getLinkbaseSource().hashCode()
						: 0);
		return hash;
	}

	/**
	 * @return true
	 */
	public boolean isLocator() {
		return true;
	}

	/**
	 * @return false
	 */
	public boolean isResource() {
		return false;
	}

	/**
	 * @return Concept this locator refers to.
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param concept
	 *            Concept this locator refers to.
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * @return xbrldt:usable attribute of the according arc.
	 */
	public boolean isUsable() {
		return usable;
	}

	/**
	 * @param b
	 *            xbrldt:usable attribute of the according arc.
	 */
	public void setUsable(boolean b) {
		usable = b;
	}

	/**
	 * @return Resource this locator refers to.
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            Resource this locator refers to.
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
