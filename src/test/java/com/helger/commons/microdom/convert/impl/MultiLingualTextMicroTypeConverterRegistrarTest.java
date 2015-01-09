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
package com.helger.commons.microdom.convert.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.text.ISimpleMultiLingualText;
import com.helger.commons.text.impl.MultiLingualText;
import com.helger.commons.text.impl.ReadonlyMultiLingualText;
import com.helger.commons.text.impl.TextProvider;

/**
 * Test class for class {@link MultiLingualTextMicroTypeConverterRegistrar}.
 * 
 * @author Philip Helger
 */
public final class MultiLingualTextMicroTypeConverterRegistrarTest
{
  @Test
  public void testMultiLingualText ()
  {
    final MultiLingualText aMLT = new MultiLingualText ();
    aMLT.setText (Locale.GERMAN, "Cumberlandstraße");
    aMLT.setText (Locale.CHINA, "Whatsoever");

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);
    assertNull (MicroTypeConverter.convertToMicroElement (null, "mtext"));

    final MultiLingualText aMLT2 = MicroTypeConverter.convertToNative (aElement, MultiLingualText.class);
    assertEquals (aMLT, aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, MultiLingualText.class));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aMLT, aMLT2);
  }

  @Test
  public void testReadonlyMultiLingualText ()
  {
    final ReadonlyMultiLingualText aMLT = new ReadonlyMultiLingualText (ContainerHelper.newMap (new Locale [] { Locale.GERMAN,
                                                                                                               Locale.CHINA },
                                                                                                new String [] { "Cumberlandstraße",
                                                                                                               "Whatspever" }));

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);

    final ReadonlyMultiLingualText aMLT2 = MicroTypeConverter.convertToNative (aElement, ReadonlyMultiLingualText.class);
    assertEquals (aMLT, aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, ReadonlyMultiLingualText.class));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aMLT, aMLT2);
  }

  @Test
  public void testTextProvider ()
  {
    final ISimpleMultiLingualText aMLT = TextProvider.create_DE_EN ("de", "en");

    final IMicroElement aElement = MicroTypeConverter.convertToMicroElement (aMLT, "mtext");
    assertNotNull (aElement);

    // The result must be a ReadonlyMultiLingualText because it is the first
    // registered converter
    final ISimpleMultiLingualText aMLT2 = MicroTypeConverter.convertToNative (aElement, ISimpleMultiLingualText.class);
    assertTrue (aMLT2 instanceof ReadonlyMultiLingualText);
    assertEquals (new ReadonlyMultiLingualText (aMLT), aMLT2);
    assertNull (MicroTypeConverter.convertToNative (null, TextProvider.class));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new ReadonlyMultiLingualText (aMLT), aMLT2);
  }
}
