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
package com.helger.xml.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link MapBasedXPathVariableResolver}.
 *
 * @author Philip Helger
 */
public final class MapBasedXPathVariableResolverTest
{
  @Test
  public void testEmpty ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    assertEquals (0, aVR.getVariableCount ());
    assertTrue (aVR.getAllVariables ().isEmpty ());
    assertNull (aVR.resolveVariable (new QName ("any")));
    assertNotNull (aVR.toString ());
    assertSame (EChange.UNCHANGED, aVR.clear ());
  }

  @Test
  public void testCtorWithMap ()
  {
    final ICommonsMap <String, Object> aMap = new CommonsHashMap <> ();
    aMap.put ("var1", "value1");

    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver (aMap);
    assertEquals (1, aVR.getVariableCount ());
    assertEquals ("value1", aVR.resolveVariable (new QName ("var1")));
    // The namespace URI is ignored, only the local part matters
    assertEquals ("value1", aVR.resolveVariable (new QName ("urn:example", "var1")));

    // null map
    assertEquals (0, new MapBasedXPathVariableResolver ((java.util.Map <String, ?>) null).getVariableCount ());
  }

  @Test
  public void testAddUniqueVariable ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    assertSame (EChange.CHANGED, aVR.addUniqueVariable ("var1", "value1"));
    // Already contained
    assertSame (EChange.UNCHANGED, aVR.addUniqueVariable ("var1", "value2"));
    assertEquals ("value1", aVR.resolveVariable (new QName ("var1")));
  }

  @Test
  public void testRemoveVariable ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    aVR.addUniqueVariable ("var1", "value1");
    aVR.addUniqueVariable ("var2", "value2");

    assertSame (EChange.UNCHANGED, aVR.removeVariable (null));
    assertSame (EChange.UNCHANGED, aVR.removeVariable ("does-not-exist"));
    assertSame (EChange.CHANGED, aVR.removeVariable ("var1"));
    assertEquals (1, aVR.getVariableCount ());

    assertSame (EChange.UNCHANGED, aVR.removeVariables (null));
    assertSame (EChange.UNCHANGED, aVR.removeVariables (new CommonsArrayList <> ("does-not-exist")));
    assertSame (EChange.CHANGED, aVR.removeVariables (new CommonsArrayList <> ("var2", "does-not-exist")));
    assertEquals (0, aVR.getVariableCount ());
  }

  @Test
  public void testSetAllVariablesAndClear ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    aVR.addUniqueVariable ("old", "value");

    final ICommonsMap <String, Object> aMap = new CommonsHashMap <> ();
    aMap.put ("var1", "value1");
    aVR.setAllVariables (aMap);
    assertEquals (1, aVR.getVariableCount ());
    assertNull (aVR.resolveVariable (new QName ("old")));

    assertSame (EChange.CHANGED, aVR.clear ());
    assertEquals (0, aVR.getVariableCount ());
  }

  @Test
  public void testAddAllFrom ()
  {
    final MapBasedXPathVariableResolver aSrc = new MapBasedXPathVariableResolver ();
    aSrc.addUniqueVariable ("var1", "new1");
    aSrc.addUniqueVariable ("var2", "new2");

    final MapBasedXPathVariableResolver aDst = new MapBasedXPathVariableResolver ();
    aDst.addUniqueVariable ("var1", "old1");

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, false));
    // Not overwritten
    assertEquals ("old1", aDst.resolveVariable (new QName ("var1")));
    assertEquals ("new2", aDst.resolveVariable (new QName ("var2")));

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, true));
    assertEquals ("new1", aDst.resolveVariable (new QName ("var1")));
  }

  @Test
  public void testAddAllFromQName ()
  {
    final MapBasedXPathVariableResolverQName aSrc = new MapBasedXPathVariableResolverQName ();
    aSrc.addUniqueVariable (new QName ("urn:example", "var1"), "new1");

    final MapBasedXPathVariableResolver aDst = new MapBasedXPathVariableResolver ();
    aDst.addUniqueVariable ("var1", "old1");

    // The namespace URI is lost, only the local part is used - so "var1" is
    // already contained and nothing changes
    assertSame (EChange.UNCHANGED, aDst.addAllFrom (aSrc, false));
    assertEquals ("old1", aDst.resolveVariable (new QName ("var1")));

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, true));
    assertEquals ("new1", aDst.resolveVariable (new QName ("var1")));
  }

  @Test
  public void testGetCloneAndEquals ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    aVR.addUniqueVariable ("var1", "value1");

    final MapBasedXPathVariableResolver aClone = aVR.getClone ();
    assertNotSame (aVR, aClone);
    TestHelper.testDefaultImplementationWithEqualContentObject (aVR, aClone);

    final MapBasedXPathVariableResolver aOther = new MapBasedXPathVariableResolver ();
    aOther.addUniqueVariable ("completely-other-name", "completely-other-value");
    TestHelper.testDefaultImplementationWithDifferentContentObject (aVR, aOther);

    // Copy constructor
    assertEquals (aVR, new MapBasedXPathVariableResolver (aVR));
  }

  @Test
  public void testInvalidParams ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    try
    {
      aVR.addUniqueVariable (null, "value");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aVR.addUniqueVariable ("var", null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aVR.resolveVariable (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aVR.addAllFrom ((MapBasedXPathVariableResolver) null, true);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aVR.addAllFrom ((MapBasedXPathVariableResolverQName) null, true);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
