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
package com.helger.json.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.json.IJsonObject;

/**
 * Test class for class {@link JsonUnmappedException}.
 *
 * @author Philip Helger
 */
public final class JsonUnmappedExceptionTest
{
  private static final ICommonsList <String> STACK_TRACE = new CommonsArrayList <> ("line1", "line2");

  @Test
  public void testBasic ()
  {
    final JsonUnmappedException aEx = new JsonUnmappedException ("java.lang.IllegalArgumentException",
                                                                 "any message",
                                                                 STACK_TRACE);
    assertEquals ("java.lang.IllegalArgumentException", aEx.getClassName ());
    assertEquals ("any message", aEx.getMessage ());
    assertEquals (STACK_TRACE, aEx.getAllStackTraceLines ());
    // Must be a copy
    assertNotSame (STACK_TRACE, aEx.getAllStackTraceLines ());
    assertNotSame (aEx.getAllStackTraceLines (), aEx.getAllStackTraceLines ());
  }

  @Test
  public void testNullMessage ()
  {
    final JsonUnmappedException aEx = new JsonUnmappedException ("java.lang.NullPointerException",
                                                                 null,
                                                                 new CommonsArrayList <> ());
    assertEquals ("java.lang.NullPointerException", aEx.getClassName ());
    assertNull (aEx.getMessage ());
    assertEquals (0, aEx.getAllStackTraceLines ().size ());

    final IJsonObject aJson = aEx.getAsJson ();
    assertNotNull (aJson);
    assertEquals ("java.lang.NullPointerException", aJson.getAsString (JsonMapper.JSON_CLASS));
    // No message was provided
    assertNull (aJson.getAsString (JsonMapper.JSON_MESSAGE));
  }

  @Test
  public void testGetAsJson ()
  {
    final JsonUnmappedException aEx = new JsonUnmappedException ("java.lang.IllegalStateException",
                                                                 "any message",
                                                                 STACK_TRACE);
    final IJsonObject aJson = aEx.getAsJson ();
    assertNotNull (aJson);
    assertEquals ("java.lang.IllegalStateException", aJson.getAsString (JsonMapper.JSON_CLASS));
    assertEquals ("any message", aJson.getAsString (JsonMapper.JSON_MESSAGE));
    assertNotNull (aJson.getAsString (JsonMapper.JSON_STACK_TRACE));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new JsonUnmappedException (null, "any message", STACK_TRACE);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new JsonUnmappedException ("java.lang.IllegalArgumentException", "any message", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
