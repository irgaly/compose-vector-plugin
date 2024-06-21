/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/common/src/main/java/com/android/utils/PositionXmlParser.java
 *
 * Copyright (C) 2011 The Android Open Source Project
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

import io.github.irgaly.compose.icons.svg.android.SdkConstants.UTF_8
import io.github.irgaly.compose.icons.svg.blame.SourcePosition
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.Locator
import org.xml.sax.SAXException
import org.xml.sax.ext.DefaultHandler2
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory
import kotlin.math.min

/**
 * A simple DOM XML parser which can retrieve exact beginning and end offsets
 * (and line and column numbers) for element nodes as well as attribute nodes.
 */
internal object PositionXmlParser {
    private const val UTF_16 = "UTF_16" //$NON-NLS-1$
    private const val UTF_16LE = "UTF_16LE" //$NON-NLS-1$
    private const val CONTENT_KEY = "contents" //$NON-NLS-1$
    private const val POS_KEY = "offsets" //$NON-NLS-1$
    private const val NAMESPACE_PREFIX_FEATURE =
        "http://xml.org/sax/features/namespace-prefixes" //$NON-NLS-1$
    private const val NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces" //$NON-NLS-1$
    private const val PROVIDE_XMLNS_URIS = "http://xml.org/sax/features/xmlns-uris" //$NON-NLS-1$

    /** See http://www.w3.org/TR/REC-xml/#NT-EncodingDecl  */
    private val ENCODING_PATTERN: Pattern =
        Pattern.compile("encoding=['\"](\\S*)['\"]") //$NON-NLS-1$
    private const val LOAD_EXTERNAL_DTD =
        "http://apache.org/xml/features/nonvalidating/load-external-dtd" //$NON-NLS-1$

    /**
     * Parses the XML content from the given input stream.
     *
     * @param input the input stream containing the XML to be parsed
     * @param checkDtd whether or not download the DTD and validate it
     * @return the corresponding document
     * @throws ParserConfigurationException if a SAX parser is not available
     * @throws SAXException if the document contains a parsing error
     * @throws IOException if something is seriously wrong. This should not
     * happen since the input source is known to be constructed from
     * a string.
     */
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun parse(input: InputStream, checkDtd: Boolean): Document {
        // Read in all the data
        val out = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        while (true) {
            val r = input.read(buf)
            if (r == -1) {
                break
            }
            out.write(buf, 0, r)
        }
        input.close()
        return parse(out.toByteArray(), checkDtd)
    }

    /**
     * @see PositionXmlParser.parse
     */
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun parse(input: InputStream): Document {
        return parse(input, true)
    }

    /**
     * @see PositionXmlParser.parse
     */
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun parse(data: ByteArray): Document {
        return parse(data, true)
    }

    /**
     * Parses the XML content from the given byte array
     *
     * @param data the raw XML data (with unknown encoding)
     * @param checkDtd whether or not download the DTD and validate it
     * @return the corresponding document
     * @throws ParserConfigurationException if a SAX parser is not available
     * @throws SAXException if the document contains a parsing error
     * @throws IOException if something is seriously wrong. This should not
     * happen since the input source is known to be constructed from
     * a string.
     */
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun parse(data: ByteArray, checkDtd: Boolean): Document {
        var xml = getXmlString(data)
        xml = XmlUtils.stripBom(xml)
        return parse(xml, InputSource(StringReader(xml)), true, checkDtd)
    }

    /**
     * Parses the given XML content.
     *
     * @param xml the XML string to be parsed. This must be in the correct
     * encoding already.
     * @return the corresponding document
     * @throws ParserConfigurationException if a SAX parser is not available
     * @throws SAXException if the document contains a parsing error
     * @throws IOException if something is seriously wrong. This should not
     * happen since the input source is known to be constructed from
     * a string.
     */
    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun parse(xml: String): Document {
        var xml = xml
        xml = XmlUtils.stripBom(xml)
        return parse(xml, InputSource(StringReader(xml)), true, true)
    }

    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    private fun parse(
        xml: String,
        input: InputSource,
        checkBom: Boolean,
        checkDtd: Boolean
    ): Document {
        var xml = xml
        try {
            val factory = SAXParserFactory.newInstance()
            if (checkDtd) {
                factory.setFeature(NAMESPACE_FEATURE, true)
                factory.setFeature(NAMESPACE_PREFIX_FEATURE, true)
                factory.setFeature(PROVIDE_XMLNS_URIS, true)
            } else {
                factory.setFeature(LOAD_EXTERNAL_DTD, false)
            }
            val parser = factory.newSAXParser()
            val handler = DomBuilder(xml)
            val xmlReader = parser.xmlReader
            xmlReader.setProperty(
                "http://xml.org/sax/properties/lexical-handler",
                handler
            )
            parser.parse(input, handler)
            return handler.document
        } catch (e: SAXException) {
            if (checkBom && e.message!!.contains("Content is not allowed in prolog")) {
                // Byte order mark in the string? Skip it. There are many markers
                // (see http://en.wikipedia.org/wiki/Byte_order_mark) so here we'll
                // just skip those up to the XML prolog beginning character, <
                xml = xml.replaceFirst("^([\\W]+)<".toRegex(), "<") //$NON-NLS-1$ //$NON-NLS-2$
                return parse(xml, InputSource(StringReader(xml)), false, checkDtd)
            }
            throw e
        }
    }

    /**
     * Returns the String corresponding to the given byte array of XML data
     * (with unknown encoding). This method attempts to guess the encoding based
     * on the XML prologue.
     * @param data the XML data to be decoded into a string
     * @return a string corresponding to the XML data
     */
    fun getXmlString(data: ByteArray): String {
        return getXmlString(data, UTF_8)
    }

    /**
     * Returns the String corresponding to the given byte array of XML data
     * (with unknown encoding). This method attempts to guess the encoding based
     * on the XML prologue.
     * @param data the XML data to be decoded into a string
     * @param defaultCharset the default charset to use if not specified by an encoding prologue
     * attribute or a byte order mark
     * @return a string corresponding to the XML data
     */
    fun getXmlString(data: ByteArray, defaultCharset: String?): String {
        var defaultCharset = defaultCharset
        var offset = 0

        var charset: String? = null
        // Look for the byte order mark, to see if we need to remove bytes from
        // the input stream (and to determine whether files are big endian or little endian) etc
        // for files which do not specify the encoding.
        // See http://unicode.org/faq/utf_bom.html#BOM for more.
        if (data.size > 4) {
            if (data[0] == 0xef.toByte() && data[1] == 0xbb.toByte() && data[2] == 0xbf.toByte()) {
                // UTF-8
                charset = UTF_8
                defaultCharset = charset
                offset += 3
            } else if (data[0] == 0xfe.toByte() && data[1] == 0xff.toByte()) {
                //  UTF-16, big-endian
                charset = UTF_16
                defaultCharset = charset
                offset += 2
            } else if (data[0] == 0x0.toByte() && data[1] == 0x0.toByte() && data[2] == 0xfe.toByte() && data[3] == 0xff.toByte()) {
                // UTF-32, big-endian
                charset = "UTF_32"
                defaultCharset = charset //$NON-NLS-1$
                offset += 4
            } else if (data[0] == 0xff.toByte() && data[1] == 0xfe.toByte() && data[2] == 0x0.toByte() && data[3] == 0x0.toByte()) {
                // UTF-32, little-endian. We must check for this *before* looking for
                // UTF_16LE since UTF_32LE has the same prefix!
                charset = "UTF_32LE"
                defaultCharset = charset //$NON-NLS-1$
                offset += 4
            } else if (data[0] == 0xff.toByte() && data[1] == 0xfe.toByte()) {
                //  UTF-16, little-endian
                charset = UTF_16LE
                defaultCharset = charset
                offset += 2
            }
        }
        val length = data.size - offset

        // Guess encoding by searching for an encoding= entry in the first line.
        // The prologue, and the encoding names, will always be in ASCII - which means
        // we don't need to worry about strange character encodings for the prologue characters.
        // However, one wrinkle is that the whole file may be encoded in something like UTF-16
        // where there are two bytes per character, so we can't just look for
        //  ['e','n','c','o','d','i','n','g'] etc in the byte array since there could be
        // multiple bytes for each character. However, since again the prologue is in ASCII,
        // we can just drop the zeroes.
        var seenOddZero = false
        var seenEvenZero = false
        var prologueStart = -1
        for (lineEnd in offset until data.size) {
            if (data[lineEnd].toInt() == 0) {
                if ((lineEnd - offset) % 2 == 0) {
                    seenEvenZero = true
                } else {
                    seenOddZero = true
                }
            } else if (data[lineEnd] == '\n'.code.toByte() || data[lineEnd] == '\r'.code.toByte()) {
                break
            } else if (data[lineEnd] == '<'.code.toByte()) {
                prologueStart = lineEnd
            } else if (data[lineEnd] == '>'.code.toByte()) {
                // End of prologue. Quick check to see if this is a utf-8 file since that's
                // common
                for (i in lineEnd - 4 downTo 0) {
                    if ((data[i] == 'u'.code.toByte() || data[i] == 'U'.code.toByte())
                        && (data[i + 1] == 't'.code.toByte() || data[i + 1] == 'T'.code.toByte())
                        && (data[i + 2] == 'f'.code.toByte() || data[i + 2] == 'F'.code.toByte())
                        && (data[i + 3] == '-'.code.toByte() || data[i + 3] == '_'.code.toByte())
                        && (data[i + 4] == '8'.code.toByte())
                    ) {
                        charset = UTF_8
                        break
                    }
                }

                if (charset == null) {
                    val sb = StringBuilder()
                    for (i in prologueStart..lineEnd) {
                        if (data[i].toInt() != 0) {
                            sb.append(Char(data[i].toUShort()))
                        }
                    }
                    val prologue = sb.toString()
                    val encodingIndex = prologue.indexOf("encoding") //$NON-NLS-1$
                    if (encodingIndex != -1) {
                        val matcher = ENCODING_PATTERN.matcher(prologue)
                        if (matcher.find(encodingIndex)) {
                            charset = matcher.group(1)
                        }
                    }
                }

                break
            }
        }

        // No prologue on the first line, and no byte order mark: Assume UTF-8/16
        if (charset == null) {
            charset = if (seenOddZero) UTF_16LE else if (seenEvenZero) UTF_16 else defaultCharset
        }

        var xml: String? = null
        try {
            xml = String(data, offset, length, charset(charset!!))
        } catch (e: UnsupportedEncodingException) {
            try {
                if (charset !== defaultCharset) {
                    xml = String(
                        data, offset, length, charset(
                            defaultCharset!!
                        )
                    )
                }
            } catch (u: UnsupportedEncodingException) {
                // Just use the default encoding below
            }
        }
        if (xml == null) {
            xml = String(data, offset, length)
        }
        return xml
    }

    /**
     * Returns the position for the given node. This is the start position. The
     * end position can be obtained via [Position.getEnd].
     *
     * @param node the node to look up position for
     * @return the position, or null if the node type is not supported for
     * position info
     */
    fun getPosition(node: Node): SourcePosition {
        return getPosition(node, -1, -1)
    }

    /**
     * Returns the position for the given node. This is the start position. The
     * end position can be obtained via [Position.getEnd]. A specific
     * range within the node can be specified with the `start` and
     * `end` parameters.
     *
     * @param node the node to look up position for
     * @param start the relative offset within the node range to use as the
     * starting position, inclusive, or -1 to not limit the range
     * @param end the relative offset within the node range to use as the ending
     * position, or -1 to not limit the range
     * @return the position, or null if the node type is not supported for
     * position info
     */
    fun getPosition(node: Node, start: Int, end: Int): SourcePosition {
        val p = getPositionHelper(node, start, end)
        return p?.toSourcePosition() ?: SourcePosition.UNKNOWN
    }

    private fun getPositionHelper(node: Node, start: Int, end: Int): Position? {
        // Look up the position information stored while parsing for the given node.
        // Note however that we only store position information for elements (because
        // there is no SAX callback for individual attributes).
        // Therefore, this method special cases this:
        //  -- First, it looks at the owner element and uses its position
        //     information as a first approximation.
        //  -- Second, it uses that, as well as the original XML text, to search
        //     within the node range for an exact text match on the attribute name
        //     and if found uses that as the exact node offsets instead.
        if (node is Attr) {
            val attr = node
            val pos = attr.ownerElement.getUserData(POS_KEY) as Position
            if (pos != null) {
                var startOffset: Int = pos.offset
                var endOffset: Int = pos.end!!.offset
                if (start != -1) {
                    startOffset += start
                    if (end != -1) {
                        endOffset = startOffset + (end - start)
                    }
                }

                // Find attribute in the text
                val contents = node.getOwnerDocument()
                    .getUserData(CONTENT_KEY) as String
                    ?: return null

                // Locate the name=value attribute in the source text
                // Fast string check first for the common occurrence
                val name = attr.name
                val pattern = Pattern.compile(
                    if (attr.prefix != null
                    ) String.format(
                        "(%1\$s\\s*=\\s*[\"'].*?[\"'])",
                        name
                    ) else String.format("[^:](%1\$s\\s*=\\s*[\"'].*?[\"'])", name)
                ) //$NON-NLS-1$
                val matcher = pattern.matcher(contents)
                if (matcher.find(startOffset) && matcher.start(1) <= endOffset) {
                    val index = matcher.start(1)
                    // Adjust the line and column to this new offset
                    var line: Int = pos.line
                    var column: Int = pos.column
                    for (offset in pos.offset until index) {
                        val t = contents[offset]
                        if (t == '\n') {
                            line++
                            column = 0
                        } else {
                            column++
                        }
                    }

                    val attributePosition = Position(line, column, index)
                    // Also set end range for retrieval in getLocation
                    attributePosition.end = Position(line, column + matcher.end(1) - index, matcher.end(1))
                    return attributePosition
                } else {
                    // No regexp match either: just fall back to element position
                    return pos
                }
            }
        } else if (node is Text) {
            // Position of parent element, if any
            var pos: Position? = null
            if (node.getPreviousSibling() != null) {
                pos = node.getPreviousSibling().getUserData(POS_KEY) as Position
            }
            if (pos == null) {
                pos = node.getParentNode().getUserData(POS_KEY) as Position
            }
            if (pos != null) {
                // Attempt to point forward to the actual text node
                val startOffset: Int = pos.offset
                val endOffset: Int = pos.end!!.offset
                var line: Int = pos.line
                var column: Int = pos.column

                // Find attribute in the text
                val contents = node.getOwnerDocument().getUserData(CONTENT_KEY) as String
                if (contents == null || contents.length < endOffset) {
                    return null
                }

                var inAttribute = false
                var offset = startOffset
                while (offset <= endOffset) {
                    val c = contents[offset]
                    if (c == '>' && !inAttribute) {
                        // Found the end of the element open tag: this is where the
                        // text begins.

                        // Skip >

                        offset++
                        column++

                        val text = node.getNodeValue()
                        var textIndex = 0
                        var textLength = text.length
                        var newLine = line
                        var newColumn = column
                        if (start != -1) {
                            textLength = min(textLength.toDouble(), start.toDouble()).toInt()
                            while (textIndex < textLength) {
                                val t = text[textIndex]
                                if (t == '\n') {
                                    newLine++
                                    newColumn = 0
                                } else {
                                    newColumn++
                                }
                                textIndex++
                            }
                        } else {
                            // Skip text whitespace prefix, if the text node contains
                            // non-whitespace characters
                            while (textIndex < textLength) {
                                val t = text[textIndex]
                                if (t == '\n') {
                                    newLine++
                                    newColumn = 0
                                } else if (!Character.isWhitespace(t)) {
                                    break
                                } else {
                                    newColumn++
                                }
                                textIndex++
                            }
                        }
                        if (textIndex == text.length) {
                            textIndex = 0 // Whitespace node
                        } else {
                            line = newLine
                            column = newColumn
                        }

                        val attributePosition = Position(line, column, offset + textIndex)
                        // Also set end range for retrieval in getLocation
                        if (end != -1) {
                            attributePosition.end = Position(line, column, offset + end)
                        } else {
                            attributePosition.end = Position(line, column, offset + textLength)
                        }
                        return attributePosition
                    } else if (c == '"') {
                        inAttribute = !inAttribute
                    } else if (c == '\n') {
                        line++
                        column = -1 // pre-subtract column added below
                    }
                    column++
                    offset++
                }

                return pos
            }
        }

        return node.getUserData(POS_KEY) as Position
    }

    /**
     * SAX parser handler which incrementally builds up a DOM document as we go
     * along, and updates position information along the way. Position
     * information is attached to the DOM nodes by setting user data with the
     * [.POS_KEY] key.
     */
    private class DomBuilder(private val mXml: String) : DefaultHandler2() {
        /** Returns the document parsed by the handler  */
        val document: Document
        private var mLocator: Locator? = null
        private var mCurrentLine = 0
        private var mCurrentOffset = 0
        private var mCurrentColumn = 0
        private val mStack: MutableList<Element> = ArrayList()
        private val mPendingText = StringBuilder()

        init {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            factory.isValidating = false
            val docBuilder = factory.newDocumentBuilder()
            document = docBuilder.newDocument()
            document.setUserData(CONTENT_KEY, mXml, null)
        }

        override fun setDocumentLocator(locator: Locator) {
            this.mLocator = locator
        }

        @Throws(SAXException::class)
        override fun startElement(
            uri: String, localName: String, qName: String,
            attributes: Attributes
        ) {
            try {
                flushText()
                val element = document.createElementNS(uri, qName)
                for (i in 0 until attributes.length) {
                    if (attributes.getURI(i) != null && !attributes.getURI(i).isEmpty()) {
                        val attr = document.createAttributeNS(
                            attributes.getURI(i),
                            attributes.getQName(i)
                        )
                        attr.value = attributes.getValue(i)
                        element.setAttributeNodeNS(attr)
                        assert(attr.ownerElement === element)
                    } else {
                        val attr = document.createAttribute(attributes.getQName(i))
                        attr.value = attributes.getValue(i)
                        element.setAttributeNode(attr)
                        assert(attr.ownerElement === element)
                    }
                }

                val pos = currentPosition

                // The starting position reported to us by SAX is really the END of the
                // open tag in an element, when all the attributes have been processed.
                // We have to scan backwards to find the real beginning. We'll do that
                // by scanning backwards.
                // -1: Make sure that when we have <foo></foo> we don't consider </foo>
                // the beginning since pos.offset will typically point to the first character
                // AFTER the element open tag, which could be a closing tag or a child open
                // tag
                element.setUserData(POS_KEY, findOpeningTag(pos), null)
                mStack.add(element)
            } catch (t: Exception) {
                throw SAXException(t)
            }
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            flushText()
            val element = mStack.removeAt(mStack.size - 1)

            val pos = checkNotNull(element.getUserData(POS_KEY) as Position)
            pos.end = currentPosition

            addNodeToParent(element)
        }

        @Throws(SAXException::class)
        override fun comment(chars: CharArray, start: Int, length: Int) {
            flushText()
            val comment = String(chars, start, length)
            val domComment = document.createComment(comment)

            // current position is the closing comment tag.
            val currentPosition = currentPosition
            val startPosition = findOpeningTag(currentPosition)
            startPosition.end = currentPosition

            domComment.setUserData(POS_KEY, startPosition, null)
            addNodeToParent(domComment)
        }

        /**
         * Adds a node to the current parent element being visited, or to the document if there is
         * no parent in context.
         * @param nodeToAdd xml node to add.
         */
        private fun addNodeToParent(nodeToAdd: Node) {
            if (mStack.isEmpty()) {
                document.appendChild(nodeToAdd)
            } else {
                val parent = mStack[mStack.size - 1]
                parent.appendChild(nodeToAdd)
            }
        }

        /**
         * Find opening tags from the current position.
         * < cannot appear in attribute values or anywhere else within
         * an element open tag, so we know the first occurrence is the real
         * element start
         * For comments, it is not legal to put < in a comment, however we are not
         * validating so we will return an invalid column in that case.
         * @param startingPosition the position to walk backwards until < is reached.
         * @return the opening tag position or startPosition if cannot be found.
         */
        private fun findOpeningTag(startingPosition: Position): Position {
            for (offset in startingPosition.offset - 1 downTo 0) {
                val c = mXml[offset]

                if (c == '<') {
                    // Adjust line position
                    var line: Int = startingPosition.line
                    run {
                        var i: Int = offset
                        val n: Int = startingPosition.offset
                        while (i < n) {
                            if (mXml[i] == '\n') {
                                line--
                            }
                            i++
                        }
                    }

                    // Compute new column position
                    var column = 0
                    var i: Int = offset - 1
                    while (i >= 0) {
                        if (mXml[i] == '\n') {
                            break
                        }
                        i--
                        column++
                    }

                    return Position(line, column, offset)
                }
            }
            // we did not find it, approximate.
            return startingPosition
        }

        private val currentPosition: Position
            /**
             * Returns a position holder for the current position. The most
             * important part of this function is to incrementally compute the
             * offset as well, by counting forwards until it reaches the new line
             * number and column position of the XML parser, counting characters as
             * it goes along.
             */
            get() {
                val line = mLocator!!.lineNumber - 1
                val column = mLocator!!.columnNumber - 1

                // Compute offset incrementally now that we have the new line and column
                // numbers
                val xmlLength = mXml.length
                while (mCurrentLine < line && mCurrentOffset < xmlLength) {
                    val c = mXml[mCurrentOffset]
                    if (c == '\r' && mCurrentOffset < xmlLength - 1) {
                        if (mXml[mCurrentOffset + 1] != '\n') {
                            mCurrentLine++
                            mCurrentColumn = 0
                        }
                    } else if (c == '\n') {
                        mCurrentLine++
                        mCurrentColumn = 0
                    } else {
                        mCurrentColumn++
                    }
                    mCurrentOffset++
                }

                mCurrentOffset += column - mCurrentColumn
                if (mCurrentOffset >= xmlLength) {
                    // The parser sometimes passes wrong column numbers at the
                    // end of the file: Ensure that the offset remains valid.
                    mCurrentOffset = xmlLength
                }
                mCurrentColumn = column

                return Position(mCurrentLine, mCurrentColumn, mCurrentOffset)
            }

        @Throws(SAXException::class)
        override fun characters(c: CharArray, start: Int, length: Int) {
            mPendingText.append(c, start, length)
        }

        private fun flushText() {
            if (mPendingText.length > 0 && !mStack.isEmpty()) {
                val element = mStack[mStack.size - 1]
                val textNode: Node = document.createTextNode(mPendingText.toString())
                element.appendChild(textNode)
                mPendingText.setLength(0)
            }
        }
    }

    private class Position
    /**
     * Creates a new [Position]
     *
     * @param line the 0-based line number, or -1 if unknown
     * @param column the 0-based column number, or -1 if unknown
     * @param offset the offset, or -1 if unknown
     */(
        /** The line number (0-based where the first line is line 0)  */
        val line: Int, val column: Int, val offset: Int
    ) {
        var end: Position? = null

        fun toSourcePosition(): SourcePosition {
            var endLine = line
            var endColumn = column
            var endOffset = offset

            if (end != null) {
                endLine = end!!.line
                endColumn = end!!.column
                endOffset = end!!.offset
            }

            return SourcePosition(line, column, offset, endLine, endColumn, endOffset)
        }
    }
}
