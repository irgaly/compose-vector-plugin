/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/common/src/main/java/com/android/utils/XmlUtils.java
 *
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.irgaly.compose.icons.svg.utls

import io.github.irgaly.compose.icons.svg.android.SdkConstants
import io.github.irgaly.compose.icons.svg.android.SdkConstants.AMP_ENTITY
import io.github.irgaly.compose.icons.svg.android.SdkConstants.ANDROID_NS_NAME
import io.github.irgaly.compose.icons.svg.android.SdkConstants.ANDROID_URI
import io.github.irgaly.compose.icons.svg.android.SdkConstants.APOS_ENTITY
import io.github.irgaly.compose.icons.svg.android.SdkConstants.APP_PREFIX
import io.github.irgaly.compose.icons.svg.android.SdkConstants.GT_ENTITY
import io.github.irgaly.compose.icons.svg.android.SdkConstants.LT_ENTITY
import io.github.irgaly.compose.icons.svg.android.SdkConstants.QUOT_ENTITY
import io.github.irgaly.compose.icons.svg.android.SdkConstants.XMLNS
import io.github.irgaly.compose.icons.svg.android.SdkConstants.XMLNS_PREFIX
import io.github.irgaly.compose.icons.svg.android.SdkConstants.XMLNS_URI
import com.google.common.base.Charsets
import com.google.common.io.Files
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/** XML Utilities  */
internal object XmlUtils {
    const val XML_COMMENT_BEGIN: String = "<!--" //$NON-NLS-1$
    const val XML_COMMENT_END: String = "-->" //$NON-NLS-1$
    const val XML_PROLOG: String = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" //$NON-NLS-1$

    /**
     * Separator for xml namespace and localname
     */
    const val NS_SEPARATOR: Char = ':' //$NON-NLS-1$

    /**
     * Returns the namespace prefix matching the requested namespace URI.
     * If no such declaration is found, returns the default "android" prefix for
     * the Android URI, and "app" for other URI's. By default the app namespace
     * will be created. If this is not desirable, call
     * [.lookupNamespacePrefix] instead.
     *
     * @param node The current node. Must not be null.
     * @param nsUri The namespace URI of which the prefix is to be found,
     * e.g. [SdkConstants.ANDROID_URI]
     * @return The first prefix declared or the default "android" prefix
     * (or "app" for non-Android URIs)
     */
    fun lookupNamespacePrefix(node: Node?, nsUri: String?): String? {
        val defaultPrefix: String = if (ANDROID_URI.equals(nsUri)) ANDROID_NS_NAME else APP_PREFIX
        return lookupNamespacePrefix(node, nsUri, defaultPrefix, true /*create*/)
    }

    /**
     * Returns the namespace prefix matching the requested namespace URI. If no
     * such declaration is found, returns the default "android" prefix for the
     * Android URI, and "app" for other URI's.
     *
     * @param node The current node. Must not be null.
     * @param nsUri The namespace URI of which the prefix is to be found, e.g.
     * [SdkConstants.ANDROID_URI]
     * @param create whether the namespace declaration should be created, if
     * necessary
     * @return The first prefix declared or the default "android" prefix (or
     * "app" for non-Android URIs)
     */
    fun lookupNamespacePrefix(
        node: Node?, nsUri: String?,
        create: Boolean
    ): String? {
        val defaultPrefix: String = if (ANDROID_URI.equals(nsUri)) ANDROID_NS_NAME else APP_PREFIX
        return lookupNamespacePrefix(node, nsUri, defaultPrefix, create)
    }

    /**
     * Returns the namespace prefix matching the requested namespace URI. If no
     * such declaration is found, returns the default "android" prefix.
     *
     * @param node The current node. Must not be null.
     * @param nsUri The namespace URI of which the prefix is to be found, e.g.
     * [SdkConstants.ANDROID_URI]
     * @param defaultPrefix The default prefix (root) to use if the namespace is
     * not found. If null, do not create a new namespace if this URI
     * is not defined for the document.
     * @param create whether the namespace declaration should be created, if
     * necessary
     * @return The first prefix declared or the provided prefix (possibly with a
     * number appended to avoid conflicts with existing prefixes.
     */
    fun lookupNamespacePrefix(
        node: Node?, nsUri: String?, defaultPrefix: String?,
        create: Boolean
    ): String? {
        // Note: Node.lookupPrefix is not implemented in wst/xml/core NodeImpl.java
        // The following code emulates this simple call:
        //   String prefix = node.lookupPrefix(NS_RESOURCES);

        // if the requested URI is null, it denotes an attribute with no namespace.

        var node = node
        if (nsUri == null) {
            return null
        }

        // per XML specification, the "xmlns" URI is reserved
        if (XMLNS_URI.equals(nsUri)) {
            return XMLNS
        }

        val visited = HashSet<String?>()
        val doc = node?.ownerDocument

        // Ask the document about it. This method may not be implemented by the Document.
        var nsPrefix: String? = null
        try {
            nsPrefix = doc?.lookupPrefix(nsUri)
            if (nsPrefix != null) {
                return nsPrefix
            }
        } catch (t: Throwable) {
            // ignore
        }

        // If that failed, try to look it up manually.
        // This also gathers prefixed in use in the case we want to generate a new one below.
        while (node != null && node.nodeType == Node.ELEMENT_NODE
        ) {
            val attrs = node.attributes
            for (n in attrs.length - 1 downTo 0) {
                val attr = attrs.item(n)
                if (XMLNS.equals(attr.prefix)) {
                    val uri = attr.nodeValue
                    nsPrefix = attr.localName
                    // Is this the URI we are looking for? If yes, we found its prefix.
                    if (nsUri == uri) {
                        return nsPrefix
                    }
                    visited.add(nsPrefix)
                }
            }
            node = node.parentNode
        }

        // Failed the find a prefix. Generate a new sensible default prefix, unless
        // defaultPrefix was null in which case the caller does not want the document
        // modified.
        if (defaultPrefix == null) {
            return null
        }

        //
        // We need to make sure the prefix is not one that was declared in the scope
        // visited above. Pick a unique prefix from the provided default prefix.
        var prefix: String = defaultPrefix
        val base = prefix
        var i = 1
        while (visited.contains(prefix)) {
            prefix = base + i.toString()
            i++
        }
        // Also create & define this prefix/URI in the XML document as an attribute in the
        // first element of the document.
        if (doc != null) {
            node = doc.firstChild
            while (node != null && node.nodeType != Node.ELEMENT_NODE) {
                node = node.nextSibling
            }
            if (node != null && create) {
                // This doesn't work:
                //Attr attr = doc.createAttributeNS(XMLNS_URI, prefix);
                //attr.setPrefix(XMLNS);
                //
                // Xerces throws
                //org.w3c.dom.DOMException: NAMESPACE_ERR: An attempt is made to create or
                // change an object in a way which is incorrect with regard to namespaces.
                //
                // Instead pass in the concatenated prefix. (This is covered by
                // the UiElementNodeTest#testCreateNameSpace() test.)
                val attr = doc.createAttributeNS(XMLNS_URI, XMLNS_PREFIX + prefix)
                attr.value = nsUri
                node.attributes.setNamedItemNS(attr)
            }
        }

        return prefix
    }

    /**
     * Converts the given attribute value to an XML-attribute-safe value, meaning that
     * single and double quotes are replaced with their corresponding XML entities.
     *
     * @param attrValue the value to be escaped
     * @return the escaped value
     */
    fun toXmlAttributeValue(attrValue: String): String {
        var i = 0
        val n = attrValue.length
        while (i < n) {
            val c = attrValue[i]
            if (c == '"' || c == '\'' || c == '<' || c == '&') {
                val sb = StringBuilder(2 * attrValue.length)
                appendXmlAttributeValue(sb, attrValue)
                return sb.toString()
            }
            i++
        }

        return attrValue
    }

    /**
     * Converts the given XML-attribute-safe value to a java string
     *
     * @param escapedAttrValue the escaped value
     * @return the unescaped value
     */
    fun fromXmlAttributeValue(escapedAttrValue: String): String {
        var workingString: String = escapedAttrValue.replace(QUOT_ENTITY, "\"")
        workingString = workingString.replace(LT_ENTITY, "<")
        workingString = workingString.replace(APOS_ENTITY, "'")
        workingString = workingString.replace(AMP_ENTITY, "&")
        workingString = workingString.replace(GT_ENTITY, ">")

        return workingString
    }

    /**
     * Converts the given attribute value to an XML-text-safe value, meaning that
     * less than and ampersand characters are escaped.
     *
     * @param textValue the text value to be escaped
     * @return the escaped value
     */
    fun toXmlTextValue(textValue: String): String {
        var i = 0
        val n = textValue.length
        while (i < n) {
            val c = textValue[i]
            if (c == '<' || c == '&') {
                val sb = StringBuilder(2 * textValue.length)
                appendXmlTextValue(sb, textValue)
                return sb.toString()
            }
            i++
        }

        return textValue
    }

    /**
     * Appends text to the given [StringBuilder] and escapes it as required for a
     * DOM attribute node.
     *
     * @param sb the string builder
     * @param attrValue the attribute value to be appended and escaped
     */
    fun appendXmlAttributeValue(
        sb: StringBuilder,
        attrValue: String
    ) {
        val n = attrValue.length
        // &, ", ' and < are illegal in attributes; see http://www.w3.org/TR/REC-xml/#NT-AttValue
        // (' legal in a " string and " is legal in a ' string but here we'll stay on the safe
        // side)
        for (i in 0 until n) {
            val c = attrValue[i]
            if (c == '"') {
                sb.append(QUOT_ENTITY)
            } else if (c == '<') {
                sb.append(LT_ENTITY)
            } else if (c == '\'') {
                sb.append(APOS_ENTITY)
            } else if (c == '&') {
                sb.append(AMP_ENTITY)
            } else {
                sb.append(c)
            }
        }
    }

    /**
     * Appends text to the given [StringBuilder] and escapes it as required for a
     * DOM text node.
     *
     * @param sb the string builder
     * @param textValue the text value to be appended and escaped
     */
    fun appendXmlTextValue(sb: StringBuilder, textValue: String) {
        var i = 0
        val n = textValue.length
        while (i < n) {
            val c = textValue[i]
            if (c == '<') {
                sb.append(LT_ENTITY)
            } else if (c == '&') {
                sb.append(AMP_ENTITY)
            } else {
                sb.append(c)
            }
            i++
        }
    }

    /**
     * Returns true if the given node has one or more element children
     *
     * @param node the node to test for element children
     * @return true if the node has one or more element children
     */
    fun hasElementChildren(node: Node): Boolean {
        val children = node.childNodes
        var i = 0
        val n = children.length
        while (i < n) {
            if (children.item(i).nodeType == Node.ELEMENT_NODE) {
                return true
            }
            i++
        }

        return false
    }

    /**
     * Returns a character reader for the given file, which must be a UTF encoded file.
     *
     *
     * The reader does not need to be closed by the caller (because the file is read in
     * full in one shot and the resulting array is then wrapped in a byte array input stream,
     * which does not need to be closed.)
     */
    @Throws(IOException::class)
    fun getUtfReader(file: File?): Reader {
        val bytes = Files.toByteArray(file)
        val length = bytes.size
        if (length == 0) {
            return StringReader("")
        }

        when (bytes[0]) {
            0xEF.toByte() -> {
                if (length >= 3 && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
                    // UTF-8 BOM: EF BB BF: Skip it
                    return InputStreamReader(
                        ByteArrayInputStream(bytes, 3, length - 3),
                        Charsets.UTF_8
                    )
                }
            }

            0xFE.toByte() -> {
                if (length >= 2
                    && bytes[1] == 0xFF.toByte()
                ) {
                    // UTF-16 Big Endian BOM: FE FF
                    return InputStreamReader(
                        ByteArrayInputStream(bytes, 2, length - 2),
                        Charsets.UTF_16BE
                    )
                }
            }

            0xFF.toByte() -> {
                if (length >= 2
                    && bytes[1] == 0xFE.toByte()
                ) {
                    if (length >= 4 && bytes[2] == 0x00.toByte() && bytes[3] == 0x00.toByte()) {
                        // UTF-32 Little Endian BOM: FF FE 00 00
                        return InputStreamReader(
                            ByteArrayInputStream(
                                bytes, 4,
                                length - 4
                            ), "UTF-32LE"
                        )
                    }

                    // UTF-16 Little Endian BOM: FF FE
                    return InputStreamReader(
                        ByteArrayInputStream(bytes, 2, length - 2),
                        Charsets.UTF_16LE
                    )
                }
            }

            0x00.toByte() -> {
                if (length >= 4 && bytes[0] == 0x00.toByte() && bytes[1] == 0x00.toByte() && bytes[2] == 0xFE.toByte() && bytes[3] == 0xFF.toByte()) {
                    // UTF-32 Big Endian BOM: 00 00 FE FF
                    return InputStreamReader(
                        ByteArrayInputStream(bytes, 4, length - 4),
                        "UTF-32BE"
                    )
                }
            }
        }
        // No byte order mark: Assume UTF-8 (where the BOM is optional).
        return InputStreamReader(ByteArrayInputStream(bytes), Charsets.UTF_8)
    }

    /**
     * Parses the given XML string as a DOM document, using the JDK parser. The parser does not
     * validate, and is optionally namespace aware.
     *
     * @param xml            the XML content to be parsed (must be well formed)
     * @param namespaceAware whether the parser is namespace aware
     * @return the DOM document
     */
    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun parseDocument(xml: String, namespaceAware: Boolean): Document {
        var xml = xml
        xml = stripBom(xml)
        val factory = DocumentBuilderFactory.newInstance()
        val `is` = InputSource(StringReader(xml))
        factory.isNamespaceAware = namespaceAware
        factory.isValidating = false
        val builder = factory.newDocumentBuilder()
        return builder.parse(`is`)
    }

    /**
     * Parses the given UTF file as a DOM document, using the JDK parser. The parser does not
     * validate, and is optionally namespace aware.
     *
     * @param file           the UTF encoded file to parse
     * @param namespaceAware whether the parser is namespace aware
     * @return the DOM document
     */
    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun parseUtfXmlFile(file: File?, namespaceAware: Boolean): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val reader = getUtfReader(file)
        try {
            val `is` = InputSource(reader)
            factory.isNamespaceAware = namespaceAware
            factory.isValidating = false
            val builder = factory.newDocumentBuilder()
            return builder.parse(`is`)
        } finally {
            reader.close()
        }
    }

    /** Strips out a leading UTF byte order mark, if present  */
    fun stripBom(xml: String): String {
        if (!xml.isEmpty() && xml[0] == '\uFEFF') {
            return xml.substring(1)
        }
        return xml
    }

    /**
     * Parses the given XML string as a DOM document, using the JDK parser. The parser does not
     * validate, and is optionally namespace aware. Any parsing errors are silently ignored.
     *
     * @param xml            the XML content to be parsed (must be well formed)
     * @param namespaceAware whether the parser is namespace aware
     * @return the DOM document, or null
     */
    fun parseDocumentSilently(xml: String, namespaceAware: Boolean): Document? {
        try {
            return parseDocument(xml, namespaceAware)
        } catch (e: Exception) {
            // pass
            // This method is deliberately silent; will return null
        }

        return null
    }

    /**
     * Dump an XML tree to string. This does not perform any pretty printing.
     * To perform pretty printing, use `XmlPrettyPrinter.prettyPrint(node)` in
     * `sdk-common`.
     */
    fun toXml(node: Node): String {
        val sb = StringBuilder(1000)
        append(sb, node, 0)
        return sb.toString()
    }

    /** Dump node to string without indentation adjustments  */
    private fun append(
        sb: StringBuilder,
        node: Node,
        indent: Int
    ) {
        val nodeType = node.nodeType
        when (nodeType) {
            Node.DOCUMENT_NODE, Node.DOCUMENT_FRAGMENT_NODE -> {
                sb.append(XML_PROLOG)
                val children = node.childNodes
                var i = 0
                val n = children.length
                while (i < n) {
                    append(sb, children.item(i), indent)
                    i++
                }
            }

            Node.COMMENT_NODE -> {
                sb.append(XML_COMMENT_BEGIN)
                sb.append(node.nodeValue)
                sb.append(XML_COMMENT_END)
            }

            Node.TEXT_NODE -> {
                sb.append(toXmlTextValue(node.nodeValue))
            }

            Node.CDATA_SECTION_NODE -> {
                sb.append("<![CDATA[") //$NON-NLS-1$
                sb.append(node.nodeValue)
                sb.append("]]>") //$NON-NLS-1$
            }

            Node.ELEMENT_NODE -> {
                sb.append('<')
                val element = node as Element
                sb.append(element.tagName)

                val attributes = element.attributes
                val children = element.childNodes
                val childCount = children.length
                val attributeCount = attributes.length

                if (attributeCount > 0) {
                    var i = 0
                    while (i < attributeCount) {
                        val attribute = attributes.item(i)
                        sb.append(' ')
                        sb.append(attribute.nodeName)
                        sb.append('=').append('"')
                        sb.append(toXmlAttributeValue(attribute.nodeValue))
                        sb.append('"')
                        i++
                    }
                }

                if (childCount == 0) {
                    sb.append('/')
                }
                sb.append('>')
                if (childCount > 0) {
                    var i = 0
                    while (i < childCount) {
                        val child = children.item(i)
                        append(sb, child, indent + 1)
                        i++
                    }
                    sb.append('<').append('/')
                    sb.append(element.tagName)
                    sb.append('>')
                }
            }

            else -> throw UnsupportedOperationException(
                "Unsupported node type $nodeType: not yet implemented"
            )
        }
    }

    /**
     * Format the given floating value into an XML string, omitting decimals if
     * 0
     *
     * @param value the value to be formatted
     * @return the corresponding XML string for the value
     */
    fun formatFloatAttribute(value: Double): String {
        return if (value != value.toInt().toDouble()) {
            // Run String.format without a locale, because we don't want locale-specific
            // conversions here like separating the decimal part with a comma instead of a dot!
            String.format(null as Locale?, "%.2f", value) //$NON-NLS-1$
        } else {
            value.toInt().toString()
        }
    }
}
