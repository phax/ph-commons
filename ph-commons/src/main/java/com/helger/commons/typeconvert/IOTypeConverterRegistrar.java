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
    aRegistry.registerTypeConverter (File.class, String.class, File::getAbsolutePath);
    aRegistry.registerTypeConverter (File.class, URI.class, File::toURI);
    aRegistry.registerTypeConverter (File.class, URL.class, aSource -> URLHelper.getAsURL (aSource.toURI ()));
    aRegistry.registerTypeConverter (URI.class, File.class, File::new);
    aRegistry.registerTypeConverter (URI.class, URL.class, URLHelper::getAsURL);
    aRegistry.registerTypeConverter (URL.class, String.class, URL::toExternalForm);
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
    aRegistry.registerTypeConverter (URL.class, URI.class, URLHelper::getAsURI);
    aRegistry.registerTypeConverter (String.class, File.class, File::new);
    aRegistry.registerTypeConverter (String.class, URI.class, URLHelper::getAsURI);
    aRegistry.registerTypeConverter (String.class, URL.class, URLHelper::getAsURL);

    // IResourceBase to string
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         String.class,
                                                                         IResourceBase::getPath);

    // IReadableResource to URL
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         URL.class,
                                                                         IResourceBase::getAsURL);

    // IResourceBase to File
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IResourceBase.class,
                                                                         File.class,
                                                                         IResourceBase::getAsFile);

    // IInputStreamProvider to InputStream
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasInputStream.class,
                                                                         InputStream.class,
                                                                         IHasInputStream::getInputStream);

    // IOutputStreamProvider to OutputStream
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasOutputStream.class,
                                                                         OutputStream.class,
                                                                         aSource -> aSource.getOutputStream (EAppend.DEFAULT));

    // IReaderProvider to Reader
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasReader.class,
                                                                         Reader.class,
                                                                         IHasReader::getReader);

    // IWriterProvider to Writer
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (IHasWriter.class,
                                                                         Writer.class,
                                                                         IHasWriter::getWriter);

    // ClassPathResource
    aRegistry.registerTypeConverter (String.class, ClassPathResource.class, ClassPathResource::new);
    aRegistry.registerTypeConverter (URL.class, ClassPathResource.class, ClassPathResource::new);

    // FileSystemResource
    aRegistry.registerTypeConverter (String.class, FileSystemResource.class, FileSystemResource::new);
    aRegistry.registerTypeConverter (URL.class, FileSystemResource.class, aSource -> {
      try
      {
        final URI aURI = aSource.toURI ();
        return new FileSystemResource (aURI);
      }
      catch (final IllegalArgumentException | URISyntaxException ex)
      {
        // IllegalArgumentException: When passing a "http://..." URL into the
        // file ctor
        // URISyntaxException: Fall through
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
    aRegistry.registerTypeConverter (URL.class, URLResource.class, URLResource::new);
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
