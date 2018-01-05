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
package com.helger.commons.io.resourceresolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.ClassHelper;

/**
 * Test class for class {@link DefaultResourceResolver}.
 *
 * @author Philip Helger
 */
public final class DefaultResourceResolverTest
{
  @Test
  public void testBasic () throws IOException
  {
    final String sBaseDir = new File (".").getCanonicalFile ().getAbsolutePath () + File.separatorChar;
    final String sBaseDirParent = new File (".").getCanonicalFile ().getParentFile ().getAbsolutePath () +
                                  File.separatorChar;

    IReadableResource aRes;

    // Using URLs as the base
    aRes = DefaultResourceResolver.getResolvedResource ("", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com", aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("dir/file.txt", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("../dir/file.txt", "http://www.helger.com");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("../dir/file.txt", "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    // system ID is a fixed URL
    aRes = DefaultResourceResolver.getResolvedResource ("http://www.example.org/file.txt",
                                                        "http://www.helger.com/abc/");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    // Using files as the basis
    aRes = DefaultResourceResolver.getResolvedResource ("dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "abc/dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    // system ID has a protocol prefix
    aRes = DefaultResourceResolver.getResolvedResource ("file:dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("file:dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "abc/dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("file:../dir/include.xml", "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("file:../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("file:../../dir/include.xml", "abc/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDirParent, "dir/include.xml").getPath (), aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource ("file:../../dir/include.xml", "abc/def/pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertEquals (new File (sBaseDir, "dir/include.xml").getPath (), aRes.getPath ());

    {
      // system ID has a protocol prefix with a single slash
      aRes = DefaultResourceResolver.getResolvedResource ("file:/dir/include.xml", "pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:/dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:/../dir/include.xml", "pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:/../dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:/../../dir/include.xml", "abc/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:/../../dir/include.xml", "abc/def/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());

      aRes = DefaultResourceResolver.getResolvedResource ("file:///dir/include.xml", "abc/def/pom.xml");
      assertTrue (aRes instanceof FileSystemResource);
      assertEquals (new File ("/dir/include.xml").getAbsolutePath (), aRes.getPath ());
    }

    // system ID is a fixed URL
    aRes = DefaultResourceResolver.getResolvedResource ("http://www.example.org/file.txt", "abc/pom.xml");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    // system ID is a fixed URL
    aRes = DefaultResourceResolver.getResolvedResource ("http://www.example.org/file.txt", null);
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.example.org/file.txt", aRes.getPath ());

    aRes = DefaultResourceResolver.getResolvedResource (null, "pom.xml");
    assertTrue (aRes instanceof FileSystemResource);
    assertTrue (aRes.getPath (), aRes.getPath ().endsWith ("pom.xml"));

    // System URL contains paths and base URL is a classpath resource
    aRes = DefaultResourceResolver.getResolvedResource ("../dir/file.txt", "cp:/abc/orig.file");
    assertTrue (aRes instanceof ClassPathResource);
    assertEquals ("/dir/file.txt", aRes.getPath ());

    // System URL contains paths and base URL is a URL
    aRes = DefaultResourceResolver.getResolvedResource ("../dir/file.txt", "http://www.helger.com/abc/orig.file");
    assertTrue (aRes instanceof URLResource);
    assertEquals ("http://www.helger.com/dir/file.txt", aRes.getPath ());

    // Base URL is a JAR URL
    {
      final URL aURL = ClassHelper.getResource (DefaultResourceResolverTest.class, "org/junit/rules/TestRule.class");
      assertNotNull (aURL);
      aRes = DefaultResourceResolver.getResolvedResource ("../Assert.class", aURL.toExternalForm ());
      assertTrue (aRes instanceof URLResource);
      assertTrue (aRes.getPath (), aRes.getPath ().endsWith ("!/org/junit/Assert.class"));
      assertTrue (aRes.exists ());
    }

    // Issue #8
    {
      aRes = DefaultResourceResolver.getResolvedResource ("file.txt",
                                                          "jar:file:/C:/Users/tore99/IdeaProjects/pia/pia-webapp/build/libs/pia.jar!/BOOT-INF/lib/ph-ubl21-3.3.0.jar!/schemas/ubl21/maindoc/UBL-Invoice-2.1.xsd");
      assertEquals ("jar:file:/C:/Users/tore99/IdeaProjects/pia/pia-webapp/build/libs/pia.jar!/BOOT-INF/lib/ph-ubl21-3.3.0.jar!/schemas/ubl21/maindoc/file.txt",
                    aRes.getPath ());
      aRes = DefaultResourceResolver.getResolvedResource ("../file.txt",
                                                          "jar:file:/C:/Users/tore99/IdeaProjects/pia/pia-webapp/build/libs/pia.jar!/BOOT-INF/lib/ph-ubl21-3.3.0.jar!/schemas/ubl21/maindoc/UBL-Invoice-2.1.xsd");
      assertEquals ("jar:file:/C:/Users/tore99/IdeaProjects/pia/pia-webapp/build/libs/pia.jar!/BOOT-INF/lib/ph-ubl21-3.3.0.jar!/schemas/ubl21/file.txt",
                    aRes.getPath ());

      // Extended tests
      aRes = DefaultResourceResolver.getResolvedResource ("file.txt", "jar:file:/C:/x.jar!/");
      assertEquals ("jar:file:/C:/x.jar!/file.txt", aRes.getPath ());
      aRes = DefaultResourceResolver.getResolvedResource ("/file.txt", "jar:file:/C:/x.jar!/");
      assertEquals ("jar:file:/C:/x.jar!/file.txt", aRes.getPath ());
      aRes = DefaultResourceResolver.getResolvedResource ("../file.txt", "jar:file:/C:/x.jar!/");
      assertEquals ("jar:file:/C:/x.jar!/../file.txt", aRes.getPath ());
    }

    try
    {
      // Both null is not OK
      DefaultResourceResolver.getResolvedResource (null, null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
