/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.PrimitiveCollectionHelper;
import com.helger.commons.id.MockHasIDInteger;
import com.helger.commons.id.MockHasIDString;
import com.helger.commons.name.MockHasName;

/**
 * Test class for implementation classes of {@link IConverter}.
 *
 * @author Philip Helger
 */
public final class IConverterTest
{
  private <SRC, DST> void _test (final IConverter <SRC, ? extends DST> aConv,
                                 final List <? extends SRC> aSrcList,
                                 final List <? extends DST> aDstList)
  {
    assertNotNull (aConv);
    assertNotNull (aSrcList);
    assertNotNull (aDstList);
    assertEquals (aSrcList.size (), aDstList.size ());
    for (int i = 0; i < aSrcList.size (); ++i)
    {
      assertEquals (aDstList.get (i), aConv.apply (aSrcList.get (i)));
    }
  }

  @Test
  public void testConversion ()
  {
    _test (new ConverterStringInteger (),
           CollectionHelper.newList ("1", "2", "47"),
           PrimitiveCollectionHelper.newPrimitiveList (1, 2, 47));
    _test (new ConverterIntegerString (),
           PrimitiveCollectionHelper.newPrimitiveList (1, 2, 47),
           CollectionHelper.newList ("1", "2", "47"));
    _test (new ConverterHasNameString (),
           CollectionHelper.newList (new MockHasName (1), new MockHasName (2), new MockHasName (47)),
           CollectionHelper.newList ("1", "2", "47"));
    _test (new ConverterHasIDID <> (),
           CollectionHelper.newList (new MockHasIDString (1), new MockHasIDString (2), new MockHasIDString (47)),
           CollectionHelper.newList ("1", "2", "47"));
    _test (new ConverterHasIDID <> (),
           CollectionHelper.newList (new MockHasIDInteger (1), new MockHasIDInteger (2), new MockHasIDInteger (47)),
           PrimitiveCollectionHelper.newPrimitiveList (1, 2, 47));
  }
}
