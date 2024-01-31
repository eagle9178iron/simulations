package xbrlcore.instance;

import xbrlcore.constants.NamespaceConstants;

/**
 * This class creates some basic units often needed in instance files. These
 * units can be created by invoking static methods of this class.<br/><br/>
 * 
 * @author Daniel Hamm
 */
public class InstanceUnitFactory {

	private static InstanceUnit unit4217EUR;

	private static InstanceUnit unitPure;

	/**
	 * 
	 * @return 4217 Euro unit.
	 */
	public static InstanceUnit getUnit4217EUR() {
		unit4217EUR = new InstanceUnit("EUR");
		unit4217EUR.setValue("EUR");
		unit4217EUR.setNamespaceURI(NamespaceConstants.ISO4217_NAMESPACE
				.getURI());
		return unit4217EUR;
	}

	/**
	 * 
	 * @return Unit for a pure item type.
	 */
	public static InstanceUnit getUnitPure() {
		unitPure = new InstanceUnit("PURE");
		unitPure.setValue("pure");
		unitPure.setNamespaceURI(NamespaceConstants.XBRLI_NAMESPACE.getURI());
		return unitPure;
	}

}
