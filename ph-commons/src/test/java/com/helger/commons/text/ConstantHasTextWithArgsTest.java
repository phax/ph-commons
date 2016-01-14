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
 * Test class for class {@link ConstantHasTextWithArgs}.
 *
 * @author Philip Helger
 */
public final class ConstantHasTextWithArgsTest
{
  @Test
  public void testAll ()
  {
    final ConstantHasTextWithArgs aCDN = new ConstantHasTextWithArgs ("any");
    assertEquals ("any", aCDN.getText (Locale.GERMAN));
    assertEquals ("any", aCDN.getTextWithArgs (Locale.GERMAN, "foo", "bar"));

    try
    {
      // null not allowed
      new ConstantHasTextWithArgs (null);
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
    try
    {
      // null locale not allowed
      aCDN.getTextWithArgs (null, "any");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStandard ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ConstantHasTextWithArgs ("any"),
                                                                       new ConstantHasTextWithArgs ("any"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ConstantHasTextWithArgs (""),
                                                                       new ConstantHasTextWithArgs (""));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ConstantHasTextWithArgs ("any"),
                                                                           new ConstantHasTextWithArgs ("anyy"));
  }
}
