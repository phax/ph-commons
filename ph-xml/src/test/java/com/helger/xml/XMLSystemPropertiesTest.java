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
package com.helger.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.base.system.EJavaVersion;

/**
 * Test class for class {@link XMLSystemProperties}
 *
 * @author Philip Helger
 */
public final class XMLSystemPropertiesTest
{
  @Test
  public void testXMLProperties ()
  {
    final boolean bIsJava24Plus = EJavaVersion.getCurrentVersion ().isNewerOrEqualsThan (EJavaVersion.JDK_24);

    assertEquals (bIsJava24Plus ? 2_500 : 64_000, XMLSystemProperties.getXMLEntityExpansionLimit ());
    assertEquals (bIsJava24Plus ? 200 : 10_000, XMLSystemProperties.getXMLElementAttributeLimit ());
    assertEquals (5000, XMLSystemProperties.getXMLMaxOccur ());
    assertEquals (bIsJava24Plus ? 100_000 : 50_000_000, XMLSystemProperties.getXMLTotalEntitySizeLimit ());
    assertEquals (0, XMLSystemProperties.getXMLMaxGeneralEntitySizeLimit ());
    assertEquals (0, XMLSystemProperties.getXMLMaxParameterEntitySizeLimit ());
  }
}
