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
package com.helger.commons.typeconvert;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.IInputStreamProvider;
import com.helger.commons.io.IOutputStreamProvider;
import com.helger.commons.io.IReaderProvider;
import com.helger.commons.io.IResourceBase;
import com.helger.commons.io.IWriterProvider;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleAssignableSourceFixedDestination;
import com.helger.commons.url.URLHelper;

/**
 * Register the IO specific type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class IOTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // File
    aRegistry.registerTypeConverter (File.class, String.class, new ITypeConverter ()
    {
      public String convert (@Nonnull final Object aSource)
      {
        return ((File) aSource).getAbsolutePath ();
      }
    });
    aRegistry.registerTypeConverter (File.class, URI.class, new ITypeConverter ()
    {
      public URI convert (@Nonnull final Object aSource)
      {
        return ((File) aSource).toURI ();
      }
    });
    aRegistry.registerTypeConverter (File.class, URL.class, new ITypeConverter ()
    {
      public URL convert (@Nonnull final Object aSource)
      {
        return URLHelper.getAsURL (((File) aSource).toURI ());
      }
    });
    aRegistry.registerTypeConverter (URI.class, File.class, new ITypeConverter ()
    {
      public File convert (@Nonnull final Object aSource)
      {
        return new File ((URI) aSource);
      }
    });
    aRegistry.registerTypeConverter (URI.class, URL.class, new ITypeConverter ()
    {
      public URL convert (@Nonnull final Object aSource)
      {
        return URLHelper.getAsURL ((URI) aSource);
      }
    });
    aRegistry.registerTypeConverter (URL.class, String.class, new ITypeConverter ()
    {
      public String convert (@Nonnull final Object aSource)
      {
        return ((URL) aSource).toExternalForm ();
      }
    });
    aRegistry.registerTypeConverter (URL.class, File.class, new ITypeConverter ()
    {
      public File convert (@Nonnull final Object aSource)
      {
        final URL aURL = (URL) aSource;
        try
        {
          return new File (aURL.toURI ().getSchemeSpecificPart ());
        }
        catch (final URISyntaxException ex)
        {
          // Fallback for URLs that are not valid URIs
          return new File (aURL.getPath ());
        }
      }
    });
    aRegistry.registerTypeConverter (URL.class, URI.class, new ITypeConverter ()
    {
      public URI convert (@Nonnull final Object aSource)
      {
        return URLHelper.getAsURI ((URL) aSource);
      }
    });
    aRegistry.registerTypeConverter (String.class, File.class, new ITypeConverter ()
    {
      public File convert (@Nonnull final Object aSource)
      {
        return new File ((String) aSource);
      }
    });
    aRegistry.registerTypeConverter (String.class, URI.class, new ITypeConverter ()
    {
      public URI convert (@Nonnull final Object aSource)
      {
        return URLHelper.getAsURI ((String) aSource);
      }
    });
    aRegistry.registerTypeConverter (String.class, URL.class, new ITypeConverter ()
    {
      public URL convert (@Nonnull final Object aSource)
      {
        return URLHelper.getAsURL ((String) aSource);
      }
    });

    // IResourceBase to string
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                                                        String.class)
    {
      public String convert (@Nonnull final Object aSource)
      {
        return ((IResourceBase) aSource).getPath ();
      }
    });

    // IReadableResource to URL
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                                                        URL.class)
    {
      public URL convert (@Nonnull final Object aSource)
      {
        return ((IResourceBase) aSource).getAsURL ();
      }
    });

    // IResourceBase to File
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                                                        File.class)
    {
      public File convert (@Nonnull final Object aSource)
      {
        return ((IResourceBase) aSource).getAsFile ();
      }
    });

    // IInputStreamProvider to InputStream
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IInputStreamProvider.class,
                                                                                                        InputStream.class)
    {
      public InputStream convert (@Nonnull final Object aSource)
      {
        return ((IInputStreamProvider) aSource).getInputStream ();
      }
    });

    // IOutputStreamProvider to OutputStream
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IOutputStreamProvider.class,
                                                                                                        OutputStream.class)
    {
      public OutputStream convert (@Nonnull final Object aSource)
      {
        return ((IOutputStreamProvider) aSource).getOutputStream (EAppend.DEFAULT);
      }
    });

    // IReaderProvider to Reader
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IReaderProvider.class,
                                                                                                        Reader.class)
    {
      public Reader convert (@Nonnull final Object aSource)
      {
        return ((IReaderProvider) aSource).getReader ();
      }
    });

    // IWriterProvider to Writer
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (IWriterProvider.class,
                                                                                                        Writer.class)
    {
      public Writer convert (@Nonnull final Object aSource)
      {
        return ((IWriterProvider) aSource).getWriter ();
      }
    });

    // ClassPathResource
    aRegistry.registerTypeConverter (String.class, ClassPathResource.class, new ITypeConverter ()
    {
      public ClassPathResource convert (@Nonnull final Object aSource)
      {
        return new ClassPathResource ((String) aSource);
      }
    });
    aRegistry.registerTypeConverter (URL.class, ClassPathResource.class, new ITypeConverter ()
    {
      public ClassPathResource convert (@Nonnull final Object aSource)
      {
        return new ClassPathResource ((URL) aSource);
      }
    });

    // FileSystemResource
    aRegistry.registerTypeConverter (String.class, FileSystemResource.class, new ITypeConverter ()
    {
      public FileSystemResource convert (@Nonnull final Object aSource)
      {
        return new FileSystemResource ((String) aSource);
      }
    });
    aRegistry.registerTypeConverter (URL.class, FileSystemResource.class, new ITypeConverter ()
    {
      public FileSystemResource convert (@Nonnull final Object aSource)
      {
        try
        {
          final URI aURI = ((URL) aSource).toURI ();
          return new FileSystemResource (aURI);
        }
        catch (final IllegalArgumentException e)
        {
          // When passing a "http://..." URL into the file ctor
        }
        catch (final URISyntaxException e)
        {
          // Fall through
        }
        return null;
      }
    });

    // URLResource
    aRegistry.registerTypeConverter (String.class, URLResource.class, new ITypeConverter ()
    {
      public URLResource convert (@Nonnull final Object aSource)
      {
        try
        {
          return new URLResource ((String) aSource);
        }
        catch (final MalformedURLException e)
        {
          return null;
        }
      }
    });
    aRegistry.registerTypeConverter (URL.class, URLResource.class, new ITypeConverter ()
    {
      public URLResource convert (@Nonnull final Object aSource)
      {
        return new URLResource ((URL) aSource);
      }
    });
    aRegistry.registerTypeConverter (URI.class, URLResource.class, new ITypeConverter ()
    {
      public URLResource convert (@Nonnull final Object aSource)
      {
        try
        {
          return new URLResource ((URI) aSource);
        }
        catch (final MalformedURLException ex)
        {
          return null;
        }
      }
    });
  }
}
