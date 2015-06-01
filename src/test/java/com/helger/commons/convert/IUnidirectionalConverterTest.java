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
package com.helger.commons.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.id.MockHasIDInteger;
import com.helger.commons.id.MockHasIDString;
import com.helger.commons.name.MockHasName;

/**
 * Test class for implementation classes of {@link IUnidirectionalConverter}.
 *
 * @author Philip Helger
 */
public final class IUnidirectionalConverterTest
{
  private <SRC, DST> void _test (final IUnidirectionalConverter <SRC, ? extends DST> aConv,
                                 final List <? extends SRC> aSrcList,
                                 final List <? extends DST> aDstList)
  {
    assertNotNull (aConv);
    assertNotNull (aSrcList);
    assertNotNull (aDstList);
    assertEquals (aSrcList.size (), aDstList.size ());
    for (int i = 0; i < aSrcList.size (); ++i)
    {
      assertEquals (aDstList.get (i), aConv.convert (aSrcList.get (i)));
    }
  }

  @Test
  public void testConversion ()
  {
    _test (new UnidirectionalConverterStringInteger (),
           CollectionHelper.newList ("1", "2", "47"),
           CollectionHelper.newIntList (1, 2, 47));
    _test (new UnidirectionalConverterIntegerString (),
           CollectionHelper.newIntList (1, 2, 47),
           CollectionHelper.newList ("1", "2", "47"));
    _test (new UnidirectionalConverterHasNameString (),
           CollectionHelper.newList (new MockHasName (1), new MockHasName (2), new MockHasName (47)),
           CollectionHelper.newList ("1", "2", "47"));
    _test (UnidirectionalConverterHasIDID.<String> create (),
           CollectionHelper.newList (new MockHasIDString (1), new MockHasIDString (2), new MockHasIDString (47)),
           CollectionHelper.newList ("1", "2", "47"));
    _test (UnidirectionalConverterHasIDID.<Integer> create (),
           CollectionHelper.newList (new MockHasIDInteger (1), new MockHasIDInteger (2), new MockHasIDInteger (47)),
           CollectionHelper.newIntList (1, 2, 47));
  }
}
