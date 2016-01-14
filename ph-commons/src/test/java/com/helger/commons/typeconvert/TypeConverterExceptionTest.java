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
package com.helger.commons.typeconvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

import com.helger.commons.typeconvert.TypeConverterException.EReason;

/**
 * Test class for class {@link TypeConverterException}.
 *
 * @author Philip Helger
 */
public final class TypeConverterExceptionTest
{
  @Test
  public void testAll ()
  {
    final TypeConverterException tex = new TypeConverterException (String.class, URL.class, EReason.CONVERSION_FAILED);
    assertEquals (String.class, tex.getSrcClass ());
    assertEquals (URL.class, tex.getDstClass ());
    assertEquals (EReason.CONVERSION_FAILED, tex.getReason ());

    try
    {
      final TypeConverterException e = new TypeConverterException (null, URL.class, EReason.CONVERSION_FAILED);
      fail ();
      throw e;
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      final TypeConverterException e = new TypeConverterException (String.class, null, EReason.CONVERSION_FAILED);
      fail ();
      throw e;
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      final TypeConverterException e = new TypeConverterException (String.class, URL.class, null);
      fail ();
      throw e;
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testReason ()
  {
    for (final EReason e : EReason.values ())
      assertSame (e, EReason.valueOf (e.name ()));
  }
}
