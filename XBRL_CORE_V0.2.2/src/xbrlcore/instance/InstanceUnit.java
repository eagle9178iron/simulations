package xbrlcore.instance;

import java.io.Serializable;

/**
 * This class represents a unit of an instance file as it is described in the
 * XBRL 2.1 Specification which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class InstanceUnit implements Serializable {

	static final long serialVersionUID = 7384917324858720165L;

	private String id;

	private String namespaceURI;

	private String value;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the unit.
	 */
	public InstanceUnit(String id) {
		this.id = id;
	}

	/**
	 * Determines whether two units are the same. They are the same if and only
	 * if <br/>- they have the same id <br/>- they have the same value <br/>-
	 * they share the same namespace <br/>
	 * 
	 * @param obj
	 *            InstanceUnit object which is compared to this object.
	 * @return true if both objects are equal, otherwise false.
	 * 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof InstanceUnit))
			return false;
		InstanceUnit otherUnit = (InstanceUnit) obj;
		return id == otherUnit.getId()
				&& (namespaceURI == null ? otherUnit.getNamespaceURI() == null
						: namespaceURI.equals(otherUnit.getNamespaceURI()))
				&& (value == null ? otherUnit.getValue() == null : value
						.equals(otherUnit.getValue()));
	}

	/**
	 * @return Returns a hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + id.hashCode();
		hash = hash * 31 + (namespaceURI != null ? namespaceURI.hashCode() : 0);
		hash = hash * 31 + (value != null ? value.hashCode() : 0);
		return hash;
	}

	/**
	 * @return ID of the unit.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param string
	 *            ID of the unit.
	 */
	public void setId(String string) {
		id = string;
	}

	/**
	 * @return URI of the namespace.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * @return Value of the unit.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 *            URI of the namespace.
	 */
	public void setNamespaceURI(String string) {
		namespaceURI = string;
	}

	/**
	 * @param string
	 *            Value of the unit.
	 */
	public void setValue(String string) {
		value = string;
	}
}
