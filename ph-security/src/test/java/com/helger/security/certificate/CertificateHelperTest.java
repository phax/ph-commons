/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.security.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.junit.Test;

/**
 * Test class for class {@link CertificateHelper}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class CertificateHelperTest
{
  @Test
  public void testConvertStringToCertficate () throws CertificateException
  {
    assertNull (CertificateHelper.convertStringToCertficate (null));
    assertNull (CertificateHelper.convertStringToCertficate (""));

    // The web page certificate from Google
    final String sValidCert = "MIIDVDCCAjygAwIBAgIDAjRWMA0GCSqGSIb3DQEBBQUAMEIxCzAJBgNVBAYTAlVT\r\n" +
                              "MRYwFAYDVQQKEw1HZW9UcnVzdCBJbmMuMRswGQYDVQQDExJHZW9UcnVzdCBHbG9i\r\n" +
                              "YWwgQ0EwHhcNMDIwNTIxMDQwMDAwWhcNMjIwNTIxMDQwMDAwWjBCMQswCQYDVQQG\r\n" +
                              "EwJVUzEWMBQGA1UEChMNR2VvVHJ1c3QgSW5jLjEbMBkGA1UEAxMSR2VvVHJ1c3Qg\r\n" +
                              "R2xvYmFsIENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2swYYzD9\r\n" +
                              "9BcjGlZ+W988bDjkcbd4kdS8odhM+KhDtgPpTSEHCIjaWC9mOSm9BXiLnTjoBbdq\r\n" +
                              "fnGk5sRgprDvgOSJKA+eJdbtg/OtppHHmMlCGDUUna2YRpIuT8rxh0PBFpVXLVDv\r\n" +
                              "iS2Aelet8u5fa9IAjbkU+BQVNdnARqN7csiRv8lVK83Qlz6cJmTM386DGXHKTubU\r\n" +
                              "1XupGc1V3sjs0l44U+VcT4wt/lAjNvxm5suOpDkZALeVAjmRCw7+OC7RHQWa9k0+\r\n" +
                              "bw8HHa8sHo9gOeL6NlMTOdReJivbPagUvTLrGAMoUgRx5aszPeE4uwc2hGKceeoW\r\n" +
                              "MPRfwCvocWvk+QIDAQABo1MwUTAPBgNVHRMBAf8EBTADAQH/MB0GA1UdDgQWBBTA\r\n" +
                              "ephojYn7qwVkDBF9qn1luMrMTjAfBgNVHSMEGDAWgBTAephojYn7qwVkDBF9qn1l\r\n" +
                              "uMrMTjANBgkqhkiG9w0BAQUFAAOCAQEANeMpauUvXVSOKVCUn5kaFOSPeCpilKIn\r\n" +
                              "Z57QzxpeR+nBsqTP3UEaBU6bS+5Kb1VSsyShNwrrZHYqLizz/Tt1kL/6cdjHPTfS\r\n" +
                              "tQWVYrmm3ok9Nns4d0iXrKYgjy6myQzCsplFAMfOEVEiIuCl6rYVSAlk6l5PdPcF\r\n" +
                              "PseKUgzbFbS9bZvlxrFUaKnjaZC2mqUPuLk/IH2uSrW4nOQdtqvmlKXBx4Ot2/Un\r\n" +
                              "hw4EbNX/3aBd7YdStysVAq45pmp06drE57xNNB6pXE0zX5IJL4hmXXeXxx12E6nV\r\n" +
                              "5fEWCRE11azbJHFwLJhWC9kXtNHjUStedejV0NxPNO3CBWaAocvmMw==";
    X509Certificate aCert = CertificateHelper.convertStringToCertficate (sValidCert);
    assertNotNull (aCert);
    assertEquals ("CN=GeoTrust Global CA, O=GeoTrust Inc., C=US", aCert.getIssuerDN ().getName ());

    aCert = CertificateHelper.convertStringToCertficate (sValidCert + "\r\n");
    assertNotNull (aCert);

    aCert = CertificateHelper.convertStringToCertficate (CertificateHelper.BEGIN_CERTIFICATE +
                                                         "\r\n" +
                                                         sValidCert +
                                                         "\r\n" +
                                                         CertificateHelper.END_CERTIFICATE);
    assertNotNull (aCert);

    aCert = CertificateHelper.convertStringToCertficate ("\r\n" +
                                                         CertificateHelper.BEGIN_CERTIFICATE +
                                                         "\r\n" +
                                                         sValidCert +
                                                         "\r\n" +
                                                         CertificateHelper.END_CERTIFICATE +
                                                         "\r\n");
    assertNotNull (aCert);

    aCert = CertificateHelper.convertStringToCertficate ("\r\n" +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         CertificateHelper.BEGIN_CERTIFICATE +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         sValidCert +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         CertificateHelper.END_CERTIFICATE +
                                                         "\r\n" +
                                                         "\r\n" +
                                                         "\r\n");
    assertNotNull (aCert);

    aCert = CertificateHelper.convertStringToCertficate ("\r\n" +
                                                         CertificateHelper.BEGIN_CERTIFICATE_INVALID +
                                                         "\r\n" +
                                                         sValidCert +
                                                         "\r\n" +
                                                         CertificateHelper.END_CERTIFICATE_INVALID +
                                                         "\r\n");
    assertNotNull (aCert);

    try
    {
      CertificateHelper.convertStringToCertficate ("abc");
      fail ();
    }
    catch (final CertificateException ex)
    {
      // expected
    }
    try
    {
      CertificateHelper.convertStringToCertficate ("abcd");
      fail ();
    }
    catch (final CertificateException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetRFC1421CompliantString ()
  {
    assertNull (CertificateHelper.getRFC1421CompliantString (null, true));
    assertNull (CertificateHelper.getRFC1421CompliantString (null, false));
    assertNull (CertificateHelper.getRFC1421CompliantString ("", true));
    assertNull (CertificateHelper.getRFC1421CompliantString ("", false));

    // for up to 64 chars it makes no difference
    for (int i = 1; i <= 64; ++i)
    {
      final char [] aChars = new char [i];
      Arrays.fill (aChars, 'a');
      final String sText = new String (aChars);
      assertEquals (CertificateHelper.BEGIN_CERTIFICATE +
                    "\n" +
                    sText +
                    "\n" +
                    CertificateHelper.END_CERTIFICATE,
                    CertificateHelper.getRFC1421CompliantString (sText, true));
      assertEquals (sText, CertificateHelper.getRFC1421CompliantString (sText, false));
    }

    final String sLong = "123456789012345678901234567890123456789012345678901234567890abcd" +
                         "123456789012345678901234567890123456789012345678901234567890abcd" +
                         "xyz";
    assertEquals (CertificateHelper.BEGIN_CERTIFICATE +
                  "\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\r\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\r\n" +
                  "xyz" +
                  "\n" +
                  CertificateHelper.END_CERTIFICATE,
                  CertificateHelper.getRFC1421CompliantString (sLong, true));
    assertEquals (CertificateHelper.BEGIN_CERTIFICATE +
                  "\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\n" +
                  "xyz" +
                  "\n" +
                  CertificateHelper.END_CERTIFICATE,
                  CertificateHelper.getRFC1421CompliantString (sLong, true, "\n"));
    assertEquals ("123456789012345678901234567890123456789012345678901234567890abcd\r\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\r\n" +
                  "xyz",
                  CertificateHelper.getRFC1421CompliantString (sLong, false));
    assertEquals ("123456789012345678901234567890123456789012345678901234567890abcd\n" +
                  "123456789012345678901234567890123456789012345678901234567890abcd\n" +
                  "xyz",
                  CertificateHelper.getRFC1421CompliantString (sLong, false, "\n"));
  }
}
