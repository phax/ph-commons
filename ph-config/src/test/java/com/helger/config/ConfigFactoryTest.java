/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.math.MathHelper;
import com.helger.commons.typeconvert.TypeConverterException;

public class ConfigFactoryTest
{
  private void _testDefault (@Nonnull final IConfig aConfig)
  {
    assertEquals ("from-application-json", aConfig.getAsString ("element1"));
    assertEquals ("from-application-properties2", aConfig.getAsString ("element2"));
    assertEquals ("from-reference-properties3", aConfig.getAsString ("element3"));
    assertNull (aConfig.getAsString ("element4"));

    assertEquals (-1, aConfig.getAsInt ("element1", -1));
    try
    {
      aConfig.getAsInt ("element1");
      fail ();
    }
    catch (final TypeConverterException ex)
    {
      // Expected
    }

    assertEquals (123456, aConfig.getAsInt ("int"));
    assertEquals ("123456", aConfig.getAsString ("int"));

    assertTrue (EqualsHelper.equals (MathHelper.toBigDecimal (123.45678), aConfig.getAsBigDecimal ("dbl")));
    assertEquals ("123.45678", aConfig.getAsString ("dbl"));
  }

  @Test
  public void testDefault ()
  {
    _testDefault (ConfigFactory.getDefaultConfig ());
    _testDefault (Config.create (ConfigFactory.createDefaultValueProvider ()));
    _testDefault (Config.create (ConfigFactory.createDefaultValueProvider ().getClone ()));
    _testDefault (Config.create (ConfigFactory.createDefaultValueProvider ().getClone ().getClone ()));
  }
}
