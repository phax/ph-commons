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
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.IHasOutputStream;
import com.helger.commons.io.IHasReader;
import com.helger.commons.io.IHasWriter;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IResourceBase;
import com.helger.commons.io.resource.URLResource;
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
    aRegistry.registerTypeConverter (File.class, String.class, aSource -> aSource.getAbsolutePath ());
    aRegistry.registerTypeConverter (File.class, URI.class, aSource -> aSource.toURI ());
    aRegistry.registerTypeConverter (File.class, URL.class, aSource -> URLHelper.getAsURL (aSource.toURI ()));
    aRegistry.registerTypeConverter (URI.class, File.class, aSource -> new File (aSource));
    aRegistry.registerTypeConverter (URI.class, URL.class, aSource -> URLHelper.getAsURL (aSource));
    aRegistry.registerTypeConverter (URL.class, String.class, aSource -> aSource.toExternalForm ());
    aRegistry.registerTypeConverter (URL.class, File.class, aSource -> {
      try
      {
        return new File (aSource.toURI ().getSchemeSpecificPart ());
      }
      catch (final URISyntaxException ex)
      {
        // Fallback for URLs that are not valid URIs
        return new File (aSource.getPath ());
      }
    });
    aRegistry.registerTypeConverter (URL.class, URI.class, aSource -> URLHelper.getAsURI (aSource));
    aRegistry.registerTypeConverter (String.class, File.class, aSource -> new File (aSource));
    aRegistry.registerTypeConverter (String.class, URI.class, aSource -> URLHelper.getAsURI (aSource));
    aRegistry.registerTypeConverter (String.class, URL.class, aSource -> URLHelper.getAsURL (aSource));

    // IResourceBase to string
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         String.class,
                                                                         aSource -> aSource.getPath ());

    // IReadableResource to URL
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         URL.class,
                                                                         aSource -> aSource.getAsURL ());

    // IResourceBase to File
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         File.class,
                                                                         aSource -> aSource.getAsFile ());

    // IInputStreamProvider to InputStream
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasInputStream.class,
                                                                         InputStream.class,
                                                                         aSource -> aSource.getInputStream ());

    // IOutputStreamProvider to OutputStream
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasOutputStream.class,
                                                                         OutputStream.class,
                                                                         aSource -> aSource.getOutputStream (EAppend.DEFAULT));

    // IReaderProvider to Reader
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasReader.class,
                                                                         Reader.class,
                                                                         aSource -> aSource.getReader ());

    // IWriterProvider to Writer
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasWriter.class,
                                                                         Writer.class,
                                                                         aSource -> aSource.getWriter ());

    // ClassPathResource
    aRegistry.registerTypeConverter (String.class, ClassPathResource.class, aSource -> new ClassPathResource (aSource));
    aRegistry.registerTypeConverter (URL.class, ClassPathResource.class, aSource -> new ClassPathResource (aSource));

    // FileSystemResource
    aRegistry.registerTypeConverter (String.class,
                                     FileSystemResource.class,
                                     aSource -> new FileSystemResource (aSource));
    aRegistry.registerTypeConverter (URL.class, FileSystemResource.class, aSource -> {
      try
      {
        final URI aURI = aSource.toURI ();
        return new FileSystemResource (aURI);
      }
      catch (final IllegalArgumentException e1)
      {
        // When passing a "http://..." URL into the file ctor
      }
      catch (final URISyntaxException e2)
      {
        // Fall through
      }
      return null;
    });

    // URLResource
    aRegistry.registerTypeConverter (String.class, URLResource.class, aSource -> {
      try
      {
        return new URLResource (aSource);
      }
      catch (final MalformedURLException e)
      {
        return null;
      }
    });
    aRegistry.registerTypeConverter (URL.class, URLResource.class, aSource -> new URLResource (aSource));
    aRegistry.registerTypeConverter (URI.class, URLResource.class, aSource -> {
      try
      {
        return new URLResource (aSource);
      }
      catch (final MalformedURLException ex)
      {
        return null;
      }
    });
  }
}
