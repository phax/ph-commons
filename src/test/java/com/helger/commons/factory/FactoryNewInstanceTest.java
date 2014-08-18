/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.annotations.UsedViaReflection;
import com.helger.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link FactoryNewInstance}.
 * 
 * @author Philip Helger
 */
public final class FactoryNewInstanceTest
{
  private abstract static class AbstractClass
  {
    @UsedViaReflection
    @SuppressWarnings ("unused")
    public AbstractClass ()
    {}
  }

  private static class ClassWithoutDefaultCtor
  {
    @UsedViaReflection
    @SuppressWarnings ("unused")
    public ClassWithoutDefaultCtor (final int i)
    {
      // just use parameter
      assertTrue (i != -1 || i != -2);
    }
  }

  @Test
  public void testGetNewInstanceFactory ()
  {
    // test with a valid class
    IFactory <?> aFactory = FactoryNewInstance.create (FactoryNewInstanceTest.class);
    assertNotNull (aFactory);
    assertNotNull (aFactory.create ());

    // null parameter
    aFactory = FactoryNewInstance.create ((Class <Object>) null);
    assertNotNull (aFactory);
    assertNull (aFactory.create ());

    // class is abstract -> cannot create instance
    aFactory = FactoryNewInstance.create (AbstractClass.class);
    assertNotNull (aFactory);
    assertNull (aFactory.create ());

    // class has no default constructor -> cannot create instance
    aFactory = FactoryNewInstance.create (ClassWithoutDefaultCtor.class);
    assertNotNull (aFactory);
    assertNull (aFactory.create ());
  }

  @Test
  public void testGetNewInstanceFactorySafe ()
  {
    // test with a valid class
    final IFactory <?> aFactory = FactoryNewInstance.create (FactoryNewInstanceTest.class, true);
    assertNotNull (aFactory);
    assertNotNull (aFactory.create ());

    // null parameter
    try
    {
      new FactoryNewInstance <Object> (null, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // class is abstract -> cannot create instance
    try
    {
      new FactoryNewInstance <AbstractClass> (AbstractClass.class, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // class has no default constructor -> cannot create instance
    try
    {
      new FactoryNewInstance <ClassWithoutDefaultCtor> (ClassWithoutDefaultCtor.class, true);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testEqualsAndHashCode ()
  {
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (FactoryNewInstance.create (String.class),
                                                                    FactoryNewInstance.create (String.class));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (FactoryNewInstance.create (String.class),
                                                                        FactoryNewInstance.create (StringBuilder.class));
  }
}
