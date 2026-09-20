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
package com.helger.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link SettingsWithDefault}.
 *
 * @author Philip Helger
 */
public final class SettingsWithDefaultTest
{
  private static ISettings _createDefault ()
  {
    final Settings aDefault = new Settings ("default");
    aDefault.putIn ("key1", "default1");
    aDefault.putIn ("key2", "default2");
    return aDefault;
  }

  @Test
  public void testNameFromDefault ()
  {
    final ISettings aDefault = _createDefault ();
    final SettingsWithDefault aSettings = new SettingsWithDefault (aDefault);
    assertEquals (aDefault.getName (), aSettings.getName ());
    assertSame (aDefault, aSettings.getDefaultSettings ());
    assertNotNull (aSettings.toString ());
  }

  @Test
  public void testExplicitName ()
  {
    final SettingsWithDefault aSettings = new SettingsWithDefault ("own", _createDefault ());
    assertEquals ("own", aSettings.getName ());
  }

  @Test
  public void testContainsKeyAndGetValue ()
  {
    final SettingsWithDefault aSettings = new SettingsWithDefault (_createDefault ());

    // Nothing set directly yet - everything comes from the default
    assertFalse (aSettings.containsKeyDirect ("key1"));
    assertTrue (aSettings.containsKey ("key1"));
    assertNull (aSettings.getValueDirect ("key1"));
    assertEquals ("default1", aSettings.getValue ("key1"));

    // Unknown key
    assertFalse (aSettings.containsKeyDirect ("key3"));
    assertFalse (aSettings.containsKey ("key3"));
    assertNull (aSettings.getValue ("key3"));

    // Now set it directly
    aSettings.putIn ("key1", "own1");
    assertTrue (aSettings.containsKeyDirect ("key1"));
    assertTrue (aSettings.containsKey ("key1"));
    assertEquals ("own1", aSettings.getValueDirect ("key1"));
    assertEquals ("own1", aSettings.getValue ("key1"));
  }

  @Test
  public void testSetToDefault ()
  {
    final SettingsWithDefault aSettings = new SettingsWithDefault (_createDefault ());
    aSettings.putIn ("key1", "own1");
    assertFalse (aSettings.isSetToDefault ("key1"));

    assertSame (EChange.CHANGED, aSettings.setToDefault ("key1"));
    assertEquals ("default1", aSettings.getValueDirect ("key1"));
    assertTrue (aSettings.isSetToDefault ("key1"));
    // Already the default value
    assertSame (EChange.UNCHANGED, aSettings.setToDefault ("key1"));

    // No default value present
    assertSame (EChange.UNCHANGED, aSettings.setToDefault ("key3"));
    assertSame (EChange.UNCHANGED, aSettings.setToDefault (null));
    assertFalse (aSettings.isSetToDefault ("key3"));
    assertFalse (aSettings.isSetToDefault (null));
  }

  @Test
  public void testSetAllToDefault ()
  {
    final SettingsWithDefault aSettings = new SettingsWithDefault (_createDefault ());
    assertSame (EChange.CHANGED, aSettings.setAllToDefault ());
    assertEquals ("default1", aSettings.getValueDirect ("key1"));
    assertEquals ("default2", aSettings.getValueDirect ("key2"));
    assertTrue (aSettings.isSetToDefault ("key1"));
    assertTrue (aSettings.isSetToDefault ("key2"));

    // Everything is already at the default
    assertSame (EChange.UNCHANGED, aSettings.setAllToDefault ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    final ISettings aDefault = _createDefault ();
    TestHelper.testDefaultImplementationWithEqualContentObject (new SettingsWithDefault (aDefault),
                                                                new SettingsWithDefault (aDefault));

    final Settings aOtherDefault = new Settings ("default");
    aOtherDefault.putIn ("key1", "completely-other-value");
    TestHelper.testDefaultImplementationWithDifferentContentObject (new SettingsWithDefault (aDefault),
                                                                    new SettingsWithDefault (aOtherDefault));

    final SettingsWithDefault aWithValue = new SettingsWithDefault (aDefault);
    aWithValue.putIn ("own", "own-value");
    TestHelper.testDefaultImplementationWithDifferentContentObject (new SettingsWithDefault (aDefault), aWithValue);
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new SettingsWithDefault (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new SettingsWithDefault ("own", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
