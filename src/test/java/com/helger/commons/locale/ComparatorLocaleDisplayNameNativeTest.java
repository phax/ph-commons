/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.locale;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.AbstractPhlocTestCase;

/**
 * Test class for class {@link ComparatorLocaleDisplayNameNative}.
 * 
 * @author Philip Helger
 */
public final class ComparatorLocaleDisplayNameNativeTest extends AbstractPhlocTestCase
{
  @Test
  public void testAll ()
  {
    final Set <Locale> aAll = LocaleCache.getAllLocales ();
    assertEquals (aAll.size (), ContainerHelper.getSorted (aAll, new ComparatorLocaleDisplayNameNative (L_DE)).size ());
  }
}
