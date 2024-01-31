package xbrlcore.constants;

import org.jdom.Namespace;

/**
 * This class defines some constants for namespaces and schema files /
 * locations.<br/><br/>
 * 
 * @author Daniel Hamm
 */
public class NamespaceConstants {

	/* namespaces */
	public static final Namespace XBRLI_NAMESPACE = Namespace.getNamespace(
			"xbrli", "http://www.xbrl.org/2003/instance");

	public static final Namespace XLINK_NAMESPACE = Namespace.getNamespace(
			"xlink", "http://www.w3.org/1999/xlink");

	public static final Namespace XBRLDT_NAMESPACE = Namespace.getNamespace(
			"xbrldt", "http://xbrl.org/2005/xbrldt");

	public static final Namespace XBRLDI_NAMESPACE = Namespace.getNamespace(
			"xbrldi", "http://xbrl.org/2006/xbrldi");

	public static final Namespace ISO4217_NAMESPACE = Namespace.getNamespace(
			"iso4217", "http://www.xbrl.org/2003/iso4217");

	public static final Namespace XSI_NAMESPACE = Namespace.getNamespace("xsi",
			"http://www.w3.org/2001/XMLSchema-instance");

	public static final Namespace LINK_NAMESPACE = Namespace.getNamespace(
			"link", "http://www.xbrl.org/2003/linkbase");

	public static final Namespace XL_NAMESPACE = Namespace.getNamespace("xl",
			"http://www.xbrl.org/2003/XLink");

	public static final Namespace XML_NAMESPACE = Namespace.getNamespace("xml",
			"http://www.w3.org/XML/1998/namespace");

	/* schema locations */
	public static final String XBRL_SCHEMA_LOC_INSTANCE_URI = "http://www.xbrl.org/2003/instance";

	public static final String XBRL_SCHEMA_NAME_XBRLI = "xbrl-instance-2003-12-31.xsd";

	public static final String XBRL_SCHEMA_LOC_LINKBASE_URI = "http://www.xbrl.org/2003/linkbase";

	public static final String XBRL_SCHEMA_NAME_LINKBASE = "xbrl-linkbase-2003-12-31.xsd";

	public static final String XBRL_SCHEMA_LOC_XLINK_URI = "http://www.w3.org/1999/xlink";

	public static final String XBRL_SCHEMA_NAME_XLINK = "xlink-2003-12-31.xsd";

	public static final String XBRL_SCHEMA_LOC_XBRLDI_URI = "http://xbrl.org/2005/xbrldi";

	public static final String XBRL_SCHEMA_NAME_XBRLDI = "xbrldi-2005.xsd";
}
