/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.io.File;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.log.IHasConditionalLogger;
import com.helger.base.string.StringHelper;
import com.helger.base.url.CURL;
import com.helger.io.file.FilenameHelper;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.resource.FileSystemResource;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.URLResource;
import com.helger.io.url.URLHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A simple resource resolver that can handle URLs, JAR files and file system resources.
 *
 * @author Philip Helger
 * @since 8.6.6
 */
@Immutable
public class DefaultResourceResolver implements IHasConditionalLogger
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DefaultResourceResolver.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER);

  protected DefaultResourceResolver ()
  {}

  public static boolean isDebugResolve ()
  {
    return CONDLOG.isEnabled ();
  }

  public static void setDebugResolve (final boolean bDebugResolve)
  {
    CONDLOG.setEnabled (bDebugResolve);
  }

  public static boolean isExplicitJarFileResource (@Nullable final String sName)
  {
    // jar:file - regular JDK
    // wsjar:file - Websphere
    return StringHelper.startsWith (sName, "jar:file:") ||
           StringHelper.startsWith (sName, "wsjar:file:") ||
           StringHelper.startsWith (sName, "zip:file:");
  }

  /**
   * Do the standard resource resolving of sSystemId relative to sBaseURI
   *
   * @param sSystemId
   *        The resource to search. May be <code>null</code> if base URI is set.
   * @param sBaseURI
   *        The base URI from where the search is initiated. May be <code>null</code> if systemId is
   *        set.
   * @return The non-<code>null</code> resource. May be non-existing!
   * @throws UncheckedIOException
   *         In case the file resolution (to an absolute file) fails.
   */
  @Nonnull
  public static IReadableResource getResolvedResource (@Nullable final String sSystemId,
                                                       @Nullable final String sBaseURI)
  {
    return getResolvedResource (sSystemId, sBaseURI, (ClassLoader) null);
  }

  @Nonnull
  private static ClassPathResource _resolveClassPathResource (final String sSystemId,
                                                              final String sBaseURI,
                                                              final ClassLoader aClassLoader)
  {
    // Skip leading "cp:" or "classpath:"
    final String sBaseURIWithoutPrefix = ClassPathResource.getWithoutClassPathPrefix (sBaseURI);

    // Get the parent path of the base path
    final File aBaseFile = new File (sBaseURIWithoutPrefix).getParentFile ();

    // Concatenate the path with the URI to search
    final String sNewPath = FilenameHelper.getCleanPath (aBaseFile == null ? sSystemId : aBaseFile.getPath () +
                                                                                         '/' +
                                                                                         sSystemId);

    final ClassPathResource ret = new ClassPathResource (sNewPath, aClassLoader);
    CONDLOG.info ( () -> "  [ClassPath] resolved base + system to " + ret);
    return ret;
  }

  @Nonnull
  private static URLResource _resolveJarFileResource (@Nonnull final String sSystemId, @Nonnull final String sBaseURI)
                                                                                                                       throws MalformedURLException
  {
    // Base URI is inside a jar file? Skip the JAR file
    // See issue #8 - use lastIndexOf here
    final int i = sBaseURI.lastIndexOf ("!/");
    final String sPrefix;
    String sBasePath;
    if (i < 0)
    {
      sPrefix = "";
      sBasePath = sBaseURI;
    }
    else
    {
      sPrefix = sBaseURI.substring (0, i + 2);
      sBasePath = sBaseURI.substring (i + 2);
    }
    // Skip any potentially leading path separator
    if (FilenameHelper.startsWithPathSeparatorChar (sBasePath))
      sBasePath = sBasePath.substring (1);

    // Get the parent path of the base path
    final File aBaseFile = new File (sBasePath).getParentFile ();

    // Concatenate the path with the URI to search
    final String sNewPath = FilenameHelper.getCleanPath (aBaseFile == null ? sSystemId : aBaseFile.getPath () +
                                                                                         '/' +
                                                                                         sSystemId);

    final String sAggregatedPath;
    if (sPrefix.endsWith ("/") && sNewPath.startsWith ("/"))
    {
      // Avoid "//"
      sAggregatedPath = sPrefix + sNewPath.substring (1);
    }
    else
      sAggregatedPath = sPrefix + sNewPath;
    final URLResource ret = new URLResource (sAggregatedPath);
    CONDLOG.info ( () -> "  [JarFile] resolved base + system to " + ret);
    return ret;
  }

  @Nonnull
  private static URLResource _resolveURLResource (final String sSystemId, @Nonnull final URL aBaseURL)
                                                                                                       throws MalformedURLException
  {
    // Take only the path
    String sBasePath = aBaseURL.getPath ();

    /*
     * Heuristics to check if the base URI is a file is to check for the existence of a dot ('.') in
     * the last part of the filename. This is not ideal but should do the trick In case you have a
     * filename that has no extension (e.g. 'test') simply append a dot (e.g. 'test.') to have the
     * same effect.
     */
    final String sBaseFilename = FilenameHelper.getWithoutPath (sBasePath);
    if (sBaseFilename != null && sBaseFilename.indexOf ('.') >= 0)
    {
      // Take only the path
      sBasePath = FilenameHelper.getPath (sBasePath);
    }
    // Concatenate the path with the URI to search
    final String sNewPath = FilenameHelper.getCleanConcatenatedUrlPath (sBasePath, sSystemId);

    // Rebuild the URL with the new path
    final URL aNewURL = new URL (aBaseURL.getProtocol (),
                                 aBaseURL.getHost (),
                                 aBaseURL.getPort (),
                                 URLHelper.getURLString (sNewPath, aBaseURL.getQuery (), aBaseURL.getRef ()));
    final URLResource ret = new URLResource (aNewURL);
    CONDLOG.info ( () -> "  [URL] resolved base + system to " + ret);
    return ret;
  }

  @Nonnull
  private static FileSystemResource _getChildResource (@Nullable final File aBaseFile, @Nonnull final File aSystemFile)
  {
    if (aBaseFile == null)
      return new FileSystemResource (aSystemFile);

    final File aParent = aBaseFile.isDirectory () ? aBaseFile : aBaseFile.getParentFile ();
    final File aRealFile = new File (aParent, aSystemFile.getPath ());
    // path is cleaned (canonicalized) inside FileSystemResource
    return new FileSystemResource (aRealFile);
  }

  /**
   * Do the standard resource resolving of sSystemId relative to sBaseURI
   *
   * @param sSystemId
   *        The resource to search. May be relative to the base URI or absolute. May be
   *        <code>null</code> if base URI is set.
   * @param sBaseURI
   *        The base URI from where the search is initiated. May be <code>null</code> if sSystemId
   *        is set.
   * @param aClassLoader
   *        The class loader to be used for {@link ClassPathResource} objects. May be
   *        <code>null</code> in which case the default class loader is used.
   * @return The non-<code>null</code> resource. May be non-existing!
   * @throws UncheckedIOException
   *         In case the file resolution (to an absolute file) fails.
   */
  @Nonnull
  public static IReadableResource getResolvedResource (@Nullable final String sSystemId,
                                                       @Nullable final String sBaseURI,
                                                       @Nullable final ClassLoader aClassLoader)
  {
    if (StringHelper.isEmpty (sSystemId) && StringHelper.isEmpty (sBaseURI))
      throw new IllegalArgumentException ("Both systemID and baseURI are null!");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Trying to resolve resource '" +
                    sSystemId +
                    "' from base '" +
                    sBaseURI +
                    (aClassLoader == null ? "'" : "' with ClassLoader " + aClassLoader));

    CONDLOG.info ( () -> "doStandardResourceResolving ('" + sSystemId + "', '" + sBaseURI + "', " + aClassLoader + ")");

    // It happens quite often that some resolution does not work here
    final URL aSystemURL = URLHelper.getAsURL (sSystemId, false);

    // Was an absolute URL requested?
    if (aSystemURL != null)
    {
      // File URL are handled separately, as they might be relative (as in
      // 'file:../dir/include.xml')!
      if (!aSystemURL.getProtocol ().equals (CURL.PROTOCOL_FILE))
      {
        final URLResource ret = new URLResource (aSystemURL);
        CONDLOG.info ( () -> "  resolved system URL to " + ret);
        return ret;
      }
    }
    if (ClassPathResource.isExplicitClassPathResource (sBaseURI))
      return _resolveClassPathResource (sSystemId, sBaseURI, aClassLoader);

    // jar:file or wsjar:file or zip:file???
    if (isExplicitJarFileResource (sBaseURI))
      try
      {
        return _resolveJarFileResource (sSystemId, sBaseURI);
      }
      catch (final MalformedURLException ex)
      {
        throw new UncheckedIOException (ex);
      }

    // Try whether the base is a URI
    final URL aBaseURL = URLHelper.getAsURL (sBaseURI);

    // Handle "file" protocol separately
    if (aBaseURL != null && !aBaseURL.getProtocol ().equals (CURL.PROTOCOL_FILE))
      try
      {
        return _resolveURLResource (sSystemId, aBaseURL);
      }
      catch (final MalformedURLException ex)
      {
        throw new UncheckedIOException (ex);
      }

    // Base is not a URL or a file based URL
    final File aBaseFile;
    if (aBaseURL != null)
      aBaseFile = URLHelper.getAsFile (aBaseURL);
    else
      if (sBaseURI != null)
        aBaseFile = new File (sBaseURI);
      else
        aBaseFile = null;

    if (StringHelper.isEmpty (sSystemId))
    {
      // Nothing to resolve
      // Note: BaseFile should always be set here!
      final FileSystemResource ret = new FileSystemResource (aBaseFile);
      CONDLOG.info ( () -> "  resolved base URL to " + ret);
      return ret;
    }
    // Get the system ID file
    final File aSystemFile;
    if (aSystemURL != null)
      aSystemFile = URLHelper.getAsFile (aSystemURL);
    else
      aSystemFile = new File (sSystemId);

    // If the provided file is an absolute file, take it
    if (aSystemFile.isAbsolute ())
    {
      final FileSystemResource aAbsFile = new FileSystemResource (aSystemFile);
      if (!aAbsFile.exists ())
      {
        // Sometimes paths starting with "/" are passed in - as they are
        // considered absolute when running on Linux, try if a combined file
        // eventually exists
        final FileSystemResource aMerged = _getChildResource (aBaseFile, aSystemFile);
        if (aMerged.exists ())
        {
          CONDLOG.info ( () -> "  resolved base + system URL to " + aMerged);
          return aMerged;
        }
      }
      // If the absolute version exists, or if both the absolute and the merged
      // version do NOT exist, return the absolute version anyway.
      CONDLOG.info ( () -> "  resolved system URL to " + aAbsFile);
      return aAbsFile;
    }
    final FileSystemResource ret = _getChildResource (aBaseFile, aSystemFile);
    CONDLOG.info ( () -> "  resolved base + system URL to " + ret);
    return ret;
  }
}
