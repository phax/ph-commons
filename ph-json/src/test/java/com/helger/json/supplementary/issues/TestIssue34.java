/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.json.supplementary.issues;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.commons.wrapper.Wrapper;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.parser.JsonParseException;
import com.helger.json.parser.JsonParserSettings;
import com.helger.json.serialize.JsonReader;

public final class TestIssue34
{
  @Nonnull
  public static String _createNestedDoc (@Nonnegative final int nNesting,
                                         @Nonnull final String sBeginning,
                                         @Nonnull final String sLevelOpen,
                                         @Nonnull final String sContent,
                                         @Nonnull final String sLevelClose,
                                         @Nonnull final String sEnd)
  {
    final StringBuilder ret = new StringBuilder (sBeginning.length () +
                                                 nNesting * (sLevelOpen.length () + sLevelClose.length ()) +
                                                 1 +
                                                 sContent.length () +
                                                 1 +
                                                 (nNesting / 32) * 2 +
                                                 sEnd.length ());
    ret.append (sBeginning);
    for (int i = 0; i < nNesting; ++i)
    {
      ret.append (sLevelOpen);
      if ((i & 31) == 0)
        ret.append ('\n');
    }
    ret.append ('\n').append (sContent).append ('\n');
    for (int i = 0; i < nNesting; ++i)
    {
      ret.append (sLevelClose);
      if ((i & 31) == 0)
        ret.append ('\n');
    }
    ret.append (sEnd);
    return ret.toString ();
  }

  @Test
  public void testArrayMax ()
  {
    // Stack overflow with JSON array with nesting 5176
    final int nMax = JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH * 2;
    for (int nNesting = 1; nNesting < nMax; ++nNesting)
    {
      final String sNestedDoc = _createNestedDoc (nNesting, "", "[", "0", "]", "");
      final Wrapper <JsonParseException> aWrapper = new Wrapper <> ();
      final IJson aJson = JsonReader.builder ().source (sNestedDoc).customExceptionCallback (aWrapper::set).read ();
      if (aWrapper.isSet ())
      {
        assertNull (aJson);
        assertTrue ("Failed nesting is " + nNesting, nNesting > JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH);
      }
      else
      {
        assertNotNull (aJson);
        assertTrue (nNesting <= JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH);
      }
    }
  }

  @Test
  public void testArrayMin ()
  {
    // Default: nesting okay
    IJsonArray aJson = JsonReader.builder ().source ("[[0]]").readAsArray ();
    assertNotNull (aJson);
    // Nested too deep
    aJson = JsonReader.builder ().source ("[[0]]").customizeCallback (p -> p.setMaxNestingDepth (1)).readAsArray ();
    assertNull (aJson);
    // Nesting okay
    aJson = JsonReader.builder ().source ("[0]").customizeCallback (p -> p.setMaxNestingDepth (1)).readAsArray ();
    assertNotNull (aJson);
  }

  @Test
  public void testObjectMax ()
  {
    // Stack overflow with JSON object with nesting 5650
    // Start with 2, because we always have an outer bracket
    final int nMax = JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH * 2;
    for (int nNesting = 2; nNesting < nMax; ++nNesting)
    {
      final String sNestedDoc = _createNestedDoc (nNesting - 1, "{", "'a':{ ", "'b':0", "} ", "}");
      final Wrapper <JsonParseException> aWrapper = new Wrapper <> ();
      final IJson aJson = JsonReader.builder ().source (sNestedDoc).customExceptionCallback (aWrapper::set).read ();
      if (aWrapper.isSet ())
      {
        assertNull (aJson);
        assertTrue ("Failed nesting is " + nNesting, nNesting > JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH);
      }
      else
      {
        assertNotNull (aJson);
        assertTrue (nNesting <= JsonParserSettings.DEFAULT_MAX_NESTING_DEPTH);
      }
    }
  }

  @Test
  public void testObjectMin ()
  {
    // Default: nesting okay
    IJsonObject aJson = JsonReader.builder ().source ("{'a':{'a':0}}").readAsObject ();
    assertNotNull (aJson);
    // Nested too deep
    aJson = JsonReader.builder ()
                      .source ("{'a':{'a':0}}")
                      .customizeCallback (p -> p.setMaxNestingDepth (1))
                      .readAsObject ();
    assertNull (aJson);
    // Nesting okay
    aJson = JsonReader.builder ().source ("{'a':0}").customizeCallback (p -> p.setMaxNestingDepth (1)).readAsObject ();
    assertNotNull (aJson);
  }
}
