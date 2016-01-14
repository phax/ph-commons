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
package com.helger.commons.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.wrapper.Wrapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StringParser}.
 *
 * @author Philip Helger
 */
public final class StringParserTest extends AbstractCommonsTestCase
{
  @Test
  @SuppressFBWarnings (value = "DM_BOOLEAN_CTOR")
  public void testParseBool ()
  {
    assertTrue (StringParser.parseBool ("true"));
    assertTrue (StringParser.parseBool ("TRUE"));
    assertTrue (StringParser.parseBool (Boolean.TRUE.toString ()));
    assertFalse (StringParser.parseBool ("false"));
    assertFalse (StringParser.parseBool ("FALSE"));
    assertFalse (StringParser.parseBool (Boolean.FALSE.toString ()));
    assertFalse (StringParser.parseBool ("anything"));
    assertFalse (StringParser.parseBool ((String) null));

    assertTrue (StringParser.parseBool ((Object) "true"));
    assertFalse (StringParser.parseBool ((Object) "false"));
    assertFalse (StringParser.parseBool ((Object) "anything"));
    assertTrue (StringParser.parseBool (Boolean.TRUE));
    assertFalse (StringParser.parseBool (Boolean.FALSE));
    assertTrue (StringParser.parseBool (new Boolean (true)));
    assertFalse (StringParser.parseBool (new Boolean (false)));
    assertFalse (StringParser.parseBool ((Object) null));
    assertFalse (StringParser.parseBool (Integer.valueOf (0)));
    assertFalse (StringParser.parseBool (Integer.valueOf (1)));
    assertTrue (StringParser.parseBool (Integer.valueOf (0), true));
    assertTrue (StringParser.parseBool (Integer.valueOf (1), true));
  }

  @Test
  @SuppressFBWarnings (value = "DM_BOOLEAN_CTOR")
  public void testParseBoolObj ()
  {
    assertEquals (Boolean.TRUE, StringParser.parseBoolObj ("true"));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObj ("TRUE"));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObj (Boolean.TRUE.toString ()));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ("false"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ("FALSE"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Boolean.FALSE.toString ()));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ("anything"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ((String) null));

    assertEquals (Boolean.TRUE, StringParser.parseBoolObj ((Object) "true"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ((Object) "false"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj ((Object) "anything"));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObj (Boolean.TRUE));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Boolean.FALSE));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObj (new Boolean (true)));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (new Boolean (false)));
    assertNull (StringParser.parseBoolObj ((Object) null));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Integer.valueOf (0)));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Integer.valueOf (1)));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Integer.valueOf (0), Boolean.TRUE));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Integer.valueOf (1), Boolean.TRUE));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObj (Integer.valueOf (0), null));
  }

  @Test
  public void testParseBoolObjExact ()
  {
    assertEquals (Boolean.TRUE, StringParser.parseBoolObjExact ("true"));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObjExact ("trUE"));
    assertEquals (Boolean.TRUE, StringParser.parseBoolObjExact (Boolean.TRUE.toString ()));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObjExact ("false"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObjExact ("FALse"));
    assertEquals (Boolean.FALSE, StringParser.parseBoolObjExact (Boolean.FALSE.toString ()));
    assertNull (StringParser.parseBoolObjExact ("anything"));
    assertNull (StringParser.parseBoolObjExact (""));
    assertNull (StringParser.parseBoolObjExact (null));
  }

  @Test
  public void testParseByte ()
  {
    // Object
    assertEquals (1, StringParser.parseByte ((Object) "1", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "1.2", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "0", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "0000", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "-129", (byte) 0));
    assertEquals (-128, StringParser.parseByte ((Object) "-128", (byte) 0));
    assertEquals (-1, StringParser.parseByte ((Object) "-1", (byte) 0));
    assertEquals (44, StringParser.parseByte ((Object) "44", (byte) 0));
    assertEquals (127, StringParser.parseByte ((Object) "127", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "128", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "257", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "445566abcdef", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "abcdef445566", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) "", (byte) 0));
    assertEquals (0, StringParser.parseByte ((Object) null, (byte) 0));

    assertEquals (0, StringParser.parseByte (Wrapper.create ("any"), (byte) 0));

    assertEquals (1, StringParser.parseByte (Byte.valueOf ((byte) 1), (byte) 0));
    assertEquals (1, StringParser.parseByte (Double.valueOf (1), (byte) 0));
    assertEquals (1, StringParser.parseByte (BigDecimal.ONE, (byte) 0));

    // String
    assertEquals (1, StringParser.parseByte ("1", (byte) 0));
    assertEquals (0, StringParser.parseByte ("1.2", (byte) 0));
    assertEquals (0, StringParser.parseByte ("0", (byte) 0));
    assertEquals (0, StringParser.parseByte ("0000", (byte) 0));
    assertEquals (0, StringParser.parseByte ("-129", (byte) 0));
    assertEquals (-128, StringParser.parseByte ("-128", (byte) 0));
    assertEquals (-1, StringParser.parseByte ("-1", (byte) 0));
    assertEquals (44, StringParser.parseByte ("44", (byte) 0));
    assertEquals (127, StringParser.parseByte ("127", (byte) 0));
    assertEquals (0, StringParser.parseByte ("128", (byte) 0));
    assertEquals (0, StringParser.parseByte ("257", (byte) 0));
    assertEquals (0, StringParser.parseByte ("445566", (byte) 0));
    assertEquals (0, StringParser.parseByte ("445566abcdef", (byte) 0));
    assertEquals (0, StringParser.parseByte ("abcdef445566", (byte) 0));
    assertEquals (0, StringParser.parseByte ("", (byte) 0));
    assertEquals (0, StringParser.parseByte (null, (byte) 0));

    final byte nDefault = 17;
    assertEquals (1, StringParser.parseByte ("1", nDefault));
    assertEquals (nDefault, StringParser.parseByte ("1.2", nDefault));
    assertEquals (0, StringParser.parseByte ("0", nDefault));
    assertEquals (0, StringParser.parseByte ("0000", nDefault));
    assertEquals (-1, StringParser.parseByte ("-1", nDefault));
    assertEquals (nDefault, StringParser.parseByte ("445566", nDefault));
    assertEquals (nDefault, StringParser.parseByte ("445566abcdef", nDefault));
    assertEquals (nDefault, StringParser.parseByte ("abcdef445566", nDefault));
    assertEquals (nDefault, StringParser.parseByte ("", nDefault));
    assertEquals (nDefault, StringParser.parseByte (null, nDefault));
  }

  @Test
  public void testParseByteObj ()
  {
    final Byte b_1 = Byte.valueOf ((byte) -1);
    final Byte b0 = Byte.valueOf ((byte) 0);
    final Byte b1 = Byte.valueOf ((byte) 1);

    // Object
    assertEquals (b1, StringParser.parseByteObj ((Object) "1"));
    assertNull (StringParser.parseByteObj ((Object) "abc"));
    assertEquals (b1, StringParser.parseByteObj ((Object) "1", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "1.2", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "0", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "0000", b0));
    assertEquals (b_1, StringParser.parseByteObj ((Object) "-1", b0));
    assertEquals (Byte.valueOf ((byte) 44), StringParser.parseByteObj ((Object) "44", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "445566abcdef", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "abcdef445566", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) "", b0));
    assertEquals (b0, StringParser.parseByteObj ((Object) null, b0));

    assertEquals (b0, StringParser.parseByteObj (Wrapper.create ("any"), b0));

    assertEquals (b1, StringParser.parseByteObj (Byte.valueOf ((byte) 1), b0));
    assertEquals (b1, StringParser.parseByteObj (Double.valueOf (1), b0));
    assertEquals (b1, StringParser.parseByteObj (BigDecimal.ONE, b0));

    // String
    assertEquals (b1, StringParser.parseByteObj ("1"));
    assertNull (StringParser.parseByteObj ("abc"));
    assertEquals (b1, StringParser.parseByteObj ("1", b0));
    assertEquals (b0, StringParser.parseByteObj ("1.2", b0));
    assertEquals (b0, StringParser.parseByteObj ("0", b0));
    assertEquals (b0, StringParser.parseByteObj ("0000", b0));
    assertEquals (b_1, StringParser.parseByteObj ("-1", b0));
    assertEquals (Byte.valueOf ((byte) 44), StringParser.parseByteObj ("44", b0));
    assertEquals (b0, StringParser.parseByteObj ("445566abcdef", b0));
    assertEquals (b0, StringParser.parseByteObj ("abcdef445566", b0));
    assertEquals (b0, StringParser.parseByteObj ("", b0));
    assertEquals (b0, StringParser.parseByteObj (null, b0));

    final Byte aDefault = Byte.valueOf ((byte) 17);
    assertEquals (b1, StringParser.parseByteObj ("1", aDefault));
    assertEquals (aDefault, StringParser.parseByteObj ("1.2", aDefault));
    assertEquals (b0, StringParser.parseByteObj ("0", aDefault));
    assertEquals (b0, StringParser.parseByteObj ("0000", aDefault));
    assertEquals (b_1, StringParser.parseByteObj ("-1", aDefault));
    assertEquals (Byte.valueOf ((byte) 44), StringParser.parseByteObj ("44", aDefault));
    assertEquals (aDefault, StringParser.parseByteObj ("445566abcdef", aDefault));
    assertEquals (aDefault, StringParser.parseByteObj ("abcdef445566", aDefault));
    assertEquals (aDefault, StringParser.parseByteObj ("", aDefault));
    assertEquals (aDefault, StringParser.parseByteObj (null, aDefault));
  }

  @Test
  public void testParseInt ()
  {
    // Object
    assertEquals (1, StringParser.parseInt ((Object) "1", 0));
    assertEquals (0, StringParser.parseInt ((Object) "1.2", 0));
    assertEquals (0, StringParser.parseInt ((Object) "0", 0));
    assertEquals (0, StringParser.parseInt ((Object) "0000", 0));
    assertEquals (-1, StringParser.parseInt ((Object) "-1", 0));
    assertEquals (445566, StringParser.parseInt ((Object) "445566", 0));
    assertEquals (0, StringParser.parseInt ((Object) "445566abcdef", 0));
    assertEquals (0, StringParser.parseInt ((Object) "abcdef445566", 0));
    assertEquals (0, StringParser.parseInt ((Object) "", 0));
    assertEquals (0, StringParser.parseInt ((Object) null, 0));

    assertEquals (0, StringParser.parseInt (Wrapper.create ("any"), 0));

    assertEquals (1, StringParser.parseInt (Integer.valueOf (1), 0));
    assertEquals (1, StringParser.parseInt (Double.valueOf (1), 0));
    assertEquals (1, StringParser.parseInt (BigDecimal.ONE, 0));

    // String
    assertEquals (1, StringParser.parseInt ("1", 0));
    assertEquals (0, StringParser.parseInt ("1.2", 0));
    assertEquals (0, StringParser.parseInt ("0", 0));
    assertEquals (0, StringParser.parseInt ("0000", 0));
    assertEquals (-1, StringParser.parseInt ("-1", 0));
    assertEquals (445566, StringParser.parseInt ("445566", 0));
    assertEquals (0, StringParser.parseInt ("445566abcdef", 0));
    assertEquals (0, StringParser.parseInt ("abcdef445566", 0));
    assertEquals (0, StringParser.parseInt ("", 0));
    assertEquals (0, StringParser.parseInt (null, 0));

    final int nDefault = 17;
    assertEquals (1, StringParser.parseInt ("1", nDefault));
    assertEquals (nDefault, StringParser.parseInt ("1.2", nDefault));
    assertEquals (0, StringParser.parseInt ("0", nDefault));
    assertEquals (0, StringParser.parseInt ("0000", nDefault));
    assertEquals (-1, StringParser.parseInt ("-1", nDefault));
    assertEquals (445566, StringParser.parseInt ("445566", nDefault));
    assertEquals (nDefault, StringParser.parseInt ("445566abcdef", nDefault));
    assertEquals (nDefault, StringParser.parseInt ("abcdef445566", nDefault));
    assertEquals (nDefault, StringParser.parseInt ("", nDefault));
    assertEquals (nDefault, StringParser.parseInt (null, nDefault));
  }

  @Test
  public void testParseIntObj ()
  {
    // Object
    assertEquals (I1, StringParser.parseIntObj ((Object) "1"));
    assertNull (StringParser.parseIntObj ((Object) "abc"));
    assertEquals (I1, StringParser.parseIntObj ((Object) "1", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "1.2", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "0", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "0000", I0));
    assertEquals (I_1, StringParser.parseIntObj ((Object) "-1", I0));
    assertEquals (Integer.valueOf (445566), StringParser.parseIntObj ((Object) "445566", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "445566abcdef", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "abcdef445566", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) "", I0));
    assertEquals (I0, StringParser.parseIntObj ((Object) null, I0));

    assertEquals (I0, StringParser.parseIntObj (Wrapper.create ("any"), I0));

    assertEquals (I1, StringParser.parseIntObj (Integer.valueOf (1), I0));
    assertEquals (I1, StringParser.parseIntObj (Double.valueOf (1), I0));
    assertEquals (I1, StringParser.parseIntObj (BigDecimal.ONE, I0));

    // String
    assertEquals (I1, StringParser.parseIntObj ("1"));
    assertNull (StringParser.parseIntObj ("abc"));
    assertEquals (I1, StringParser.parseIntObj ("1", I0));
    assertEquals (I0, StringParser.parseIntObj ("1.2", I0));
    assertEquals (I0, StringParser.parseIntObj ("0", I0));
    assertEquals (I0, StringParser.parseIntObj ("0000", I0));
    assertEquals (I_1, StringParser.parseIntObj ("-1", I0));
    assertEquals (Integer.valueOf (445566), StringParser.parseIntObj ("445566", I0));
    assertEquals (I0, StringParser.parseIntObj ("445566abcdef", I0));
    assertEquals (I0, StringParser.parseIntObj ("abcdef445566", I0));
    assertEquals (I0, StringParser.parseIntObj ("", I0));
    assertEquals (I0, StringParser.parseIntObj (null, I0));

    final Integer aDefault = Integer.valueOf (17);
    assertEquals (I1, StringParser.parseIntObj ("1", aDefault));
    assertEquals (aDefault, StringParser.parseIntObj ("1.2", aDefault));
    assertEquals (I0, StringParser.parseIntObj ("0", aDefault));
    assertEquals (I0, StringParser.parseIntObj ("0000", aDefault));
    assertEquals (I_1, StringParser.parseIntObj ("-1", aDefault));
    assertEquals (Integer.valueOf (445566), StringParser.parseIntObj ("445566", aDefault));
    assertEquals (aDefault, StringParser.parseIntObj ("445566abcdef", aDefault));
    assertEquals (aDefault, StringParser.parseIntObj ("abcdef445566", aDefault));
    assertEquals (aDefault, StringParser.parseIntObj ("", aDefault));
    assertEquals (aDefault, StringParser.parseIntObj (null, aDefault));
  }

  @Test
  public void testParseLong ()
  {
    // Object
    assertEquals (1L, StringParser.parseLong ((Object) "1", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "1.2", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "0", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "0000", 0));
    assertEquals (-1L, StringParser.parseLong ((Object) "-1", 0));
    assertEquals (445566L, StringParser.parseLong ((Object) "445566", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "445566abcdef", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "abcdef445566", 0));
    assertEquals (0L, StringParser.parseLong ((Object) "", 0));
    assertEquals (0L, StringParser.parseLong ((Object) null, 0));

    assertEquals (0L, StringParser.parseLong (Wrapper.create ("any"), 0));

    assertEquals (1L, StringParser.parseLong (Integer.valueOf (1), 0));
    assertEquals (1L, StringParser.parseLong (Double.valueOf (1), 0));
    assertEquals (1L, StringParser.parseLong (BigDecimal.ONE, 0));

    // String
    assertEquals (1L, StringParser.parseLong ("1", 0));
    assertEquals (0L, StringParser.parseLong ("1.2", 0));
    assertEquals (0L, StringParser.parseLong ("0", 0));
    assertEquals (0L, StringParser.parseLong ("0000", 0));
    assertEquals (-1L, StringParser.parseLong ("-1", 0));
    assertEquals (445566L, StringParser.parseLong ("445566", 0));
    assertEquals (445566445566L, StringParser.parseLong ("445566445566", 0));
    assertEquals (445566445566445566L, StringParser.parseLong ("445566445566445566", 0));
    assertEquals (0L, StringParser.parseLong ("445566abcdef", 0));
    assertEquals (0L, StringParser.parseLong ("abcdef445566", 0));
    assertEquals (0L, StringParser.parseLong ("", 0));
    assertEquals (0L, StringParser.parseLong (null, 0));

    final long nDefault = 171819171819171819L;
    assertEquals (1L, StringParser.parseLong ("1", nDefault));
    assertEquals (nDefault, StringParser.parseLong ("1.2", nDefault));
    assertEquals (0L, StringParser.parseLong ("0", nDefault));
    assertEquals (0L, StringParser.parseLong ("0000", nDefault));
    assertEquals (-1L, StringParser.parseLong ("-1", nDefault));
    assertEquals (445566L, StringParser.parseLong ("445566", nDefault));
    assertEquals (445566445566L, StringParser.parseLong ("445566445566", nDefault));
    assertEquals (445566445566445566L, StringParser.parseLong ("445566445566445566", nDefault));
    assertEquals (nDefault, StringParser.parseLong ("445566abcdef", nDefault));
    assertEquals (nDefault, StringParser.parseLong ("abcdef445566", nDefault));
    assertEquals (nDefault, StringParser.parseLong ("", nDefault));
    assertEquals (nDefault, StringParser.parseLong (null, nDefault));
  }

  @Test
  public void testParseLongObj ()
  {
    // Object
    assertEquals (L1, StringParser.parseLongObj ((Object) "1"));
    assertNull (StringParser.parseLongObj ((Object) "abc"));
    assertEquals (L1, StringParser.parseLongObj ((Object) "1", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "1.2", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "0", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "0000", L0));
    assertEquals (L_1, StringParser.parseLongObj ((Object) "-1", L0));
    assertEquals (Long.valueOf (445566), StringParser.parseLongObj ((Object) "445566", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "445566abcdef", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "abcdef445566", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) "", L0));
    assertEquals (L0, StringParser.parseLongObj ((Object) null, L0));

    assertEquals (L0, StringParser.parseLongObj (Wrapper.create ("any"), L0));

    assertEquals (L1, StringParser.parseLongObj (Integer.valueOf (1), L0));
    assertEquals (L1, StringParser.parseLongObj (Double.valueOf (1), L0));
    assertEquals (L1, StringParser.parseLongObj (BigDecimal.ONE, L0));

    // String
    assertEquals (L1, StringParser.parseLongObj ("1"));
    assertNull (StringParser.parseLongObj ("abc"));
    assertEquals (L1, StringParser.parseLongObj ("1", L0));
    assertEquals (L0, StringParser.parseLongObj ("1.2", L0));
    assertEquals (L0, StringParser.parseLongObj ("0", L0));
    assertEquals (L0, StringParser.parseLongObj ("0000", L0));
    assertEquals (L_1, StringParser.parseLongObj ("-1", L0));
    assertEquals (Long.valueOf (445566), StringParser.parseLongObj ("445566", L0));
    assertEquals (L0, StringParser.parseLongObj ("445566abcdef", L0));
    assertEquals (L0, StringParser.parseLongObj ("abcdef445566", L0));
    assertEquals (L0, StringParser.parseLongObj ("", L0));
    assertEquals (L0, StringParser.parseLongObj (null, L0));

    final Long aDefault = Long.valueOf (-173267823468L);
    assertEquals (L1, StringParser.parseLongObj ("1", aDefault));
    assertEquals (aDefault, StringParser.parseLongObj ("1.2", aDefault));
    assertEquals (L0, StringParser.parseLongObj ("0", aDefault));
    assertEquals (L0, StringParser.parseLongObj ("0000", aDefault));
    assertEquals (L_1, StringParser.parseLongObj ("-1", aDefault));
    assertEquals (Long.valueOf (445566), StringParser.parseLongObj ("445566", aDefault));
    assertEquals (aDefault, StringParser.parseLongObj ("445566abcdef", aDefault));
    assertEquals (aDefault, StringParser.parseLongObj ("abcdef445566", aDefault));
    assertEquals (aDefault, StringParser.parseLongObj ("", aDefault));
    assertEquals (aDefault, StringParser.parseLongObj (null, aDefault));
  }

  @Test
  public void testParseDouble ()
  {
    final double dDefault = 3.145667;

    // Object
    CommonsAssert.assertEquals (dDefault, StringParser.parseDouble ((Object) null, dDefault));
    CommonsAssert.assertEquals (1, StringParser.parseDouble (BigDecimal.ONE, dDefault));
    CommonsAssert.assertEquals (dDefault, StringParser.parseDouble (Wrapper.create ("any"), dDefault));

    // String
    CommonsAssert.assertEquals (dDefault, StringParser.parseDouble ((String) null, dDefault));
    CommonsAssert.assertEquals (dDefault, StringParser.parseDouble ("", dDefault));
    CommonsAssert.assertEquals (1.2, StringParser.parseDouble ("1.2", dDefault));
    CommonsAssert.assertEquals (-1.23456, StringParser.parseDouble ("-1.23456", dDefault));
    CommonsAssert.assertEquals (dDefault, StringParser.parseDouble ("bla", dDefault));
  }

  @Test
  public void testParseDoubleObj ()
  {
    final Double aDefault = Double.valueOf (3.145667);

    // Object
    assertNull (StringParser.parseDoubleObj ((Object) null));
    assertEquals (aDefault, StringParser.parseDoubleObj ((Object) null, aDefault));
    CommonsAssert.assertEquals (1, StringParser.parseDoubleObj (BigDecimal.ONE, aDefault));
    assertEquals (aDefault, StringParser.parseDoubleObj (Wrapper.create ("any"), aDefault));

    // String
    assertNull (StringParser.parseDoubleObj ("foo"));
    assertEquals (aDefault, StringParser.parseDoubleObj ((String) null, aDefault));
    assertEquals (aDefault, StringParser.parseDoubleObj ("", aDefault));
    CommonsAssert.assertEquals (1.2, StringParser.parseDoubleObj ("1.2", aDefault));
    CommonsAssert.assertEquals (-1.23456, StringParser.parseDoubleObj ("-1.23456", aDefault));
    assertEquals (aDefault, StringParser.parseDoubleObj ("bla", aDefault));
  }

  @Test
  public void testParseFloat ()
  {
    final float fDefault = 3.145667f;

    // Object
    CommonsAssert.assertEquals (fDefault, StringParser.parseFloat ((Object) null, fDefault));
    CommonsAssert.assertEquals (1, StringParser.parseFloat (BigDecimal.ONE, fDefault));
    CommonsAssert.assertEquals (fDefault, StringParser.parseFloat (Wrapper.create ("any"), fDefault));

    // String
    CommonsAssert.assertEquals (fDefault, StringParser.parseFloat ((String) null, fDefault));
    CommonsAssert.assertEquals (fDefault, StringParser.parseFloat ("", fDefault));
    CommonsAssert.assertEquals (1.2, StringParser.parseFloat ("1.2", fDefault));
    CommonsAssert.assertEquals (-1.23456, StringParser.parseFloat ("-1.23456", fDefault));
    CommonsAssert.assertEquals (fDefault, StringParser.parseFloat ("bla", fDefault));
  }

  @Test
  public void testParseFloatObj ()
  {
    final Float aDefault = Float.valueOf (3.145667f);

    // Object
    assertNull (StringParser.parseFloatObj ((Object) null));
    assertEquals (aDefault, StringParser.parseFloatObj ((Object) null, aDefault));
    CommonsAssert.assertEquals (1, StringParser.parseFloatObj (BigDecimal.ONE, aDefault));
    assertEquals (aDefault, StringParser.parseFloatObj (Wrapper.create ("any"), aDefault));

    // String
    assertNull (StringParser.parseFloatObj ("foo"));
    assertEquals (aDefault, StringParser.parseFloatObj ((String) null, aDefault));
    assertEquals (aDefault, StringParser.parseFloatObj ("", aDefault));
    CommonsAssert.assertEquals (1.2f, StringParser.parseFloatObj ("1.2", aDefault));
    CommonsAssert.assertEquals (-1.23456f, StringParser.parseFloatObj ("-1.23456", aDefault));
    assertEquals (aDefault, StringParser.parseFloatObj ("bla", aDefault));
  }

  @Test
  public void testParseBigInteger ()
  {
    final BigInteger aDefault = new BigInteger ("123462786432798234676875657234709");

    // String
    assertEquals (BigInteger.ONE, StringParser.parseBigInteger ("1"));
    assertEquals (BigInteger.TEN, StringParser.parseBigInteger ("10", 10));
    assertEquals (BigInteger.valueOf (16), StringParser.parseBigInteger ("10", 16));
    assertNull (StringParser.parseBigInteger ("abc"));
    assertEquals (BigInteger.ONE, StringParser.parseBigInteger ("1", aDefault));
    assertEquals (new BigInteger ("46278643279823467687565723"),
                  StringParser.parseBigInteger ("46278643279823467687565723"));
    assertEquals (aDefault, StringParser.parseBigInteger ("abc", aDefault));
    assertEquals (aDefault, StringParser.parseBigInteger ((String) null, aDefault));
    assertEquals (aDefault, StringParser.parseBigInteger ("", aDefault));
    assertEquals (aDefault, StringParser.parseBigInteger ("1.2", aDefault));
    assertEquals (aDefault, StringParser.parseBigInteger ("-1.23456", aDefault));
    assertEquals (aDefault, StringParser.parseBigInteger ("bla", aDefault));
  }

  @Test
  public void testParseBigDecimal ()
  {
    final BigDecimal aDefault = new BigDecimal ("1234627864327.98234676875657234709");

    // String
    assertEquals (BigDecimal.ONE, StringParser.parseBigDecimal ("1"));
    assertNull (StringParser.parseBigDecimal ("abc"));
    assertEquals (BigDecimal.ONE, StringParser.parseBigDecimal ("1", aDefault));
    assertEquals (new BigDecimal ("46278643279.823467687565723"),
                  StringParser.parseBigDecimal ("46278643279.823467687565723"));
    assertEquals (aDefault, StringParser.parseBigDecimal ("abc", aDefault));
    assertEquals (aDefault, StringParser.parseBigDecimal ((String) null, aDefault));
    assertEquals (aDefault, StringParser.parseBigDecimal ("", aDefault));
    assertEquals (new BigDecimal ("1.2"), StringParser.parseBigDecimal ("1.2", aDefault));
    assertEquals (new BigDecimal ("-1.23456"), StringParser.parseBigDecimal ("-1.23456", aDefault));
    assertEquals (aDefault, StringParser.parseBigDecimal ("bla", aDefault));
  }

  @Test
  public void testIsInt ()
  {
    assertTrue (StringParser.isInt ("1"));
    assertFalse (StringParser.isInt ("1.2"));
    assertTrue (StringParser.isInt ("0"));
    assertTrue (StringParser.isInt ("0000"));
    assertTrue (StringParser.isInt ("-1"));
    assertTrue (StringParser.isInt ("445566"));
    assertFalse (StringParser.isInt ("445566 "));
    assertFalse (StringParser.isInt (" 445566"));
    assertFalse (StringParser.isInt ("445566445566"));
    assertFalse (StringParser.isInt ("445566445566445566"));
    assertFalse (StringParser.isInt ("445566abcdef"));
    assertFalse (StringParser.isInt ("abcdef445566"));
    assertFalse (StringParser.isInt (""));
    assertFalse (StringParser.isInt (null));
  }

  @Test
  public void testIsLong ()
  {
    assertTrue (StringParser.isLong ("1"));
    assertFalse (StringParser.isLong ("1.2"));
    assertTrue (StringParser.isLong ("0"));
    assertTrue (StringParser.isLong ("0000"));
    assertTrue (StringParser.isLong ("-1"));
    assertTrue (StringParser.isLong ("445566"));
    assertTrue (StringParser.isLong ("445566445566"));
    assertTrue (StringParser.isLong ("445566445566445566"));
    assertFalse (StringParser.isLong ("445566445566445566 "));
    assertFalse (StringParser.isLong (" 445566445566445566"));
    assertFalse (StringParser.isLong ("445566abcdef"));
    assertFalse (StringParser.isLong ("abcdef445566"));
    assertFalse (StringParser.isLong (""));
    assertFalse (StringParser.isLong (null));
  }

  @Test
  public void testIsUnsignedInt ()
  {
    assertTrue (StringParser.isUnsignedInt ("1"));
    assertFalse (StringParser.isUnsignedInt ("1.2"));
    assertTrue (StringParser.isUnsignedInt ("0"));
    assertTrue (StringParser.isUnsignedInt ("0000"));
    assertFalse (StringParser.isUnsignedInt ("-1"));
    assertTrue (StringParser.isUnsignedInt ("445566"));
    assertFalse (StringParser.isUnsignedInt ("445566445566"));
    assertFalse (StringParser.isUnsignedInt ("445566445566445566"));
    assertFalse (StringParser.isUnsignedInt ("445566abcdef"));
    assertFalse (StringParser.isUnsignedInt ("abcdef445566"));
    assertFalse (StringParser.isUnsignedInt (""));
    assertFalse (StringParser.isUnsignedInt (null));
  }

  @Test
  public void testIsUnsignedLong ()
  {
    assertTrue (StringParser.isUnsignedLong ("1"));
    assertFalse (StringParser.isUnsignedLong ("1.2"));
    assertTrue (StringParser.isUnsignedLong ("0"));
    assertTrue (StringParser.isUnsignedLong ("0000"));
    assertFalse (StringParser.isUnsignedLong ("-1"));
    assertTrue (StringParser.isUnsignedLong ("445566"));
    assertTrue (StringParser.isUnsignedLong ("445566445566"));
    assertTrue (StringParser.isUnsignedLong ("445566445566445566"));
    assertFalse (StringParser.isUnsignedLong ("445566abcdef"));
    assertFalse (StringParser.isUnsignedLong ("abcdef445566"));
    assertFalse (StringParser.isUnsignedLong (""));
    assertFalse (StringParser.isUnsignedLong (null));
  }

  @Test
  public void testIsDouble ()
  {
    assertTrue (StringParser.isDouble ("1"));
    assertTrue (StringParser.isDouble ("1.2"));
    assertTrue (StringParser.isDouble ("1,2"));
    assertTrue (StringParser.isDouble ("0"));
    assertTrue (StringParser.isDouble ("0000"));
    assertTrue (StringParser.isDouble ("-1"));
    assertTrue (StringParser.isDouble ("445566"));
    assertTrue (StringParser.isDouble ("445566445566"));
    assertTrue (StringParser.isDouble ("445566445566445566"));
    assertFalse (StringParser.isDouble ("445566abcdef"));
    assertFalse (StringParser.isDouble ("abcdef445566"));
    assertFalse (StringParser.isDouble (""));
    assertFalse (StringParser.isDouble (null));
  }
}
