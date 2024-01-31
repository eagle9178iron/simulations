package xbrlcore.linkbase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import xbrlcore.xlink.Resource;

/**
 * This class represents a label linkbase of a DTS. This contains one or more
 * labels for concepts. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class LabelLinkbase extends Linkbase {

	static final long serialVersionUID = 1794952740633234300L;

	private Set languageSet;

	/**
	 * Constructor.
	 * 
	 * @param dts
	 *            The taxonomy this label linkbase refers to.
	 */
	public LabelLinkbase(DiscoverableTaxonomySet dts) {
		super(dts);
	}

	/**
	 * Returns a certain label contained in this label linkbase. The label is
	 * determined by the role, which in this case is
	 * http://www.xbrl.org/2003/role/label (the default extended link role).
	 * 
	 * @param concept
	 *            The element the label refers to.
	 * @param lang
	 *            The language of the label.
	 * @param xbrlExtendedLinkRole
	 *            The extended link role of the label. If null, the default link
	 *            role (http://www.xbrl.org/2003/role/link) is taken.
	 * @return The label matching the parameters. If no label is found, null is
	 *         returned.
	 */
	public String getLabel(Concept concept, String lang,
			String xbrlExtendedLinkRole) {
		if (xbrlExtendedLinkRole == null) {
			xbrlExtendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List possibleResults = getTargetExtendedLinkElements(concept,
				xbrlExtendedLinkRole);
		Iterator possibleResultsIterator = possibleResults.iterator();
		while (possibleResultsIterator.hasNext()) {
			ExtendedLinkElement tmpElement = (ExtendedLinkElement) possibleResultsIterator
					.next();
			if (tmpElement.isResource()) {
				Resource currResource = (Resource) tmpElement;
				if (currResource.getLang().equals(lang)
						&& tmpElement.getRole().equals(
								GeneralConstants.XBRL_ROLE_LABEL)) {
					return currResource.getValue();
				}
			} else if (tmpElement.isLocator()) {
				Locator currLoc = (Locator) tmpElement;
				if (currLoc.getResource() != null) {
					if (currLoc.getResource().getLang().equals(lang)
							&& tmpElement.getRole().equals(
									GeneralConstants.XBRL_ROLE_LABEL)) {
						return currLoc.getResource().getValue();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns a certain label of a certain role contained in the label
	 * linkbase.
	 * 
	 * @param concept
	 *            The element the label refers to.
	 * @param lang
	 *            The language of the label (if NULL, the language is not taken
	 *            into account).
	 * @param role
	 *            The role of the label.
	 * @param xbrlExtendedLinkRole
	 *            The extended link role of the label. If null, the default link
	 *            role (http://www.xbrl.org/2003/role/link) is taken.
	 * @return The label matching the parameters. If no label is found, null is
	 *         returned.
	 */
	public String getLabel(Concept concept, String lang, String role,
			String xbrlExtendedLinkRole) {
		if (xbrlExtendedLinkRole == null) {
			xbrlExtendedLinkRole = GeneralConstants.XBRL_LINKBASE_DEFAULT_LINKROLE;
		}
		List possibleResults = getTargetExtendedLinkElements(concept,
				xbrlExtendedLinkRole);
		Iterator possibleResultsIterator = possibleResults.iterator();
		while (possibleResultsIterator.hasNext()) {
			ExtendedLinkElement tmpElement = (ExtendedLinkElement) possibleResultsIterator
					.next();
			if (tmpElement.isResource()) {
				Resource currResource = (Resource) tmpElement;
				if ((lang != null && currResource.getLang().equals(lang) && tmpElement
						.getRole().equals(role))
						|| (lang == null && tmpElement.getRole().equals(role))) {
					return currResource.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Returns a certain label of a certain role contained in the label
	 * linkbase, but does neither take the language nor the extended link role
	 * into account.
	 * 
	 * @param concept
	 *            The element the label refers to.
	 * @param role
	 *            The role of the label.
	 * @return The label matching the parameters. If no label is found, null is
	 *         returned.
	 */
	public String getLabel(Concept concept, String role) {
		Set extendedLinkRoles = getExtendedLinkRoles();
		Iterator extendedLinkRolesIterator = extendedLinkRoles.iterator();
		while (extendedLinkRolesIterator.hasNext()) {
			String currExtendedLinkRole = (String) extendedLinkRolesIterator
					.next();
			String tmpLabel = getLabel(concept, null, role,
					currExtendedLinkRole);
			if (tmpLabel != null)
				return tmpLabel;
		}
		return null;
	}

	/**
	 * Returns whether this label linkbase contains labels of a specific
	 * language.
	 * 
	 * @param lang
	 *            Language which is checked whether it is contained in this
	 *            label linkbase.
	 * @return True if there is at least one label of the specified language,
	 *         false otherwise.
	 */
	public boolean containsLanguage(String lang) {
		return getLanguageSet().contains(lang);
	}

	/**
	 * @return Set of languages of labels used in this label linkbase.
	 */
	public Set getLanguageSet() {
		if (languageSet == null) {
			/* build language set */
			languageSet = new HashSet();
			Set extendedLinkElements = getExtendedLinkElements();
			Iterator extendedLinkElementsIterator = extendedLinkElements
					.iterator();
			while (extendedLinkElementsIterator.hasNext()) {
				ExtendedLinkElement currExLinkElement = (ExtendedLinkElement) extendedLinkElementsIterator
						.next();
				if (currExLinkElement.isResource()) {
					Resource currResource = (Resource) currExLinkElement;
					languageSet.add(currResource.getLang());
				}
			}
		}
		return languageSet;
	}

}
