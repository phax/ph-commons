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
package com.helger.commons.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.util.TextHelper;

/**
 * Test class for class {@link MultilingualText}.
 *
 * @author Philip Helger
 */
public final class MultilingualTextTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor ()
  {
    IMutableMultilingualText aMLT = new MultilingualText ();
    assertEquals (0, aMLT.getSize ());
    assertNotNull (aMLT.getAllLocales ());
    assertTrue (aMLT.getAllLocales ().isEmpty ());

    aMLT = TextHelper.create_DE_EN ("de", "en");
    assertEquals (2, aMLT.getSize ());
    assertEquals ("de", aMLT.getText (L_DE));
    assertEquals ("en", aMLT.getText (L_EN));

    try
    {
      new MultilingualText ((IMultilingualText) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAddText ()
  {
    final IMutableMultilingualText aMLT = new MultilingualText ();
    final MockChangeCallback aNotify = new MockChangeCallback ();
    try
    {
      aMLT.getChangeNotifyCallbacks ().addCallback (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    aMLT.getChangeNotifyCallbacks ().addCallback (aNotify);

    try
    {
      // null locale not ok
      aMLT.addText (null, "Hello");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    assertEquals (aNotify.getCallCountBefore (), 0);
    assertEquals (aNotify.getCallCountAfter (), 0);

    assertTrue (aMLT.addText (Locale.ENGLISH, "Hello").isChanged ());
    assertEquals (aNotify.getCallCountBefore (), 1);
    assertEquals (aNotify.getCallCountAfter (), 1);
    assertEquals (aMLT.getSize (), 1);
    assertTrue (aMLT.addText (Locale.ENGLISH, "Hello2").isUnchanged ());
    assertEquals (aNotify.getCallCountBefore (), 1);
    assertEquals (aNotify.getCallCountAfter (), 1);
    assertEquals (aMLT.getSize (), 1);
    assertTrue (aMLT.getAllLocales ().contains (Locale.ENGLISH));
    assertFalse (aMLT.getAllLocales ().contains (Locale.GERMAN));

    assertTrue (aMLT.addText (Locale.GERMAN, "Hallo").isChanged ());
    assertEquals (aNotify.getCallCountBefore (), 2);
    assertEquals (aNotify.getCallCountAfter (), 2);
    assertTrue (aMLT.addText (Locale.GERMAN, "Hallo2").isUnchanged ());
    assertEquals (aNotify.getCallCountBefore (), 2);
    assertEquals (aNotify.getCallCountAfter (), 2);
    assertEquals (aMLT.getSize (), 2);
    assertTrue (aMLT.getAllLocales ().contains (Locale.ENGLISH));
    assertTrue (aMLT.getAllLocales ().contains (Locale.GERMAN));
  }

  @Test
  public void testSetText ()
  {
    final IMutableMultilingualText aMLT = new MultilingualText ();

    try
    {
      // null locale not ok
      aMLT.setText (null, "Hello");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertTrue (aMLT.setText (Locale.ENGLISH, "Hello").isChanged ());
    assertEquals (aMLT.getSize (), 1);
    assertFalse (aMLT.setText (Locale.ENGLISH, "Hello").isChanged ());
    assertTrue (aMLT.setText (Locale.ENGLISH, "Hello2").isChanged ());
    assertEquals (aMLT.getSize (), 1);
    assertTrue (aMLT.containsLocale (Locale.ENGLISH));
    assertTrue (aMLT.getAllLocales ().contains (Locale.ENGLISH));
    assertFalse (aMLT.containsLocale (Locale.GERMAN));
    assertFalse (aMLT.getAllLocales ().contains (Locale.GERMAN));
    assertEquals (aMLT.getText (Locale.ENGLISH), "Hello2");
    assertNull (aMLT.getText (Locale.GERMAN));

    aMLT.setText (Locale.GERMAN, "Hallo");
    aMLT.setText (Locale.GERMAN, "Hallo2");
    assertEquals (aMLT.getSize (), 2);
    assertTrue (aMLT.containsLocale (Locale.ENGLISH));
    assertTrue (aMLT.getAllLocales ().contains (Locale.ENGLISH));
    assertTrue (aMLT.containsLocale (Locale.GERMAN));
    assertTrue (aMLT.getAllLocales ().contains (Locale.GERMAN));
    assertEquals (aMLT.getText (Locale.ENGLISH), "Hello2");
    assertEquals (aMLT.getText (Locale.GERMAN), "Hallo2");
  }

  @Test
  public void testEquals ()
  {
    final IMutableMultilingualText aMLT = new MultilingualText ();
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aMLT, new MultilingualText ());
  }

  @Test
  public void testClear ()
  {
    final MultilingualText t = new MultilingualText ();
    assertTrue (t.isEmpty ());
    assertTrue (t.clear ().isUnchanged ());
    assertTrue (t.setText (L_DE, "de").isChanged ());
    assertTrue (t.clear ().isChanged ());
    assertTrue (t.clear ().isUnchanged ());
  }

  @Test
  public void testAssignFrom ()
  {
    final MultilingualText t = new MultilingualText ();

    // 1 element
    assertTrue (t.assignFrom (new ReadOnlyMultilingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.containsLocale (L_DE));

    // Assign the exact same content again
    assertFalse (t.assignFrom (new ReadOnlyMultilingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.containsLocale (L_DE));

    // Assign empty text
    assertTrue (t.assignFrom (new MultilingualText ()).isChanged ());
    assertEquals (0, t.getSize ());

    try
    {
      t.assignFrom (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetText ()
  {
    MultilingualText aTP = TextHelper.create_DE_EN ("Hallo", "Hello");
    assertEquals ("Hallo", aTP.getText (L_DE_DE));
    assertEquals ("Hallo", aTP.getText (L_DE));
    assertNull (aTP.getText (L_FR));

    aTP = TextHelper.create_DE ("Hallo");
    assertEquals ("Hallo", aTP.getText (L_DE_DE));
    assertEquals ("Hallo", aTP.getText (L_DE));
    assertNull (aTP.getText (L_FR));

    aTP = TextHelper.create_EN ("Hello");
    assertEquals ("Hello", aTP.getText (L_EN_US));
    assertEquals ("Hello", aTP.getText (L_EN_GB));
    assertEquals ("Hello", aTP.getText (L_EN));
    assertNull (aTP.getText (L_FR));
  }

  @Test
  public void testGetTextWithArgs ()
  {
    final MultilingualText aTP = TextHelper.create_DE_EN ("Hallo {0}", "{0} Hello");
    assertEquals ("Hallo {0}", aTP.getText (L_DE_DE));
    assertEquals ("Hallo {0}", aTP.getText (L_DE));
    assertNull (aTP.getText (L_FR));

    assertEquals ("Hallo Hugo", aTP.getTextWithArgs (L_DE_DE, "Hugo"));
    assertEquals ("Hallo Hugo", aTP.getTextWithArgs (L_DE, "Hugo"));
    assertEquals ("Hugo Hello", aTP.getTextWithArgs (L_EN, "Hugo"));
    assertNull (aTP.getTextWithArgs (L_FR, "any"));
  }

  @Test
  public void testIsEmpty ()
  {
    IMultilingualText aTP = TextHelper.create_DE_EN ("Hallo", "Hello");
    assertFalse (aTP.isEmpty ());

    aTP = new ReadOnlyMultilingualText ();
    assertTrue (aTP.isEmpty ());
  }

  @Test
  public void testQuotes ()
  {
    assertNotNull (TextHelper.create_DE_EN ("Test 123!", ""));
    assertNotNull (TextHelper.create_DE_EN ("Test {0} 123!", ""));
    assertNotNull (TextHelper.create_DE_EN ("Test ''{0}'' 123!", ""));

    AbstractReadOnlyMapBasedMultilingualText.setPerformConsistencyChecks (true);
    try
    {
      // should log a warning
      TextHelper.create_DE_EN ("Test\\nmasked new line", "");

      try
      {
        // Must use two single quotes
        TextHelper.create_DE_EN ("Test '{0}' 123!", "");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      try
      {
        TextHelper.create_DE_EN ("'{0}' 123!", "");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      try
      {
        TextHelper.create_DE_EN ("Test '{0}'", "");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      try
      {
        // Requires only a single quote
        TextHelper.create_DE ("Test '' no arguments");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }
    finally
    {
      AbstractReadOnlyMapBasedMultilingualText.setPerformConsistencyChecks (false);
    }
  }
}
