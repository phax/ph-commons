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
package com.helger.base.rt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.system.SystemProperties;

/**
 * Test class for class {@link PropertiesHelper} and {@link ByteBufferHelper}.
 *
 * @author Philip Helger
 */
public final class PropertiesHelperTest
{
  private static final String SYS_PROP = "ph-commons.junittest.propertieshelper";

  @After
  public void clearSystemProperty ()
  {
    SystemProperties.removePropertyValue (SYS_PROP);
  }

  @Test
  public void testGetAsStringMap ()
  {
    final Properties aProps = new Properties ();
    aProps.setProperty ("a", "1");
    aProps.setProperty ("b", "2");

    final Map <String, String> aMap = PropertiesHelper.getAsStringMap (aProps);
    assertEquals (2, aMap.size ());
    assertEquals ("1", aMap.get ("a"));
    assertEquals ("2", aMap.get ("b"));
  }

  @Test
  public void testLoadProperties ()
  {
    final String sContent = "a=1\nb=2\n";

    final NonBlockingProperties aFromIS = PropertiesHelper.loadProperties (new NonBlockingByteArrayInputStream (sContent.getBytes (StandardCharsets.ISO_8859_1)));
    assertNotNull (aFromIS);
    assertEquals ("1", aFromIS.get ("a"));

    final NonBlockingProperties aFromReader = PropertiesHelper.loadProperties (new NonBlockingStringReader (sContent));
    assertNotNull (aFromReader);
    assertEquals ("2", aFromReader.get ("b"));
  }

  @Test
  public void testExpandProperties ()
  {
    final Map <String, String> aValues = new HashMap <> ();
    aValues.put ("name", "World");
    aValues.put ("empty", "");

    assertNull (PropertiesHelper.expandProperties (null, aValues::get));
    // Nothing to expand
    assertEquals ("plain", PropertiesHelper.expandProperties ("plain", aValues::get));
    // A simple expansion
    assertEquals ("Hello World", PropertiesHelper.expandProperties ("Hello ${name}", aValues::get));
    assertEquals ("World!", PropertiesHelper.expandProperties ("${name}!", aValues::get));
    // An unknown key stays as is
    assertNotNull (PropertiesHelper.expandProperties ("${unknown}", aValues::get));
    // A double brace is not expanded
    assertNotNull (PropertiesHelper.expandProperties ("${{name}}", aValues::get));
    // An unterminated placeholder
    assertNotNull (PropertiesHelper.expandProperties ("${name", aValues::get));
  }

  @Test
  public void testExpandSystemProperties ()
  {
    assertNull (PropertiesHelper.expandSystemProperties (null));
    assertEquals ("plain", PropertiesHelper.expandSystemProperties ("plain"));

    SystemProperties.setPropertyValue (SYS_PROP, "the-value");
    assertEquals ("x the-value y", PropertiesHelper.expandSystemProperties ("x ${" + SYS_PROP + "} y"));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      PropertiesHelper.expandProperties ("any", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testByteBufferTransfer ()
  {
    final byte [] aBytes = "Hello World".getBytes (StandardCharsets.ISO_8859_1);

    // Without flipping - the source is already ready to read
    final ByteBuffer aSrc = ByteBuffer.wrap (aBytes);
    final ByteBuffer aDst = ByteBuffer.allocate (aBytes.length);
    assertEquals (aBytes.length, ByteBufferHelper.transfer (aSrc, aDst, false));

    // With flipping - the source is in write mode
    final ByteBuffer aSrc2 = ByteBuffer.allocate (aBytes.length);
    aSrc2.put (aBytes);
    final ByteBuffer aDst2 = ByteBuffer.allocate (aBytes.length);
    assertEquals (aBytes.length, ByteBufferHelper.transfer (aSrc2, aDst2, true));

    // An empty source transfers nothing
    final ByteBuffer aEmpty = ByteBuffer.allocate (4);
    assertEquals (0, ByteBufferHelper.transfer (aEmpty, ByteBuffer.allocate (4), true));

    // A destination smaller than the source transfers only what fits
    final ByteBuffer aSrc3 = ByteBuffer.wrap (aBytes);
    final ByteBuffer aSmall = ByteBuffer.allocate (4);
    assertEquals (4, ByteBufferHelper.transfer (aSrc3, aSmall, false));

    try
    {
      ByteBufferHelper.transfer (null, aDst, false);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
