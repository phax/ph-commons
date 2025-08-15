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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.junit.Test;

import com.helger.base.state.IClearable;
import com.helger.base.state.IStoppable;
import com.helger.base.system.EJavaVersion;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.commons.hierarchy.MockChildrenProvider;
import com.helger.commons.type.IHasObjectType;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ServiceLoader}.
 *
 * @author Philip Helger
 */
// SKIPJDK5
public final class ServiceLoaderFuncTest
{
  @Test
  public void testLoadEmptyService ()
  {
    // No such service file present
    final ServiceLoader <MockChildrenProvider> aSL = ServiceLoader.load (MockChildrenProvider.class);
    final Iterator <MockChildrenProvider> it = aSL.iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    TestHelper.testToStringImplementation (aSL);
  }

  @Test
  public void testLoadNonExistingImplementation ()
  {
    // The service file contains a non-existing implementation class
    final Iterable <IHasObjectType> aSL = ServiceLoader.load (IHasObjectType.class);
    final Iterator <IHasObjectType> it = aSL.iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    try
    {
      it.next ();
      fail ();
    }
    catch (final ServiceConfigurationError ex)
    {}
  }

  @Test
  public void testLoadNonExistingImplementationWithSpecialCL ()
  {
    // The service file contains a non-existing implementation class
    final Iterable <IHasObjectType> aSL = ServiceLoader.loadInstalled (IHasObjectType.class);
    final Iterator <IHasObjectType> it = aSL.iterator ();
    assertNotNull (it);
    assertFalse (it.hasNext ());
  }

  @Test
  public void testLoadCrappyServiceFile ()
  {
    // The service file contains a non-existing implementation class
    final ServiceLoader <IStoppable> aSL = ServiceLoader.load (IStoppable.class);
    final Iterator <IStoppable> it = aSL.iterator ();
    assertNotNull (it);
    if (EJavaVersion.getCurrentVersion ().isOlderOrEqualsThan (EJavaVersion.JDK_1_8))
    {
      try
      {
        // this fails
        it.hasNext ();
        fail ();
      }
      catch (final ServiceConfigurationError ex)
      {}
    }
    else
    {
      // Here it works but fails when reading
      assertTrue (it.hasNext ());
      try
      {
        // this fails
        CollectionHelperExt.newList (it);
        fail ();
      }
      catch (final ServiceConfigurationError ex)
      {}
    }
  }

  @Test
  public void testLoadValid ()
  {
    // Empty service file present
    IClearable aNext;
    final ServiceLoader <IClearable> aSL = ServiceLoader.load (IClearable.class);
    final Iterator <IClearable> it = aSL.iterator ();
    assertNotNull (it);
    assertTrue (it.hasNext ());
    try
    {
      // first item is the invalid one (does not implement IClearable)
      aNext = it.next ();
      fail ();
    }
    catch (final ServiceConfigurationError ex)
    {}
    assertTrue (it.hasNext ());
    // now get the valid one
    aNext = it.next ();
    assertNotNull (aNext);
    assertTrue (aNext instanceof MockSPIClearableValid);
    assertEquals (0, ((MockSPIClearableValid) aNext).getCallCount ());
    assertFalse (it.hasNext ());
    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
  }
}
