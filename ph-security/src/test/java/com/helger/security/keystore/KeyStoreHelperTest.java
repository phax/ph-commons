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
package com.helger.security.keystore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.ClassPathResource;

/**
 * Test class for class {@link KeyStoreHelper}.
 *
 * @author Philip Helger
 */
public final class KeyStoreHelperTest
{
  @BeforeClass
  public static void init ()
  {
    Security.addProvider (new BouncyCastleProvider ());
  }

  private static KeyPair _createKeyPair (final int nKeySizeInBits) throws Exception
  {
    final KeyPairGenerator aGenerator = KeyPairGenerator.getInstance ("RSA");
    aGenerator.initialize (nKeySizeInBits);
    final KeyPair keyPair = aGenerator.generateKeyPair ();
    return keyPair;
  }

  private static X509Certificate _createX509V1Certificate (final KeyPair aKeyPair) throws Exception
  {
    // generate the certificate
    final PublicKey aPublicKey = aKeyPair.getPublic ();
    final PrivateKey aPrivateKey = aKeyPair.getPrivate ();
    final ContentSigner aContentSigner = new JcaContentSignerBuilder ("SHA256WithRSA").setProvider (BouncyCastleProvider.PROVIDER_NAME)
                                                                                      .build (aPrivateKey);

    final X509CertificateHolder aCertHolder = new JcaX509v1CertificateBuilder (new X500Principal ("CN=Test Certificate"),
                                                                               BigInteger.valueOf (System.currentTimeMillis ()),
                                                                               new Date (System.currentTimeMillis () -
                                                                                         50000),
                                                                               new Date (System.currentTimeMillis () +
                                                                                         50000),
                                                                               new X500Principal ("CN=Test Certificate"),
                                                                               aPublicKey).build (aContentSigner);
    // Convert to JCA X509Certificate
    return new JcaX509CertificateConverter ().getCertificate (aCertHolder);
  }

  @Test
  public void testLoadKeyStoreDirect () throws Exception
  {
    final KeyPair aKeyPair = _createKeyPair (1024);
    final Certificate [] certs = { _createX509V1Certificate (aKeyPair), _createX509V1Certificate (aKeyPair) };

    // Load from classpath
    KeyStore ks = KeyStoreHelper.loadKeyStoreDirect ("keystores/keystore-no-pw.jks", (String) null);
    assertEquals (KeyStoreHelper.KEYSTORE_TYPE_JKS, ks.getType ());
    assertEquals (1, CollectionHelper.getSize (ks.aliases ()));
    assertTrue (ks.containsAlias ("1"));
    final Certificate c1 = ks.getCertificate ("1");
    assertNotNull (c1);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    // Load from file
    ks = KeyStoreHelper.loadKeyStoreDirect (new ClassPathResource ("keystores/keystore-no-pw.jks").getAsFile ()
                                                                                                  .getAbsolutePath (),
                                            (String) null);
    assertEquals (KeyStoreHelper.KEYSTORE_TYPE_JKS, ks.getType ());
    assertEquals (1, CollectionHelper.getSize (ks.aliases ()));
    assertTrue (ks.containsAlias ("1"));
    assertNotNull (ks.getCertificate ("1"));
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    // Load from classpath
    ks = KeyStoreHelper.loadKeyStoreDirect ("keystores/keystore-pw-peppol.jks", (String) null);
    assertEquals (1, CollectionHelper.getSize (ks.aliases ()));
    assertTrue (ks.containsAlias ("1"));
    final Certificate c2 = ks.getCertificate ("1");
    assertNotNull (c2);
    assertEquals (c1, c2);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    // Load from file
    ks = KeyStoreHelper.loadKeyStoreDirect (new ClassPathResource ("keystores/keystore-pw-peppol.jks").getAsFile ()
                                                                                                      .getAbsolutePath (),
                                            (String) null);
    assertEquals (1, CollectionHelper.getSize (ks.aliases ()));
    assertTrue (ks.containsAlias ("1"));
    assertNotNull (ks.getCertificate ("1"));
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    ks = KeyStoreHelper.loadKeyStoreDirect ("keystores/keystore-pw-peppol.jks", "peppol");
    assertEquals (1, CollectionHelper.getSize (ks.aliases ()));
    assertTrue (ks.containsAlias ("1"));
    final Certificate c3 = ks.getCertificate ("1");
    assertNotNull (c3);
    assertEquals (c2, c3);
    ks.setKeyEntry ("2", aKeyPair.getPrivate (), "key2".toCharArray (), certs);

    try
    {
      // Non-existing file
      KeyStoreHelper.loadKeyStoreDirect ("keystores/keystore-not-existing.jks", (String) null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // Invalid password
      KeyStoreHelper.loadKeyStoreDirect ("keystores/keystore-pw-peppol.jks", "wrongpw");
      fail ();
    }
    catch (final IOException ex)
    {}
  }

  @Test
  public void testLoadKeyStore () throws Exception
  {
    LoadedKeyStore ks = KeyStoreHelper.loadKeyStore ("keystores/keystore-no-pw.jks", (String) null);
    assertNotNull (ks);
    assertTrue (ks.isSuccess ());
    assertNotNull (ks.getKeyStore ());
    assertNull (ks.getError ());
    assertEquals (1, CollectionHelper.getSize (ks.getKeyStore ().aliases ()));

    ks = KeyStoreHelper.loadKeyStore ("keystores/keystore-pw-peppol.jks", "peppol");
    assertNotNull (ks);
    assertTrue (ks.isSuccess ());
    assertNotNull (ks.getKeyStore ());
    assertNull (ks.getError ());
    assertEquals (1, CollectionHelper.getSize (ks.getKeyStore ().aliases ()));

    // Non-existing file
    ks = KeyStoreHelper.loadKeyStore ("keystores/keystore-not-existing.jks", (String) null);
    assertNotNull (ks);
    assertTrue (ks.isFailure ());
    assertNull (ks.getKeyStore ());
    assertEquals (EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_NON_EXISTING, ks.getError ());

    // Invalid password
    ks = KeyStoreHelper.loadKeyStore ("keystores/keystore-pw-peppol.jks", "wrongpw");
    assertNotNull (ks);
    assertTrue (ks.isFailure ());
    assertNull (ks.getKeyStore ());
    assertEquals (EKeyStoreLoadError.KEYSTORE_INVALID_PASSWORD, ks.getError ());

    // Not a keystore
    ks = KeyStoreHelper.loadKeyStore ("keystores/no-keystore.txt", "wrongpw");
    assertNotNull (ks);
    assertTrue (ks.isFailure ());
    assertNull (ks.getKeyStore ());
    assertEquals (EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_FORMAT_ERROR, ks.getError ());

    // Non existing file
    ks = KeyStoreHelper.loadKeyStore ("keystores/non-existing-keystore.jks", "any");
    assertNotNull (ks);
    assertTrue (ks.isFailure ());
    assertNull (ks.getKeyStore ());
    assertEquals (EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_NON_EXISTING, ks.getError ());
  }

  private static final String TRUSTSTORE_PRODUCTION_ALIAS_ROOT = "peppol root ca";
  private static final String TRUSTSTORE_PRODUCTION_ALIAS_AP = "peppol access point ca (peppol root ca)";
  private static final String TRUSTSTORE_PRODUCTION_ALIAS_SMP = "peppol service metadata publisher ca (peppol root ca)";
  private static final String TRUSTSTORE_PILOT_ALIAS_ROOT = "peppol root test ca";
  private static final String TRUSTSTORE_PILOT_ALIAS_AP = "peppol access point test ca (peppol root test ca)";
  private static final String TRUSTSTORE_PILOT_ALIAS_SMP = "peppol service metadata publisher test ca (peppol root test ca)";

  @Test
  public void testLoadPeppolTrustStoreProduction () throws Exception
  {
    // Load trust store
    final KeyStore aTrustStore = KeyStoreHelper.loadKeyStoreDirect ("keystores/truststore-peppol-prod.jks", "peppol");
    assertNotNull (aTrustStore);

    // Additionally the STS certificate is contained
    assertEquals (4, CollectionHelper.getSize (aTrustStore.aliases ()));

    // Ensure all name entries are contained
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PRODUCTION_ALIAS_ROOT));
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PRODUCTION_ALIAS_AP));
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PRODUCTION_ALIAS_SMP));

    final X509Certificate aCertAPOld = (X509Certificate) aTrustStore.getCertificate (TRUSTSTORE_PRODUCTION_ALIAS_AP);
    final String sIssuerName = aCertAPOld.getIssuerX500Principal ().getName ();
    assertEquals ("CN=PEPPOL Root CA,O=NATIONAL IT AND TELECOM AGENCY,C=DK", sIssuerName);
    final String sSubjectName = aCertAPOld.getSubjectX500Principal ().getName ();
    assertEquals ("CN=PEPPOL ACCESS POINT CA,O=NATIONAL IT AND TELECOM AGENCY,C=DK", sSubjectName);
  }

  @Test
  public void testLoadPeppolTrustStorePilot () throws Exception
  {
    // Load trust store
    final KeyStore aTrustStore = KeyStoreHelper.loadKeyStoreDirect ("keystores/truststore-peppol-pilot.jks", "peppol");
    assertNotNull (aTrustStore);

    // Additionally the STS certificate is contained
    assertEquals (4, CollectionHelper.getSize (aTrustStore.aliases ()));

    // Ensure all name entries are contained
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PILOT_ALIAS_ROOT));
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PILOT_ALIAS_AP));
    assertNotNull (aTrustStore.getCertificate (TRUSTSTORE_PILOT_ALIAS_SMP));

    final X509Certificate aCertAPOld = (X509Certificate) aTrustStore.getCertificate (TRUSTSTORE_PILOT_ALIAS_AP);
    final String sIssuerName = aCertAPOld.getIssuerX500Principal ().getName ();
    assertEquals ("CN=PEPPOL Root TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK",
                  sIssuerName);
    final String sSubjectName = aCertAPOld.getSubjectX500Principal ().getName ();
    assertEquals ("CN=PEPPOL ACCESS POINT TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK",
                  sSubjectName);
  }
}
