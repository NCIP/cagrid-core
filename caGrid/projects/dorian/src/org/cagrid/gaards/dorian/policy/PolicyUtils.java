package org.cagrid.gaards.dorian.policy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class PolicyUtils {

    private static ParserPool parserPool = new ParserPool();


    public static ParserPool getParserPool() {
        return parserPool;
    }


    public static HostAgreement domToHostAgreement(Element dom) throws Exception {
        HostAgreement ha = new HostAgreement(dom);
        return ha;
    }


    public static HostAgreement stringToHostAgreement(String str) throws Exception {
        HostAgreement ha = new HostAgreement(new ByteArrayInputStream(str.getBytes()));
        return ha;
    }


    public static String hostAgreementToString(HostAgreement ha) throws Exception {
        String xml = ha.toString();
        return xml;
    }


    public static Element hostAgreementToDOM(HostAgreement ha) throws Exception {
        return (Element) ha.toDOM();
    }


    public static Element streamToElement(InputStream in) throws Exception {
        Document doc = PolicyUtils.getParserPool().parse(in);
        return doc.getDocumentElement();
    }


    /**
     * A "safe" null/empty check for strings.
     * 
     * @param s
     *            The string to check
     * @return true iff the string is null or length zero
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }


    /**
     * A "safe" assignment function for strings that blocks the empty string
     * 
     * @param s
     *            The string to check
     * @return s iff the string is non-empty or else null
     */
    public static String assign(String s) {
        return (s != null && s.length() > 0) ? s.trim() : null;
    }


    /**
     * Compares two strings for equality, allowing for nulls
     * 
     * @param s1
     *            The first operand
     * @param s2
     *            The second operand
     * @return true iff both are null or both are non-null and the same strng
     *         value
     */
    public static boolean safeCompare(String s1, String s2) {
        if (s1 == null || s2 == null)
            return s1 == s2;
        else
            return s1.equals(s2);
    }


    /**
     * Shortcut for checking a DOM element node's namespace and local name
     * 
     * @param e
     *            An element to compare against
     * @param ns
     *            An XML namespace to compare
     * @param localName
     *            A local name to compare
     * @return true iff the element's local name and namespace match the
     *         parameters
     */
    public static boolean isElementNamed(Element e, String ns, String localName) {
        return (e != null && safeCompare(ns, e.getNamespaceURI()) && safeCompare(localName, e.getLocalName()));
    }


    /**
     * Gets the first child Element of the node, skipping any Text nodes such as
     * whitespace.
     * 
     * @param n
     *            The parent in which to search for children
     * @return The first child Element of n, or null if none
     */
    public static Element getFirstChildElement(Node n) {
        Node child = n.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE)
            child = child.getNextSibling();
        if (child != null)
            return (Element) child;
        else
            return null;
    }


    /**
     * Gets the last child Element of the node, skipping any Text nodes such as
     * whitespace.
     * 
     * @param n
     *            The parent in which to search for children
     * @return The last child Element of n, or null if none
     */
    public static Element getLastChildElement(Node n) {
        Node child = n.getLastChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE)
            child = child.getPreviousSibling();
        if (child != null)
            return (Element) child;
        else
            return null;
    }


    /**
     * Gets the first child Element of the node of the given name, skipping any
     * Text nodes such as whitespace.
     * 
     * @param n
     *            The parent in which to search for children
     * @param ns
     *            The namespace URI of the element to locate
     * @param localName
     *            The local name of the element to locate
     * @return The first child Element of n with the specified name, or null if
     *         none
     */
    public static Element getFirstChildElement(Node n, String ns, String localName) {
        Element e = getFirstChildElement(n);
        while (e != null && !isElementNamed(e, ns, localName))
            e = getNextSiblingElement(e);
        return e;
    }


    /**
     * Gets the last child Element of the node of the given name, skipping any
     * Text nodes such as whitespace.
     * 
     * @param n
     *            The parent in which to search for children
     * @param ns
     *            The namespace URI of the element to locate
     * @param localName
     *            The local name of the element to locate
     * @return The last child Element of n with the specified name, or null if
     *         none
     */
    public static Element getLastChildElement(Node n, String ns, String localName) {
        Element e = getLastChildElement(n);
        while (e != null && !isElementNamed(e, ns, localName))
            e = getPreviousSiblingElement(e);
        return e;
    }


    /**
     * Gets the next sibling Element of the node, skipping any Text nodes such
     * as whitespace.
     * 
     * @param n
     *            The sibling to start with
     * @return The next sibling Element of n, or null if none
     */
    public static Element getNextSiblingElement(Node n) {
        Node sib = n.getNextSibling();
        while (sib != null && sib.getNodeType() != Node.ELEMENT_NODE)
            sib = sib.getNextSibling();
        if (sib != null)
            return (Element) sib;
        else
            return null;
    }


    /**
     * Gets the previous sibling Element of the node, skipping any Text nodes
     * such as whitespace.
     * 
     * @param n
     *            The sibling to start with
     * @return The previous sibling Element of n, or null if none
     */
    public static Element getPreviousSiblingElement(Node n) {
        Node sib = n.getPreviousSibling();
        while (sib != null && sib.getNodeType() != Node.ELEMENT_NODE)
            sib = sib.getPreviousSibling();
        if (sib != null)
            return (Element) sib;
        else
            return null;
    }


    /**
     * Gets the next sibling Element of the node of the given name, skipping any
     * Text nodes such as whitespace.
     * 
     * @param n
     *            The sibling to start with
     * @param ns
     *            The namespace URI of the element to locate
     * @param localName
     *            The local name of the element to locate
     * @return The next sibling Element of n with the specified name, or null if
     *         none
     */
    public static Element getNextSiblingElement(Node n, String ns, String localName) {
        Element e = getNextSiblingElement(n);
        while (e != null && !isElementNamed(e, ns, localName))
            e = getNextSiblingElement(e);
        return e;
    }


    /**
     * Gets the previous sibling Element of the node of the given name, skipping
     * any Text nodes such as whitespace.
     * 
     * @param n
     *            The sibling to start with
     * @param ns
     *            The namespace URI of the element to locate
     * @param localName
     *            The local name of the element to locate
     * @return The previous sibling Element of n with the specified name, or
     *         null if none
     */
    public static Element getPreviousSiblingElement(Node n, String ns, String localName) {
        Element e = getPreviousSiblingElement(n);
        while (e != null && !isElementNamed(e, ns, localName))
            e = getPreviousSiblingElement(e);
        return e;
    }

}
