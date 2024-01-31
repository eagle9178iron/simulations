package xbrlcore.taxonomy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import xbrlcore.constants.ExceptionConstants;
import xbrlcore.constants.GeneralConstants;
import xbrlcore.constants.NamespaceConstants;
import xbrlcore.exception.TaxonomyCreationException;
import xbrlcore.exception.XBRLException;
import xbrlcore.linkbase.CalculationLinkbase;
import xbrlcore.linkbase.DefinitionLinkbase;
import xbrlcore.linkbase.LabelLinkbase;
import xbrlcore.linkbase.Linkbase;
import xbrlcore.linkbase.PresentationLinkbase;
import xbrlcore.xlink.Arc;
import xbrlcore.xlink.ExtendedLinkElement;
import xbrlcore.xlink.Locator;
import xbrlcore.xlink.Resource;

/**
 * 
 * This class generates an xbrlcore.taxonomy.DiscoverableTaxonomySet object and
 * all other necessary objects for the representation of a DTS according to a
 * taxonomy file. It is modelled according to the Factory pattern. <br/><br/>
 * 
 * @author Daniel Hamm
 */

public class DTSFactory {

    private static DTSFactory dtsFactory;

    private String taxPath;

    private List importedTaxonomies;

    private SAXBuilder saxBuilder;

    private Map taxonomyNameToSchema;

    private Map taxonomyNameToDocument;

    private DiscoverableTaxonomySet dts;

    private PresentationLinkbase presLinkbase;

    private LabelLinkbase labelLinkbase;

    private DefinitionLinkbase defLinkbase;

    private CalculationLinkbase calcLinkbase;

    private static Logger logger = Logger.getLogger(DTSFactory.class);

    private DTSFactory() {
        if (Thread.currentThread().getContextClassLoader().getResource(
                "de/bundesbank/baselsolv/log4j.properties") != null) {
            PropertyConfigurator.configure(Thread.currentThread()
                    .getContextClassLoader().getResource(
                            "de/bundesbank/baselsolv/log4j.properties"));
        }
    }

    /**
     * Returns an object of the class DTSFactory. This is the only way to create
     * such an object, the constructor cannot be invoked.
     * 
     * @return An instance of this object.
     */
    public static synchronized DTSFactory get() {
        if (dtsFactory == null) {
            dtsFactory = new DTSFactory();
        }
        return dtsFactory;
    }

    /**
     * Creates and returns a discoverable taxonomy set (DTS), represented
     * through a DiscoverableTaxonomySet object. The DTS is created based on the
     * given file, which is expected to be an XBRL taxonomy. If this taxonomy
     * imports other taxonomies, they are also created by this method and can be
     * obtained afterwards via the according methods.
     * 
     * @param taxonomyFile
     *            File containing the XBRL taxonomy.
     * @return Discoverable taxonomy set, based on the given file, including all
     *         imported taxonomies.
     * @throws IOException
     * @throws JDOMException
     * @throws TaxonomyCreationException
     */
    public DiscoverableTaxonomySet createTaxonomy(File taxonomyFile)
            throws IOException, JDOMException, TaxonomyCreationException,
            XBRLException {

        logger.info("Processing discoverable taxonomy set "
                + taxonomyFile.getName() + " ...");

        importedTaxonomies = new ArrayList();
        taxPath = taxonomyFile.getAbsolutePath().substring(0,
                taxonomyFile.getAbsolutePath().lastIndexOf(File.separator) + 1);
        saxBuilder = new SAXBuilder();
        taxonomyNameToSchema = new HashMap();
        taxonomyNameToDocument = new HashMap();

        dts = new DiscoverableTaxonomySet();

        importedTaxonomies.add(taxonomyFile.getName());
        collectImportedTaxonomies(taxonomyFile);

        buildTaxonomySchemas();

        presLinkbase = new PresentationLinkbase(dts);
        labelLinkbase = new LabelLinkbase(dts);
        defLinkbase = new DefinitionLinkbase(dts);
        calcLinkbase = new CalculationLinkbase(dts);

        buildLinkbase(presLinkbase,
                "http://www.xbrl.org/2003/role/presentationLinkbaseRef",
                "presentationLink");
        buildLinkbase(labelLinkbase,
                "http://www.xbrl.org/2003/role/labelLinkbaseRef", "labelLink");
        buildLinkbase(defLinkbase,
                "http://www.xbrl.org/2003/role/definitionLinkbaseRef",
                "definitionLink");
        buildLinkbase(calcLinkbase,
                "http://www.xbrl.org/2003/role/calculationLinkbaseRef",
                "calculationLink");

        buildArcs(presLinkbase,
                "http://www.xbrl.org/2003/role/presentationLinkbaseRef",
                "presentationLink", "presentationArc");
        buildArcs(labelLinkbase,
                "http://www.xbrl.org/2003/role/labelLinkbaseRef", "labelLink",
                "labelArc");
        buildArcs(defLinkbase,
                "http://www.xbrl.org/2003/role/definitionLinkbaseRef",
                "definitionLink", "definitionArc");
        buildArcs(calcLinkbase,
                "http://www.xbrl.org/2003/role/calculationLinkbaseRef",
                "calculationLink", "calculationArc");

        presLinkbase.buildPresentationLinkbase();
        defLinkbase.buildDefinitionLinkbase();

        dts.setTopTaxonomy((TaxonomySchema) taxonomyNameToSchema
                .get(taxonomyFile.getName()));
        dts.setPresentationLinkbase(presLinkbase);
        dts.setLabelLinkbase(labelLinkbase);
        dts.setDefinitionLinkbase(defLinkbase);
        dts.setCalculationLinkbase(calcLinkbase);
        return dts;
    }

    private void buildTaxonomySchemas() throws IOException, JDOMException,
            TaxonomyCreationException {
        Iterator importedTaxonomiesIterator = importedTaxonomies.iterator();
        while (importedTaxonomiesIterator.hasNext()) {
            String currTaxonomySchemaName = (String) importedTaxonomiesIterator
                    .next();
            logger.info("Processing taxonomy schema " + currTaxonomySchemaName
                    + " ... ");
            TaxonomySchema currTaxSchema = new TaxonomySchema(dts);
            Document taxonomySource = (Document) taxonomyNameToDocument
                    .get(currTaxonomySchemaName);
            currTaxSchema.setName(currTaxonomySchemaName);
            String targetNamespaceURI = taxonomySource.getRootElement()
                    .getAttributeValue("targetNamespace");
            String targetNamespacePrefix = "ns_"
                    + targetNamespaceURI.substring(targetNamespaceURI
                            .lastIndexOf("/") + 1, targetNamespaceURI.length());
            currTaxSchema.setNamespace(Namespace.getNamespace(
                    targetNamespacePrefix, targetNamespaceURI));

            /* set imported taxonomy schemas */
            currTaxSchema.setImportedTaxonomyNames(new HashSet(
                    getImportedTaxonomyFileNames(taxonomySource)));

            /* set concepts */
            Element rootElement = taxonomySource.getRootElement();
            List conceptElementList = rootElement.getChildren("element",
                    Namespace.getNamespace("xsd",
                            "http://www.w3.org/2001/XMLSchema"));
            for (int i = 0; i < conceptElementList.size(); i++) {
                Element currConceptElement = (Element) conceptElementList
                        .get(i);
                if (currConceptElement.getAttributeValue("id") != null) {
                    Concept currConcept = new Concept(currConceptElement
                            .getAttributeValue("name"));
                    currConcept.setId(currConceptElement
                            .getAttributeValue("id"));
                    currConcept.setType(currConceptElement
                            .getAttributeValue("type"));
                    currConcept.setSubstitutionGroup(currConceptElement
                            .getAttributeValue("substitutionGroup"));
                    currConcept
                            .setPeriodType(currConceptElement
                                    .getAttributeValue(
                                            "periodType",
                                            Namespace
                                                    .getNamespace("xbrli",
                                                            "http://www.xbrl.org/2003/instance")));
                    currConcept.setAbstract(currConceptElement
                            .getAttribute("abstract") != null
                            && currConceptElement.getAttributeValue("abstract")
                                    .equals("true"));
                    currConcept.setNillable(currConceptElement
                            .getAttribute("nillable") != null
                            && currConceptElement.getAttributeValue("nillable")
                                    .equals("true"));
                    currConcept.setTypedDomainRef(currConceptElement
                            .getAttributeValue("typedDomainRef", Namespace
                                    .getNamespace("xbrldt",
                                            "http://xbrl.org/2005/xbrldt")));
                    currConcept.setTaxonomySchemaName(currTaxSchema.getName());
                    currConcept.setNamespace(currTaxSchema.getNamespace());
                    currTaxSchema.addConcept(currConcept);
                }
            }
            dts.addTaxonomy(currTaxSchema);
            taxonomyNameToSchema.put(currTaxonomySchemaName, currTaxSchema);
        }
    }

    private void buildLinkbase(Linkbase linkbase, String role,
            String extendedLinkRole) throws IOException, JDOMException,
            TaxonomyCreationException {
        Iterator importedTaxonomiesIterator = importedTaxonomies.iterator();

        while (importedTaxonomiesIterator.hasNext()) {
            String currTaxonomySchemaName = (String) importedTaxonomiesIterator
                    .next();
            Document taxonomySource = (Document) taxonomyNameToDocument
                    .get(currTaxonomySchemaName);
            Element rootElement = taxonomySource.getRootElement();
            Element annotationElement = rootElement.getChild("annotation",
                    Namespace.getNamespace("xsd",
                            "http://www.w3.org/2001/XMLSchema"));
            if (annotationElement != null) {
                Element appInfoElement = annotationElement.getChild("appinfo",
                        Namespace.getNamespace("xsd",
                                "http://www.w3.org/2001/XMLSchema"));
                if (appInfoElement != null) {
                    List linkbaseRefList = appInfoElement.getChildren(
                            "linkbaseRef", Namespace.getNamespace("link",
                                    "http://www.xbrl.org/2003/linkbase"));
                    for (int i = 0; i < linkbaseRefList.size(); i++) {
                        Element currLinkbaseRefElement = (Element) linkbaseRefList
                                .get(i);
                        if (currLinkbaseRefElement.getAttributeValue(
                                "role",
                                Namespace.getNamespace("xlink",
                                        "http://www.w3.org/1999/xlink"))
                                .equals(role)) {
                            String linkbaseSource = currLinkbaseRefElement
                                    .getAttributeValue(
                                            "href",
                                            Namespace
                                                    .getNamespace("xlink",
                                                            "http://www.w3.org/1999/xlink"));
                            logger.info("Building linkbase document "
                                    + linkbaseSource + " ... ");
                            Document linkbaseDocument = saxBuilder
                                    .build(taxPath + linkbaseSource);
                            /* collect extended link roles */
                            List extendedLinkRolesList = linkbaseDocument
                                    .getRootElement()
                                    .getChildren(
                                            extendedLinkRole,
                                            Namespace
                                                    .getNamespace("http://www.xbrl.org/2003/linkbase"));
                            for (int j = 0; j < extendedLinkRolesList.size(); j++) {
                                Element newExtendedLinkRoleElement = (Element) extendedLinkRolesList
                                        .get(j);
                                String currExtendedLinkRole = newExtendedLinkRoleElement
                                        .getAttributeValue(
                                                "role",
                                                Namespace
                                                        .getNamespace("xlink",
                                                                "http://www.w3.org/1999/xlink"));
                                linkbase
                                        .addExtendedLinkRole(currExtendedLinkRole);
                                List linkbaseElements = newExtendedLinkRoleElement
                                        .getChildren();
                                for (int k = 0; k < linkbaseElements.size(); k++) {
                                    Element currLinkbaseElement = (Element) linkbaseElements
                                            .get(k);
                                    Attribute typeAttr = currLinkbaseElement
                                            .getAttribute(
                                                    "type",
                                                    Namespace
                                                            .getNamespace(
                                                                    "xlink",
                                                                    "http://www.w3.org/1999/xlink"));
                                    if (typeAttr != null
                                            && (typeAttr.getValue().equals(
                                                    "locator") || typeAttr
                                                    .getValue().equals(
                                                            "resource"))) {
                                        /* create extended link element */
                                        String label = currLinkbaseElement
                                                .getAttributeValue(
                                                        "label",
                                                        Namespace
                                                                .getNamespace(
                                                                        "xlink",
                                                                        "http://www.w3.org/1999/xlink"));
                                        if (label == null
                                                || label.length() == 0) {
                                            /** TODO: throw exception! */
                                            System.err
                                                    .println("Could not find label for extended link element");
                                        }
                                        if (typeAttr.getValue().equals(
                                                "locator")) {
                                            /* a locator has to be created */
                                            Locator newLocator = new Locator(
                                                    label, linkbaseSource);
                                            newLocator
                                                    .setExtendedLinkRole(currExtendedLinkRole);
                                            newLocator
                                                    .setRole(currLinkbaseElement
                                                            .getAttributeValue(
                                                                    "role",
                                                                    Namespace
                                                                            .getNamespace(
                                                                                    "xlink",
                                                                                    "http://www.w3.org/1999/xlink")));
                                            newLocator
                                                    .setTitle(currLinkbaseElement
                                                            .getAttributeValue(
                                                                    "title",
                                                                    Namespace
                                                                            .getNamespace(
                                                                                    "xlink",
                                                                                    "http://www.w3.org/1999/xlink")));
                                            newLocator
                                                    .setId(currLinkbaseElement
                                                            .getAttributeValue("id"));

                                            String conceptName = currLinkbaseElement
                                                    .getAttributeValue(
                                                            "href",
                                                            Namespace
                                                                    .getNamespace(
                                                                            "xlink",
                                                                            "http://www.w3.org/1999/xlink"));
                                            if (conceptName == null) {
                                                /** TODO: throw excpetion */
                                                System.err
                                                        .println("Could not find concept the label refers to");
                                            } else {
                                                /*
                                                 * concept name is in form
                                                 * taxonomy#elementID - only
                                                 * elementID is needed
                                                 */
                                                String elementId = conceptName
                                                        .substring(
                                                                conceptName
                                                                        .indexOf("#") + 1,
                                                                conceptName
                                                                        .length());
                                                Concept refConcept = dts
                                                        .getConceptByID(elementId);
                                                if (refConcept != null) {
                                                    newLocator
                                                            .setConcept(refConcept);
                                                } else {
                                                    /*
                                                     * now the concept cannot be
                                                     * resolved, try to get an
                                                     * already existing resource
                                                     */
                                                    Resource resource = linkbase
                                                            .getResource(elementId);
                                                    if (resource == null) {
                                                        /*
                                                         * the location of the
                                                         * target of this
                                                         * locator could not be
                                                         * found
                                                         */
                                                        throw new TaxonomyCreationException(
                                                                ExceptionConstants.EX_LINKBASE_LOCATOR_WITHOUT_REF
                                                                        + ": "
                                                                        + elementId
                                                                        + " in Linkbase "
                                                                        + linkbaseSource);
                                                    }
                                                    newLocator
                                                            .setResource(resource);
                                                }
                                            }
                                            linkbase
                                                    .addExtendedLinkElement(newLocator);
                                        } else {
                                            /* a resource has to be created */
                                            Resource newResource = new Resource(
                                                    label, linkbaseSource);
                                            newResource
                                                    .setExtendedLinkRole(currExtendedLinkRole);
                                            newResource
                                                    .setRole(currLinkbaseElement
                                                            .getAttributeValue(
                                                                    "role",
                                                                    Namespace
                                                                            .getNamespace(
                                                                                    "xlink",
                                                                                    "http://www.w3.org/1999/xlink")));
                                            newResource
                                                    .setTitle(currLinkbaseElement
                                                            .getAttributeValue(
                                                                    "title",
                                                                    Namespace
                                                                            .getNamespace(
                                                                                    "xlink",
                                                                                    "http://www.w3.org/1999/xlink")));
                                            newResource
                                                    .setId(currLinkbaseElement
                                                            .getAttributeValue("id"));

                                            newResource
                                                    .setLang(currLinkbaseElement
                                                            .getAttributeValue(
                                                                    "lang",
                                                                    Namespace
                                                                            .getNamespace(
                                                                                    "xml",
                                                                                    "http://www.w3.org/XML/1998/namespace")));
                                            newResource
                                                    .setId(currLinkbaseElement
                                                            .getAttributeValue("id"));
                                            newResource
                                                    .setValue(currLinkbaseElement
                                                            .getValue());
                                            linkbase
                                                    .addExtendedLinkElement(newResource);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildArcs(Linkbase linkbase, String role,
            String xbrlExtendedLinkRole, String arcName) throws IOException,
            JDOMException {
        Iterator importedTaxonomiesIterator = importedTaxonomies.iterator();

        while (importedTaxonomiesIterator.hasNext()) {

            String currTaxonomySchemaName = (String) importedTaxonomiesIterator
                    .next();

            Document taxonomySource = (Document) taxonomyNameToDocument
                    .get(currTaxonomySchemaName);
            Element rootElement = taxonomySource.getRootElement();
            Element annotationElement = rootElement.getChild("annotation",
                    Namespace.getNamespace("xsd",
                            "http://www.w3.org/2001/XMLSchema"));
            if (annotationElement != null) {
                Element appInfoElement = annotationElement.getChild("appinfo",
                        Namespace.getNamespace("xsd",
                                "http://www.w3.org/2001/XMLSchema"));
                if (appInfoElement != null) {
                    List linkbaseRefList = appInfoElement.getChildren(
                            "linkbaseRef", Namespace.getNamespace("link",
                                    "http://www.xbrl.org/2003/linkbase"));
                    for (int i = 0; i < linkbaseRefList.size(); i++) {
                        Element currLinkbaseRefElement = (Element) linkbaseRefList
                                .get(i);
                        if (currLinkbaseRefElement.getAttributeValue(
                                "role",
                                Namespace.getNamespace("xlink",
                                        "http://www.w3.org/1999/xlink"))
                                .equals(role)) {
                            String linkbaseSource = currLinkbaseRefElement
                                    .getAttributeValue(
                                            "href",
                                            Namespace
                                                    .getNamespace("xlink",
                                                            "http://www.w3.org/1999/xlink"));
                            Document linkbaseDocument = saxBuilder
                                    .build(taxPath + linkbaseSource);
                            List extendedLinkRolesList = linkbaseDocument
                                    .getRootElement()
                                    .getChildren(
                                            xbrlExtendedLinkRole,
                                            Namespace
                                                    .getNamespace("http://www.xbrl.org/2003/linkbase"));
                            for (int j = 0; j < extendedLinkRolesList.size(); j++) {
                                Element newExtendedLinkRoleElement = (Element) extendedLinkRolesList
                                        .get(j);
                                String currExtendedLinkRole = newExtendedLinkRoleElement
                                        .getAttributeValue(
                                                "role",
                                                Namespace
                                                        .getNamespace("xlink",
                                                                "http://www.w3.org/1999/xlink"));
                                List arcElementsList = newExtendedLinkRoleElement
                                        .getChildren(
                                                arcName,
                                                Namespace
                                                        .getNamespace("http://www.xbrl.org/2003/linkbase"));
                                for (int k = 0; k < arcElementsList.size(); k++) {
                                    Element currArcElement = (Element) arcElementsList
                                            .get(k);
                                    /* create a new Arc */
                                    String fromAttribute = currArcElement
                                            .getAttributeValue(
                                                    "from",
                                                    NamespaceConstants.XLINK_NAMESPACE);
                                    List fromElements = linkbase
                                            .getExtendedLinkElements(
                                                    fromAttribute,
                                                    currExtendedLinkRole,
                                                    linkbaseSource);
                                    String toAttribute = currArcElement
                                            .getAttributeValue(
                                                    "to",
                                                    NamespaceConstants.XLINK_NAMESPACE);
                                    List toElements = linkbase
                                            .getExtendedLinkElements(
                                                    toAttribute,
                                                    currExtendedLinkRole,
                                                    linkbaseSource);
                                    /*
                                     * Create the arcs. Usually these will be
                                     * one-to-one arcs, but according to Spec.
                                     * section 3.5.3.9, one-to-many and
                                     * many-to-many relationships are also
                                     * possible.
                                     */
                                    for (int fromNumber = 0; fromNumber < fromElements
                                            .size(); fromNumber++) {
                                        ExtendedLinkElement currFromElement = (ExtendedLinkElement) fromElements
                                                .get(fromNumber);
                                        for (int toNumber = 0; toNumber < toElements
                                                .size(); toNumber++) {
                                            ExtendedLinkElement currToElement = (ExtendedLinkElement) toElements
                                                    .get(toNumber);

                                            Arc newArc = new Arc(
                                                    currExtendedLinkRole);
                                            newArc
                                                    .setSourceElement(currFromElement);
                                            newArc
                                                    .setTargetElement(currToElement);
                                            newArc
                                                    .setXbrlExtendedLinkRole(currExtendedLinkRole);
                                            newArc
                                                    .setArcrole(currArcElement
                                                            .getAttributeValue(
                                                                    "arcrole",
                                                                    NamespaceConstants.XLINK_NAMESPACE));
                                            newArc
                                                    .setTargetRole(currArcElement
                                                            .getAttributeValue(
                                                                    "targetRole",
                                                                    NamespaceConstants.XBRLDT_NAMESPACE));
                                            newArc
                                                    .setContextElement(currArcElement
                                                            .getAttributeValue(
                                                                    "contextElement",
                                                                    NamespaceConstants.XBRLDT_NAMESPACE));
                                            newArc.setAttributes(currArcElement
                                                    .getAttributes());
                                            if (currArcElement
                                                    .getAttributeValue("order") != null) {
                                                float order = (new Float(
                                                        currArcElement
                                                                .getAttributeValue("order")))
                                                        .floatValue();
                                                newArc.setOrder(order);
                                            }
                                            newArc
                                                    .setUseAttribute(currArcElement
                                                            .getAttributeValue("use"));
                                            if (currArcElement
                                                    .getAttributeValue("priority") != null) {
                                                newArc
                                                        .setPriorityAttribute(new Integer(
                                                                currArcElement
                                                                        .getAttributeValue("priority"))
                                                                .intValue());
                                            }
                                            if (currArcElement
                                                    .getAttributeValue(
                                                            "usable",
                                                            NamespaceConstants.XBRLDT_NAMESPACE) != null
                                                    && currArcElement
                                                            .getAttributeValue(
                                                                    "usable",
                                                                    NamespaceConstants.XBRLDT_NAMESPACE)
                                                            .equals(
                                                                    GeneralConstants.CONST_FALSE)) {
                                                if (newArc.getTargetElement()
                                                        .isLocator()) {
                                                    ((Locator) newArc
                                                            .getTargetElement())
                                                            .setUsable(false);
                                                }
                                            }
                                            if (currArcElement
                                                    .getAttributeValue("weight") != null) {
                                                newArc
                                                        .setWeightAttribute(new Float(
                                                                currArcElement
                                                                        .getAttributeValue("weight"))
                                                                .floatValue());
                                            }

                                            /* add arc to linkbase */

                                            linkbase.addArc(newArc);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void collectImportedTaxonomies(File taxonomyFile)
            throws IOException, JDOMException {
        Document tmpTaxDocument = saxBuilder.build(taxonomyFile);
        taxonomyNameToDocument.put(taxonomyFile.getName(), tmpTaxDocument);
        List tmpImportedTaxonomyNames = getImportedTaxonomyFileNames(tmpTaxDocument);
        if (tmpImportedTaxonomyNames.size() == 0) {
            return;
        } else {
            for (int i = 0; i < tmpImportedTaxonomyNames.size(); i++) {
                if (!importedTaxonomies.contains(tmpImportedTaxonomyNames
                        .get(i))) {
                    importedTaxonomies.add(0, tmpImportedTaxonomyNames.get(i));
                }
            }
            Iterator tmpImportedTaxonomyNamesIterator = tmpImportedTaxonomyNames
                    .iterator();
            while (tmpImportedTaxonomyNamesIterator.hasNext()) {
                String nextTaxonomyFileName = (String) tmpImportedTaxonomyNamesIterator
                        .next();
                collectImportedTaxonomies(new File(taxPath
                        + nextTaxonomyFileName));
            }
        }
    }

    private List getImportedTaxonomyFileNames(Document taxonomySource) {
        List resultSet = new ArrayList();
        Element rootElement = taxonomySource.getRootElement();
        List children = rootElement.getChildren("import", Namespace
                .getNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        for (int i = 0; i < children.size(); i++) {
            Element currElement = (Element) children.get(i);
            resultSet.add(new File(currElement
                    .getAttributeValue("schemaLocation")).getName());
        }
        return resultSet;
    }

}
