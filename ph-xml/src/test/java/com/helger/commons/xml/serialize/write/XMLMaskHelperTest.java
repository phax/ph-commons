/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link XMLMaskHelper}.
 *
 * @author Philip Helger
 */
public final class XMLMaskHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testGetMaskedXMLText ()
  {
    // Emit as usual
    assertEquals ("\u0001",
                  new String (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                              EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                              EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                              "\u0001")));
    assertEquals ("&#1;",
                  new String (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                              EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                              EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                              "\u0001")));
    assertEquals ("\u0001",
                  new String (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                              EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                              EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                              "\u0001")));

    // Replace with ""
    assertArrayEquals (ArrayHelper.EMPTY_CHAR_ARRAY,
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "\u0001"));
    assertArrayEquals (ArrayHelper.EMPTY_CHAR_ARRAY,
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "\u0000"));
    assertArrayEquals (ArrayHelper.EMPTY_CHAR_ARRAY,
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "\u0000"));

    // Invalid char inbetween
    assertArrayEquals ("abc".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0001c"));
    assertArrayEquals ("abc".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0000c"));
    assertArrayEquals ("abc".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0000c"));

    // Throw exception
    try
    {
      XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                      EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                      EXMLIncorrectCharacterHandling.THROW_EXCEPTION,
                                      "\u0001");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                      EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                      EXMLIncorrectCharacterHandling.THROW_EXCEPTION,
                                      "\u0000");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                      EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                      EXMLIncorrectCharacterHandling.THROW_EXCEPTION,
                                      "\u0000");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Emit as usual
    assertArrayEquals ("1 &amp; \u0001".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                       "1 & \u0001"));
    assertArrayEquals ("1 &amp; &#1;".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                       "1 & \u0001"));
    assertArrayEquals ("1 &amp; \u0001".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                       "1 & \u0001"));

    // Emit as usual
    assertArrayEquals ("1 &amp; \u0001".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_LOG_WARNING,
                                                       "1 & \u0001"));
    assertArrayEquals ("1 &amp; &#1;".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_LOG_WARNING,
                                                       "1 & \u0001"));
    assertArrayEquals ("1 &amp; \u0001".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.WRITE_TO_FILE_LOG_WARNING,
                                                       "1 & \u0001"));

    // Emit as usual
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0001"));
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0000"));
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0001"));

    // Special chars
    assertArrayEquals ("ab&lt;cd>ef&amp;gh&quot;ij".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab<cd>ef&gh\"ij"));
    assertArrayEquals ("ab&lt;cd>ef&amp;gh&quot;ij".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab<cd>ef&gh\"ij"));
    assertArrayEquals ("ab&lt;cd&gt;ef&amp;gh&quot;ij".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab<cd>ef&gh\"ij"));

    // Different quotes - double quotes
    assertArrayEquals ("'ab'&quot;cd&quot;".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));
    assertArrayEquals ("'ab'&quot;cd&quot;".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));
    assertArrayEquals ("&#39;ab&#39;&quot;cd&quot;".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));

    // Different quotes - single quotes
    assertArrayEquals ("&apos;ab&apos;\"cd\"".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));
    assertArrayEquals ("&apos;ab&apos;\"cd\"".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));
    assertArrayEquals ("&#39;ab&#39;\"cd\"".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_SINGLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "'ab'\"cd\""));
  }

  @Test
  public void testGetMaskedXMLTextLength ()
  {
    assertEquals (1,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_10,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "\u0001"));
    assertEquals (4,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_11,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "\u0001"));
    assertEquals (2 +
                  5 +
                  1 +
                  1,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_10,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "1 & \u0001"));
    assertEquals (2 +
                  5 +
                  1 +
                  4,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_11,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "1 & \u0001"));
  }
}
