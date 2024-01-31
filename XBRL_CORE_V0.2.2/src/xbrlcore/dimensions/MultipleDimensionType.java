package xbrlcore.dimensions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import xbrlcore.taxonomy.Concept;

/**
 * This class represents a model of multiple dimension/domain member
 * combinations.<br/><br/> Unlike a Hypercube, which represents one or
 * multiple Dimensions with a set of domain members, this class represents one
 * or multiple dimensions with <i>only one specific</i> domain member.<br/><br/>
 * 
 * @author Daniel Hamm
 * 
 */
public class MultipleDimensionType implements Serializable, Cloneable {

	static final long serialVersionUID = -182614528658642605L;

	private SingleDimensionType presentDimensionDomain;

	private Set predecessorDimensionDomainSet;

	/**
	 * Constructor.
	 * 
	 * @param dimensionElement
	 *            Concept object representing the current dimension.
	 * @param domainElement
	 *            Concept object representing the current domain member.
	 */
	public MultipleDimensionType(Concept dimensionElement, Concept domainElement) {
		if (domainElement != null) {
			presentDimensionDomain = new SingleDimensionType(dimensionElement,
					domainElement);
		} else {
			/* TODO: This is not implemented correctly! */
			presentDimensionDomain = new SingleDimensionType(dimensionElement,
					new Element("a"));
		}
		predecessorDimensionDomainSet = new HashSet();
	}

	/**
	 * Constructor.
	 * 
	 * @param sdt
	 *            SingleDimensionType representing the current (or "first")
	 *            dimension and the current domain member.
	 */
	public MultipleDimensionType(SingleDimensionType sdt) {
		presentDimensionDomain = sdt;
		predecessorDimensionDomainSet = new HashSet();
	}

	/**
	 * TODO: Bad practice: The method does not invoke super.clone(). This method
	 * creates a clone of the current object.
	 * 
	 * @return A clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException {
		MultipleDimensionType newMDT = new MultipleDimensionType(
				(SingleDimensionType) presentDimensionDomain.clone());
		Iterator predecessorDimensionDomainListIterator = predecessorDimensionDomainSet
				.iterator();
		while (predecessorDimensionDomainListIterator.hasNext()) {
			SingleDimensionType currSDT = (SingleDimensionType) predecessorDimensionDomainListIterator
					.next();
			newMDT.addPredecessorDimensionDomain((SingleDimensionType) currSDT
					.clone());
		}
		return newMDT;
	}

	/**
	 * Adds all dimensions/domain member of a MultipleDimensionType object
	 * (including current and previous ones) to the previous dimensions/domain
	 * member of this MultipleDimensionType.<br/><br/> Example:<br/> This
	 * object contains of Dimension d1 with domain member m1 and Dimension d2
	 * with domain member m2: d1m1, d2m2. If newMDT consists of Dimension d3
	 * with domain member m3 and Dimension d4 with domain member m4, after
	 * invoking this method this object contains of d1m1, d2m2, d3m3, d4m4
	 * whereas d1m1 still is the "current" dimenson / domain member combination.
	 * 
	 * @param newMDT
	 *            The MultipleDimensionType object whose elements shall be added
	 *            to this object.
	 */
	public void addPredecessorDimensionDomain(MultipleDimensionType newMDT) {
		predecessorDimensionDomainSet.add(newMDT.getPresentDimensionDomain());
		predecessorDimensionDomainSet.addAll((newMDT
				.getPredecessorDimensionDomainSet()));
	}

	/**
	 * Adds the dimension/domain member of a SingleDimensionType object to the
	 * previous dimensions/domain member of this MultipleDimensionType.
	 * 
	 * @param newSDT
	 *            The SingleDimensionType object whose elements shall be added
	 *            to this object.
	 */
	public void addPredecessorDimensionDomain(SingleDimensionType newSDT) {
		predecessorDimensionDomainSet.add(newSDT);
	}

	/**
	 * Changes the current dimension/domain to the two given concepts. The
	 * former dimension/domain are added to the previous dimension/domain
	 * combinations.
	 * 
	 * @param sdt
	 *            The object representing the new dimension / domain member
	 *            combination.
	 */
	/* TODO: Unit Test Missing */
	public void shuffleDimensionDomain(SingleDimensionType sdt) {
		addPredecessorDimensionDomain(presentDimensionDomain);
		presentDimensionDomain = sdt;
	}

	/**
	 * This method takes a dimension/domain member combination from the previous
	 * dimension/domain member combinations and sets it to the current
	 * dimension/domain member.
	 * 
	 * @param dimConcept
	 *            The dimension which shall be activated, that is which shall be
	 *            set to the current dimension/domain.
	 * @return True if the "activation" succeeded (that is, dimConcept could be
	 *         found in the previous dimension/domain combinations), false
	 *         otherwise.
	 */
	/* TODO: Unit Test Missing */
	public boolean activateDimension(Concept dimConcept)
			throws CloneNotSupportedException {
		SingleDimensionType tmpSdt = getSingleDimensionType(dimConcept);
		if (tmpSdt != null) {
			predecessorDimensionDomainSet.remove(tmpSdt);
			presentDimensionDomain = tmpSdt;
			return true;
		}
		return false;
	}

	/**
	 * This method overrides the domain of an already existing dimension. If the
	 * dimension is not part of this multiple dimension type, nothing happens.
	 * 
	 * @param sdt
	 */
	public void overrideDimensionDomain(SingleDimensionType sdt) {
		if (presentDimensionDomain.getDimensionConcept().equals(
				sdt.getDimensionConcept())) {
			presentDimensionDomain = sdt;
		} else {
			SingleDimensionType oldSdt = getSingleDimensionType(sdt
					.getDimensionConcept());
			if (oldSdt != null) {
				predecessorDimensionDomainSet.remove(oldSdt);
				predecessorDimensionDomainSet.add(sdt);
			}
		}
	}

	/**
	 * This method checks whether a specific dimension element is part of this
	 * object.
	 * 
	 * @param dimensionElement
	 *            Concept which is checked whether it is part of this object.
	 * @return True if the concept is part of this object, otherwise false.
	 */
	public boolean containsDimension(Concept dimensionElement) {
		return getSingleDimensionType(dimensionElement) != null;
	}

	/**
	 * This method returns the SingleDimensionType which fits to the given
	 * dimension element.
	 * 
	 * @param dimensionElement
	 *            Dimension of the desired SingleDimensionType.
	 * @return According SingleDimensionType to the given dimension element.
	 *         NULL if no according SingleDimensionType is available.
	 */
	public SingleDimensionType getSingleDimensionType(Concept dimensionElement) {
		if (presentDimensionDomain.getDimensionConcept().equals(
				dimensionElement)) {
			return presentDimensionDomain;
		}
		Iterator predecessorDimensionDomainIterator = predecessorDimensionDomainSet
				.iterator();
		while (predecessorDimensionDomainIterator.hasNext()) {
			SingleDimensionType currSDT = (SingleDimensionType) predecessorDimensionDomainIterator
					.next();
			if (currSDT.getDimensionConcept().equals(dimensionElement)) {
				return currSDT;
			}
		}
		return null;
	}

	/**
	 * This method checks if two objects of MultipleDimensionType are "equal".
	 * This is true if <br/>- both refer to the same "current" dimension/domain
	 * and<br/>- if also all the predecessor dimension/domain member
	 * combinations are the same (the order of the previous dimension / domain
	 * member combinations is not taken into account).
	 * 
	 * @return True if both objects are equal, false otherwise.
	 * 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MultipleDimensionType))
			return false;
		MultipleDimensionType newType = (MultipleDimensionType) obj;
		return presentDimensionDomain.equals(newType
				.getPresentDimensionDomain())
				&& predecessorDimensionDomainSet.equals(newType
						.getPredecessorDimensionDomainSet());
	}

	/**
	 * @return A hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + presentDimensionDomain.hashCode();
		hash = hash * 31 + predecessorDimensionDomainSet.hashCode();
		return hash;
	}

	/**
	 * 
	 * @return All dimension concepts of this object represented in a
	 *         java.util.List object.
	 */
	/* TODO: Unit Test Missing! */
	public List getAllDimensions() {
		List dimensionList = new ArrayList();
		dimensionList.add(presentDimensionDomain.getDimensionConcept());
		Iterator predecessorDimensionDomainIterator = predecessorDimensionDomainSet
				.iterator();
		while (predecessorDimensionDomainIterator.hasNext()) {
			SingleDimensionType sdt = (SingleDimensionType) predecessorDimensionDomainIterator
					.next();
			dimensionList.add(sdt.getDimensionConcept());
		}
		return dimensionList;
	}

	/**
	 * Returns a domain member concept to a given dimension concept within this
	 * MultipleDimensionType object.
	 * 
	 * @param dimension
	 *            Given dimension concept to which the according domain member
	 *            is searched.
	 * @return Domain member concept to the given dimension concept.
	 */
	public Concept getDomainMemberElement(Concept dimension) {
		if (presentDimensionDomain.getDimensionConcept().equals(dimension)) {
			return presentDimensionDomain.getDomainMemberConcept();
		}
		Iterator predecessorDimensionDomainIterator = predecessorDimensionDomainSet
				.iterator();
		while (predecessorDimensionDomainIterator.hasNext()) {
			SingleDimensionType currSDT = (SingleDimensionType) predecessorDimensionDomainIterator
					.next();
			if (currSDT.getDimensionConcept().equals(dimension)) {
				return currSDT.getDomainMemberConcept();
			}
		}
		return null;
	}

	/**
	 * @return Concept representing the current dimension.
	 */
	public Concept getDimensionElement() {
		return presentDimensionDomain.getDimensionConcept();
	}

	/**
	 * @return Concept representing the current domain member.
	 */
	public Concept getDomainMemberElement() {
		return presentDimensionDomain.getDomainMemberConcept();
	}

	/**
	 * @return All previous dimension/domain member combinations (the key is the
	 *         Concept dimension object, the value is the Concept domain
	 *         object).
	 */
	public Map getPredecessorDimensionDomainMap() {
		Map returnMap = new HashMap();
		Iterator predecessorDimensionDomainIterator = predecessorDimensionDomainSet
				.iterator();
		while (predecessorDimensionDomainIterator.hasNext()) {
			SingleDimensionType currentSDT = (SingleDimensionType) predecessorDimensionDomainIterator
					.next();
			returnMap.put(currentSDT.getDimensionConcept(), currentSDT
					.getDomainMemberConcept());
		}

		return returnMap;
	}

	/**
	 * 
	 * @return All dimension/domain member combinations, that is current
	 *         dimension/current domain and all previous dimension/domain member
	 *         combinations (the key is the Concept dimension object, the value
	 *         is the Concept domain object).
	 */
	public Map getAllDimensionDomainMap() {
		Map allDimensionDomain = new HashMap();
		allDimensionDomain.put(presentDimensionDomain.getDimensionConcept(),
				presentDimensionDomain.getDomainMemberConcept());
		allDimensionDomain.putAll(getPredecessorDimensionDomainMap());
		return allDimensionDomain;
	}

	public List getAllSingleDimensionTypeList() {
		List returnList = new ArrayList();
		returnList.add(presentDimensionDomain);
		returnList.addAll(predecessorDimensionDomainSet);
		return returnList;
	}

	/**
	 * 
	 * @param domainMemberElement
	 */
	public void setDomainMemberElement(Concept domainMemberElement) {
		presentDimensionDomain.setDomainMemberConcept(domainMemberElement);
	}

	/**
	 * @return First dimension / domain combination.
	 */
	public SingleDimensionType getPresentDimensionDomain() {
		return presentDimensionDomain;
	}

	/**
	 * @return All dimension / domain combinations except the first one.
	 */
	public Set getPredecessorDimensionDomainSet() {
		return predecessorDimensionDomainSet;
	}

	/**
	 * @param type
	 *            First dimension / domain combination.
	 */
	public void setPresentDimensionDomain(SingleDimensionType type) {
		presentDimensionDomain = type;
	}

	/**
	 * @param predecessorDimensionDomainSet
	 *            The predecessorDimensionDomainSet to set.
	 */
	public void setPredecessorDimensionDomainSet(
			Set predecessorDimensionDomainSet) {
		this.predecessorDimensionDomainSet = predecessorDimensionDomainSet;
	}
}