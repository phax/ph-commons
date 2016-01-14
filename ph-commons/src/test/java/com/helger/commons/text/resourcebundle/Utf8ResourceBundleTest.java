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
package com.helger.commons.text.resourcebundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link Utf8ResourceBundle}.
 *
 * @author Philip Helger
 */
public final class Utf8ResourceBundleTest extends AbstractCommonsTestCase
{
  @Test
  public void testAll ()
  {
    assertNotNull (Utf8ResourceBundle.getBundle ("properties/test-utf8"));
    assertNotNull (Utf8ResourceBundle.getBundle ("properties/test-utf8", L_DE));
    final ResourceBundle rb = Utf8ResourceBundle.getBundle ("properties/test-utf8",
                                                            L_DE,
                                                            ClassLoaderHelper.getDefaultClassLoader ());
    assertNotNull (rb);
    assertTrue (rb instanceof Utf8PropertyResourceBundle);
    assertEquals (2, CollectionHelper.newList (rb.getKeys ()).size ());
  }
}
