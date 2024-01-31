package xbrlcore.constants;

/**
 * This class defines some general constants needed for the application. <br/><br/>
 * 
 * @author Daniel Hamm
 * 
 */
public class GeneralConstants {

	/* general constants */
	public static final String CONST_TRUE = "true";

	public static final String CONST_FALSE = "false";

	/* roles and arcroles */
	public static final String XBRL_ARCROLE_DOMAIN_MEMBER = "http://xbrl.org/int/dim/arcrole/domain-member";

	public static final String XBRL_ARCROLE_HYPERCUBE_DIMENSION = "http://xbrl.org/int/dim/arcrole/hypercube-dimension";

	public static final String XBRL_ARCROLE_DIMENSION_DOMAIN = "http://xbrl.org/int/dim/arcrole/dimension-domain";

	public static final String XBRL_ARCROLE_HYPERCUBE_ALL = "http://xbrl.org/int/dim/arcrole/all";

	public static final String XBRL_ARCROLE_HYPERCUBE_NOTALL = "http://xbrl.org/int/dim/arcrole/notAll";

	public static final String XBRL_ROLE_LABEL = "http://www.xbrl.org/2003/role/label";

	public static final String XBRL_ROLE_LABEL_DOCUMENTATION = "http://www.xbrl.org/2003/role/documentation";

	public static final String XBRL_INSTANCE_LINKBASE_ARCROLE = "http://www.w3.org/1999/xlink/properties/linkbase";

	public static final String XBRL_SUMMATION_ITEM_ARCROLE = "http://www.xbrl.org/2003/arcrole/summation-item";

	/* linkbases */
	public static final int XBRL_LINKBASE_LABEL = 0;

	public static final int XBRL_LINKBASE_PRESENTATION = 1;

	public static final int XBRL_LINKBASE_DEFINITION = 2;

	public static final int XBRL_LINKBASE_CALCULATION = 3;

	public static final int XBRL_LINKBASE_REFERENCE = 4;

	public static final String XBRL_LINKBASE_DEFAULT_LINKROLE = "http://www.xbrl.org/2003/role/link";

	public static final String XBRL_LINKBASE_ROLE_LABEL = "http://www.xbrl.org/2003/role/labelLinkbaseRef";

	public static final String XBRL_LINKBASE_ROLE_DEFINITION = "http://www.xbrl.org/2003/role/definitionLinkbaseRef";

	public static final String XBRL_LINKBASE_ROLE_PRESENTATION = "http://www.xbrl.org/2003/role/presentationLinkbaseRef";

	public static final String XBRL_LINKBASE_ROLE_CALCULATION = "http://www.xbrl.org/2003/role/calculationLinkbaseRef";

	public static final String XBRL_LINKBASE_ROLE_REFERENCE = "http://www.xbrl.org/2003/role/referenceLinkbaseRef";

	public static final String XBRL_LINKBASE_LINK_LABEL = "labelLink";

	public static final String XBRL_LINKBASE_LINK_PRESENTATION = "presentationLink";

	public static final String XBRL_LINKBASE_LINK_DEFINITION = "definitionLink";

	public static final String XBRL_LINKBASE_ARC_LABEL = "labelArc";

	public static final String XBRL_LINKBASE_ARC_PRESENTATION = "presentationArc";

	public static final String XBRL_LINKBASE_ARC_DEFINITION = "definitionArc";

	/* ??? */
	public static final String XBRL_SCHEMA_NAME_ISO4217 = "iso4217.xsd";

	public static final String XBRL_SCHEMA_NAME_XSI = "XMLSchema-instance.xsd";

	/* substitution groups */
	public static final String XBRL_SUBST_GROUP_DIMENSION_ITEM = "xbrldt:dimensionItem";

	public static final String XBRL_SUBST_GROUP_HYPERCUBE_ITEM = "xbrldt:hypercubeItem";

	/*
	 * Constants whether to set dimensional information to <scenario> or to
	 * <segment> element
	 */
	public static final int DIM_SCENARIO = 0;

	public static final int DIM_SEGMENT = 1;

	/* Constants for period instant or period duration */
	public static final String CONTEXT_INSTANT = "instant";

	public static final String CONTEXT_DURATION = "duration";

}
