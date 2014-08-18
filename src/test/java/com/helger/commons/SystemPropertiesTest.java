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
package com.helger.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.AbstractPhlocTestCase;

/**
 * Test class for class {@link SystemProperties}
 * 
 * @author Philip Helger
 */
public final class SystemPropertiesTest extends AbstractPhlocTestCase
{
  @Test
  public void testGetAll ()
  {
    for (final Map.Entry <String, String> aEntry : ContainerHelper.getSortedByKey (SystemProperties.getAllProperties ())
                                                                  .entrySet ())
      m_aLogger.info ("[all] " + aEntry.getKey () + "=" + aEntry.getValue ());

    for (final String sKey : SystemProperties.getAllPropertyNames ())
      assertNotNull (SystemProperties.getPropertyValue (sKey));

    assertNotNull (SystemProperties.getJavaVersion ());
    assertNotNull (SystemProperties.getJavaVendor ());
    assertNotNull (SystemProperties.getJavaVendorURL ());
    assertNotNull (SystemProperties.getJavaHome ());
    assertNotNull (SystemProperties.getJavaClassVersion ());
    assertNotNull (SystemProperties.getJavaClassPath ());
    assertNotNull (SystemProperties.getOsName ());
    assertNotNull (SystemProperties.getOsArch ());
    assertNotNull (SystemProperties.getOsVersion ());
    assertNotNull (SystemProperties.getFileSeparator ());
    assertNotNull (SystemProperties.getPathSeparator ());
    assertNotNull (SystemProperties.getLineSeparator ());
    assertNotNull (SystemProperties.getUserName ());
    assertNotNull (SystemProperties.getUserHome ());
    assertNotNull (SystemProperties.getUserDir ());
    assertNotNull (SystemProperties.getJavaVmSpecificationVersion ());
    assertNotNull (SystemProperties.getJavaVmSpecificationVendor ());
    SystemProperties.getJavaVmSpecificationUrl (); // is null :(
    assertNotNull (SystemProperties.getJavaVmVersion ());
    assertNotNull (SystemProperties.getJavaVmVendor ());
    SystemProperties.getJavaVmUrl (); // is null :(
    assertNotNull (SystemProperties.getJavaSpecificationVersion ());
    assertNotNull (SystemProperties.getJavaSpecificationVendor ());
    SystemProperties.getJavaSpecificationUrl (); // is null;
    SystemProperties.getTmpDir ();
    for (final String sName : SystemProperties.getAllProperties ().keySet ())
      assertTrue (SystemProperties.containsPropertyName (sName));
  }

  @Test
  public void testRemove ()
  {
    // Test remove
    assertFalse (SystemProperties.containsPropertyName ("phloc.x"));
    SystemProperties.setPropertyValue ("phloc.x", "y");
    assertTrue (SystemProperties.containsPropertyName ("phloc.x"));
    assertEquals ("y", SystemProperties.removePropertyValue ("phloc.x"));
    assertFalse (SystemProperties.containsPropertyName ("phloc.x"));
    assertNull (SystemProperties.removePropertyValue ("phloc.x"));

    // Test set to null
    SystemProperties.setPropertyValue ("phloc.x", "y");
    assertTrue (SystemProperties.containsPropertyName ("phloc.x"));
    SystemProperties.setPropertyValue ("phloc.x", null);
    assertFalse (SystemProperties.containsPropertyName ("phloc.x"));
  }

  @Test
  public void testXMLProperties ()
  {
    assertTrue (SystemProperties.getXMLEntityExpansionLimit () > 0);
    assertEquals (10000, SystemProperties.getXMLElementAttributeLimit ());
    assertEquals (5000, SystemProperties.getXMLMaxOccur ());
    assertEquals (5 * (int) 10E7, SystemProperties.getXMLTotalEntitySizeLimit ());
    assertEquals (0, SystemProperties.getXMLMaxGeneralEntitySizeLimit ());
    assertEquals (0, SystemProperties.getXMLMaxParameterEntitySizeLimit ());
  }
}
