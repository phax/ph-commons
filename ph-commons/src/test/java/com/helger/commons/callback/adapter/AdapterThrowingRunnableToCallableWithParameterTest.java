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

import java.io.IOException;

import org.junit.Test;

import com.helger.commons.callback.IThrowingRunnableWithParameter;

/**
 * Test class for class {@link AdapterThrowingRunnableToCallableWithParameter}
 *
 * @author Philip Helger
 */
public final class AdapterThrowingRunnableToCallableWithParameterTest
{
  @Test
  public void testAll () throws Exception
  {
    final IThrowingRunnableWithParameter <String, IOException> r = sCurrentObject -> {
      // empty
    };
    final AdapterThrowingRunnableToCallableWithParameter <Object, String, IOException> rc = AdapterThrowingRunnableToCallableWithParameter.createAdapter (r);
    assertNull (rc.call ("any"));
    final AdapterThrowingRunnableToCallableWithParameter <String, String, IOException> rcs = AdapterThrowingRunnableToCallableWithParameter.createAdapter (r,
                                                                                                                                                           "abc");
    assertEquals ("abc", rcs.call ("any"));

    try
    {
      AdapterThrowingRunnableToCallableWithParameter.createAdapter (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      AdapterThrowingRunnableToCallableWithParameter.createAdapter (null, "retval");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
