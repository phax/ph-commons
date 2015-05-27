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
package com.helger.commons.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.commons.factory.FactoryNull;
import com.helger.commons.factory.IFactory;

/**
 * Test class for class {@link FactoryNull}.
 * 
 * @author Philip Helger
 */
public final class FactoryNullTest
{
  @Test
  public void testNullFactory ()
  {
    final IFactory <?> aFactory = FactoryNull.getInstance ();
    assertNotNull (aFactory);
    assertNull (aFactory.create ());
    assertNull (aFactory.create ());
    assertNull (aFactory.create ());
    assertNull (aFactory.create ());

    assertSame (aFactory, FactoryNull.getInstance ());
  }
}
