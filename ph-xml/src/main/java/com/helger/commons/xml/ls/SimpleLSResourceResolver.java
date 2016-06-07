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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;

/**
 * A simple LS resource resolver that can handle URLs, JAR files and file system
 * resources.
 *
 * @author Philip Helger
 */
public class SimpleLSResourceResolver extends AbstractLSResourceResolver implements IHasClassLoader
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SimpleLSResourceResolver.class);

  private final ClassLoader m_aClassLoader;

  public SimpleLSResourceResolver ()
  {
    this ((ClassLoader) null);
  }

  public SimpleLSResourceResolver (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = aClassLoader;
  }

  @Nullable
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader;
  }

  /**
   * Do the standard resource resolving of sSystemId relative to sBaseURI
   *
   * @param sSystemId
   *        The resource to search. May be <code>null</code> if base URI is set.
   * @param sBaseURI
   *        The base URI from where the search is initiated. May be
   *        <code>null</code> if systemId is set.
   * @return The non-<code>null</code> resource. May be non-existing!
   * @throws IOException
   *         In case the file resolution (to an absolute file) fails.
   */
  @Nonnull
  public static IReadableResource doStandardResourceResolving (@Nullable final String sSystemId,
                                                               @Nullable final String sBaseURI) throws IOException
  {
    return doStandardResourceResolving (sSystemId, sBaseURI, (ClassLoader) null);
  }

  public static boolean isExplicitJarFileResource (@Nullable final String sName)
  {
    // jar:file - regular JDK
    // wsjar:file - Websphere
    return StringHelper.startsWith (sName, "jar:file:") ||
           StringHelper.startsWith (sName, "wsjar:file:") ||
           StringHelper.startsWith (sName, "zip:file:");
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
    final String sNewPath = FilenameHelper.getCleanPath (aBaseFile == null ? sSystemId
                                                                           : aBaseFile.getPath () + '/' + sSystemId);

    final ClassPathResource ret = new ClassPathResource (sNewPath, aClassLoader);
    if (DEBUG_RESOLVE)
      s_aLogger.info ("  [ClassPath] resolved base + system to " + ret);
    return ret;
  }

  @Nonnull
  private static URLResource _resolveJarFileResource (@Nonnull final String sSystemId,
                                                      @Nonnull final String sBaseURI) throws MalformedURLException
  {
    // Base URI is inside a jar file? Skip the JAR file
    final int i = sBaseURI.indexOf ("!/");
    String sPrefix;
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
    final String sNewPath = FilenameHelper.getCleanPath (aBaseFile == null ? sSystemId
                                                                           : aBaseFile.getPath () + '/' + sSystemId);

    final URLResource ret = new URLResource (sPrefix + sNewPath);
    if (DEBUG_RESOLVE)
      s_aLogger.info ("  [JarFile] resolved base + system to " + ret);
    return ret;
  }

  @Nonnull
  private static URLResource _resolveURLResource (final String sSystemId,
                                                  final URL aBaseURL) throws MalformedURLException
  {
    // Take only the path
    String sBasePath = aBaseURL.getPath ();

    /*
     * Heuristics to check if the base URI is a file is to check for the
     * existence of a dot ('.') in the last part of the filename. This is not
     * ideal but should do the trick In case you have a filename that has no
     * extension (e.g. 'test') simply append a dot (e.g. 'test.') to have the
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
    if (DEBUG_RESOLVE)
      s_aLogger.info ("  [URL] resolved base + system to " + ret);
    return ret;
  }

  /**
   * Do the standard resource resolving of sSystemId relative to sBaseURI
   *
   * @param sSystemId
   *        The resource to search. May be relative to the base URI or absolute.
   *        May be <code>null</code> if base URI is set.
   * @param sBaseURI
   *        The base URI from where the search is initiated. May be
   *        <code>null</code> if sSystemId is set.
   * @param aClassLoader
   *        The class loader to be used for {@link ClassPathResource} objects.
   *        May be <code>null</code> in which case the default class loader is
   *        used.
   * @return The non-<code>null</code> resource. May be non-existing!
   * @throws IOException
   *         In case the file resolution (to an absolute file) fails.
   */
  @Nonnull
  public static IReadableResource doStandardResourceResolving (@Nullable final String sSystemId,
                                                               @Nullable final String sBaseURI,
                                                               @Nullable final ClassLoader aClassLoader) throws IOException
  {
    if (sSystemId == null && sBaseURI == null)
      throw new IllegalArgumentException ("Both systemID and baseURI are null!");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Trying to resolve resource " +
                       sSystemId +
                       " from base " +
                       sBaseURI +
                       (aClassLoader == null ? "" : " with ClassLoader " + aClassLoader));

    if (DEBUG_RESOLVE)
      s_aLogger.info ("doStandardResourceResolving ('" + sSystemId + "', '" + sBaseURI + "', " + aClassLoader + ")");

    final URL aSystemURL = URLHelper.getAsURL (sSystemId);

    // Was an absolute URL requested?
    if (aSystemURL != null)
    {
      // File URL are handled separately, as they might be relative (as in
      // 'file:../dir/include.xml')!
      if (!aSystemURL.getProtocol ().equals (URLHelper.PROTOCOL_FILE))
      {
        final URLResource ret = new URLResource (aSystemURL);
        if (DEBUG_RESOLVE)
          s_aLogger.info ("  resolved system URL to " + ret);
        return ret;
      }
    }

    if (ClassPathResource.isExplicitClassPathResource (sBaseURI))
      return _resolveClassPathResource (sSystemId, sBaseURI, aClassLoader);

    // jar:file or wsjar:file or zip:file???
    if (isExplicitJarFileResource (sBaseURI))
      return _resolveJarFileResource (sSystemId, sBaseURI);

    // Try whether the base is a URI
    final URL aBaseURL = URLHelper.getAsURL (sBaseURI);

    // Handle "file" protocol separately
    if (aBaseURL != null && !aBaseURL.getProtocol ().equals (URLHelper.PROTOCOL_FILE))
      return _resolveURLResource (sSystemId, aBaseURL);

    // Base is not a URL or a file based URL
    File aBaseFile;
    if (aBaseURL != null)
      aBaseFile = URLHelper.getAsFile (aBaseURL);
    else
      aBaseFile = new File (sBaseURI);

    if (StringHelper.hasNoText (sSystemId))
    {
      // Nothing to resolve
      final FileSystemResource ret = new FileSystemResource (aBaseFile);
      if (DEBUG_RESOLVE)
        s_aLogger.info ("  resolved base URL to " + ret);
      return ret;
    }

    // Get the system ID file
    File aSystemFile;
    if (aSystemURL != null)
      aSystemFile = URLHelper.getAsFile (aSystemURL);
    else
      aSystemFile = new File (sSystemId);

    if (aSystemFile.isAbsolute ())
    {
      final FileSystemResource ret = new FileSystemResource (aSystemFile);
      if (DEBUG_RESOLVE)
        s_aLogger.info ("  resolved system URL to " + ret);
      return ret;
    }

    final File aParent = aBaseFile.getParentFile ();
    final File aRealFile = new File (aParent, aSystemFile.getPath ());
    // path is cleaned (canonicalized) inside FileSystemResource
    final FileSystemResource ret = new FileSystemResource (aRealFile);
    if (DEBUG_RESOLVE)
      s_aLogger.info ("  resolved base + system URL to " + ret);
    return ret;
  }

  /**
   * Internal resource resolving
   *
   * @param sType
   *        The type of the resource being resolved. For XML [
   *        <a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]
   *        resources (i.e. entities), applications must use the value <code>
   *        "http://www.w3.org/TR/REC-xml"</code>. For XML Schema [
   *        <a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] , applications must use the value <code>
   *        "http://www.w3.org/2001/XMLSchema"</code>. Other types of resources
   *        are outside the scope of this specification and therefore should
   *        recommend an absolute URI in order to use this method.
   * @param sNamespaceURI
   *        The namespace of the resource being resolved, e.g. the target
   *        namespace of the XML Schema [
   *        <a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] when resolving XML Schema resources.
   * @param sPublicId
   *        The public identifier of the external entity being referenced, or
   *        <code>null</code> if no public identifier was supplied or if the
   *        resource is not an entity.
   * @param sSystemId
   *        the path of the resource to find - may be relative to the including
   *        resource. The system identifier, a URI reference [
   *        <a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of
   *        the external resource being referenced, or <code>null</code> if no
   *        system identifier was supplied.
   * @param sBaseURI
   *        The systemId of the including resource.The absolute base URI of the
   *        resource being parsed, or <code>null</code> if there is no base URI.
   * @return <code>null</code> if the resource could not be resolved.
   * @throws Exception
   *         in case something goes wrong
   */
  @OverrideOnDemand
  @Nullable
  protected IReadableResource internalResolveResource (@Nonnull @Nonempty final String sType,
                                                       @Nullable final String sNamespaceURI,
                                                       @Nullable final String sPublicId,
                                                       @Nullable final String sSystemId,
                                                       @Nullable final String sBaseURI) throws Exception
  {
    if (DEBUG_RESOLVE)
      s_aLogger.info ("internalResolveResource (" +
                      sType +
                      ", " +
                      sNamespaceURI +
                      ", " +
                      sPublicId +
                      ", " +
                      sSystemId +
                      ", " +
                      sBaseURI +
                      ")");

    return doStandardResourceResolving (sSystemId, sBaseURI, m_aClassLoader);
  }

  /**
   * Resolve a resource with the passed parameters
   *
   * @param sType
   *        The type of the resource being resolved. For XML [
   *        <a href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]
   *        resources (i.e. entities), applications must use the value <code>
   *        "http://www.w3.org/TR/REC-xml"</code>. For XML Schema [
   *        <a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] , applications must use the value <code>
   *        "http://www.w3.org/2001/XMLSchema"</code>. Other types of resources
   *        are outside the scope of this specification and therefore should
   *        recommend an absolute URI in order to use this method.
   * @param sNamespaceURI
   *        The namespace of the resource being resolved, e.g. the target
   *        namespace of the XML Schema [
   *        <a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] when resolving XML Schema resources.
   * @param sPublicId
   *        The public identifier of the external entity being referenced, or
   *        <code>null</code> if no public identifier was supplied or if the
   *        resource is not an entity.
   * @param sSystemId
   *        the path of the resource to find - may be relative to the including
   *        resource. The system identifier, a URI reference [
   *        <a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of
   *        the external resource being referenced, or <code>null</code> if no
   *        system identifier was supplied.
   * @param sBaseURI
   *        The systemId of the including resource.The absolute base URI of the
   *        resource being parsed, or <code>null</code> if there is no base URI.
   * @return <code>null</code> if the resource could not be resolved.
   */
  @Override
  @Nullable
  public final LSInput mainResolveResource (@Nonnull @Nonempty final String sType,
                                            @Nullable final String sNamespaceURI,
                                            @Nullable final String sPublicId,
                                            @Nullable final String sSystemId,
                                            @Nullable final String sBaseURI)
  {
    try
    {
      // Try to get the resource
      final IReadableResource aResolvedResource = internalResolveResource (sType,
                                                                           sNamespaceURI,
                                                                           sPublicId,
                                                                           sSystemId,
                                                                           sBaseURI);
      if (aResolvedResource != null)
        return new ResourceLSInput (aResolvedResource);
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException ("Failed to resolve resource '" +
                                       sType +
                                       "', '" +
                                       sNamespaceURI +
                                       "', '" +
                                       sPublicId +
                                       "', '" +
                                       sSystemId +
                                       "', '" +
                                       sBaseURI +
                                       "'",
                                       ex);
    }

    return null;
  }
}
