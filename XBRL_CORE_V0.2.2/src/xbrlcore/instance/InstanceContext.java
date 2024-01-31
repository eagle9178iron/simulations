package xbrlcore.instance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.dimensions.MultipleDimensionType;

/**
 * This class represents a context within an XBRL instance document as it is
 * described in the XBRL 2.1 Specification which can be obtained from
 * http://www.xbrl.org/SpecRecommendations/. <br/><br/>
 * 
 * @author Daniel Hamm
 */

public class InstanceContext implements Serializable {

	static final long serialVersionUID = -6600638570679254507L;

	private String id;

	private String identifierScheme;

	private String identifier;

	private String periodValue;

	private String periodStartDate;

	private String periodEndDate;

	private List scenarioElements; /*
									 * List of elements within the scenario
									 * element (besides dimensional information)
									 */

	private List segmentElements; /*
									 * List of elements within the segment
									 * element (besides dimensional information
									 */

	private MultipleDimensionType mdtScenario; /*
												 * represents the dimensions and
												 * domains this context refers
												 * to in the <scenario> element
												 */

	private MultipleDimensionType mdtSegment; /*
												 * represents the dimensions and
												 * domains this context refers
												 * to in the <segment> element
												 */

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            ID of the instance, must be unique within one instance
	 *            document.
	 */
	public InstanceContext(String id) {
		this.id = id;
		scenarioElements = new ArrayList();
		segmentElements = new ArrayList();
	}

	/**
	 * 
	 * This method checks if two InstanceContext objects are equal. This is true
	 * if and only if: <br/>- both have the same ID <br/>- both refer to the
	 * same period <br/>- both have the same identifier and identifier scheme
	 * <br/>- the multidimensional information in each part of the context (<segment>
	 * or <scenario>) of both objects is equal <br/>- both have the same
	 * elements in their <segment> and <scenario> part
	 * 
	 * @return True if both objects are equal, otherwise false.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof InstanceContext))
			return false;
		InstanceContext otherContext = (InstanceContext) obj;
		return id.equals(otherContext.getId())
				&& (identifierScheme == null ? otherContext
						.getIdentifierScheme() == null : identifierScheme
						.equals(otherContext.getIdentifierScheme()))
				&& (identifier == null ? otherContext.getIdentifier() == null
						: identifier.equals(otherContext.getIdentifier()))
				&& (periodValue == null ? otherContext.getPeriodValue() == null
						: periodValue.equals(otherContext.getPeriodValue()))
				&& scenarioElements.equals(otherContext.getScenarioElements())
				&& segmentElements.equals(otherContext.getSegmentElements())
				&& (mdtScenario == null ? otherContext
						.getDimensionalInformation(GeneralConstants.DIM_SCENARIO) == null
						: mdtScenario
								.equals(otherContext
										.getDimensionalInformation(GeneralConstants.DIM_SCENARIO)))
				&& (mdtSegment == null ? otherContext
						.getDimensionalInformation(GeneralConstants.DIM_SEGMENT) == null
						: mdtSegment
								.equals(otherContext
										.getDimensionalInformation(GeneralConstants.DIM_SEGMENT)));
	}

	/**
	 * @return Returns a hash code of this object.
	 */
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + id.hashCode();
		hash = hash * 31
				+ (identifierScheme != null ? identifierScheme.hashCode() : 0);
		hash = hash * 31 + (identifier != null ? identifier.hashCode() : 0);
		hash = hash * 31 + (periodValue != null ? periodValue.hashCode() : 0);
		hash = hash * 31 + scenarioElements.hashCode();
		hash = hash * 31 + segmentElements.hashCode();
		hash = hash * 31 + (mdtScenario != null ? mdtScenario.hashCode() : 0);
		hash = hash * 31 + (mdtSegment != null ? mdtSegment.hashCode() : 0);
		return hash;
	}

	/**
	 * Adds a new child element to the <scenario>element (but no dimensional
	 * information).
	 * 
	 * @param newElement
	 *            The new child element of <scenario>.
	 */
	public void addScenarioElement(Element newElement) {
		scenarioElements.add(newElement);
	}

	/**
	 * Adds a new child element to the <segment>element (but no dimensional
	 * information).
	 * 
	 * @param newElement
	 *            The new child element of <segment>.
	 */
	public void addSegmentElement(Element newElement) {
		segmentElements.add(newElement);
	}

	/**
	 * 
	 * @return List of org.jdom.Element objects within the <scenario>element of
	 *         this context (besides dimensional information).
	 */
	public List getScenarioElements() {
		return scenarioElements;
	}

	/**
	 * 
	 * @return List of org.jdom.Element objects within the <segment>element of
	 *         this context (besides dimensional information).
	 */
	public List getSegmentElements() {
		return segmentElements;
	}

	/**
	 * 
	 * @return Value of the <xbrli:identifier>within this context.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * 
	 * @param identifier
	 *            Value of the <xbrli:identifier>within this context.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * 
	 * @return Value of the attribute "scheme" within the <xbrli:identifier>
	 *         within this context.
	 */
	public String getIdentifierScheme() {
		return identifierScheme;
	}

	/**
	 * 
	 * @param identifierScheme
	 *            Value of the attribute "scheme" within the <xbrli:identifier>
	 *            within this context.
	 */
	public void setIdentifierScheme(String identifierScheme) {
		this.identifierScheme = identifierScheme;
	}

	/**
	 * 
	 * @return Value of the period.
	 */
	public String getPeriodValue() {
		return periodValue;
	}

	/**
	 * 
	 * @param periodValue
	 *            Value of the period.
	 */
	public void setPeriodValue(String periodValue) {
		this.periodValue = periodValue;
	}

	/**
	 * 
	 * @return ID of this context.
	 */
	public String getId() {
		return id;
	}

	/**
	 * This method returns an xbrlcore.Dimensions.MultipleDimensionType object
	 * which describes a combination of dimensions and according domain members
	 * of the <scenario> or <segment> element of the context.
	 * 
	 * @param scen_seg
	 *            Determines whether the dimensional information of the
	 *            <scenario>or <segment>element are taken. To specify them,
	 *            please use the constants DIM_SCENARIO and DIM_SEGMENT of
	 *            xbrlcore.constants.GeneralConstants.
	 * 
	 * @return The MultipleDimensionType object representing the dimensional
	 *         information this context refers to.
	 */
	public MultipleDimensionType getDimensionalInformation(int scen_seg) {
		if (scen_seg == GeneralConstants.DIM_SCENARIO)
			return mdtScenario;
		else if (scen_seg == GeneralConstants.DIM_SEGMENT)
			return mdtSegment;
		else
			return null;
	}

	/**
	 * This method sets an xbrlcore.Dimensions.MultipleDimensionType object
	 * which describes a combination of dimensions and according domain members
	 * to the <scenario> or <segment> part of the context.
	 * 
	 * @param mdt
	 *            The MultipleDimensionType object representing the dimensional
	 *            information this context refers to.
	 * @param scen_seg
	 *            Determines whether the dimensional information of the
	 *            <scenario>or <segment>element are taken. To specify them,
	 *            please use the constants DIM_SCENARIO and DIM_SEGMENT of
	 *            xbrlcore.constants.GeneralConstants.
	 */
	public void setDimensionalInformation(MultipleDimensionType mdt,
			int scen_seg) throws CloneNotSupportedException {
		if (scen_seg == GeneralConstants.DIM_SCENARIO)
			this.mdtScenario = (MultipleDimensionType) mdt.clone();
		else if (scen_seg == GeneralConstants.DIM_SEGMENT)
			this.mdtSegment = (MultipleDimensionType) mdt.clone();
	}

	/**
	 * @return Returns the periodEndDate.
	 */
	public String getPeriodEndDate() {
		return periodEndDate;
	}

	/**
	 * @param periodEndDate
	 *            The periodEndDate to set.
	 */
	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	/**
	 * @return Returns the periodStartDate.
	 */
	public String getPeriodStartDate() {
		return periodStartDate;
	}

	/**
	 * @param periodStartDate
	 *            The periodStartDate to set.
	 */
	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}
}
