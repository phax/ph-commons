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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.helger.commons.annotation.Nonempty;

/**
 * Abstract {@link LSResourceResolver} base implementation.
 *
 * @author Philip Helger
 */
public abstract class AbstractLSResourceResolver implements LSResourceResolver
{
  /** Internal debug flag for console debugging */
  protected static final boolean DEBUG_RESOLVE = false;

  private LSResourceResolver m_aWrappedResourceResolver;

  public AbstractLSResourceResolver ()
  {}

  @Nullable
  public LSResourceResolver getWrappedResourceResolver ()
  {
    return m_aWrappedResourceResolver;
  }

  @Nonnull
  public AbstractLSResourceResolver setWrappedResourceResolver (@Nullable final LSResourceResolver aWrappedResourceResolver)
  {
    m_aWrappedResourceResolver = aWrappedResourceResolver;
    return this;
  }

  /**
   * Allow the application to resolve external resources. <br>
   * The <code>LSParser</code> will call this method before opening any external
   * resource, including the external DTD subset, external entities referenced
   * within the DTD, and external entities referenced within the document
   * element (however, the top-level document entity is not passed to this
   * method). The application may then request that the <code>LSParser</code>
   * resolve the external resource itself, that it use an alternative URI, or
   * that it use an entirely different input source. <br>
   * Application writers can use this method to redirect external system
   * identifiers to secure and/or local URI, to look up public identifiers in a
   * catalogue, or to read an entity from a database or other input source
   * (including, for example, a dialog box).
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
   *        The system identifier, a URI reference [
   *        <a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>], of
   *        the external resource being referenced, or <code>null</code> if no
   *        system identifier was supplied.
   * @param sBaseURI
   *        The absolute base URI of the resource being parsed, or
   *        <code>null</code> if there is no base URI.
   * @return A <code>LSInput</code> object describing the new input source, or
   *         <code>null</code> to request that the parser open a regular URI
   *         connection to the resource.
   */
  @Nullable
  public abstract LSInput mainResolveResource (@Nonnull @Nonempty final String sType,
                                               @Nullable final String sNamespaceURI,
                                               @Nullable final String sPublicId,
                                               @Nullable final String sSystemId,
                                               @Nullable final String sBaseURI);

  @Nullable
  public final LSInput resolveResource (@Nonnull @Nonempty final String sType,
                                        @Nullable final String sNamespaceURI,
                                        @Nullable final String sPublicId,
                                        @Nullable final String sSystemId,
                                        @Nullable final String sBaseURI)
  {
    final LSInput ret = mainResolveResource (sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);
    if (ret != null)
      return ret;

    // Pass to parent (if available)
    if (m_aWrappedResourceResolver != null)
      return m_aWrappedResourceResolver.resolveResource (sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);

    // Not found
    return null;
  }
}
