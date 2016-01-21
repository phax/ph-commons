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
package com.helger.commons.microdom.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.math.MathHelper;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.state.EEnabled;
import com.helger.commons.state.EFinish;
import com.helger.commons.state.EInterrupt;
import com.helger.commons.state.ELeftRight;
import com.helger.commons.state.EMandatory;
import com.helger.commons.state.ESuccess;
import com.helger.commons.state.ETopBottom;
import com.helger.commons.state.ETriState;
import com.helger.commons.state.EValidity;
import com.helger.commons.system.ENewLineMode;

/**
 * Test class for class {@link BasicMicroTypeConverterRegistrar}.
 *
 * @author Philip Helger
 */
public final class BasicMicroTypeConverterRegistrarTest
{
  @Test
  public void testSimpleString ()
  {
    final Object [] aDefinedObjs = new Object [] { "InputString",
                                                   BigDecimal.ONE,
                                                   MathHelper.toBigDecimal (Double.MAX_VALUE),
                                                   new BigDecimal ("123446712345678765456547865789762131.111123446712345678765456547865789762131"),
                                                   BigInteger.ZERO,
                                                   new BigInteger ("123446712345678765456547865789762131"),
                                                   Byte.valueOf ((byte) 5),
                                                   Byte.valueOf (Byte.MIN_VALUE),
                                                   Byte.valueOf (Byte.MAX_VALUE),
                                                   Boolean.FALSE,
                                                   Boolean.TRUE,
                                                   Character.valueOf ('c'),
                                                   Character.valueOf (Character.MIN_VALUE),
                                                   Character.valueOf (Character.MAX_VALUE),
                                                   Double.valueOf (1245.3433),
                                                   Double.valueOf (Double.MIN_VALUE),
                                                   Double.valueOf (Double.MAX_VALUE),
                                                   Float.valueOf (31.451f),
                                                   Float.valueOf (Float.MIN_VALUE),
                                                   Float.valueOf (Float.MAX_VALUE),
                                                   Integer.valueOf (17),
                                                   Integer.valueOf (Integer.MIN_VALUE),
                                                   Integer.valueOf (Integer.MAX_VALUE),
                                                   Long.valueOf (-23000),
                                                   Long.valueOf (Long.MIN_VALUE),
                                                   Long.valueOf (Long.MAX_VALUE),
                                                   Short.valueOf ((short) -23),
                                                   Short.valueOf (Short.MIN_VALUE),
                                                   Short.valueOf (Short.MAX_VALUE),
                                                   EChange.CHANGED,
                                                   EChange.UNCHANGED,
                                                   EContinue.CONTINUE,
                                                   EContinue.BREAK,
                                                   EEnabled.ENABLED,
                                                   EEnabled.DISABLED,
                                                   EFinish.FINISHED,
                                                   EFinish.UNFINISHED,
                                                   EInterrupt.INTERRUPTED,
                                                   EInterrupt.NOT_INTERRUPTED,
                                                   ELeftRight.LEFT,
                                                   ELeftRight.RIGHT,
                                                   EMandatory.MANDATORY,
                                                   EMandatory.OPTIONAL,
                                                   ESuccess.SUCCESS,
                                                   ESuccess.FAILURE,
                                                   ETopBottom.BOTTOM,
                                                   ETopBottom.TOP,
                                                   ETriState.TRUE,
                                                   ETriState.FALSE,
                                                   ETriState.UNDEFINED,
                                                   EValidity.VALID,
                                                   EValidity.INVALID,
                                                   CharsetManager.getAsBytes ("Jägalä",
                                                                              CCharset.CHARSET_ISO_8859_1_OBJ),
                                                   new StringBuffer ("Äh ja - wie is das jetzt?"),
                                                   new StringBuilder ("Thät lüks greyt!") };
    for (final Object aObj : aDefinedObjs)
    {
      // Convert to XML
      final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aObj, "any");
      assertNotNull (aElement);
      final String sXML = MicroWriter.getXMLString (aElement);
      assertTrue (sXML.startsWith ("<any>"));
      assertTrue (sXML.endsWith ("</any>" + ENewLineMode.DEFAULT.getText ()));

      // Convert back to native
      final Object aNative = MicroTypeConverter.convertToNative (aElement, aObj.getClass ());
      assertNotNull (aNative);
      assertTrue (EqualsHelper.equals (aObj, aNative));
    }

    // These object don't implement equals!
    final Object [] aDefinedObjsStringEquals = new Object [] { new StringBuilder ("string builder"),
                                                               new StringBuffer ("string buffer") };
    for (final Object aObj : aDefinedObjsStringEquals)
    {
      // Convert to XML
      final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aObj, "any");
      assertNotNull (aElement);
      final String sXML = MicroWriter.getXMLString (aElement);
      assertTrue (sXML.startsWith ("<any>"));
      assertTrue (sXML.endsWith ("</any>" + ENewLineMode.DEFAULT.getText ()));

      // Convert back to native
      final Object aNative = MicroTypeConverter.convertToNative (aElement, aObj.getClass ());
      assertNotNull (aNative);
      assertEquals (aObj.toString (), aNative.toString ());
    }
  }
}
