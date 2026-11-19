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
package com.helger.io.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ZipWalkerSettings}.
 *
 * @author Philip Helger
 */
public final class ZipWalkerSettingsTest
{
  @Test
  public void testBasic ()
  {
    final ZipWalkerSettings aSettings = new ZipWalkerSettings ();
    assertEquals (ZipWalkerSettings.DEFAULT_MAX_NESTING_DEPTH, aSettings.getMaxNestingDepth ());
    assertEquals (ZipWalkerSettings.DEFAULT_MAX_ENTRY_COUNT, aSettings.getMaxEntryCount ());
    assertEquals (ZipWalkerSettings.DEFAULT_MAX_TOTAL_UNCOMPRESSED_BYTES, aSettings.getMaxTotalUncompressedBytes ());
    TestHelper.testDefaultImplementationWithEqualContentObject (aSettings, new ZipWalkerSettings ());
    TestHelper.testDefaultImplementationWithEqualContentObject (aSettings, aSettings.getClone ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSettings,
                                                                     new ZipWalkerSettings ().setMaxNestingDepth (0));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSettings,
                                                                     new ZipWalkerSettings ().setMaxEntryCount (1));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aSettings,
                                                                     new ZipWalkerSettings ().setMaxTotalUncompressedBytes (1));

    final ZipWalkerSettings aOther = new ZipWalkerSettings ().setMaxNestingDepth (1)
                                                             .setMaxEntryCount (2)
                                                             .setMaxTotalUncompressedBytes (3);
    final ZipWalkerSettings aClone = aOther.getClone ();
    assertNotSame (aOther, aClone);
    assertEquals (aOther, aClone);
    assertEquals (aOther, new ZipWalkerSettings ().assignFrom (aOther));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidMaxEntryCount ()
  {
    new ZipWalkerSettings ().setMaxEntryCount (0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidMaxTotalUncompressedBytes ()
  {
    new ZipWalkerSettings ().setMaxTotalUncompressedBytes (0);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidMaxNestingDepth ()
  {
    new ZipWalkerSettings ().setMaxNestingDepth (-1);
  }
}
