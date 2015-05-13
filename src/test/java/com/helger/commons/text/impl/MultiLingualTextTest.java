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
package com.helger.commons.text.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.text.IMultiLingualText;
import com.helger.commons.text.IReadonlyMultiLingualText;

/**
 * Test class for class {@link MultiLingualText}.
 *
 * @author Philip Helger
 */
public final class MultiLingualTextTest extends AbstractPHTestCase
{
  @Test
  public void testCtor ()
  {
    IMultiLingualText aMLT = new MultiLingualText ();
    assertEquals (0, aMLT.getSize ());
    assertNotNull (aMLT.getAllLocales ());
    assertTrue (aMLT.getAllLocales ().isEmpty ());

    aMLT = new MultiLingualText (TextProvider.create_DE_EN ("de", "en"));
    assertEquals (2, aMLT.getSize ());
    assertEquals ("de", aMLT.getText (L_DE));
    assertEquals ("en", aMLT.getText (L_EN));

    try
    {
      new MultiLingualText ((IReadonlyMultiLingualText) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAddText ()
  {
    final IMultiLingualText aMLT = new MultiLingualText ();
    final MockChangeNotify aNotify = new MockChangeNotify ();
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
    final IMultiLingualText aMLT = new MultiLingualText ();

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
  public void testGetCopyWithLocales ()
  {
    final IMultiLingualText aMLT = new MultiLingualText ();
    assertTrue (aMLT.setText (Locale.ENGLISH, "Hi").isChanged ());
    assertTrue (aMLT.setText (Locale.GERMAN, "Moin").isChanged ());
    assertEquals (aMLT.getSize (), 2);

    final List <Locale> aLocaleList = new ArrayList <Locale> ();
    aLocaleList.add (Locale.ENGLISH);
    final IReadonlyMultiLingualText aMLT2 = MultiLingualText.getCopyWithLocales (aMLT, aLocaleList);
    assertTrue (aMLT2.containsLocale (Locale.ENGLISH));
    assertFalse (aMLT2.containsLocale (Locale.GERMAN));

    assertTrue (aMLT.containsLocale (Locale.ENGLISH));
    assertTrue (aMLT.containsLocale (Locale.GERMAN));
  }

  @Test
  public void testEquals ()
  {
    final IMultiLingualText aMLT = new MultiLingualText ();
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aMLT, new MultiLingualText ());
  }

  @Test
  public void testCreateFromRequest ()
  {
    final Map <String, String> aParamNames = new HashMap <String, String> ();
    IReadonlyMultiLingualText aMLT = MultiLingualText.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 0);

    aParamNames.put ("de", "x");
    aParamNames.put ("en", "y");
    aMLT = MultiLingualText.createFromMap (aParamNames);
    assertEquals (aMLT.getSize (), 2);
    assertEquals (aMLT.getTextWithLocaleFallback (L_DE), "x");
    assertEquals (aMLT.getTextWithLocaleFallback (L_EN), "y");
  }

  @Test
  public void testClear ()
  {
    final MultiLingualText t = new MultiLingualText ();
    assertTrue (t.isEmpty ());
    assertTrue (t.clear ().isUnchanged ());
    assertTrue (t.setText (L_DE, "de").isChanged ());
    assertTrue (t.clear ().isChanged ());
    assertTrue (t.clear ().isUnchanged ());
  }

  @Test
  public void testAssignFrom ()
  {
    final MultiLingualText t = new MultiLingualText ();

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
}
