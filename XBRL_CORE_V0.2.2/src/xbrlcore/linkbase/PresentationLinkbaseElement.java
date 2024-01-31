package xbrlcore.linkbase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xbrlcore.taxonomy.Concept;
import xbrlcore.xlink.Locator;

/**
 * For each Concept in a taxonomy which appears in the presentation linkbase,
 * there is one corresponding PresentationLinkbaseElement. This class represents
 * a concept within the presentation linkbase and contains additional
 * information as for example all successors. <br/><br/>
 * 
 * @author Daniel Hamm
 * 
 */

/* TODO: This class could be changed to a generic LinkbaseElement */
public class PresentationLinkbaseElement implements Serializable {

	static final long serialVersionUID = -8410690195993834311L;

	private Locator locator;

	private Concept concept;

	private Concept parentElement;

	private int level;

	private int numSuccessorAtDeepestLevel;

	private List successorElements;

	private int positionDeepestLevel;

	private String extendedLinkRole;

	/**
	 * Constructor.
	 * 
	 * @param locator
	 *            XBRL element this PresentationLinkbaseElement refers to.
	 */
	public PresentationLinkbaseElement(Locator locator) {
		this.locator = locator;
		concept = locator.getConcept();
		parentElement = null;
		level = 0;
		numSuccessorAtDeepestLevel = 0;
		successorElements = new ArrayList();
		positionDeepestLevel = -1;
	}

	/**
	 * @return Number of direct successors.
	 */
	public int getNumDirectSuccessor() {
		return successorElements.size();
	}

	/**
	 * @return XBRL element of the parent element.
	 */
	public Concept getParentElement() {
		return parentElement;
	}

	/**
	 * @return XBRL element of the current element.
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param element
	 *            XBRL element of the parent element.
	 */
	public void setParentElement(Concept element) {
		parentElement = element;
	}

	/**
	 * @return List of Concept objects of the successor elements.
	 */
	public List getSuccessorElements() {
		return successorElements;
	}

	/**
	 * @param list
	 *            List of Concept objects of the successor elements.
	 */
	public void setSuccessorElements(List list) {
		successorElements = list;
	}

	/**
	 * @return Level of the current element.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param i
	 *            Level of the current element.
	 */
	public void setLevel(int i) {
		level = i;
	}

	/**
	 * @return Number of successors at the deepest level.
	 */
	public int getNumSuccessorAtDeepestLevel() {
		return numSuccessorAtDeepestLevel;
	}

	/**
	 * @param i
	 *            Number of successors at the deepest level.
	 */
	public void setNumSuccessorAtDeepestLevel(int i) {
		numSuccessorAtDeepestLevel = i;
	}

	/**
	 * @return If this element is at the deepest level, this is its position at
	 *         the deepest level. If it is not at the deepest level, -1 is
	 *         returned.
	 */
	public int getPositionDeepestLevel() {
		return positionDeepestLevel;
	}

	/**
	 * @param i
	 *            This is the position of this element at the deepest level (if
	 *            it is at the deepest level).
	 */
	public void setPositionDeepestLevel(int i) {
		positionDeepestLevel = i;
	}

	/**
	 * @return Returns the extendedLinkRole.
	 */
	public String getExtendedLinkRole() {
		return extendedLinkRole;
	}

	/**
	 * @param extendedLinkRole
	 *            The extendedLinkRole to set.
	 */
	public void setExtendedLinkRole(String extendedLinkRole) {
		this.extendedLinkRole = extendedLinkRole;
	}

	/**
	 * @return Returns the locator.
	 */
	public Locator getLocator() {
		return locator;
	}
}
