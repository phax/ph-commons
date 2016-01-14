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
package com.helger.commons.xml.ls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Permission;
import java.util.HashMap;

import javax.xml.XMLConstants;

import org.apache.felix.framework.FrameworkFactory;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.ClassHelper;

/**
 * Test class for class {@link SimpleLSResourceResolver}.
 *
 * @author Philip Helger
 */
public final class SimpleLSResourceResolverTest
{
  public static final class LoggingSecurityManager extends SecurityManager
  {
    private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingSecurityManager.class);

    @Override
    public void checkPermission (final Permission perm)
    {
      s_aLogger.info (perm.toString ());
    }

    @Override
    public void checkPermission (final Permission perm, final Object context)
    {
      s_aLogger.info ("[CTX] " + context + ": " + perm.toString ());
    }
  }

  static
  {
    // Just for playing around :)
    if (false)
      System.setSecurityManager (new LoggingSecurityManager ());
  }

  @Test
  public void testDoStandardResourceResolving () throws IOException
  {
    final String sBaseDir = new File (".").getCanonicalFile ().getAbsolutePath () + File.separatorChar;
    final String sBaseDirParent = new File (".").getCanonicalFile ().getParentFile ().getAbsolutePath () + File.separatorChar;

    IReadableResource aRes;

    // Using URLs as the base
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com", aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("dir/file.txt", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt", "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    // system ID is a fixed URL
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("http://www.example.org/file.txt", "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    // Using files as the basis
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "abc/dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    // system ID has a protocol prefix
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "abc/dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../../dir/include.xml", "abc/def/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    {
      // system ID has a protocol prefix with a single slash
      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/dir/include.xml", "pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/../dir/include.xml", "pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/../dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/../../dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:/../../dir/include.xml", "abc/def/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:///dir/include.xml", "abc/def/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());
    }

    // system ID is a fixed URL
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("http://www.example.org/file.txt", "abc/pom.xml");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    // system ID is a fixed URL
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("http://www.example.org/file.txt", null);
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving (null, "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith ("pom.xml"));

    // System URL contains paths and base URL is a classpath resource
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt", "cp:/abc/orig.file");
    assertTrue (aRes instanceof ClassPathResource);
    assertEquals ("/dir/file.txt", aRes.getPath ());

    // System URL contains paths and base URL is a URL
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt", "http://www.helger.com/abc/orig.file");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    // Base URL is a JAR URL
    {
      final URL aURL = ClassHelper.getResource (SimpleLSResourceResolverTest.class, "org/junit/rules/TestRule.class");
      assertNotNull (aURL);
      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../Assert.class", aURL.toExternalForm ());
      assertTrue (aRes instanceof URLResource);
      assertTrue (aRes.getPath (), aRes.getPath ().endsWith ("!/org/junit/Assert.class"));
      assertTrue (aRes.exists ());
    }

    try
    {
      // Both null is not OK
      SimpleLSResourceResolver.doStandardResourceResolving (null, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testOSGIBundle () throws BundleException
  {
    LSInput aRes;

    // Initializing Apache Felix as OSGI container is required to get the
    // "bundle" URL protocol installed correctly
    // Otherwise the first call would end up as a "file" resource ;-)
    final Framework aOSGI = new FrameworkFactory ().newFramework (new HashMap <String, String> ());
    aOSGI.start ();
    try
    {
      // Bundle 0 is the org.apache.felix.framework bundle
      final Bundle b = aOSGI.getBundleContext ().getBundle (0);
      assertNotNull (b);
      assertTrue (b.getState () == Bundle.ACTIVE);

      // No leading slash is important as the ClassLoader is used!
      assertNotNull (b.getResource ("org/apache/felix/framework/util/Mutex.class"));

      final LSResourceResolver aRR = new SimpleLSResourceResolver ();

      // No class loader
      aRes = aRR.resolveResource (XMLConstants.W3C_XML_SCHEMA_NS_URI,
                                  null,
                                  null,
                                  "../Felix.class",
                                  "bundle://0.0:1/org/apache/felix/framework/util/Mutex.class");
      assertTrue (aRes instanceof ResourceLSInput);
      final IHasInputStream aISP = ((ResourceLSInput) aRes).getInputStreamProvider ();
      assertTrue (aISP instanceof URLResource);
      // Path maybe a "jar:file:" resource
      assertTrue (((URLResource) aISP).getPath ().endsWith ("org/apache/felix/framework/Felix.class"));
    }
    finally
    {
      aOSGI.stop ();
    }
  }
}
