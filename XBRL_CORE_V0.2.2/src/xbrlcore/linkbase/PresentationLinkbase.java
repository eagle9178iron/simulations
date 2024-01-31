package xbrlcore.linkbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.exception.XBRLCoreException;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;

/**
 * This class represents a presentation linkbase of a DTS. The purpose of this
 * linkbase is to structure the elements in a hierarchical order. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class PresentationLinkbase extends Linkbase {

	static final long serialVersionUID = 2459167242500244987L;

	/*
	 * maps each extended link role to the corresponding
	 * presentationLinkbaseElementList
	 */
	private Map linkRoleToElementList;

	private int positionDeepestLevel;

	/**
	 * Constructor.
	 * 
	 * @param dts
	 *            Taxonomy which the presentation linkbase refers to.
	 */
	public PresentationLinkbase(DiscoverableTaxonomySet dts) {
		super(dts);

		linkRoleToElementList = new HashMap();
	}

	/**
	 * This method builds the presentation linkbase. More detailed, the
	 * hierarchical structure of the elements is built. This structure is built
	 * in various PresentationLinkbaseElement objects.
	 * 
	 */
	public void buildPresentationLinkbase() {

		/* build the presentationLinkbaseElementList for each extended link role */
		Set extendedLinkRoles = getExtendedLinkRoles();
		Iterator extendedLinkRolesIterator = extendedLinkRoles.iterator();
		while (extendedLinkRolesIterator.hasNext()) {
			String currExtendedLinkRole = (String) extendedLinkRolesIterator
					.next();
			List presentationLinkbaseElementList = new ArrayList();

			List extendedLinkElementList = getExtendedLinkElementsFromBaseSet(currExtendedLinkRole);

			Iterator extendedLinkElementIterator = extendedLinkElementList
					.iterator();
			while (extendedLinkElementIterator.hasNext()) {
				ExtendedLinkElement currExtendedLinkElement = (ExtendedLinkElement) extendedLinkElementIterator
						.next();
				Concept currXBRLElement = ((Locator) currExtendedLinkElement)
						.getConcept();

				PresentationLinkbaseElement currPresentationLinkbaseElement = new PresentationLinkbaseElement(
						(Locator) currExtendedLinkElement);
				currPresentationLinkbaseElement
						.setExtendedLinkRole(currExtendedLinkRole);

				/* set successor elements */
				List xLinkElementsSuccessor = getTargetExtendedLinkElements(
						currXBRLElement, currExtendedLinkRole);
				List xbrlElementSuccessor = new ArrayList();
				Iterator xLinkElementsSuccessorIterator = xLinkElementsSuccessor
						.iterator();
				while (xLinkElementsSuccessorIterator.hasNext()) {
					ExtendedLinkElement currXLinkElement = (ExtendedLinkElement) xLinkElementsSuccessorIterator
							.next();
					if (currXLinkElement.isLocator()) {
						xbrlElementSuccessor.add(((Locator) currXLinkElement)
								.getConcept());
					}
				}
				currPresentationLinkbaseElement
						.setSuccessorElements(xbrlElementSuccessor);

				/* set parent element (there can be only one) */
				List xLinkList = getSourceExtendedLinkElements(currXBRLElement,
						currExtendedLinkRole);
				if (xLinkList.size() > 0) {
					ExtendedLinkElement xLinkElementParent = (ExtendedLinkElement) xLinkList
							.get(0);
					if (xLinkElementParent != null
							&& xLinkElementParent.isLocator()) {
						currPresentationLinkbaseElement
								.setParentElement(((Locator) xLinkElementParent)
										.getConcept());
					}
				}

				/* set level */
				int level = determineLevel(0, currXBRLElement,
						currExtendedLinkRole);
				currPresentationLinkbaseElement.setLevel(level);

				/* set number of successor at deepest level */
				if (xbrlElementSuccessor.size() == 0) {
					currPresentationLinkbaseElement
							.setNumSuccessorAtDeepestLevel(0);
				} else {
					int numberOfSuccessorAtDeepestLevel = determineNumberOfSuccessorAtDeepestLevel(
							0, currXBRLElement, currExtendedLinkRole);
					currPresentationLinkbaseElement
							.setNumSuccessorAtDeepestLevel(numberOfSuccessorAtDeepestLevel);
				}
				presentationLinkbaseElementList
						.add(currPresentationLinkbaseElement);

			}
			linkRoleToElementList.put(currExtendedLinkRole,
					presentationLinkbaseElementList);
		}
	}

	/**
	 * Returns a list of PresentationLinkbaseElement objects according to the
	 * presentation linkbase. The list already is in correct order.
	 * 
	 * @param taxonomyName
	 *            The name of the taxonomy of which the presentation shall be
	 *            obtained (if NULL, the whole DTS is taken).
	 * @param extendedLinkRole
	 *            Extended link role from which the presentation shall be
	 *            obtained (if NULL, the default link role is taken).
	 * @return List of xbrlcore.linkbase.PresentationLinkbaseElement objects.
	 */
	public List getPresentationLinkbaseElementListForTaxonomy(
			String taxonomyName, String extendedLinkRole)
			throws XBRLCoreException {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List resultList = new ArrayList();
		List finalResultList = new ArrayList();

		positionDeepestLevel = 0;

		List rootElementList = getPresentationLinkbaseElementRoot(extendedLinkRole);

		for (int i = 0; i < rootElementList.size(); i++) {
			PresentationLinkbaseElement currRootElement = (PresentationLinkbaseElement) rootElementList
					.get(i);

			List tmpResultList = new ArrayList();
			tmpResultList = collectPresentationLinkbaseElementList(
					currRootElement, extendedLinkRole, tmpResultList);
			resultList.addAll(tmpResultList);
		}

		for (int i = 0; i < resultList.size(); i++) {
			PresentationLinkbaseElement currElement = (PresentationLinkbaseElement) resultList
					.get(i);
			if ((taxonomyName != null && currElement.getConcept()
					.getTaxonomySchemaName().equals(taxonomyName))
					|| taxonomyName == null) {
				finalResultList.add(currElement);
			}
		}

		return finalResultList;
	}

	/**
	 * Returns a list of PresentationLinkbaseElement objects which form the
	 * hierarchical presentation tree below a given concept.
	 * 
	 * @param concept
	 *            Root of the hierarchical presentation tree which is returned.
	 * @param extendedLinkRole
	 *            Extended link role of the presentation linkbase.
	 * @return Hierarchical presentation tree with concept being the root.
	 */
	public List getPresentationLinkbaseElementListForConcept(Concept concept,
			String extendedLinkRole) {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}

		List resultList = new ArrayList();
		positionDeepestLevel = 0;

		PresentationLinkbaseElement rootElement = getPresentationLinkbaseElement(
				concept, extendedLinkRole);
		if (rootElement == null) {
			return null;
		}
		resultList = collectPresentationLinkbaseElementList(rootElement,
				extendedLinkRole, resultList);
		return resultList;
	}

	/**
	 * Returns a list of PresentationLinkbaseElement objects according to the
	 * default link role (http://www.xbrl.org/2003/role/link) of the
	 * presentation linkbase which belong to a certain taxonomy within the DTS.
	 * The list already is in correct order.
	 * 
	 * @param taxonomyName
	 *            Name of the taxonomy within the DTS the elements have to
	 *            belong to.
	 * @return List of xbrlcore.linkbase.PresentationLinkbaseElement objects.
	 */
	public List getPresentationLinkbaseElementList(String taxonomyName)
			throws XBRLCoreException {
		return getPresentationLinkbaseElementListForTaxonomy(taxonomyName,
				GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Returns a certain PresentationLinkbaseElement object.
	 * 
	 * @param tmpElement
	 *            XBRL element the PresentationLinkbaseElement object refers to.
	 * @param extendedLinkRole
	 *            Extended link role from which the PresentationLinkbaseElement
	 *            shall be obtained (if NULL, the default link role is taken).
	 * @return The xbrlcore.linkbase.PresentationLinkbaseElement object which
	 *         matches to the given parameters.
	 */
	public PresentationLinkbaseElement getPresentationLinkbaseElement(
			Concept tmpElement, String extendedLinkRole) {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List presentationLinkbaseElementList = (List) linkRoleToElementList
				.get(extendedLinkRole);
		if (presentationLinkbaseElementList != null) {
			Iterator presentationLinkbaseElementListIterator = presentationLinkbaseElementList
					.iterator();
			while (presentationLinkbaseElementListIterator.hasNext()) {
				PresentationLinkbaseElement currElement = (PresentationLinkbaseElement) presentationLinkbaseElementListIterator
						.next();
				if (currElement.getConcept().getId().equals(tmpElement.getId())) {
					return currElement;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a certain PresentationLinkbaseElement object from the default
	 * link role (http://www.xbrl.org/2003/role/link).
	 * 
	 * @param tmpElement
	 *            XBRL element the PresentationLinkbaseElement object refers to.
	 * @return The xbrlcore.linkbase.PresentationLinkbaseElement object which
	 *         matches to the given parameters.
	 */
	public PresentationLinkbaseElement getPresentationLinkbaseElement(
			Concept tmpElement) {
		return getPresentationLinkbaseElement(tmpElement,
				GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Helping method to build the hierarchical structure.
	 */
	private List collectPresentationLinkbaseElementList(
			PresentationLinkbaseElement currElement, String extendedLinkRole,
			List currList) {
		currList.add(currList.size(), currElement);

		if (currElement.getNumDirectSuccessor() == 0
				|| !currElement.getConcept().isAbstract()) {
			currElement.setPositionDeepestLevel(positionDeepestLevel++);
		}

		List successorElements = currElement.getSuccessorElements();
		Iterator successorElementsIterator = successorElements.iterator();
		while (successorElementsIterator.hasNext()) {
			Concept currXBRLElement = (Concept) successorElementsIterator
					.next();
			PresentationLinkbaseElement nextElement = getPresentationLinkbaseElement(
					currXBRLElement, extendedLinkRole);
			collectPresentationLinkbaseElementList(nextElement,
					extendedLinkRole, currList);
		}
		return currList;
	}

	/**
	 * Helping method to get the level of a certain element within the
	 * presentation linkbase.
	 */
	private int determineLevel(int i, Concept currXBRLElement,
			String extendedLinkRole) {
		i++;
		List xLinkList = getSourceExtendedLinkElements(currXBRLElement,
				extendedLinkRole);
		if (xLinkList.size() > 0) {
			ExtendedLinkElement xLinkElementParent = (ExtendedLinkElement) xLinkList
					.get(0);
			if (xLinkElementParent != null && xLinkElementParent.isLocator()) {
				i = determineLevel(i, ((Locator) xLinkElementParent)
						.getConcept(), extendedLinkRole);
			}
		}
		return i;
	}

	/**
	 * Helping method to get the number of successors at the deepest level (this
	 * is needed later to render the template).
	 */
	private int determineNumberOfSuccessorAtDeepestLevel(int i,
			Concept currXBRLElement, String extendedLinkRole) {
		List currXLinkElement = getTargetExtendedLinkElements(currXBRLElement,
				extendedLinkRole);
		/*
		 * if element is not the source of any other element, it is at the
		 * deepest level
		 */
		if (currXLinkElement.size() == 0) {
			i++;
		} else {
			Iterator currXLinkElementIterator = currXLinkElement.iterator();
			while (currXLinkElementIterator.hasNext()) {
				ExtendedLinkElement xLinkElementChild = (ExtendedLinkElement) currXLinkElementIterator
						.next();
				if (xLinkElementChild.isLocator()) {
					i = determineNumberOfSuccessorAtDeepestLevel(i,
							((Locator) xLinkElementChild).getConcept(),
							extendedLinkRole);
				}
			}
		}
		return i;
	}

	/**
	 * Returns root elements of the presentation linkbase.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the presentation linkbase.
	 * @return List A list with all the elements which are root elements in the
	 *         presentation linkbase (that means, which have no parent element)
	 *         in a specific extended link role.
	 */
	public List getPresentationLinkbaseElementRoot(String extendedLinkRole) {

		List resultList = new ArrayList();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List presentationLinkbaseElementList = (List) linkRoleToElementList
				.get(extendedLinkRole);
		if (presentationLinkbaseElementList != null) {
			Iterator presentationLinkbaseElementListIterator = presentationLinkbaseElementList
					.iterator();
			while (presentationLinkbaseElementListIterator.hasNext()) {
				PresentationLinkbaseElement currElement = (PresentationLinkbaseElement) presentationLinkbaseElementListIterator
						.next();
				if (currElement.getParentElement() == null) {
					resultList.add(currElement);
				}
			}
		}
		return resultList;
	}
}
