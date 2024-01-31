package xbrlcore.exception;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;

/**
 * This class represents an Exception which is thrown if a calculation rule
 * according to the calculation linkbase does not validate.<br/><br/> Besides
 * a describing text, this class contains detailed information about the reason
 * for the violated calculation rule.
 * 
 * @author Daniel Hamm
 */
public class CalculationValidationException extends InstanceValidationException {

	static final long serialVersionUID = -3926142243011925877L;

	private DiscoverableTaxonomySet dts;

	private BigDecimal expectedResult;

	private BigDecimal calculatedResult;

	private Set calculatedConceptSet;

	private boolean missingValues;

	private Concept missingConcept;

	/**
	 * Constructor.
	 * 
	 * @param msg
	 *            Text of the exception message.
	 */
	public CalculationValidationException(String msg) {
		super(msg);
		calculatedConceptSet = new HashSet();
		missingValues = false;
	}

	/**
	 * Adds a concept to the Set of concepts which are taken into account of the
	 * calculation rule.
	 * 
	 * @param concept
	 *            Concept which is added.
	 */
	public void addCalculatedItem(Concept concept) {
		calculatedConceptSet.add(concept);
	}

	/**
	 * @return Returns the calculatedResult.
	 */
	public BigDecimal getCalculatedResult() {
		return calculatedResult;
	}

	/**
	 * @param calculatedResult
	 *            The calculatedResult to set.
	 */
	public void setCalculatedResult(BigDecimal calculatedResult) {
		this.calculatedResult = calculatedResult;
	}

	/**
	 * @return Returns the expectedResult.
	 */
	public BigDecimal getExpectedResult() {
		return expectedResult;
	}

	/**
	 * @param expectedResult
	 *            The expectedResult to set.
	 */
	public void setExpectedResult(BigDecimal expectedResult) {
		this.expectedResult = expectedResult;
	}

	/**
	 * @return Returns the calculatedConceptSet.
	 */
	public Set getCalculatedConceptSet() {
		return calculatedConceptSet;
	}

	/**
	 * @param calculatedConceptSet
	 *            The calculatedConceptSet to set.
	 */
	public void setCalculatedConceptSet(Set calculatedConceptSet) {
		this.calculatedConceptSet = calculatedConceptSet;
	}

	/**
	 * @return Returns the missingValues.
	 */
	public boolean isMissingValues() {
		return missingValues;
	}

	/**
	 * @param missingValues
	 *            The missingValues to set.
	 */
	public void setMissingValues(boolean missingValues) {
		this.missingValues = missingValues;
	}

	/**
	 * @return Returns the missingConcept.
	 */
	public Concept getMissingConcept() {
		return missingConcept;
	}

	/**
	 * @param missingConcept
	 *            The missingConcept to set.
	 */
	public void setMissingConcept(Concept missingConcept) {
		this.missingConcept = missingConcept;
	}

	/**
	 * @return Returns the dts.
	 */
	public DiscoverableTaxonomySet getDts() {
		return dts;
	}

	/**
	 * @param dts
	 *            The dts to set.
	 */
	public void setDts(DiscoverableTaxonomySet dts) {
		this.dts = dts;
	}
}
