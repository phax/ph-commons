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
package com.helger.xml.ls;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceresolver.DefaultResourceResolver;
import com.helger.commons.lang.IHasClassLoader;

/**
 * A simple LS resource resolver that can handle URLs, JAR files and file system
 * resources.
 *
 * @author Philip Helger
 */
public class SimpleLSResourceResolver extends AbstractLSResourceResolver implements IHasClassLoader
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SimpleLSResourceResolver.class);

  private final WeakReference <ClassLoader> m_aClassLoader;

  public SimpleLSResourceResolver ()
  {
    this ((ClassLoader) null);
  }

  public SimpleLSResourceResolver (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = new WeakReference <> (aClassLoader);
  }

  @Nullable
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader.get ();
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
      if (s_aLogger.isInfoEnabled ())
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

    return DefaultResourceResolver.getResolvedResource (sSystemId, sBaseURI, getClassLoader ());
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
