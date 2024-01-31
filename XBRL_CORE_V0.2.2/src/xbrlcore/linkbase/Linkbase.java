package xbrlcore.linkbase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.exception.XBRLException;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import xbrlcore.xlink.Resource;

/**
 * This class is a superclass for all classes implementing linkbases. It
 * provides basic linkbase methods each linkbase has to implement. It has to
 * manage a list of xbrlcore.xlink.ExtendedLinkElement and xbrlcore.xlink.Arc
 * objects. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class Linkbase implements Serializable {

	static final long serialVersionUID = 6720994841702824990L;

	private Set extendedLinkElements; /* list of XLinkElement objects */

	/* maps extended link roles to list with arcs for this link role */
	private Map arcsBaseSet;

	private Map overridenArcs;

	private Map prohibitingArcs;

	private Map prohibitedArcs;

	private Set extendedLinkRoles; /*
									 * different extended link roles of this
									 * linkbase
									 */

	private DiscoverableTaxonomySet dts; /*
											 * the taxonomy this linkbase
											 * belongs to
											 */

	private static Logger logger = Logger.getLogger(Linkbase.class);

	/**
	 * Constructor.
	 * 
	 * @param dts
	 *            Discoverable Taxonomy Set (DTS) this linkbase refers to.
	 */
	public Linkbase(DiscoverableTaxonomySet dts) {
		this.dts = dts;
		extendedLinkElements = new HashSet();
		arcsBaseSet = new HashMap();
		overridenArcs = new HashMap();
		prohibitingArcs = new HashMap();
		prohibitedArcs = new HashMap();
		extendedLinkRoles = new HashSet();
	}

	/**
	 * Returns a base set of arcs (list of Arc objects) to a specific extended
	 * link role.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the base set.
	 * @return Base set of the specified extended link role.
	 */
	public List getArcBaseSet(String extendedLinkRole) {
		return (List) arcsBaseSet.get(extendedLinkRole);
	}

	/**
	 * Returns a base set of arcs (list of Arc objects) from the default link
	 * role (http://www.xbrl.org/2003/role/link).
	 * 
	 * @return Base set of arcs from the default link role.
	 */
	public List getArcBaseSet() {
		return getArcBaseSet(GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Returns a base set of arcs (list of Arc objects) with a given arcrole in
	 * a given exended link role.
	 * 
	 * @param arcrole
	 *            Role of the arc.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase. If NULL, the default link
	 *            role is taken.
	 * @return List of arc objects that match to the given parameters.
	 */
	public List getArcBaseSet(String arcrole, String extendedLinkRole) {
		List resultList = new ArrayList();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List tmpArcBaseSet = getArcBaseSet(extendedLinkRole);
		if (tmpArcBaseSet != null) {
			Iterator xArcsIterator = tmpArcBaseSet.iterator();
			while (xArcsIterator.hasNext()) {
				Arc xArc = (Arc) xArcsIterator.next();
				if (xArc.getArcrole().equals(arcrole)) {
					resultList.add(xArc);
				}
			}
		}
		return resultList;
	}

	/**
	 * Returns a base set of arcs (list of Arc objects) with a given list of arc
	 * roles (list of String objects) in a given extended link role.
	 * 
	 * @param arcrole
	 *            List of arc roles (String objects).
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List of arc objects that match to the given parameters.
	 */
	public List getArcBaseSet(List arcrole, String extendedLinkRole) {
		List resultList = new ArrayList();
		for (int i = 0; i < arcrole.size(); i++) {
			String currentArcrole = (String) arcrole.get(i);
			resultList.addAll(getArcBaseSet(currentArcrole, extendedLinkRole));
		}
		return resultList;
	}

	/**
	 * Returns all overriden arcs (list of Arc objects) of a specific extended
	 * link role.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the base set.
	 * @return List of overriden arcs in the specified extended link role.
	 */
	public List getOverridenArcs(String extendedLinkRole) {
		return (List) overridenArcs.get(extendedLinkRole);
	}

	/**
	 * Returns all overriden arcs (list of Arc objects) of the default link
	 * role.
	 * 
	 * @return List of overriden arcs in the default link role.
	 */
	public List getOverridenArcs() {
		return getOverridenArcs(GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Returns all prohibiting arcs (list of Arc objects) of a specific extended
	 * link role.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the prohibiting arcs.
	 * @return List of prohibiting arcs in the specified extended link role.
	 */
	public List getProhibitingArcs(String extendedLinkRole) {
		return (List) prohibitingArcs.get(extendedLinkRole);
	}

	/**
	 * Returns all prohibiting arcs (list of Arc objects) of the default link
	 * role.
	 * 
	 * @return List of prohibiting arcs in the default link role.
	 */
	public List getProhibitingArcs() {
		return getProhibitingArcs(GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Returns all prohibited arcs (list of Arc objects) of a specific extended
	 * link role.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the prohibited arcs.
	 * @return List of prohibited arcs in the specified extended link role.
	 */
	public List getProhibitedArcs(String extendedLinkRole) {
		return (List) prohibitedArcs.get(extendedLinkRole);
	}

	/**
	 * Returns all prohibited arcs (list of Arc objects) of the default link
	 * role.
	 * 
	 * @return List of prohibited arcs in the default link role.
	 */
	public List getProhibitedArcs() {
		return getProhibitedArcs(GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE);
	}

	/**
	 * Adds a new ExtendedLinkElement (either a resource or a locator) object to
	 * the list of objects.
	 * 
	 * @param element
	 *            The XLinkElement object that shall be added.
	 */
	public void addExtendedLinkElement(ExtendedLinkElement element)
			throws TaxonomyCreationException {
		extendedLinkElements.add(element);
	}

	/**
	 * Adds a new Arc to the linkbase. The order of the arc is taken into
	 * account, so it is placed at the correct position within its base set. It
	 * is also determined whether this arc is a prohibiting or overriding arc
	 * for another one.
	 * 
	 * @param newArc
	 *            The Arc object that shall be added.
	 */
	public void addArc(Arc newArc) {
		/*
		 * Check whether this is a prohibiting arc
		 */
		if (newArc.getUseAttribute().equals("prohibited")) {
			addProhibitedArc(newArc);
			return;
		}

		List tmpArcBaseSet = getArcBaseSet(newArc.getExtendedLinkRole());
		if (tmpArcBaseSet == null) {
			/* first arc of this extended link role */
			tmpArcBaseSet = new ArrayList();
			arcsBaseSet.put(newArc.getExtendedLinkRole(), tmpArcBaseSet);
			tmpArcBaseSet.add(newArc);
			return;
		}

		/* newArc must be placed to the correct position, according to the order */
		boolean sourceAvailable = false;
		int numSourceAvailable = -1;
		Arc startArc = null;
		Arc endArc = null;

		/* Check whether newArc is already available */
		Iterator tmpArcBaseSetIterator = tmpArcBaseSet.iterator();
		while (tmpArcBaseSetIterator.hasNext()) {
			Arc tmpArc = (Arc) tmpArcBaseSetIterator.next();
			if (tmpArc.equalSourceTarget(newArc)
					&& tmpArc.getPriorityAttribute() == newArc
							.getPriorityAttribute()
					&& tmpArc.getUseAttribute() == newArc.getUseAttribute()) {
				logger.info("Duplicate arc from "
						+ newArc.getSourceElement().getLabel() + " to "
						+ newArc.getTargetElement().getLabel() + " ("
						+ newArc.getArcrole() + ") in "
						+ newArc.getExtendedLinkRole());
				return;
			}
		}

		/*
		 * Check whether this arc is already prohibited by another one.
		 */
		List prohibitingArcList = (List) prohibitingArcs.get(newArc
				.getExtendedLinkRole());
		if (prohibitingArcList != null) {
			for (int i = 0; i < prohibitingArcList.size(); i++) {
				Arc prohibitingArc = (Arc) prohibitingArcList.get(i);
				if (newArc.isProhibitedByArc(prohibitingArc)) {
					/*
					 * newArc is already prohibited, therefore just stop the
					 * processing
					 */
					/* add it to list of prohibited arcs */
					List tmpProhibitedArcList = getProhibitedArcs(newArc
							.getExtendedLinkRole());
					if (tmpProhibitedArcList == null) {
						tmpProhibitedArcList = new ArrayList();
						prohibitedArcs.put(newArc.getExtendedLinkRole(),
								tmpProhibitedArcList);
					}
					tmpProhibitedArcList.add(newArc);
					return;
				}
			}
		}

		/* now put arc to the base set of arcsBaseSet */
		/* check if there already is an arc with the same source element */
		for (int i = 0; i < tmpArcBaseSet.size(); i++) {
			Arc currArc = (Arc) tmpArcBaseSet.get(i);
			if (currArc.equalSource(newArc)) {
				sourceAvailable = true;
				numSourceAvailable = i;
				startArc = currArc;
				break;
			}
		}

		if (!sourceAvailable) {
			/* first arc with this source, put it at the end */
			tmpArcBaseSet.add(tmpArcBaseSet.size(), newArc);
		} else {
			/* now check at which position the new arc has to be added */
			while (true) {
				/*
				 * check whether one of startArc and newArc is an overriding
				 * link for the other
				 */
				if (newArc.overridesArc(startArc)) {
					/* remove startArc and put newArc in the base set */
					tmpArcBaseSet.add(numSourceAvailable, newArc);
					tmpArcBaseSet.remove(startArc);
					List tmpOverridenArcList = getOverridenArcs(startArc
							.getExtendedLinkRole());
					if (tmpOverridenArcList == null) {
						tmpOverridenArcList = new ArrayList();
						overridenArcs.put(startArc.getExtendedLinkRole(),
								tmpOverridenArcList);
					}
					tmpOverridenArcList.add(startArc);
					/* processing is finished now and method returns */
					break;
				} else if (newArc.isOverriddenByArc(startArc)) {
					/*
					 * newArc is overriden and therefore not taken into account
					 * for the base set
					 */
					List tmpOverridenArcList = getOverridenArcs(startArc
							.getExtendedLinkRole());
					if (tmpOverridenArcList == null) {
						tmpOverridenArcList = new ArrayList();
						overridenArcs.put(newArc.getExtendedLinkRole(),
								tmpOverridenArcList);
					}
					tmpOverridenArcList.add(newArc);
					/* processing is finished now and method returns */
					break;
				}

				/* startArc and newArc are not overriding arcs */
				startArc = (Arc) tmpArcBaseSet.get(numSourceAvailable);
				/*
				 * if numSourceAvailable is last position, put new arc at the
				 * end
				 */
				if (numSourceAvailable >= tmpArcBaseSet.size() - 1) {
					if (newArc.getOrder() >= startArc.getOrder()) {
						tmpArcBaseSet.add(tmpArcBaseSet.size(), newArc);
					} else {
						tmpArcBaseSet.add(tmpArcBaseSet.size() - 1, newArc);
					}
					break;
				}
				endArc = (Arc) tmpArcBaseSet.get(numSourceAvailable + 1);

				/*
				 * if the new arc is before startArc and both have the same
				 * source, put it at position numSourceAvailable
				 */
				if (startArc.getOrder() > newArc.getOrder()
						&& newArc.equalSource(startArc)) {
					tmpArcBaseSet.add(numSourceAvailable, newArc);
					break;
				}

				/*
				 * if arc at numSourceAvailable+1 has another source, put newArc
				 * at position numSourceAvailable+1
				 */
				if (!newArc.equalSource(endArc)) {
					tmpArcBaseSet.add(numSourceAvailable + 1, newArc);
					break;
				}

				/*
				 * if the new arc is between startArc and endArc, put it between
				 * both
				 */
				if (startArc.getOrder() <= newArc.getOrder()
						&& newArc.getOrder() <= endArc.getOrder()) {
					tmpArcBaseSet.add(numSourceAvailable + 1, newArc);
					break;
				}
				/* else continue with the next two arcsBaseSet */
				else {
					numSourceAvailable++;
				}
			}
		}
	}

	/**
	 * Adds a prohibiting arc to the linkbase and removes all according
	 * prohibited arcs from the base set.
	 * 
	 * @param newArc
	 *            Prohibiting arc.
	 */
	private void addProhibitedArc(Arc newArc) {
		List tmpArcBaseSet = getArcBaseSet(newArc.getExtendedLinkRole());
		if (tmpArcBaseSet == null) {
			/* first arc of this extended link role */
			tmpArcBaseSet = new ArrayList();
			arcsBaseSet.put(newArc.getExtendedLinkRole(), tmpArcBaseSet);
		}

		/* add this arc to the prohibiting arcs */
		List tmpProhibitingArcList = getProhibitingArcs(newArc
				.getExtendedLinkRole());
		if (tmpProhibitingArcList == null) {
			tmpProhibitingArcList = new ArrayList();
			prohibitingArcs.put(newArc.getExtendedLinkRole(),
					tmpProhibitingArcList);
		}
		tmpProhibitingArcList.add(newArc);

		/* now check all the possible arcs whether they are prohibited */
		Iterator possibleProhibitedArcIterator = tmpArcBaseSet.iterator();
		/*
		 * Arcs which shall be removed are stored in a separate list, since is
		 * is not possible to remove elements from tmpArcBaseSet during the
		 * iteration
		 */
		List arcsToRemove = new ArrayList();
		while (possibleProhibitedArcIterator.hasNext()) {
			Arc possibleProhibitedArc = (Arc) possibleProhibitedArcIterator
					.next();
			if (newArc.prohibitsArc(possibleProhibitedArc)) {
				/* possibleProhibitedArc is prohibited */
				/* remove it from base set */
				arcsToRemove.add(possibleProhibitedArc);
				/* and add it to list of prohibited arcs */
				List tmpProhibitedArcList = getProhibitedArcs(possibleProhibitedArc
						.getExtendedLinkRole());
				if (tmpProhibitedArcList == null) {
					tmpProhibitedArcList = new ArrayList();
					prohibitedArcs.put(possibleProhibitedArc
							.getExtendedLinkRole(), tmpProhibitedArcList);
				}
				tmpProhibitedArcList.add(possibleProhibitedArc);
			}
		}
		/* now remove all the prohibited arcs from the base set */
		for (int i = 0; i < arcsToRemove.size(); i++) {
			tmpArcBaseSet.remove(arcsToRemove.get(i));
		}
	}

	/**
	 * Returns a resource for a certain ID.
	 * 
	 * @param id
	 *            ID for which the resource is taken.
	 * @return The according xbrlcore.link.Resource object which matches the
	 *         given ID.
	 */
	public Resource getResource(String id) {
		Iterator extendedLinkElementsIterator = extendedLinkElements.iterator();
		while (extendedLinkElementsIterator.hasNext()) {
			ExtendedLinkElement currExLinkElement = (ExtendedLinkElement) extendedLinkElementsIterator
					.next();
			if (currExLinkElement.isResource()
					&& currExLinkElement.getId() != null
					&& currExLinkElement.getId().equals(id)) {
				return (Resource) currExLinkElement;
			}
		}
		return null;
	}

	/**
	 * Returns the according extended link element objects to a given label
	 * within a given extended link role. According to Spec. section 3.5.3.9,
	 * there can be multiple same labels within one extended link role, since
	 * one-to-many and many-to-many arc representations are possible.
	 * 
	 * @param label
	 *            Label of the extended link element object.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List with all ExtendedLinkElement objects matching the given
	 *         parameters.
	 */
	public List getExtendedLinkElements(String label, String extendedLinkRole,
			String linkbaseSource) {
		List resultList = new ArrayList();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		Iterator xLinkElementsIterator = extendedLinkElements.iterator();
		outer: while (xLinkElementsIterator.hasNext()) {
			ExtendedLinkElement extendedLinkElement = (ExtendedLinkElement) xLinkElementsIterator
					.next();
			if (extendedLinkElement.getLabel().equals(label)
					&& extendedLinkElement.getExtendedLinkRole().equals(
							extendedLinkRole)) {
				/*
				 * check if they are from the same linkbase file, only if
				 * linkbaseSource is set
				 */
				if ((linkbaseSource != null && extendedLinkElement
						.getLinkbaseSource().equals(linkbaseSource))
						|| linkbaseSource == null) {

					resultList.add(extendedLinkElement);
				}

			}
		}
		return resultList;
	}

	/**
	 * Returns only those extended link elements (list of ExtendedLinkElement)
	 * which are part of the base set of arcs.
	 * 
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase
	 * @return List with all ExtendedLinkElement objects matching the given
	 *         parameters. Furthermore, they must be part of the base set of
	 *         arcs.
	 */
	public List getExtendedLinkElementsFromBaseSet(String extendedLinkRole) {
		List resultList = new ArrayList();
		Set usedExtendedLinkElements = new HashSet();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}

		List arcList = getArcBaseSet(extendedLinkRole);
		for (int i = 0; i < arcList.size(); i++) {
			Arc currArc = (Arc) arcList.get(i);
			ExtendedLinkElement sourceElement = currArc.getSourceElement();
			ExtendedLinkElement targetElement = currArc.getTargetElement();
			if (!usedExtendedLinkElements.contains(sourceElement)) {
				resultList.add(resultList.size(), sourceElement);
			}
			if (!usedExtendedLinkElements.contains(targetElement)) {
				resultList.add(resultList.size(), targetElement);
			}
			usedExtendedLinkElements.add(sourceElement);
			usedExtendedLinkElements.add(targetElement);
		}

		return resultList;
	}

	/**
	 * Returns a locator from a specific base set to a specific concept. To each
	 * concept, there can be only one locator within one extended link role AND
	 * within one base set of arcs. If there are mulitple locators, not all of
	 * the corresponding arcs are part of the base set (i.e. one arc is
	 * overriding another).
	 * 
	 * @param concept
	 *            The concept for which all XLinkElement objects shall be
	 *            determined.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return Locator of the concept from the base set of arcs in the given
	 *         extended link role.
	 */
	public ExtendedLinkElement getExtendedLinkElementFromBaseSet(
			Concept concept, String extendedLinkRole) {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}

		List tmpArcBaseSet = getArcBaseSet(extendedLinkRole);

		if (tmpArcBaseSet == null) {
			return null;
		}

		for (int i = 0; i < tmpArcBaseSet.size(); i++) {
			Arc currArc = (Arc) tmpArcBaseSet.get(i);
			if (currArc.getSourceElement().isLocator()
					&& ((Locator) currArc.getSourceElement()).getConcept()
							.equals(concept)) {
				return currArc.getSourceElement();
			} else if (currArc.getTargetElement().isLocator()
					&& ((Locator) currArc.getTargetElement()).getConcept()
							.equals(concept)) {
				return currArc.getTargetElement();
			}
		}
		return null;
	}

	/**
	 * Returns all extended link element objects a certain concept is linked to.
	 * That is, the concept is the "from" element, and the return list contains
	 * all the "to" elements, but only those of arcs belonging to the base set
	 * of arcs (i.e. overriden links are not taken into account).
	 * 
	 * @param concept
	 *            Concept which is the source of the links.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List of extended link element objects which the given concept is
	 *         linked to.
	 */
	public List getTargetExtendedLinkElements(Concept concept,
			String extendedLinkRole) {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List resultList = new ArrayList();
		List tmpArcBaseSet = getArcBaseSet(extendedLinkRole);
		if (tmpArcBaseSet != null) {
			for (int i = 0; i < tmpArcBaseSet.size(); i++) {
				Arc arc = (Arc) tmpArcBaseSet.get(i);
				ExtendedLinkElement sourceLinkElement = arc.getSourceElement();
				if (arc.getExtendedLinkRole().equals(extendedLinkRole)
						&& sourceLinkElement.isLocator()) {
					Locator currLoc = (Locator) sourceLinkElement;
					if (currLoc.getConcept().equals(concept)) {
						resultList.add(resultList.size(), arc
								.getTargetElement());
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * Returns all extended link element objects a certain concept is linked
	 * from. That is, the concept is the "to" element, and the return list
	 * contains all the "from" elements, but only those of arcs belonging to the
	 * base set of arcs (i.e. overriden links are not taken into account).
	 * 
	 * @param concept
	 *            Concept which is the target of the links.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List of extended link element objects which are linked to the
	 *         given concept.
	 */
	public List getSourceExtendedLinkElements(Concept concept,
			String extendedLinkRole) {
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List resultList = new ArrayList();
		List tmpArcBaseSet = getArcBaseSet(extendedLinkRole);
		if (tmpArcBaseSet != null) {
			Iterator xArcsIterator = tmpArcBaseSet.iterator();
			while (xArcsIterator.hasNext()) {
				Arc arc = (Arc) xArcsIterator.next();
				ExtendedLinkElement targetLinkElement = arc.getTargetElement();
				if (arc.getExtendedLinkRole().equals(extendedLinkRole)
						&& targetLinkElement.isLocator()) {
					Locator currLoc = (Locator) targetLinkElement;
					if (currLoc.getConcept().equals(concept)) {
						resultList.add(arc.getSourceElement());
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * Adds a new extended link role to the linkbase.
	 * 
	 * @param xbrlExtendedLinkRole
	 *            New extended link role.
	 */
	public void addExtendedLinkRole(String xbrlExtendedLinkRole) {
		extendedLinkRoles.add(xbrlExtendedLinkRole);
	}

	/**
	 * Returns a list with the complete "source network" of extended link
	 * elements in the given extended link role for the given concept. So if
	 * there is a link (= an arc) from concept a to concept b, and one from
	 * concept b to concept c, the "source network" of extended link elements
	 * for concept c would be the according extended link element for concept b
	 * and concept a.
	 * 
	 * @param concept
	 *            XBRL concept for which the source network shall be obtained.
	 * @param arcRole
	 *            The arcRole attribute of the links.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List with ExtendedLinkElement objects of the source network of
	 *         the given concept.
	 */
	public Set buildSourceNetwork(Concept concept, String arcRole,
			String extendedLinkRole) {
		Set resultSet = new HashSet();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}

		ExtendedLinkElement linkElement = getExtendedLinkElementFromBaseSet(
				concept, extendedLinkRole);

		if (linkElement == null) {
			return null;
		}

		resultSet = collectSourceNetwork(linkElement, arcRole,
				extendedLinkRole, resultSet);

		return resultSet;
	}

	/**
	 * Returns a list with all the extended link elements in the given extended
	 * link role which are linked to a certain concept. That means, if a concept
	 * a is linked to concept b and c, and concept b is linked to concept d and
	 * e, the "target network" for concept a would contain the extended link
	 * element objects of concept b, c, d and e.
	 * 
	 * @param concept
	 *            XBRL concept for which the target network shall be obtained.
	 * @param arcRole
	 *            The arcRole attribute of the links.
	 * @param extendedLinkRole
	 *            Extended link role of the linkbase.
	 * @return List with ExtendedLinkElement objects of the target network of
	 *         the given concept.
	 */
	public Set buildTargetNetwork(Concept concept, String arcRole,
			String extendedLinkRole) throws XBRLException {
		Set resultSet = new HashSet();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}

		ExtendedLinkElement linkElement = getExtendedLinkElementFromBaseSet(
				concept, extendedLinkRole);

		if (linkElement == null) {
			return null;
		}

		resultSet = collectTargetNetwork(linkElement, arcRole,
				extendedLinkRole, resultSet);

		/* remove the first element */
		resultSet.remove(getExtendedLinkElementFromBaseSet(concept,
				extendedLinkRole));
		return resultSet;
	}

	/**
	 * Helping method for buildTargetNetwork. The method is working recursively.
	 */
	private Set collectTargetNetwork(ExtendedLinkElement xLinkElement,
			String arcRole, String extendedLinkRole, Set resultSet)
			throws XBRLException {
		resultSet.add(xLinkElement);

		List arcs = getTargetArcsFromExtendedLinkElement(xLinkElement, arcRole,
				extendedLinkRole);
		Iterator arcsIterator = arcs.iterator();
		while (arcsIterator.hasNext()) {
			Arc currArc = (Arc) arcsIterator.next();
			ExtendedLinkElement xLinkElementTo = currArc.getTargetElement();
			if (currArc.getTargetRole() != null) {
				/*
				 * arc is pointing to a different extended link role
				 * (xbrldt:targetRole="...")
				 */
				collectTargetNetwork(xLinkElementTo, arcRole, currArc
						.getTargetRole(), resultSet);
			} else {
				collectTargetNetwork(xLinkElementTo, arcRole, currArc
						.getExtendedLinkRole(), resultSet);
			}
		}
		return resultSet;
	}

	/**
	 * Helping method for buildSourceNetwork. The method is working recursively.
	 */
	private Set collectSourceNetwork(ExtendedLinkElement xLinkElement,
			String arcRole, String extendedLinkRole, Set resultSet) {
		resultSet.add(xLinkElement);
		List sourceElementList = getSourceExtendedLinkElements(
				((Locator) xLinkElement).getConcept(), extendedLinkRole);
		Iterator sourceElementIterator = sourceElementList.iterator();
		while (sourceElementIterator.hasNext()) {
			ExtendedLinkElement currExLinkElement = (ExtendedLinkElement) sourceElementIterator
					.next();
			collectSourceNetwork(currExLinkElement, arcRole, extendedLinkRole,
					resultSet);
		}
		return resultSet;
	}

	/**
	 * Returns an Arc for a given source locator (only base set of arcs is taken
	 * into account).
	 * 
	 * @param loc
	 *            Source locator for which the arc shall be obtained.
	 * @return Arc with the given source locator.
	 */
	public Arc getArcForSourceLocator(Locator loc) {
		if (loc == null)
			return null;
		List tmpArcBaseSet = getArcBaseSet(loc.getExtendedLinkRole());
		if (tmpArcBaseSet != null) {
			Iterator arcsIterator = tmpArcBaseSet.iterator();
			while (arcsIterator.hasNext()) {
				Arc arc = (Arc) arcsIterator.next();
				if (arc.getSourceElement().equals(loc)) {
					return arc;
				}
			}
		}
		return null;
	}

	/**
	 * Returns an Arc for a given target locator (only base set of arcs is taken
	 * into account).
	 * 
	 * @param loc
	 *            Target locator for which the arc shall be obtained.
	 * @return Arc with the given target locator.
	 */
	public Arc getArcForTargetLocator(Locator loc) {
		if (loc == null)
			return null;
		List tmpArcBaseSet = getArcBaseSet(loc.getExtendedLinkRole());
		if (tmpArcBaseSet != null) {
			Iterator arcsIterator = tmpArcBaseSet.iterator();
			while (arcsIterator.hasNext()) {
				Arc arc = (Arc) arcsIterator.next();
				Locator target = (Locator) arc.getTargetElement();
				if (loc.getConcept().equals(target.getConcept())) {
					return arc;
				}
			}
		}
		return null;
	}

	/**
	 * Gets all the arc objects which link from a given extended link element to
	 * any other one.
	 * 
	 * @param xLinkElement
	 *            The extended link element for which the target network shall
	 *            be obtained.
	 * @param arcRole
	 *            The arcrole attribute of the links. If NULL, the arcrole is
	 *            not taken into account.
	 * @param extendedLinkRole
	 *            The extended link role of the linkbase. If NULL, the default
	 *            link role is taken.
	 * @return List with arc objects which belongs to the target network of the
	 *         given extended link element.
	 */
	public List getTargetArcsFromExtendedLinkElement(
			ExtendedLinkElement xLinkElement, String arcRole,
			String extendedLinkRole) throws XBRLException {
		/* Determine concept the extended link (a locator) refers to */
		Concept concept = null;
		if (xLinkElement.isLocator()) {
			concept = ((Locator) xLinkElement).getConcept();
		} else {
			throw new XBRLException(
					"Extended link element is no locator, so no arcs can be obtained");
		}
		List resultList = new ArrayList();
		if (extendedLinkRole == null) {
			extendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List tmpArcBaseSet = getArcBaseSet(extendedLinkRole);

		if (tmpArcBaseSet != null) {
			Iterator arcsIterator = tmpArcBaseSet.iterator();
			while (arcsIterator.hasNext()) {
				Arc arc = (Arc) arcsIterator.next();
				ExtendedLinkElement arcSourceElement = arc.getSourceElement();
				Concept arcSourceConcept = ((Locator) arcSourceElement)
						.getConcept();
				if (arc.getExtendedLinkRole().equals(extendedLinkRole)
						&& arcSourceConcept.equals(concept)) {
					if ((arcRole != null && arc.getArcrole().equals(arcRole))
							|| arcRole == null) {
						resultList.add(arc);
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * @return Set of String objects representing the extended link roles of the
	 *         linkbase.
	 */
	public Set getExtendedLinkRoles() {
		return extendedLinkRoles;
	}

	/**
	 * @return Discoverable taxonomy set this linkbase refers to.
	 */
	protected DiscoverableTaxonomySet getDts() {
		return dts;
	}

	/**
	 * @return List with extendedLinkElement objects.
	 */
	public Set getExtendedLinkElements() {
		return extendedLinkElements;
	}

	/**
	 * @return List with Arc objects.
	 */
	public Map getArcsBaseSet() {
		return arcsBaseSet;
	}
}
