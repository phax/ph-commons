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
package com.helger.commons.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.pair.IReadonlyPair;
import com.helger.commons.collections.pair.ReadonlyPair;
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
    @SuppressWarnings ("unchecked")
    final List <IReadonlyPair <Integer, String>> aPairs = ContainerHelper.newList (ReadonlyPair.create (Integer.valueOf (1),
                                                                                                        "Hallo"),
                                                                                   ReadonlyPair.create (Integer.valueOf (2),
                                                                                                        "welt"),
                                                                                   ReadonlyPair.create (Integer.valueOf (47),
                                                                                                        "!"));
    _test (UnidirectionalConverterPairFirst.<Integer, String> create (),
           aPairs,
           ContainerHelper.newList (Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (47)));
    _test (UnidirectionalConverterPairSecond.<Integer, String> create (),
           aPairs,
           ContainerHelper.newList ("Hallo", "welt", "!"));
    _test (new UnidirectionalConverterStringInteger (null),
           ContainerHelper.newList ("1", "2", "47"),
           ContainerHelper.newList (Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (47)));
    _test (UnidirectionalConverterIntegerString.getInstance (),
           ContainerHelper.newList (Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (47)),
           ContainerHelper.newList ("1", "2", "47"));
    _test (UnidirectionalConverterHasNameString.getInstance (),
           ContainerHelper.newList (new MockHasName (1), new MockHasName (2), new MockHasName (47)),
           ContainerHelper.newList ("1", "2", "47"));
    _test (UnidirectionalConverterHasIDID.<String> create (),
           ContainerHelper.newList (new MockHasIDString (1), new MockHasIDString (2), new MockHasIDString (47)),
           ContainerHelper.newList ("1", "2", "47"));
    _test (UnidirectionalConverterHasIDID.<Integer> create (),
           ContainerHelper.newList (new MockHasIDInteger (1), new MockHasIDInteger (2), new MockHasIDInteger (47)),
           ContainerHelper.newList (Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (47)));
  }
}
