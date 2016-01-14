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
package com.helger.commons.typeconvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;

/**
 * Test class for class {@link IOTypeConverterRegistrar}.
 *
 * @author Philip Helger
 */
public final class IOTypeConverterRegistrarTest
{
  @Test
  public void testConvert () throws IOException
  {
    final IReadableResource [] aRess = new IReadableResource [] { new ClassPathResource ("Does not exist"),
                                                                  new ClassPathResource ("test1.txt"),
                                                                  new FileSystemResource ("does-not-exist-file"),
                                                                  new FileSystemResource ("src/test/resources/test1.txt"),
                                                                  new URLResource ("http://www.example.org/gibtsned"),
                                                                  new URLResource ("file://src/test/resources/test1.txt") };
    for (final IReadableResource aRes : aRess)
    {
      final String s = TypeConverter.convertIfNecessary (aRes, String.class);
      final IReadableResource aRes2 = TypeConverter.convertIfNecessary (s, aRes.getClass ());
      assertEquals (aRes, aRes2);

      // Avoid calling exists on the URLs
      if (aRes instanceof ClassPathResource && aRes.exists ())
      {
        final URL u = TypeConverter.convertIfNecessary (aRes, URL.class);
        final IReadableResource aRes3 = TypeConverter.convertIfNecessary (u, aRes.getClass ());
        assertEquals (aRes.getResourceID (), aRes3.getResourceID ());
      }
    }

    // File <--> String
    final File f = new File ("x/../targetfile").getAbsoluteFile ();
    final String s = TypeConverter.convertIfNecessary (f, String.class);
    assertEquals (f, TypeConverter.convertIfNecessary (s, File.class));

    // FileSystemResource <--> URL
    final URL u = FileHelper.getCanonicalFile (f).toURI ().toURL ();
    final FileSystemResource fs = TypeConverter.convertIfNecessary (u, FileSystemResource.class);
    try
    {
      TypeConverter.convertIfNecessary (new URL ("http://www.google.com"), FileSystemResource.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
    assertEquals (u, TypeConverter.convertIfNecessary (fs, URL.class));

    // String <--> URLResource
    final URLResource ur = new URLResource (new URL ("http://www.helger.com"));
    final String s2 = TypeConverter.convertIfNecessary (ur, String.class);
    assertEquals (ur, TypeConverter.convertIfNecessary (s2, URLResource.class));
    try
    {
      TypeConverter.convertIfNecessary ("what the heck - I don't care!", URLResource.class);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}

    // URL <--> URLResource
    final URL u2 = TypeConverter.convertIfNecessary (ur, URL.class);
    assertEquals (ur, TypeConverter.convertIfNecessary (u2, URLResource.class));
  }
}
