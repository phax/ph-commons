/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link XMLMaskHelper}. The JDK XML parser serves as the independent
 * reference: masked text must be read back unchanged.
 *
 * @author Philip Helger
 */
public final class XMLMaskHelperPropertyTest
{
  /** Characters with a special meaning in XML, line ends, tabs, non-ASCII and letters */
  private static final String SPECIAL_CHARS = "<>&\"' \t\n\r]\u00e4\u20ac\u0085\u2028ab";

  private static boolean _isValidXml10Char (final int nCodePoint)
  {
    return nCodePoint == 0x9 ||
           nCodePoint == 0xa ||
           nCodePoint == 0xd ||
           (nCodePoint >= 0x20 && nCodePoint <= 0xd7ff) ||
           (nCodePoint >= 0xe000 && nCodePoint <= 0xfffd) ||
           (nCodePoint >= 0x10000 && nCodePoint <= 0x10ffff);
  }

  private static boolean _isValidXml10 (final String sText)
  {
    return sText.codePoints ().allMatch (XMLMaskHelperPropertyTest::_isValidXml10Char);
  }

  /** Text consisting of valid XML 1.0 characters only */
  private static final IGenerator <String> VALID_TEXT = Generators.oneOf (Generators.strings (SPECIAL_CHARS, 0, 30),
                                                                          Generators.unicodeStrings (0, 30)
                                                                                    .filter (XMLMaskHelperPropertyTest::_isValidXml10));

  /** Any text, including characters that are invalid in XML */
  private static final IGenerator <String> ANY_TEXT = Generators.oneOf (VALID_TEXT,
                                                                        Generators.strings ('\u0000', '\u00ff', 0, 30),
                                                                        Generators.unicodeStrings (0, 30));

  private static final IGenerator <EXMLSerializeVersion> VERSIONS = Generators.of (EXMLSerializeVersion.values ());
  private static final IGenerator <EXMLCharMode> MODES = Generators.of (EXMLCharMode.values ());
  private static final IGenerator <EXMLIncorrectCharacterHandling> NON_THROWING_HANDLINGS = Generators.of (EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                                                                           EXMLIncorrectCharacterHandling.DO_NOT_WRITE_NO_LOG);

  /**
   * The array variant, the Writer variant (with its optimised plain ASCII prefix) and the length
   * calculation must agree for all versions, modes and any text.
   */
  @Test
  public void testVariantsAreConsistent ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (VERSIONS, MODES, NON_THROWING_HANDLINGS, ANY_TEXT, (eVersion, eMode, eHandling, sText) -> {
                            final String sMasked = new String (XMLMaskHelper.getMaskedXMLText (eVersion,
                                                                                               eMode,
                                                                                               eHandling,
                                                                                               sText));
                            final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
                            XMLMaskHelper.maskXMLTextTo (eVersion, eMode, eHandling, sText, aSW);
                            assertEquals (sMasked, aSW.getAsString ());
                            assertEquals (sMasked.length (),
                                          XMLMaskHelper.getMaskedXMLTextLength (eVersion, eMode, eHandling, sText));
                          });
  }

  private static Element _parse (final String sXml) throws Exception
  {
    final DocumentBuilderFactory aDBF = DocumentBuilderFactory.newInstance ();
    aDBF.setNamespaceAware (true);
    return aDBF.newDocumentBuilder ().parse (new InputSource (new NonBlockingStringReader (sXml))).getDocumentElement ();
  }

  private static String _mask (final EXMLSerializeVersion eVersion, final EXMLCharMode eMode, final String sText)
  {
    return new String (XMLMaskHelper.getMaskedXMLText (eVersion,
                                                       eMode,
                                                       EXMLIncorrectCharacterHandling.THROW_EXCEPTION,
                                                       sText));
  }

  /**
   * Masked text and attribute values must be read back unchanged by the JDK parser - including line
   * ends and tabs, which the parser normalises unless they are masked. In XML 1.1 this includes the
   * line ends U+0085 and U+2028 and the restricted characters.
   */
  @Test
  @SuppressWarnings ("removal")
  public void testReadBackByParser ()
  {
    new PropertyChecker ().forAll (Generators.of (EXMLSerializeVersion.XML_10, EXMLSerializeVersion.XML_11),
                                   VALID_TEXT,
                                   (eVersion, sText) -> {
                                     final String sDecl = eVersion == EXMLSerializeVersion.XML_11 ? "1.1" : "1.0";
                                     final String sXml = "<?xml version=\"" +
                                                         sDecl +
                                                         "\"?><r dq=\"" +
                                                         _mask (eVersion, EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES, sText) +
                                                         "\" sq='" +
                                                         _mask (eVersion, EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES, sText) +
                                                         "'>" +
                                                         _mask (eVersion, EXMLCharMode.TEXT, sText) +
                                                         "</r>";
                                     final Element aRoot = _parse (sXml);
                                     assertEquals (sXml, sText, aRoot.getTextContent ());
                                     assertEquals (sXml, sText, aRoot.getAttribute ("dq"));
                                     assertEquals (sXml, sText, aRoot.getAttribute ("sq"));
                                   });
  }
}
