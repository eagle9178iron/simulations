package xbrlcore.instance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import xbrlcore.constants.GeneralConstants;
import xbrlcore.constants.NamespaceConstants;
import xbrlcore.dimensions.SingleDimensionType;
import xbrlcore.taxonomy.Concept;
import xbrlcore.taxonomy.DiscoverableTaxonomySet;

/**
 * This class creates the XML structure of an Instance object. So by using this
 * class, the content of an instance document - represented in an
 * xbrlcore.instance.Instance object - can be put out. <br/><br/>
 * 
 * @author Daniel Hamm
 *  
 */
public class InstanceOutputter {

    Instance instance;

    /**
     * Constructor.
     * 
     * @param instance
     *            Instance which shall be put out.
     */
    public InstanceOutputter(Instance instance) {
        this.instance = instance;
    }

    /**
     * This method saves the xbrlcore.instance.Instance object as an XBRL
     * Instance document file on the file system. The file is UTF-8 encoded.
     * 
     * @param directory
     *            Name of the directory (if it does not end with
     *            File.separatorChar, this is added).
     * @param fileName
     *            Name of the file.
     * @return If method succeeds a File object of the new file is returned,
     *         otherwise NULL.
     */
    public File saveAsFile(String directory, String fileName) {
        char[] charArray = new char[1];
        charArray[0] = File.separatorChar;
        if (directory.lastIndexOf(File.separatorChar) != directory.length() - 1) {
            /* append File.separatorChar */
            directory = directory.concat(new String(charArray));
        }
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(directory
                    + fileName), "UTF-8");
            out.write(getXMLString());
            out.close();
        } catch (IOException ex) {
            return null;
        }

        return new File(directory, fileName);

    }

    /**
     * 
     * @return XML structure of the instance as java.lang.String object.
     */
    public String getXMLString() {
        /* and output everything */
        XMLOutputter serializer = new XMLOutputter();
        Format f = Format.getPrettyFormat();
        serializer.setFormat(f);
        return serializer.outputString(getXML());
    }

    /**
     * 
     * @return XML structure which is the content of the instance.
     */
    public Document getXML() {
        Set factSet = instance.getFactSet();
        Map contextMap = instance.getContextMap();
        Map unitMap = instance.getUnitMap();

        Document resultDocument = new Document();
        resultDocument.setRootElement(getRootElement());
        /* set schemaRef elements */
        resultDocument.getRootElement().addContent(getSchemaRefElement());

        Iterator factSetIterator = factSet.iterator();
        while (factSetIterator.hasNext()) {
            resultDocument.getRootElement().addContent(
                    getFactElement((Fact) factSetIterator.next()));
        }
        Set contextMapEntrySet = contextMap.entrySet();
        Iterator contextMapIterator = contextMapEntrySet.iterator();
        while (contextMapIterator.hasNext()) {
            Map.Entry currEntry = (Map.Entry) contextMapIterator.next();
            InstanceContext currContext = (InstanceContext) currEntry
                    .getValue();
            resultDocument.getRootElement().addContent(
                    getContextElement(currContext));
        }

        Set unitMapEntrySet = unitMap.entrySet();
        Iterator unitMapIterator = unitMapEntrySet.iterator();
        while (unitMapIterator.hasNext()) {
            Map.Entry currEntry = (Map.Entry) unitMapIterator.next();
            InstanceUnit currUnit = (InstanceUnit) currEntry.getValue();
            resultDocument.getRootElement()
                    .addContent(getUnitElement(currUnit));
        }
        return resultDocument;
    }

    /**
     * @return Returns root element of the instance including all namespaces and
     *         schema locations.
     */
    private Element getRootElement() {
        Element rootElement = new Element("xbrl");
        rootElement.setNamespace(instance.getInstanceNamespace());

        /* add additinal namespace declarations */
        Set additionalNamespaceMap = instance.getAdditionalNamespaceSet();
        Iterator additionalNamespaceIterator = additionalNamespaceMap
                .iterator();
        while (additionalNamespaceIterator.hasNext()) {
            Namespace currNamespace = (Namespace) additionalNamespaceIterator
                    .next();
            rootElement.addNamespaceDeclaration(currNamespace);
        }

        /* set value of attribute schemaLocation */
        String schemaLocationValue = "";
        Map schemaLocationMap = instance.getSchemaLocationMap();
        Set schemaLocationEntrySet = schemaLocationMap.entrySet();
        Iterator schemaLocationIterator = schemaLocationEntrySet.iterator();
        while (schemaLocationIterator.hasNext()) {
            Map.Entry currEntry = (Map.Entry) schemaLocationIterator.next();
            String schemaURI = (String) currEntry.getKey();
            String schemaName = (String) currEntry.getValue();
            if (schemaLocationValue.length() > 0) {
                schemaLocationValue += " ";
            }
            schemaLocationValue += schemaURI + " " + schemaName;
        }

        if (instance.getComment() != null) {
            rootElement.addContent(new Comment(instance.getComment()));
        }
        return rootElement;
    }

    /**
     * 
     * @return Set of schemaRef elements of the instance document, which points
     *         to the according taxonomies.
     */
    private Set getSchemaRefElement() {
        Set schemaRefElementSet = new HashSet();
        Set dtsSet = instance.getDiscoverableTaxonomySet();
        Iterator dtsSetIterator = dtsSet.iterator();
        while (dtsSetIterator.hasNext()) {
            DiscoverableTaxonomySet currDts = (DiscoverableTaxonomySet) dtsSetIterator
                    .next();
            Element schemaRefElement = new Element("schemaRef");
            schemaRefElement.setNamespace(instance
                    .getNamespace(NamespaceConstants.LINK_NAMESPACE.getURI()));

            Attribute typeAttribute = new Attribute("type", "simple");
            typeAttribute.setNamespace(instance
                    .getNamespace(NamespaceConstants.XLINK_NAMESPACE.getURI()));

            Attribute arcroleAttribute = new Attribute("arcrole",
                    GeneralConstants.XBRL_INSTANCE_LINKBASE_ARCROLE);
            arcroleAttribute.setNamespace(instance
                    .getNamespace(NamespaceConstants.XLINK_NAMESPACE.getURI()));

            Attribute hrefAttribute = new Attribute("href", currDts
                    .getTopTaxonomy().getName());
            hrefAttribute.setNamespace(instance
                    .getNamespace(NamespaceConstants.XLINK_NAMESPACE.getURI()));

            schemaRefElement.setAttribute(typeAttribute);
            schemaRefElement.setAttribute(arcroleAttribute);
            schemaRefElement.setAttribute(hrefAttribute);

            schemaRefElementSet.add(schemaRefElement);
        }

        return schemaRefElementSet;
    }

    /**
     * Creates element of a specific fact.
     * 
     * @param fact
     *            Fact for which an element is created.
     * @return Element of the according fact.
     */
    private Element getFactElement(Fact fact) {
        Element factElement = new Element(fact.getConcept().getName());
        factElement.setNamespace(fact.getConcept().getNamespace());

        if (fact.getDecimals() != null) {
            factElement.setAttribute(new Attribute("decimals", fact
                    .getDecimals()));
        }
        if (fact.getPrecision() != null) {
            factElement.setAttribute(new Attribute("precision", fact
                    .getPrecision()));
        }
        if (fact.getInstanceUnit() != null) {
            factElement.setAttribute(new Attribute("unitRef", fact
                    .getInstanceUnit().getId()));
        }
        factElement.setAttribute(new Attribute("contextRef", fact
                .getInstanceContext().getId()));

        factElement.setText(fact.getValue().replaceAll(",", "."));

        return factElement;
    }

    /**
     * Creates unit structure of a specific unit.
     * 
     * @param unit
     *            Unit for which the XML structure is created.
     * @return XML structure of the according unit.
     */
    private Element getUnitElement(InstanceUnit unit) {
        Element unitElement = new Element("unit");

        unitElement.setAttribute(new Attribute("id", unit.getId()));
        unitElement.setNamespace(instance.getInstanceNamespace());

        Element measureElement = new Element("measure");
        measureElement.setNamespace(instance.getInstanceNamespace());
        String namespacePrefix = (instance.getNamespace(unit.getNamespaceURI()))
                .getPrefix();
        measureElement.setText(namespacePrefix + ":" + unit.getValue());

        unitElement.addContent(measureElement);

        return unitElement;
    }

    /**
     * Creates context structure of a specific context.
     * 
     * @param context
     *            Context for which the XML structure is created.
     * @return XML structure of the according context.
     */
    private Element getContextElement(InstanceContext context) {
        Element contextElement = new Element("context");

        contextElement.setAttribute(new Attribute("id", context.getId()));
        contextElement.setNamespace(instance.getInstanceNamespace());

        /* set <xbrli:entity> */
        Element elementIdentifier = new Element("identifier");
        elementIdentifier.setNamespace(instance.getInstanceNamespace());
        if (context.getIdentifierScheme() != null) {
            elementIdentifier.setAttribute(new Attribute("scheme", context
                    .getIdentifierScheme()));
        }
        if (context.getIdentifier() != null) {
            elementIdentifier.setText(context.getIdentifier());
        }

        Element elementEntity = new Element("entity");
        elementEntity.setNamespace(instance.getInstanceNamespace());
        elementEntity.addContent(elementIdentifier);

        /* set <xbrli:period> */
        Element elementPeriod = new Element("period");
        elementPeriod.setNamespace(instance.getInstanceNamespace());

        Element elementPeriodType = null;

        if (context.getPeriodValue() != null) {
            if (context.getPeriodValue().equals("forever")) {
                elementPeriodType = new Element("forever");
            } else {
                elementPeriodType = new Element("instant");
                elementPeriodType.setText(context.getPeriodValue());
            }
            elementPeriodType.setNamespace(instance.getInstanceNamespace());
            elementPeriod.addContent(elementPeriodType);
        } else if (context.getPeriodStartDate() != null
                && context.getPeriodEndDate() != null) {
            Element elementStartDate = new Element("startDate");
            Element elementEndDate = new Element("endDate");

            elementStartDate.setText(context.getPeriodStartDate());
            elementEndDate.setText(context.getPeriodEndDate());

            elementStartDate.setNamespace(instance.getInstanceNamespace());
            elementEndDate.setNamespace(instance.getInstanceNamespace());

            elementPeriod.addContent(elementStartDate);
            elementPeriod.addContent(elementEndDate);
        }

        /* set <scenario> and <segment> only when they have child elements */
        Element scenarioElement = new Element("scenario");
        scenarioElement.setNamespace(instance.getInstanceNamespace());
        Iterator scenarioElementsIterator = context.getScenarioElements()
                .iterator();
        while (scenarioElementsIterator.hasNext()) {
            Element currElement = (Element) scenarioElementsIterator.next();
            scenarioElement.addContent(currElement);
        }

        Element segmentElement = new Element("segment");
        segmentElement.setNamespace(instance.getInstanceNamespace());
        Iterator segmentElementsIterator = context.getSegmentElements()
                .iterator();
        while (segmentElementsIterator.hasNext()) {
            Element currElement = (Element) segmentElementsIterator.next();
            scenarioElement.addContent(currElement);
        }

        /* now set dimensional information */
        List scenSegElementList = new ArrayList();
        scenSegElementList.add(0, new Integer(GeneralConstants.DIM_SCENARIO));
        scenSegElementList.add(1, new Integer(GeneralConstants.DIM_SEGMENT));

        for (int i = 0; i < scenSegElementList.size(); i++) {
            int currScenSeg = ((Integer) scenSegElementList.get(i)).intValue();
            if (context.getDimensionalInformation(currScenSeg) != null) {
                List allDimensionDomain = context.getDimensionalInformation(
                        currScenSeg).getAllSingleDimensionTypeList();
                Iterator allDimensionDomainIterator = allDimensionDomain
                        .iterator();
                while (allDimensionDomainIterator.hasNext()) {
                    SingleDimensionType currSDT = (SingleDimensionType) allDimensionDomainIterator
                            .next();
                    Concept dimensionElement = currSDT.getDimensionConcept();
                    Element currDimElement = null;
                    if (currSDT.isTypedDimension()) {
                        currDimElement = new Element("typedMember");
                    } else {
                        currDimElement = new Element("explicitMember");
                    }
                    currDimElement.setNamespace(instance
                            .getNamespace(NamespaceConstants.XBRLDI_NAMESPACE
                                    .getURI()));
                    /* Attribute type=xlink:href="..." */
                    Attribute hrefAttribute = new Attribute("dimension",
                            dimensionElement.getNamespace().getPrefix() + ":"
                                    + dimensionElement.getName());
                    currDimElement.setAttribute(hrefAttribute);
                    /* set the value of the element */
                    if (currSDT.isTypedDimension()) {
                        /* the dimension is a typed dimension */
                        if (currSDT.getTypedDimensionElement() != null) {
                            /* now add the correct namespace */
                            Element rootElement = currSDT
                                    .getTypedDimensionElement();
                            /*
                             * if there is a parent element, it must be
                             * detached, otherwise addContent() throws an
                             * Exception
                             */
                            if (rootElement.getParent() != null) {
                                rootElement.detach();
                            }
                            currDimElement.addContent(rootElement);
                        }
                    } else {
                        /* the dimension is an explicit dimension */
                        Concept domainElement = currSDT
                                .getDomainMemberConcept();
                        currDimElement.setText(domainElement.getNamespace()
                                .getPrefix()
                                + ":" + domainElement.getName());
                    }
                    /*
                     * now add this element to scenarioElement or segment
                     * element
                     */
                    if (currScenSeg == GeneralConstants.DIM_SCENARIO)
                        scenarioElement.addContent(currDimElement);
                    else if (currScenSeg == GeneralConstants.DIM_SEGMENT)
                        segmentElement.addContent(currDimElement);
                }
            }
        }

        /* add segment to the entity element (only if there are child elements) */
        if (segmentElement.getChildren().size() > 0) {
            elementEntity.addContent(segmentElement);
        }

        /* now add everything to the context element */
        contextElement.addContent(elementEntity);
        contextElement.addContent(elementPeriod);
        /* <scenario> only if there are child elements */
        if (scenarioElement.getChildren().size() > 0) {
            contextElement.addContent(scenarioElement);
        }

        return contextElement;
    }
}
