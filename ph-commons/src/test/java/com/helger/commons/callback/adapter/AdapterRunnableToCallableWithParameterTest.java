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
package com.helger.commons.callback.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.callback.INonThrowingRunnableWithParameter;

/**
 * Test class for class {@link AdapterRunnableToCallableWithParameter}
 *
 * @author Philip Helger
 */
public final class AdapterRunnableToCallableWithParameterTest
{
  @Test
  public void testAll ()
  {
    final INonThrowingRunnableWithParameter <String> r = sCurrentObject -> {
      // empty
    };
    final AdapterRunnableToCallableWithParameter <Object, String> rc = AdapterRunnableToCallableWithParameter.createAdapter (r);
    assertNull (rc.call ("any"));
    final AdapterRunnableToCallableWithParameter <String, String> rcs = AdapterRunnableToCallableWithParameter.createAdapter (r,
                                                                                                                              "abc");
    assertEquals ("abc", rcs.call ("any"));

    try
    {
      AdapterRunnableToCallableWithParameter.createAdapter (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      AdapterRunnableToCallableWithParameter.createAdapter (null, "retval");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
