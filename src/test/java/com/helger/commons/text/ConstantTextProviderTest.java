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
package com.helger.commons.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.text.ConstantTextProvider;

/**
 * Test class for class {@link ConstantTextProvider}.
 * 
 * @author Philip Helger
 */
public final class ConstantTextProviderTest
{
  @Test
  public void testAll ()
  {
    final ConstantTextProvider aCDN = new ConstantTextProvider ("any");
    assertEquals ("any", aCDN.getDisplayName ());
    assertEquals ("any", aCDN.getDisplayText (Locale.GERMAN));
    assertEquals ("any", aCDN.getName ());
    assertEquals ("any", aCDN.getText (Locale.GERMAN));
    assertEquals ("any", aCDN.getTextWithLocaleFallback (Locale.GERMAN));
    assertEquals ("any", aCDN.getTextWithArgs (Locale.GERMAN));
    assertEquals ("any", aCDN.getTextWithLocaleFallbackAndArgs (Locale.GERMAN));

    try
    {
      // null not allowed
      new ConstantTextProvider (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      aCDN.getText (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      aCDN.getTextWithArgs (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      aCDN.getTextWithLocaleFallback (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null not allowed
      aCDN.getTextWithLocaleFallbackAndArgs (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStandard ()
  {
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new ConstantTextProvider ("any"),
                                                                    new ConstantTextProvider ("any"));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new ConstantTextProvider (""),
                                                                    new ConstantTextProvider (""));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new ConstantTextProvider ("any"),
                                                                        new ConstantTextProvider ("anyy"));
  }
}
