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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link XMLCharHelper}.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("removal")
public final class XMLCharHelperTest
{
  @Test
  public void testAttributeValue ()
  {
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_10, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_11, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.HTML, '<'));

    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_10, '\u0001'));
    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.XML_11, '\u007f'));
    assertTrue (XMLCharHelper.isInvalidXMLAttributeValueChar (EXMLSerializeVersion.HTML, '\u007f'));
  }

  @Test
  public void testText ()
  {
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_10, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.XML_11, '<'));
    assertFalse (XMLCharHelper.isInvalidXMLTextChar (EXMLSerializeVersion.HTML, '<'));
  }

  /** All chars of the BMP - that is where all the lookup tables are defined */
  private static final int MAX_CHAR = 0x10000;
  private static final String VALID_NAME = "elementName";
  private static final String INVALID_NAME = "1\u0001element";

  @Test
  public void testIsInvalidCharAllVersions ()
  {
    // Invoke every single char check with the whole BMP, so that all branches
    // of the underlying lookup tables are hit
    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
      for (int c = 0; c < MAX_CHAR; ++c)
      {
        XMLCharHelper.isInvalidXMLNameStartChar (eVersion, c);
        XMLCharHelper.isInvalidXMLNameChar (eVersion, c);
        XMLCharHelper.isInvalidXMLTextChar (eVersion, c);
        XMLCharHelper.isInvalidXMLCDATAChar (eVersion, c);
        XMLCharHelper.isInvalidXMLAttributeValueChar (eVersion, c);
      }
  }

  @Test
  public void testContainsInvalidXMLNameChar ()
  {
    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, (String) null));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, ""));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, (char []) null));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, new char [0]));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, VALID_NAME));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, VALID_NAME.toCharArray ()));
      assertFalse (XMLCharHelper.containsInvalidXMLNameChar (eVersion, VALID_NAME.toCharArray (), 0, 0));

      assertTrue (XMLCharHelper.containsInvalidXMLNameChar (eVersion, INVALID_NAME));
      assertTrue (XMLCharHelper.containsInvalidXMLNameChar (eVersion, INVALID_NAME.toCharArray ()));
      assertTrue (XMLCharHelper.containsInvalidXMLNameChar (eVersion,
                                                            INVALID_NAME.toCharArray (),
                                                            0,
                                                            INVALID_NAME.length ()));
    }
  }

  @Test
  public void testGetAllInvalidXMLNameChars ()
  {
    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      assertNull (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, (String) null));
      assertNull (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, ""));
      assertNull (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, (char []) null));
      assertNull (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, new char [0]));
      assertNull (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, VALID_NAME.toCharArray (), 0, 0));

      assertTrue (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, VALID_NAME).isEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, INVALID_NAME).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLNameChars (eVersion, INVALID_NAME.toCharArray ()).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLNameChars (eVersion,
                                                           INVALID_NAME.toCharArray (),
                                                           0,
                                                           INVALID_NAME.length ()).isNotEmpty ());
    }
  }

  /**
   * @param eVersion
   *        XML version to use
   * @return A string that contains at least one character that is invalid as XML text in the
   *         provided version. Never <code>null</code>.
   */
  private static String _getInvalidTextString (final EXMLSerializeVersion eVersion)
  {
    for (int c = 0; c < MAX_CHAR; ++c)
      if (XMLCharHelper.isInvalidXMLTextChar (eVersion, c))
        return "any " + (char) c + " text";
    throw new IllegalStateException ("No invalid text char found for " + eVersion);
  }

  /**
   * @param eVersion
   *        XML version to use
   * @return A string that contains at least one character that is invalid as an XML attribute value
   *         in the provided version. Never <code>null</code>.
   */
  private static String _getInvalidAttributeValueString (final EXMLSerializeVersion eVersion)
  {
    for (int c = 0; c < MAX_CHAR; ++c)
      if (XMLCharHelper.isInvalidXMLAttributeValueChar (eVersion, c))
        return "any " + (char) c + " value";
    throw new IllegalStateException ("No invalid attribute value char found for " + eVersion);
  }

  @Test
  public void testContainsAndGetAllInvalidTextChars ()
  {
    final String sValid = "any text";

    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      final String sInvalid = _getInvalidTextString (eVersion);

      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, (String) null));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, ""));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, (char []) null));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, new char [0]));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sValid));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sValid.toCharArray ()));
      assertFalse (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sInvalid));
      assertTrue (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sInvalid.toCharArray ()));
      assertTrue (XMLCharHelper.containsInvalidXMLTextChar (eVersion, sInvalid.toCharArray (), 0, sInvalid.length ()));

      assertNull (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, (String) null));
      assertNull (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, (char []) null));
      assertNull (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, new char [0]));
      assertNull (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, sValid).isEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, sInvalid).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, sInvalid.toCharArray ()).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLTextChars (eVersion, sInvalid.toCharArray (), 0, sInvalid.length ())
                               .isNotEmpty ());
    }
  }

  @Test
  public void testContainsAndGetAllInvalidCDATAChars ()
  {
    final String sValid = "any cdata";

    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      String sInvalid = null;
      for (int c = 0; c < MAX_CHAR; ++c)
        if (XMLCharHelper.isInvalidXMLCDATAChar (eVersion, c))
        {
          sInvalid = "any " + (char) c + " cdata";
          break;
        }
      assertNotNull (sInvalid);

      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, (String) null));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, ""));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, (char []) null));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, new char [0]));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sValid));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sValid.toCharArray ()));
      assertFalse (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sInvalid));
      assertTrue (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sInvalid.toCharArray ()));
      assertTrue (XMLCharHelper.containsInvalidXMLCDATAChar (eVersion, sInvalid.toCharArray (), 0, sInvalid.length ()));

      assertNull (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, (String) null));
      assertNull (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, (char []) null));
      assertNull (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, new char [0]));
      assertNull (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, sValid).isEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, sInvalid).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, sInvalid.toCharArray ()).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLCDATAChars (eVersion, sInvalid.toCharArray (), 0, sInvalid.length ())
                               .isNotEmpty ());
    }
  }

  @Test
  public void testContainsAndGetAllInvalidAttributeValueChars ()
  {
    final String sValid = "any value";

    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
    {
      final String sInvalid = _getInvalidAttributeValueString (eVersion);

      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, (String) null));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, ""));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, (char []) null));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, new char [0]));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, sValid));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, sValid.toCharArray ()));
      assertFalse (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, sInvalid));
      assertTrue (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion, sInvalid.toCharArray ()));
      assertTrue (XMLCharHelper.containsInvalidXMLAttributeValueChar (eVersion,
                                                                      sInvalid.toCharArray (),
                                                                      0,
                                                                      sInvalid.length ()));

      assertNull (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, (String) null));
      assertNull (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, (char []) null));
      assertNull (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, new char [0]));
      assertNull (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, sValid.toCharArray (), 0, 0));
      assertTrue (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, sValid).isEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, sInvalid).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion, sInvalid.toCharArray ()).isNotEmpty ());
      assertTrue (XMLCharHelper.getAllInvalidXMLAttributeValueChars (eVersion,
                                                                     sInvalid.toCharArray (),
                                                                     0,
                                                                     sInvalid.length ()).isNotEmpty ());
    }
  }

  @Test
  public void testContainsAndGetAllInvalidXMLCharsPerCharMode ()
  {
    for (final EXMLSerializeVersion eVersion : EXMLSerializeVersion.values ())
      for (final EXMLCharMode eCharMode : EXMLCharMode.values ())
      {
        assertFalse (XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, (String) null));
        assertFalse (XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, ""));
        assertFalse (XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, (char []) null));
        assertFalse (XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, new char [0]));

        assertNull (XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, (String) null));
        assertNull (XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, (char []) null));
        assertNull (XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, new char [0]));

        // Every char mode rejects at least the invalid XML name chars
        assertTrue (XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, INVALID_NAME) ||
                    XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, INVALID_NAME).isEmpty ());
        assertNotNull (XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, INVALID_NAME));
        assertNotNull (XMLCharHelper.getAllInvalidXMLChars (eVersion, eCharMode, INVALID_NAME.toCharArray ()));
        assertNotNull (XMLCharHelper.getAllInvalidXMLChars (eVersion,
                                                            eCharMode,
                                                            INVALID_NAME.toCharArray (),
                                                            0,
                                                            INVALID_NAME.length ()));
        XMLCharHelper.containsInvalidXMLChar (eVersion, eCharMode, INVALID_NAME.toCharArray ());
        XMLCharHelper.containsInvalidXMLChar (eVersion,
                                              eCharMode,
                                              INVALID_NAME.toCharArray (),
                                              0,
                                              INVALID_NAME.length ());
      }
  }
}
