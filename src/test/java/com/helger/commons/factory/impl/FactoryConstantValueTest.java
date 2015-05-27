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
package com.helger.commons.factory.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;

import org.junit.Test;

import com.helger.commons.factory.IFactory;
import com.helger.commons.factory.impl.FactoryConstantValue;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link FactoryConstantValue}.
 * 
 * @author Philip Helger
 */
public final class FactoryConstantValueTest
{
  private static <T> void _testConstantFactory (final T aValue, final T aValue2)
  {
    final IFactory <T> aFactory = new FactoryConstantValue <T> (aValue);
    assertNotNull (aFactory);
    assertSame (aValue, aFactory.create ());
    assertSame (aValue, aFactory.create ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFactory, new FactoryConstantValue <T> (aValue));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (aFactory, FactoryConstantValue.create (aValue));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aFactory,
                                                                        new FactoryConstantValue <T> (aValue2));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aFactory, FactoryConstantValue.create (aValue2));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aFactory,
                                                                        new FactoryConstantValue <BigDecimal> (BigDecimal.ONE));
  }

  @Test
  public void testConstantValueFactory ()
  {
    _testConstantFactory ("Hallo Welt", "any");
    _testConstantFactory (Integer.valueOf (-1), Integer.valueOf (4711));
    _testConstantFactory (new StringBuilder ("csadh"), new StringBuilder ("what so every"));
  }
}
