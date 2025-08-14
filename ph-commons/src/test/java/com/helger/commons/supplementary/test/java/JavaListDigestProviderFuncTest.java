/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.supplementary.test.java;

import java.security.Provider;
import java.security.Security;
import java.util.Comparator;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.helper.CollectionHelperExt;

public final class JavaListDigestProviderFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaListDigestProviderFuncTest.class);

  @Test
  @Ignore ("Too verbose")
  public void testListAllDigestProvider ()
  {
    for (final Provider aElement : new CommonsArrayList <> (Security.getProviders ()).getSortedInline (Comparator.comparing (Provider::getName)))
    {
      LOGGER.info ("Provider: '" + aElement + "'");

      for (final Object sAlgo : CollectionHelperExt.getSortedByKey (aElement, Comparator.comparing (Object::toString)).keySet ())
        LOGGER.info ("\t" + sAlgo);
    }
  }
}
