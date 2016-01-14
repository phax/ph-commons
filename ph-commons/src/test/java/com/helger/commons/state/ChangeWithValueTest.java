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
package com.helger.commons.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ChangeWithValue}.
 *
 * @author Philip Helger
 */
public final class ChangeWithValueTest
{
  @Test
  public void testAll ()
  {
    ChangeWithValue <String> x = new ChangeWithValue <String> (EChange.CHANGED, "bla");
    assertTrue (x.isChanged ());
    assertFalse (x.isUnchanged ());
    assertEquals ("bla", x.get ());
    assertEquals ("bla", x.getIfChanged ("other"));
    assertEquals ("bla", x.getIfChangedOrNull ());
    assertEquals ("other", x.getIfUnchanged ("other"));
    assertNull (x.getIfUnchangedOrNull ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (x, ChangeWithValue.createChanged ("bla"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (x, ChangeWithValue.createUnchanged ("bla"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (x, ChangeWithValue.createChanged ("Other"));

    x = new ChangeWithValue <String> (EChange.CHANGED, null);
    assertNull (x.get ());

    try
    {
      new ChangeWithValue <String> (null, "bla");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
