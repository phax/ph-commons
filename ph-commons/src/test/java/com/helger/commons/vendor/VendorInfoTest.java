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
package com.helger.commons.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

/**
 * Test class for class {@link VendorInfo}.
 *
 * @author Philip Helger
 */
public final class VendorInfoTest
{
  @Test
  public void testHeaderLines ()
  {
    final List <String> aList = VendorInfo.getFileHeaderLines ();
    assertNotNull (aList);
    assertFalse (aList.isEmpty ());
  }

  @Test
  public void testInceptionYear ()
  {
    assertEquals (VendorInfo.DEFAULT_INCEPTION_YEAR, VendorInfo.getInceptionYear ());
    VendorInfo.setInceptionYear (1999);
    assertEquals (1999, VendorInfo.getInceptionYear ());
    VendorInfo.setInceptionYear (VendorInfo.DEFAULT_INCEPTION_YEAR);
    assertEquals (VendorInfo.DEFAULT_INCEPTION_YEAR, VendorInfo.getInceptionYear ());
  }

  @Test
  public void testEmail ()
  {
    assertEquals (VendorInfo.DEFAULT_VENDOR_EMAIL_SUFFIX, VendorInfo.getVendorEmailSuffix ());
    assertEquals (VendorInfo.DEFAULT_VENDOR_EMAIL, VendorInfo.getVendorEmail ());
    VendorInfo.setVendorEmail ("abc@def.com");
    assertEquals ("@def.com", VendorInfo.getVendorEmailSuffix ());
    assertEquals ("abc@def.com", VendorInfo.getVendorEmail ());
    VendorInfo.setVendorEmail (VendorInfo.DEFAULT_VENDOR_EMAIL);
    assertEquals (VendorInfo.DEFAULT_VENDOR_EMAIL_SUFFIX, VendorInfo.getVendorEmailSuffix ());
    assertEquals (VendorInfo.DEFAULT_VENDOR_EMAIL, VendorInfo.getVendorEmail ());
  }

  @Test
  public void testURL ()
  {
    assertEquals (VendorInfo.DEFAULT_VENDOR_URL_WITHOUT_PROTOCOL, VendorInfo.getVendorURLWithoutProtocol ());
    assertEquals (VendorInfo.DEFAULT_VENDOR_URL, VendorInfo.getVendorURL ());
    VendorInfo.setVendorURL ("www.example.org");
    assertEquals ("www.example.org", VendorInfo.getVendorURLWithoutProtocol ());
    assertEquals ("http://www.example.org", VendorInfo.getVendorURL ());
    VendorInfo.setVendorURL ("http://example.com");
    assertEquals ("example.com", VendorInfo.getVendorURLWithoutProtocol ());
    assertEquals ("http://example.com", VendorInfo.getVendorURL ());
    VendorInfo.setVendorURL ("https://examples.com");
    assertEquals ("examples.com", VendorInfo.getVendorURLWithoutProtocol ());
    assertEquals ("https://examples.com", VendorInfo.getVendorURL ());
    VendorInfo.setVendorURL (VendorInfo.DEFAULT_VENDOR_URL);
    assertEquals (VendorInfo.DEFAULT_VENDOR_URL_WITHOUT_PROTOCOL, VendorInfo.getVendorURLWithoutProtocol ());
    assertEquals (VendorInfo.DEFAULT_VENDOR_URL, VendorInfo.getVendorURL ());
  }
}
