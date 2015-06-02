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

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.IMultiLingualText;
import com.helger.commons.text.IReadonlyMultiLingualText;
import com.helger.commons.text.MultiLingualText;
import com.helger.commons.text.MultiLingualTextThreadSafe;
import com.helger.commons.text.ReadonlyMultiLingualText;
import com.helger.commons.text.TextProvider;

/**
 * Test class for class {@link MultiLingualTextThreadSafe}.
 *
 * @author Philip Helger
 */
public final class MultiLingualTextThreadSafeTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor ()
  {
    IMultiLingualText aMLT = new MultiLingualTextThreadSafe ();
    assertEquals (0, aMLT.getSize ());
    assertNotNull (aMLT.getAllLocales ());
    assertTrue (aMLT.getAllLocales ().isEmpty ());

    aMLT = new MultiLingualTextThreadSafe (TextProvider.create_DE_EN ("de", "en"));
    assertEquals (2, aMLT.getSize ());
    assertEquals ("de", aMLT.getText (L_DE));
    assertEquals ("en", aMLT.getText (L_EN));

    try
    {
      new MultiLingualTextThreadSafe (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAddText ()
  {
    final IMultiLingualText aMLT = new MultiLingualTextThreadSafe ();
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
    final IMultiLingualText aMLT = new MultiLingualTextThreadSafe ();

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
    assertEquals (aMLT.getTextWithLocaleFallback (Locale.ENGLISH), "Hello2");
    assertNull (aMLT.getTextWithLocaleFallback (Locale.GERMAN));

    aMLT.setText (Locale.GERMAN, "Hallo");
    aMLT.setText (Locale.GERMAN, "Hallo2");
    assertEquals (aMLT.getSize (), 2);
    assertTrue (aMLT.containsLocale (Locale.ENGLISH));
    assertTrue (aMLT.getAllLocales ().contains (Locale.ENGLISH));
    assertTrue (aMLT.containsLocale (Locale.GERMAN));
    assertTrue (aMLT.getAllLocales ().contains (Locale.GERMAN));
    assertEquals (aMLT.getTextWithLocaleFallback (Locale.ENGLISH), "Hello2");
    assertEquals (aMLT.getTextWithLocaleFallback (Locale.GERMAN), "Hallo2");
  }

  @Test
  public void testEquals ()
  {
    final IMultiLingualText aMLT = new MultiLingualTextThreadSafe ();
    assertEquals (aMLT, aMLT);
    assertFalse (aMLT.equals ("any String"));
    assertFalse (aMLT.equals (null));
  }

  @Test
  public void testCreateFromRequest ()
  {
    final Map <String, String> aParamNames = new HashMap <String, String> ();
    IReadonlyMultiLingualText aMLT = MultiLingualTextThreadSafe.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 0);

    aParamNames.put ("de", "x");
    aParamNames.put ("en", "y");
    aMLT = MultiLingualTextThreadSafe.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 2);
    assertEquals ("x", aMLT.getText (L_DE));
    assertEquals ("y", aMLT.getText (L_EN));
    assertEquals ("x", aMLT.getTextWithArgs (L_DE));
    assertEquals ("y", aMLT.getTextWithArgs (L_EN));
    assertEquals ("x", aMLT.getTextWithLocaleFallback (L_DE));
    assertEquals ("y", aMLT.getTextWithLocaleFallback (L_EN));
    assertEquals ("x", aMLT.getTextWithLocaleFallbackAndArgs (L_DE));
    assertEquals ("y", aMLT.getTextWithLocaleFallbackAndArgs (L_EN));
    assertTrue (aMLT.containsLocale (L_DE));
    assertFalse (aMLT.containsLocale (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));
  }

  @Test
  public void testClear ()
  {
    final IMultiLingualText t = new MultiLingualTextThreadSafe ();
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
    final IMultiLingualText t = new MultiLingualTextThreadSafe ();

    // 1 element
    assertTrue (t.assignFrom (new ReadonlyMultiLingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.containsLocale (L_DE));

    // Assign the exact same content again
    assertFalse (t.assignFrom (new ReadonlyMultiLingualText (CollectionHelper.newMap (L_DE, "de"))).isChanged ());
    assertEquals (1, t.getSize ());
    assertTrue (t.containsLocale (L_DE));

    // Assign empty text
    assertTrue (t.assignFrom (new MultiLingualText ()).isChanged ());
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
    final IMultiLingualText t = new MultiLingualTextThreadSafe ();
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
    final MultiLingualTextThreadSafe t = new MultiLingualTextThreadSafe ();
    t.setText (L_DE, "x");
    t.setText (L_EN, "y");

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (t, new MultiLingualTextThreadSafe (t));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t, new MultiLingualTextThreadSafe ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (t,
                                                                     new MultiLingualTextThreadSafe (new ReadonlyMultiLingualText (CollectionHelper.newMap (L_DE,
                                                                                                                                                            "x"))));
  }
}
