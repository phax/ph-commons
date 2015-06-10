/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.util.TextHelper;

/**
 * Test class for class {@link MultilingualTextThreadSafe}.
 *
 * @author Philip Helger
 */
public final class MultilingualTextThreadSafeTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor ()
  {
    IMutableMultilingualText aMLT = new MultilingualTextThreadSafe ();
    assertEquals (0, aMLT.getSize ());
    assertNotNull (aMLT.getAllLocales ());
    assertTrue (aMLT.getAllLocales ().isEmpty ());

    aMLT = new MultilingualTextThreadSafe (TextHelper.create_DE_EN ("de", "en"));
    assertEquals (2, aMLT.getSize ());
    assertEquals ("de", aMLT.getText (L_DE));
    assertEquals ("en", aMLT.getText (L_EN));

    try
    {
      new MultilingualTextThreadSafe (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAddText ()
  {
    final IMutableMultilingualText aMLT = new MultilingualTextThreadSafe ();
    final MockChangeCallback aNotify = new MockChangeCallback ();
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
    final IMutableMultilingualText aMLT = new MultilingualTextThreadSafe ();

    try
    {
      // null locale not ok
      aMLT.setText (null, "Hello");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    aMLT.setText (Locale.ENGLISH, "Hello");
    assertEquals (aMLT.getSize (), 1);
    aMLT.setText (Locale.ENGLISH, "Hello2");
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
    final IMutableMultilingualText aMLT = new MultilingualTextThreadSafe ();
    assertEquals (aMLT, aMLT);
    assertFalse (aMLT.equals ("any String"));
    assertFalse (aMLT.equals (null));
  }

  @Test
  public void testCreateFromRequest ()
  {
    final Map <String, String> aParamNames = new HashMap <String, String> ();
    IMultilingualText aMLT = MultilingualTextThreadSafe.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 0);

    aParamNames.put ("de", "x");
    aParamNames.put ("en", "y");
    aMLT = MultilingualTextThreadSafe.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 2);
    assertEquals ("x", aMLT.getText (L_DE));
    assertEquals ("y", aMLT.getText (L_EN));
    assertEquals ("x", aMLT.getTextWithArgs (L_DE, "bla"));
    assertEquals ("y", aMLT.getTextWithArgs (L_EN, "foo"));
    assertTrue (aMLT.containsLocale (L_DE));
    assertFalse (aMLT.containsLocale (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));
  }

  @Test
  public void testClear ()
  {
    final IMutableMultilingualText t = new MultilingualTextThreadSafe ();
    assertTrue (t.isEmpty ());
    assertTrue (t.clear ().isUnchanged ());
    assertTrue (t.setText (L_DE, "de").isChanged ());
    assertTrue (t.clear ().isChanged ());
    assertTrue (t.clear ().isUnchanged ());
    assertTrue (t.isEmpty ());
  }

  @Test
  public void testAssignFrom ()
  {
    final IMutableMultilingualText t = new MultilingualTextThreadSafe ();

    // 1 element
    assertTrue (t.assignFrom (new ReadonlyMultilingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.containsLocale (L_DE));

    // Assign the exact same content again
    assertFalse (t.assignFrom (new ReadonlyMultilingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
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
  public void testRemove ()
  {
    final IMutableMultilingualText t = new MultilingualTextThreadSafe ();
    assertTrue (t.setText (L_DE, "de").isChanged ());
    assertEquals (1, t.getSize ());
    assertFalse (t.removeText (L_EN).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.removeText (L_DE).isChanged ());
    assertEquals (0, t.getSize ());
    assertFalse (t.removeText (L_DE).isChanged ());
    assertEquals (0, t.getSize ());
  }

  @Test
  public void testStdMethods ()
  {
    final MultilingualTextThreadSafe t = new MultilingualTextThreadSafe ();
    t.setText (L_DE, "x");
    t.setText (L_EN, "y");

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t, new MultilingualTextThreadSafe (t));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t, new MultilingualTextThreadSafe ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t,
                                                                           new MultilingualTextThreadSafe (new ReadonlyMultilingualText (CollectionHelper.newMap (L_DE,
                                                                                                                                                                  "x"))));
  }
}
