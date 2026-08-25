/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

/** Verifies the standard X.509 helpers without Bouncy Castle or {@code ph-bc}. */
public final class PhSecurityWithoutBCTest
{
  private static void _assertClassIsHidden (@NonNull final ClassLoader aClassLoader, @NonNull final String sClassName)
  {
    try
    {
      aClassLoader.loadClass (sClassName);
      fail (sClassName + " must not be visible to the isolated class loader");
    }
    catch (final ClassNotFoundException ex)
    {
      // Expected
    }
  }

  @Test
  public void testStandardX509HelpersWithoutBC () throws Exception
  {
    final URL [] aURLs = { new File ("target/classes").toURI ().toURL (), new File ("target/test-classes").toURI ()
                                                                                                          .toURL () };
    try (final URLClassLoader aCL = new URLClassLoader (aURLs, getClass ().getClassLoader ())
    {
      @Override
      protected Class <?> loadClass (final String sName, final boolean bResolve) throws ClassNotFoundException
      {
        if (sName.startsWith ("org.bouncycastle.") || sName.startsWith ("com.helger.bc."))
          throw new ClassNotFoundException ("Bouncy Castle deliberately hidden from test class loader");

        if (sName.startsWith ("com.helger.security."))
          synchronized (getClassLoadingLock (sName))
          {
            Class <?> ret = findLoadedClass (sName);
            if (ret == null)
              ret = findClass (sName);
            if (bResolve)
              resolveClass (ret);
            return ret;
          }

        return super.loadClass (sName, bResolve);
      }
    })
    {
      _assertClassIsHidden (aCL, "org.bouncycastle.asn1.ASN1Primitive");
      _assertClassIsHidden (aCL, "com.helger.bc.PBCProvider");

      final Class <?> aProbeClass = Class.forName (PhSecurityWithoutBCProbe.class.getName (), true, aCL);
      final String sResult = (String) aProbeClass.getMethod ("verify").invoke (null);
      assertEquals ("http://pki-crl.symauth.com/ca_6a937734a393a0805bf33cda8b331093/LatestCRL.crl|1|0,6", sResult);
    }
  }
}
