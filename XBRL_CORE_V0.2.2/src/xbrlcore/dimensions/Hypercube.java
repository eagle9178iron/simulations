package xbrlcore.dimensions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import xbrlcore.taxonomy.Concept;
import xbrlcore.xlink.Locator;

/**
 * This class represents an Hypercube as it is described by the Dimensions 1.0
 * Specification which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/.<br/><br/> A Hypercube consists of
 * one or multiple dimensions which are represented by
 * xbrlcore.dimensions.Dimension objects. It describes which dimensions and
 * which domain a primary item can be reported for.<br/><br/> A Hypercube
 * always consists of an XBRL concept representing it and a set of Dimensions.<br/><br/>
 * 
 * @author Daniel Hamm
 */
public class Hypercube implements Serializable {

	static final long serialVersionUID = 1068588695733245378L;

	private Concept concept; /* the concept the hypercube refers to */

	private String extendedLinkRole;

	private Set dimensionSet; /* Dimension objects */

	/**
	 * This method tests for "equality" between two Hypercube objects. They are
	 * equal if:<br/> - the concepts representing both Hypercubes are equal<br/> -
	 * the set of Dimensions of both Hypercubes are equal<br/> - both
	 * Hypercubes are defined in the same extended link role<br/>
	 * 
	 * @return True if both Hypercube objects are equal, false otherwise.
	 * 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Hypercube))
			return false;
		Hypercube otherCube = (Hypercube) obj;
		return concept.equals(otherCube.getConcept())
				&& dimensionSet.equals(otherCube.getDimensionSet())
				&& (extendedLinkRole == null ? otherCube.getExtendedLinkRole() == null
						: extendedLinkRole.equals(otherCube
								.getExtendedLinkRole()));
	}

	/**
	 * @return A hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + concept.hashCode();
		hash = hash * 31 + dimensionSet.hashCode();
		hash = hash * 31
				+ (extendedLinkRole != null ? extendedLinkRole.hashCode() : 0);
		return hash;
	}

	/**
	 * 
	 * @param concept
	 *            The Concept object which represents the hypercube.
	 */
	public Hypercube(Concept concept) {
		this.concept = concept;
		dimensionSet = new HashSet();
		extendedLinkRole = null;
	}

	/**
	 * Adds a dimension to the cube.
	 * 
	 * @param dimension
	 *            The dimension which shall be added.
	 */
	public void addDimension(Dimension dimension) {
		dimensionSet.add(dimension);
	}

	/**
	 * Gets the set of domain members to a dimension contained in the cube.
	 * 
	 * @param dimension
	 *            The dimension for which the domain shall be returned.
	 * @return A list with xbrlcore.xlink.ExtendedLinkElement objects
	 *         representing the domain of the dimension.
	 */
	public Set getDimensionDomain(Concept dimension) {
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDimension = (Dimension) dimensionIterator.next();
			if (currDimension.getConcept().equals(dimension)) {
				return currDimension.getDomainMemberSet();
			}
		}
		return null;
	}

	/**
	 * Specifies whether a certain dimension is part of that cube.
	 * 
	 * @param dimension
	 *            The Concept object representing the according dimension.
	 * @return True if the dimension is part of that cube, otherwise false.
	 */
	public boolean containsDimension(Concept dimension) {
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDimension = (Dimension) dimensionIterator.next();
			if (currDimension.getConcept().equals(dimension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Specifies whether a certain dimension with a certain domain member is
	 * part of that cube. This method does not take into account whether the
	 * given domain member has an xbrldt:usable attribute or not (use
	 * containsUsableDimensionDomain instead).
	 * 
	 * @param dimension
	 *            The Concept object representing the according dimension.
	 * @param domainMember
	 *            The Concept object representing the according domain.
	 * @return True if the dimension/domain combination is part of that cube,
	 *         otherwise false. If the dimension is a typed dimension, the
	 *         method always returns true.
	 */
	public boolean containsDimensionDomain(Concept dimension,
			Concept domainMember) {
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDimension = (Dimension) dimensionIterator.next();
			if (currDimension.getConcept().equals(dimension)) {
				/*
				 * TODO: a typed dimension always returns "true", so ALL
				 * elements can be domain member of a typed dimension. Later it
				 * must be checked whether the element is compliant with the
				 * according schema element.
				 */
				return (currDimension.isTyped() ? true : currDimension
						.containsDomainMember(domainMember, false));
			}
		}
		return false;
	}

	/**
	 * Specifies whether a certain dimension with a certain domain member is
	 * part of that cube. This method takes into account whether the given
	 * domain member has an xbrldt:usable attribute or not. If the usable
	 * attribute is set to false, it returns false.
	 * 
	 * @param dimension
	 *            The Concept object representing the according dimension.
	 * @param domainMember
	 *            The Concept object representing the according domain.
	 * @return True if the dimension/domain combination is part of that cube,
	 *         otherwise false. If the dimension is a typed dimension, the
	 *         method always returns true.
	 */
	public boolean containsUsableDimensionDomain(Concept dimension,
			Concept domainMember) {
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDimension = (Dimension) dimensionIterator.next();
			if (currDimension.getConcept().equals(dimension)) {
				/*
				 * TODO: a typed dimension always returns "true", so ALL
				 * elements can be domain member of a typed dimension. Later it
				 * must be checked whether the element is compliant with the
				 * according schema element.
				 */
				return (currDimension.isTyped() ? true : currDimension
						.containsDomainMember(domainMember, true));
			}
		}
		return false;
	}

	/**
	 * Adds all the dimensions / domain member of a second hypercube to this
	 * hypercube. This is done in the following way: If the second hypercube has
	 * a dimension which is not contained in this cube, the dimension and all
	 * its domain members are added to this cube. If the second hypercube has a
	 * dimension which is already contained in this cube, all domain members of
	 * this dimension of the second cube which are not part of the same
	 * dimension of this cube are added to this cube.
	 * 
	 * @param newCube
	 *            The second cube whose dimension/domain members are added to
	 *            this cube.
	 * @throws CloneNotSupportedException
	 */
	public void addHypercube(Hypercube newCube)
			throws CloneNotSupportedException {
		/* go through all the dimensions of newCube */
		Set newDimensionSet = newCube.getDimensionSet();
		Iterator newDimensionSetIterator = newDimensionSet.iterator();
		while (newDimensionSetIterator.hasNext()) {
			Dimension newCubeDimension = (Dimension) newDimensionSetIterator
					.next();
			/* if it is contained in this cube, add only the domain members */
			if (containsDimension(newCubeDimension.getConcept())) {
				Dimension thisDimension = getDimension(newCubeDimension
						.getConcept());
				thisDimension
						.addDomainMemberSet((Set) ((HashSet) newCubeDimension
								.getDomainMemberSet()).clone());
			}
			/*
			 * if it is not contained in this cube, add it including all the
			 * domain members
			 */
			else {
				dimensionSet.add(newCubeDimension.clone());
			}
		}
	}

	/**
	 * 
	 * @return String object describing this hypercube.
	 */
	public String toString() {
		String id = (concept != null ? concept.getId() : "anonymous");
		String str = "Hypercube " + id + "\n";
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDim = (Dimension) dimensionIterator.next();
			str += "\tDimension: " + currDim.getConcept().getId() + "\n";
			Set domainMemberSet = currDim.getDomainMemberSet();
			Iterator domainMemberIterator = domainMemberSet.iterator();
			while (domainMemberIterator.hasNext()) {
				Concept currCon = ((Locator) domainMemberIterator.next())
						.getConcept();
				str += "\t\tDomain Member: " + currCon.getId() + "\n";
			}
		}
		str += "Hypercube " + id + " finished\n";
		return str;
	}

	/**
	 * Checks whether this hypercube contains specific dimension/domain member
	 * combinations.
	 * 
	 * @param mdt
	 *            Object describing the specific dimension/domain member
	 *            combinations for which this hypercube is checkted.
	 * @return True if and only if this hypercube contains all the
	 *         dimension/domain member combinations described in the given
	 *         MultipleDimensionType object. The hypercube also must not contain
	 *         additinal dimension/domain member combinations than those which
	 *         are contained in the given MultipleDimensinType object. Otherwise
	 *         false is returned.
	 */
	public boolean hasDimensionCombination(MultipleDimensionType mdt) {
		Map dimensionDomainMap = mdt.getAllDimensionDomainMap();
		if (dimensionDomainMap.size() != dimensionSet.size()) {
			return false;
		}
		Set dimensionDomainEntrySet = dimensionDomainMap.entrySet();
		Iterator dimensionDomainIterator = dimensionDomainEntrySet.iterator();
		while (dimensionDomainIterator.hasNext()) {
			Map.Entry currEntry = (Map.Entry) dimensionDomainIterator.next();
			Concept currDimConcept = (Concept) currEntry.getKey();
			Concept currDomConcept = (Concept) currEntry.getValue();
			if (!containsUsableDimensionDomain(currDimConcept, currDomConcept)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a certain dimension from the cube.
	 * 
	 * @param dimensionElement
	 *            Concept object representing the dimension.
	 * @return Dimension object matching the given Concept object.
	 */
	public Dimension getDimension(Concept dimensionElement) {
		Iterator dimensionIterator = dimensionSet.iterator();
		while (dimensionIterator.hasNext()) {
			Dimension currDimension = (Dimension) dimensionIterator.next();
			if (currDimension.getConcept().equals(dimensionElement)) {
				return currDimension;
			}
		}
		return null;
	}

	/**
	 * @return Extended link role of this hypercube.
	 */
	public String getExtendedLinkRole() {
		return extendedLinkRole;
	}

	/**
	 * @param string
	 *            Extended link role of this hypercube.
	 */
	public void setExtendedLinkRole(String string) {
		extendedLinkRole = string;
	}

	/**
	 * @return Concept object representing this hypercube.
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @return Set with Dimension objects representing all the
	 *         set of Dimensions this hypercube consists of.
	 */
	public Set getDimensionSet() {
		return dimensionSet;
	}
}
