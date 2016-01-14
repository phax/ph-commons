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
package com.helger.commons.microdom.convert;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.id.IHasID;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.typeconvert.TypeConverterException;

/**
 * Test class for class {@link MicroTypeConverter}.
 *
 * @author Philip Helger
 */
public final class MicroTypeConverterTest
{
  @Test
  public void testConvertToMicroElement ()
  {
    // null object allowed
    assertNull (MicroTypeConverter.convertToMicroElement (null, "tag"));

    try
    {
      // empty tag name not allowed
      MicroTypeConverter.convertToMicroElement ("value", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testConvertToNative ()
  {
    // null object allowed
    assertNull (MicroTypeConverter.convertToNative (null, String.class));

    try
    {
      // null class not allowed
      MicroTypeConverter.convertToNative (new MicroElement ("any"), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // No converter present
      MicroTypeConverter.convertToNative (new MicroElement ("any"), IHasID.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }
}
