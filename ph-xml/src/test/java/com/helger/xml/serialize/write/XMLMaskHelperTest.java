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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for class {@link XMLMaskHelper}.
 *
 * @author Philip Helger
 */
public final class XMLMaskHelperTest
{
  @SuppressWarnings ("removal")
  @Test
  public void testGetMaskedXMLText ()
  {
    // Emit as usual
    assertEquals ("\u0001",
                  new String (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                              EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                              EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                              "\u0001")));
    assertEquals ("&#x1;",
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
    assertArrayEquals (CGlobal.EMPTY_CHAR_ARRAY,
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "\u0001"));
    assertArrayEquals (CGlobal.EMPTY_CHAR_ARRAY,
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "\u0000"));
    assertArrayEquals (CGlobal.EMPTY_CHAR_ARRAY,
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
    assertArrayEquals ("1 &amp; &#x1;".toCharArray (),
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
    assertArrayEquals ("1 &amp; &#x1;".toCharArray (),
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

    // Special test for text
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0000"));
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0000"));
    assertArrayEquals ("1 &amp; ".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "1 & \u0000"));
    assertArrayEquals ("abcdef".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0000cd\u0000ef"));
    assertArrayEquals ("abcdef".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_11,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0000cd\u0000ef"));
    assertArrayEquals ("abcdef".toCharArray (),
                       XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.HTML,
                                                       EXMLCharMode.TEXT,
                                                       EXMLIncorrectCharacterHandling.DO_NOT_WRITE_LOG_WARNING,
                                                       "ab\u0000cd\u0000ef"));
  }

  @SuppressWarnings ("removal")
  @Test
  public void testGetMaskedXMLTextLength ()
  {
    assertEquals (1,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_10,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "\u0001"));
    assertEquals (5,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_11,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "\u0001"));
    assertEquals (2 + 5 + 1 + 1,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_10,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "1 & \u0001"));
    assertEquals (2 + 5 + 1 + 5,
                  XMLMaskHelper.getMaskedXMLTextLength (EXMLSerializeVersion.XML_11,
                                                        EXMLCharMode.ATTRIBUTE_VALUE_DOUBLE_QUOTES,
                                                        EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                        "1 & \u0001"));
  }

  @Test
  public void testMaskXMLTextToStringAndCharArray () throws IOException
  {
    final String [] aTexts = { "a",
                               "abc",
                               "1 & 2 < 3 > 0",
                               "'single' and \"double\"",
                               "line\nbreak\ttab\r",
                               "ab\u0000cd\u0000ef",
                               "\u0000",
                               "\u0001 & \u0002",
                               "\u00e4\u20ac\ud83d\ude00",
                               "\ud800x",
                               "1abc" };
    final EXMLIncorrectCharacterHandling [] aHandlings = { EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG,
                                                           EXMLIncorrectCharacterHandling.DO_NOT_WRITE_NO_LOG,
                                                           EXMLIncorrectCharacterHandling.THROW_EXCEPTION };
    for (final EXMLSerializeVersion eXMLVersion : EXMLSerializeVersion.values ())
      for (final EXMLCharMode eXMLCharMode : EXMLCharMode.values ())
        for (final EXMLIncorrectCharacterHandling eHandling : aHandlings)
          for (final String sText : aTexts)
          {
            final String sMsg = eXMLVersion + "/" + eXMLCharMode + "/" + eHandling + "/" + sText;

            String sStringResult;
            try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
            {
              XMLMaskHelper.maskXMLTextTo (eXMLVersion, eXMLCharMode, eHandling, sText, aSW);
              sStringResult = aSW.getAsString ();
            }
            catch (final IllegalArgumentException ex)
            {
              sStringResult = "Exception: " + ex.getMessage ();
            }

            // Use an offset, to ensure it is considered
            final char [] aChars = ("xx" + sText + "yy").toCharArray ();
            String sCharArrayResult;
            try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
            {
              XMLMaskHelper.maskXMLTextTo (eXMLVersion, eXMLCharMode, eHandling, aChars, 2, sText.length (), aSW);
              sCharArrayResult = aSW.getAsString ();
            }
            catch (final IllegalArgumentException ex)
            {
              sCharArrayResult = "Exception: " + ex.getMessage ();
            }

            assertEquals (sMsg, sCharArrayResult, sStringResult);
            if (!sStringResult.startsWith ("Exception: "))
              assertEquals (sMsg,
                            new String (XMLMaskHelper.getMaskedXMLText (eXMLVersion, eXMLCharMode, eHandling, sText)),
                            sStringResult);
          }
  }
}
