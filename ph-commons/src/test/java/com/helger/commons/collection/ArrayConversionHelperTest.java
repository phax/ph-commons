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
package com.helger.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.helger.commons.convert.ConverterStringInteger;
import com.helger.commons.convert.IConverter;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for {@link ArrayHelper}
 *
 * @author Philip Helger
 */
public final class ArrayConversionHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testNewArrayFromCollectionWithConverter ()
  {
    final IConverter <String, Integer> conv = new ConverterStringInteger (null);

    Integer [] x = ArrayConversionHelper.newArray (CollectionHelper.newList ("1", "2", "3"), conv, Integer.class);
    assertNotNull (x);
    assertEquals (3, x.length);
    assertEquals (1, x[0].intValue ());
    assertEquals (2, x[1].intValue ());
    assertEquals (3, x[2].intValue ());

    x = ArrayConversionHelper.newArray (new ArrayList <String> (), conv, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      // List may not be null
      ArrayConversionHelper.newArray ((Collection <String>) null, conv, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Converter may not be null
      ArrayConversionHelper.newArray (CollectionHelper.newList ("1", "2", "3"), null, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Destination class may not be null
      ArrayConversionHelper.newArray (CollectionHelper.newList ("1", "2", "3"), conv, (Class <Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testNewArrayFromArrayWithConverter ()
  {
    final IConverter <String, Integer> conv = new ConverterStringInteger (null);

    Integer [] x = ArrayConversionHelper.newArray (new String [] { "1", "2", "3" }, conv, Integer.class);
    assertNotNull (x);
    assertEquals (3, x.length);
    assertEquals (1, x[0].intValue ());
    assertEquals (2, x[1].intValue ());
    assertEquals (3, x[2].intValue ());

    x = ArrayConversionHelper.newArray (new String [0], conv, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    x = ArrayConversionHelper.newArray ((String []) null, conv, Integer.class);
    assertNotNull (x);
    assertEquals (0, x.length);

    try
    {
      // Converter may not be null
      ArrayConversionHelper.newArray (new String [] { "1", "2", "3" }, null, Integer.class);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Destination class may not be null
      ArrayConversionHelper.newArray (new String [] { "1", "2", "3" }, conv, (Class <Integer>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test for method convert
   */
  @Test
  public void testConvert ()
  {
    final IConverter <String, Integer> conv = new ConverterStringInteger (null);

    final Integer [] dst = ArrayConversionHelper.newArray (new String [] { "2", "4", "6" }, conv, Integer.class);
    assertNotNull (dst);
    assertEquals (3, dst.length);
    assertEquals (2, dst[0].intValue ());
    assertEquals (4, dst[1].intValue ());
    assertEquals (6, dst[2].intValue ());
  }
}
