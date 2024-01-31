package xbrlcore.xlink;

/**
 * This class represents a resource within a linkbase. <br/><br/>
 * 
 * @author Daniel Hamm
 */
public class Resource extends ExtendedLinkElement {

    static final long serialVersionUID = 791825586156371335L;

    private String id;

    private String lang;

    private String value;

    /**
     * Constructor.
     * 
     * @param label
     *            Label of this resource.
     */
    public Resource(String label, String linkbaseSource) {
        super(label, linkbaseSource);
        this.id = "";
        this.value = "";
    }

    /**
     * Checks whether two Resource objects are equal. This is true if and only
     * if: <br/>- both have the same label attribute <br/>- both are located
     * within the same extended link role <br/>- both have the same language
     * <br/>- both have the same value
     * 
     * @param obj
     *            Object the current Resource is checked against.
     * @return True if both Resource objects are the same, false otherwise.
     *  
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Resource))
            return false;

        Resource otherResource = (Resource) obj;

        return getLabel().equals(otherResource.getLabel())
                && (getExtendedLinkRole() == null ? otherResource
                        .getExtendedLinkRole() == null : getExtendedLinkRole()
                        .equals(otherResource.getExtendedLinkRole()))
                && (lang == null ? otherResource.getLang() == null : lang
                        .equals(otherResource.getLang()))
                && (value == null ? otherResource.getValue() == null : value
                        .equals(otherResource.getValue()))
                && (getLinkbaseSource() == null ? otherResource
                        .getLinkbaseSource() == null : getLinkbaseSource()
                        .equals(otherResource.getLinkbaseSource()));

    }

    /**
     * @return Returns a hash code for this object.
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + getLabel().hashCode();
        hash = hash
                * 31
                + (getExtendedLinkRole() != null ? getExtendedLinkRole()
                        .hashCode() : 0);
        hash = hash * 31 + (lang != null ? lang.hashCode() : 0);
        hash = hash * 31 + (value != null ? value.hashCode() : 0);
        hash = hash
                * 31
                + (getLinkbaseSource() != null ? getLinkbaseSource().hashCode()
                        : 0);
        return hash;
    }

    /**
     * @return false
     */
    public boolean isLocator() {
        return false;
    }

    /**
     * @return true
     */
    public boolean isResource() {
        return true;
    }

    /**
     * @return id attribute of this resource.
     */
    public String getId() {
        return id;
    }

    /**
     * @return xml:lang attribute of this resource.
     */
    public String getLang() {
        return lang;
    }

    /**
     * @param string
     *            id attribute of this resource.
     */
    public void setId(String string) {
        id = string;
    }

    /**
     * @param string
     *            xml:lang attribute of this resource.
     */
    public void setLang(String string) {
        lang = string;
    }

    /**
     * @return Value of this resource (value of the according XML element).
     */
    public String getValue() {
        return value;
    }

    /**
     * @param string
     *            Value of this resource (value of the according XML element).
     */
    public void setValue(String string) {
        value = string;
    }
}
