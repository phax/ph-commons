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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLHelper;

/**
 * A simple LS resource resolver that can handle URLs, JAR files and file system
 * resources.
 *
 * @author Philip Helger
 */
public class SimpleLSResourceResolver implements LSResourceResolver
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SimpleLSResourceResolver.class);
  private final LSResourceResolver m_aWrappedResourceResolver;

  public SimpleLSResourceResolver ()
  {
    this (null);
  }

  public SimpleLSResourceResolver (@Nullable final LSResourceResolver aWrappedResourceResolver)
  {
    m_aWrappedResourceResolver = aWrappedResourceResolver;
  }

  @Nullable
  public LSResourceResolver getWrappedResourceResolver ()
  {
    return m_aWrappedResourceResolver;
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
    if (sSystemId == null && sBaseURI == null)
      throw new IllegalArgumentException ("systemID and baseURI are null!");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Trying to resolve resource " + sSystemId + " from base " + sBaseURI);

    final URL aSystemURL = URLHelper.getAsURL (sSystemId);

    // Absolute URL requested?
    if (aSystemURL != null && !aSystemURL.getProtocol ().equals (URLHelper.PROTOCOL_FILE))
    {
      // Destination system ID seems to be an absolute URL!
      return new URLResource (aSystemURL);
    }

    // jar:file - regular JDK
    // wsjar:file - Websphere
    if (StringHelper.startsWith (sBaseURI, "jar:file:") || StringHelper.startsWith (sBaseURI, "wsjar:file:"))
    {
      // Base URI is inside a jar file? Skip the JAR file
      final int i = sBaseURI.indexOf ('!');
      String sBasePath = i < 0 ? sBaseURI : sBaseURI.substring (i + 1);

      // Skip any potentially leading path separator
      if (FilenameHelper.startsWithPathSeparatorChar (sBasePath))
        sBasePath = sBasePath.substring (1);

      // Create relative path!
      final File aBaseParent = new File (sBasePath).getParentFile ();
      final String sPath = FilenameHelper.getCleanPath (aBaseParent == null ? sSystemId : aBaseParent.getPath () +
                                                                                          '/' +
                                                                                          sSystemId);

      // Build result (must contain forward slashes!)
      return new ClassPathResource (sPath);
    }

    if (ClassPathResource.isExplicitClassPathResource (sBaseURI))
    {
      // Skip leading "cp:" or "classpath:"
      final String sRealBaseURI = ClassPathResource.getWithoutClassPathPrefix (sBaseURI);
      final File aBaseFile = new File (sRealBaseURI).getParentFile ();
      return new ClassPathResource (FilenameHelper.getCleanConcatenatedUrlPath (aBaseFile == null ? "/"
                                                                                                 : aBaseFile.getPath (),
                                                                                sSystemId));
    }

    // Try whether the base is a URI
    final URL aBaseURL = URLHelper.getAsURL (sBaseURI);

    // Handle "file" protocol separately
    if (aBaseURL != null && !aBaseURL.getProtocol ().equals (URLHelper.PROTOCOL_FILE))
    {
      return new URLResource (FilenameHelper.getCleanConcatenatedUrlPath (sBaseURI, sSystemId));
    }

    // Base is potentially a URL
    File aBase;
    if (aBaseURL != null)
      aBase = URLHelper.getAsFile (aBaseURL);
    else
      aBase = new File (sBaseURI);

    if (StringHelper.hasNoText (sSystemId))
    {
      // Nothing to resolve
      return new FileSystemResource (aBase);
    }

    // Get the system ID file
    File aSystemId;
    if (aSystemURL != null)
      aSystemId = URLHelper.getAsFile (aSystemURL);
    else
      aSystemId = new File (sSystemId);

    final File aParent = aBase.getParentFile ();
    final File aRealFile = new File (aParent, aSystemId.getPath ());
    // FileSystemResource is canonicalized inside
    return new FileSystemResource (aRealFile);
  }

  /**
   * @param sType
   *        The type of the resource being resolved. For XML [<a
   *        href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]
   *        resources (i.e. entities), applications must use the value
   *        <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema [<a
   *        href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] , applications must use the value
   *        <code>"http://www.w3.org/2001/XMLSchema"</code>. Other types of
   *        resources are outside the scope of this specification and therefore
   *        should recommend an absolute URI in order to use this method.
   * @param sNamespaceURI
   *        The namespace of the resource being resolved, e.g. the target
   *        namespace of the XML Schema [<a
   *        href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] when resolving XML Schema resources.
   * @param sPublicId
   *        The public identifier of the external entity being referenced, or
   *        <code>null</code> if no public identifier was supplied or if the
   *        resource is not an entity.
   * @param sSystemId
   *        the path of the resource to find - may be relative to the including
   *        resource. The system identifier, a URI reference [<a
   *        href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of
   *        the external resource being referenced, or <code>null</code> if no
   *        system identifier was supplied.
   * @param sBaseURI
   *        The systemId of the including resource.The absolute base URI of the
   *        resource being parsed, or <code>null</code> if there is no base URI.
   * @return <code>null</code> if the resource could not be resolved.
   */
  @OverrideOnDemand
  @Nullable
  protected IReadableResource internalResolveResource (@Nonnull @Nonempty final String sType,
                                                       @Nullable final String sNamespaceURI,
                                                       @Nullable final String sPublicId,
                                                       @Nullable final String sSystemId,
                                                       @Nullable final String sBaseURI) throws Exception
  {
    return doStandardResourceResolving (sSystemId, sBaseURI);
  }

  /**
   * @param sType
   *        The type of the resource being resolved. For XML [<a
   *        href='http://www.w3.org/TR/2004/REC-xml-20040204'>XML 1.0</a>]
   *        resources (i.e. entities), applications must use the value
   *        <code>"http://www.w3.org/TR/REC-xml"</code>. For XML Schema [<a
   *        href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] , applications must use the value
   *        <code>"http://www.w3.org/2001/XMLSchema"</code>. Other types of
   *        resources are outside the scope of this specification and therefore
   *        should recommend an absolute URI in order to use this method.
   * @param sNamespaceURI
   *        The namespace of the resource being resolved, e.g. the target
   *        namespace of the XML Schema [<a
   *        href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML
   *        Schema Part 1</a>] when resolving XML Schema resources.
   * @param sPublicId
   *        The public identifier of the external entity being referenced, or
   *        <code>null</code> if no public identifier was supplied or if the
   *        resource is not an entity.
   * @param sSystemId
   *        the path of the resource to find - may be relative to the including
   *        resource. The system identifier, a URI reference [<a
   *        href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of
   *        the external resource being referenced, or <code>null</code> if no
   *        system identifier was supplied.
   * @param sBaseURI
   *        The systemId of the including resource.The absolute base URI of the
   *        resource being parsed, or <code>null</code> if there is no base URI.
   * @return <code>null</code> if the resource could not be resolved.
   */
  @Nullable
  public final LSInput resolveResource (@Nonnull @Nonempty final String sType,
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
                                       "'", ex);
    }

    // Pass to parent (if available)
    return m_aWrappedResourceResolver == null ? null : m_aWrappedResourceResolver.resolveResource (sType,
                                                                                                   sNamespaceURI,
                                                                                                   sPublicId,
                                                                                                   sSystemId,
                                                                                                   sBaseURI);
  }
}
