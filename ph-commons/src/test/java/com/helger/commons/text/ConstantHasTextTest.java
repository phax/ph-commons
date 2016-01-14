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
package com.helger.commons.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ConstantHasText}.
 *
 * @author Philip Helger
 */
public final class ConstantHasTextTest
{
  @Test
  public void testAll ()
  {
    final ConstantHasText aCDN = new ConstantHasText ("any");
    assertEquals ("any", aCDN.getText (Locale.GERMAN));

    try
    {
      // null not allowed
      new ConstantHasText (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null locale not allowed
      aCDN.getText (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStandard ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ConstantHasText ("any"),
                                                                       new ConstantHasText ("any"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ConstantHasText (""),
                                                                       new ConstantHasText (""));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ConstantHasText ("any"),
                                                                           new ConstantHasText ("anyy"));
  }
}
