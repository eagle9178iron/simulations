package xbrlcore.xlink;

import java.io.Serializable;

/**
 * This class is an abstract super class for possible extended link elements,
 * namely locators and resources. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public abstract class ExtendedLinkElement implements Serializable {

	static final long serialVersionUID = -6966857625345165480L;

	private String label;

	private String role;

	private String title;

	private String id;

	private String extendedLinkRole;

	private String linkbaseSource;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label for this extended link element.
	 */
	public ExtendedLinkElement(String label, String linkbaseSource) {
		this.label = label;
		this.linkbaseSource = linkbaseSource;
	}

	/**
	 * Checks whether two extended link elements are equal. This is true if and
	 * only if: <br/>- both must be either locators or resources <br/>- both
	 * have the same label attribute <br/>- both have the same role attribute
	 * <br/>- both have the same title attribute <br/>- both have the same id
	 * attribute <br/>- both belong to the same extended link role <br/>
	 * 
	 * @param obj
	 *            The object the current ExtendedLinkElement is checked against.
	 * @return True if both objects are equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ExtendedLinkElement))
			return false;
		ExtendedLinkElement otherElement = (ExtendedLinkElement) obj;
		return isLocator() == otherElement.isLocator()
				&& isResource() == otherElement.isResource()
				&& label.equals(otherElement.getLabel())
				&& (role == null ? otherElement.getRole() == null : role
						.equals(otherElement.getRole()))
				&& (title == null ? otherElement.getTitle() == null : title
						.equals(otherElement.getTitle()))
				&& (id == null ? otherElement.getId() == null : id
						.equals(otherElement.getId()))
				&& (extendedLinkRole == null ? otherElement
						.getExtendedLinkRole() == null : extendedLinkRole
						.equals(otherElement.getExtendedLinkRole()))
				&& (linkbaseSource == null ? otherElement.getLinkbaseSource() == null
						: linkbaseSource.equals(otherElement
								.getLinkbaseSource()));
	}

	/**
	 * @return Returns a hash code for this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + label.hashCode();
		hash = hash * 31 + (role != null ? role.hashCode() : 0);
		hash = hash * 31 + (title != null ? title.hashCode() : 0);
		hash = hash * 31 + (id != null ? id.hashCode() : 0);
		hash = hash * 31
				+ (extendedLinkRole != null ? extendedLinkRole.hashCode() : 0);
		return hash;
	}

	/**
	 * @return xlink:label attribute of this extended link element.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return xlink:role attribute of this extended link element.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @return xlink:title attribute of this extended link element.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 *            xlink:role attribute of this extended link element.
	 */
	public void setRole(String string) {
		role = string;
	}

	/**
	 * @param string
	 *            xlink:title attribute of this extended link element.
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * 
	 * @return true if this element is a locator, false otherwise.
	 */
	public abstract boolean isLocator();

	/**
	 * 
	 * @return true if this element is a resource, false otherwise.
	 */
	public abstract boolean isResource();

	/**
	 * @return Extended link role in which this element appears.
	 */
	public String getExtendedLinkRole() {
		return extendedLinkRole;
	}

	/**
	 * @param string
	 *            Extended link role in which this element appears.
	 */
	public void setExtendedLinkRole(String string) {
		extendedLinkRole = string;
	}

	/**
	 * @return The ID of this element.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The ID of this element.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the linkbaseSource.
	 */
	public String getLinkbaseSource() {
		return linkbaseSource;
	}
}
