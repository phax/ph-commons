/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.json.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.json.IJson;
import com.helger.json.IJsonValue;
import com.helger.json.parser.handler.CollectingJsonParserHandler;
import com.helger.json.serialize.JsonReader;

/**
 * Test class for class {@link JsonParser}.
 *
 * @author Philip Helger
 */
public final class JsonParserTest
{
  @Nullable
  private static IJson _read (@Nonnull final String sJson, @Nullable final IJsonParserCustomizeCallback aCustomizer)
  {
    final CollectingJsonParserHandler aHandler = new CollectingJsonParserHandler ();
    if (JsonReader.parseJson (new NonBlockingStringReader (sJson), aHandler, aCustomizer, null).isFailure ())
      return null;
    return aHandler.getJson ();
  }

  @Test
  public void testAlwaysUseBigNumberInt ()
  {
    final String sJson = "5";

    assertEquals (Integer.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    assertEquals (BigInteger.class,
                  ((IJsonValue) _read (sJson, aParser -> aParser.setAlwaysUseBigNumber (true))).getValueClass ());
  }

  @Test
  public void testAlwaysUseBigNumberDecimal ()
  {
    final String sJson = "5.1";

    assertEquals (Double.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    assertEquals (BigDecimal.class,
                  ((IJsonValue) _read (sJson, aParser -> aParser.setAlwaysUseBigNumber (true))).getValueClass ());
  }

  @Test
  public void testNumber ()
  {
    assertEquals (BigInteger.class, ((IJsonValue) _read ("-5e10", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("-5e7897", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("5e10", null)).getValueClass ());
    assertEquals (BigInteger.class, ((IJsonValue) _read ("5e7897", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("-5e-10", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("-5e-7897", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("5e-10", null)).getValueClass ());
    assertEquals (BigDecimal.class, ((IJsonValue) _read ("5e-7897", null)).getValueClass ());
  }

  @Test
  public void testReadEscaped ()
  {
    final String sJson = "'\\u1234'";

    assertEquals (String.class, ((IJsonValue) _read (sJson, null)).getValueClass ());
    final String sValue = _read (sJson, null).getAsValue ().getAsString ();
    assertNotNull (sValue);
    assertEquals (1, sValue.length ());
    assertEquals ('\u1234', sValue.charAt (0));
  }
}
