/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.util.HashMap;

import org.apache.felix.framework.FrameworkFactory;
import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;

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
    assertEquals ("http://www.helger.com/../dir/file.txt", aRes.getPath ());

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt", "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    // system ID is a fixed URL
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("http://www.example.org/file.txt",
                                                                 "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    // Using files as the basis
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("abc/dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

    // system ID has a protocol prefix
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("abc/dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("file:../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith (new File ("dir/include.xml").getPath ()));

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

    // System URL contains paths and base URL is a file
    aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../dir/file.txt",
                                                                 "http://www.helger.com/abc/orig.file");
    assertTrue (aRes instanceof URLResource);
    // This looks weird, but the URL cannot determine whether "file.txt" is a
    // file or a directory!
    assertEquals ("http://www.helger.com/abc/dir/file.txt", aRes.getPath ());

    try
    {
      SimpleLSResourceResolver.doStandardResourceResolving (null, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testOSGIBundle () throws IOException, BundleException
  {
    IReadableResource aRes;

    // Initializing Apache Felix as OSGI container is required to get the
    // "bundle" URL protocol installed correctly
    // Otherwise the first call would end up as a "file" resource ;-)
    final Framework aOSGI = new FrameworkFactory ().newFramework (new HashMap <String, String> ());
    aOSGI.start ();
    try
    {
      // No class loader
      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../common/UBL-CommonAggregateComponents-2.1.xsd",
                                                                   "bundle://23.0:1/schemas/ubl21/maindoc/UBL-ApplicationResponse-2.1.xsd");
      assertTrue (aRes instanceof URLResource);
      // Wrong result!
      assertEquals ("bundle://23.0:1/schemas/ubl21/maindoc/common/UBL-CommonAggregateComponents-2.1.xsd",
                    aRes.getPath ());

      // With class loader
      aRes = SimpleLSResourceResolver.doStandardResourceResolving ("../common/UBL-CommonAggregateComponents-2.1.xsd",
                                                                   "bundle://23.0:1/schemas/ubl21/maindoc/UBL-ApplicationResponse-2.1.xsd",
                                                                   getClass ().getClassLoader ());
      assertTrue (aRes instanceof ClassPathResource);
      // Correct result!
      assertEquals ("bundle://23.0:1/schemas/ubl21/common/UBL-CommonAggregateComponents-2.1.xsd", aRes.getPath ());

    }
    finally
    {
      aOSGI.stop ();
    }
  }
}
