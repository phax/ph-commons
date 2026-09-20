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
 * Test class for class {@link MapBasedXPathVariableResolverQName}.
 *
 * @author Philip Helger
 */
public final class MapBasedXPathVariableResolverQNameTest
{
  private static final QName VAR1 = new QName ("urn:example", "var1");
  private static final QName VAR2 = new QName ("urn:example", "var2");

  @Test
  public void testEmpty ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
    assertEquals (0, aVR.getVariableCount ());
    assertTrue (aVR.getAllVariables ().isEmpty ());
    assertNull (aVR.resolveVariable (VAR1));
    assertNotNull (aVR.toString ());
    assertSame (EChange.UNCHANGED, aVR.clear ());
  }

  @Test
  public void testCtorWithMap ()
  {
    final ICommonsMap <QName, Object> aMap = new CommonsHashMap <> ();
    aMap.put (VAR1, "value1");

    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName (aMap);
    assertEquals (1, aVR.getVariableCount ());
    assertEquals ("value1", aVR.resolveVariable (VAR1));
    // The namespace URI matters here
    assertNull (aVR.resolveVariable (new QName ("var1")));

    assertEquals (0, new MapBasedXPathVariableResolverQName ((java.util.Map <QName, ?>) null).getVariableCount ());
  }

  @Test
  public void testAddUniqueVariable ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
    assertSame (EChange.CHANGED, aVR.addUniqueVariable (VAR1, "value1"));
    assertSame (EChange.UNCHANGED, aVR.addUniqueVariable (VAR1, "value2"));
    assertEquals ("value1", aVR.resolveVariable (VAR1));
  }

  @Test
  public void testRemoveVariable ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
    aVR.addUniqueVariable (VAR1, "value1");
    aVR.addUniqueVariable (VAR2, "value2");

    assertSame (EChange.UNCHANGED, aVR.removeVariable (null));
    assertSame (EChange.UNCHANGED, aVR.removeVariable (new QName ("does-not-exist")));
    assertSame (EChange.CHANGED, aVR.removeVariable (VAR1));

    assertSame (EChange.UNCHANGED, aVR.removeVariables (null));
    assertSame (EChange.CHANGED, aVR.removeVariables (new CommonsArrayList <> (VAR2)));
    assertEquals (0, aVR.getVariableCount ());
  }

  @Test
  public void testSetAllVariablesAndClear ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
    aVR.addUniqueVariable (VAR1, "value1");

    final ICommonsMap <QName, Object> aMap = new CommonsHashMap <> ();
    aMap.put (VAR2, "value2");
    aVR.setAllVariables (aMap);
    assertEquals (1, aVR.getVariableCount ());
    assertNull (aVR.resolveVariable (VAR1));

    assertSame (EChange.CHANGED, aVR.clear ());
  }

  @Test
  public void testAddAllFrom ()
  {
    final MapBasedXPathVariableResolverQName aSrc = new MapBasedXPathVariableResolverQName ();
    aSrc.addUniqueVariable (VAR1, "new1");

    final MapBasedXPathVariableResolverQName aDst = new MapBasedXPathVariableResolverQName ();
    aDst.addUniqueVariable (VAR1, "old1");

    assertSame (EChange.UNCHANGED, aDst.addAllFrom (aSrc, false));
    assertEquals ("old1", aDst.resolveVariable (VAR1));

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, true));
    assertEquals ("new1", aDst.resolveVariable (VAR1));
  }

  @Test
  public void testAddAllFromString ()
  {
    final MapBasedXPathVariableResolver aSrc = new MapBasedXPathVariableResolver ();
    aSrc.addUniqueVariable ("var1", "new1");

    final MapBasedXPathVariableResolverQName aDst = new MapBasedXPathVariableResolverQName ();
    // A String based name becomes a QName without namespace URI
    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, false));
    assertEquals ("new1", aDst.resolveVariable (new QName ("var1")));
  }

  @Test
  public void testGetCloneAndEquals ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
    aVR.addUniqueVariable (VAR1, "value1");

    final MapBasedXPathVariableResolverQName aClone = aVR.getClone ();
    assertNotSame (aVR, aClone);
    TestHelper.testDefaultImplementationWithEqualContentObject (aVR, aClone);

    final MapBasedXPathVariableResolverQName aOther = new MapBasedXPathVariableResolverQName ();
    aOther.addUniqueVariable (new QName ("urn:other", "completely-other-name"), "completely-other-value");
    TestHelper.testDefaultImplementationWithDifferentContentObject (aVR, aOther);

    assertEquals (aVR, new MapBasedXPathVariableResolverQName (aVR));
  }

  @Test
  public void testInvalidParams ()
  {
    final MapBasedXPathVariableResolverQName aVR = new MapBasedXPathVariableResolverQName ();
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
      aVR.addUniqueVariable (VAR1, null);
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
  }
}
