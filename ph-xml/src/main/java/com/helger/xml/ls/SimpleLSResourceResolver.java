/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.classloader.IHasClassLoader;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resourceresolver.DefaultResourceResolver;
import com.helger.xml.XMLResourceSchemeHelper;

/**
 * A simple LS resource resolver that can handle URLs, JAR files and file system
 * resources.
 *
 * @author Philip Helger
 */
public class SimpleLSResourceResolver extends AbstractLSResourceResolver implements IHasClassLoader
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SimpleLSResourceResolver.class);

  private final WeakReference <ClassLoader> m_aClassLoader;
  // Remote schemes that are explicitly allowed for resolution. Empty by default
  // to prevent SSRF via XML Schema xs:import/xs:include or external entities.
  private final ICommonsSet <String> m_aAllowedRemoteSchemes = new CommonsHashSet <> ();

  /**
   * Default constructor using no specific class loader.
   */
  public SimpleLSResourceResolver ()
  {
    this ((ClassLoader) null);
  }

  /**
   * Constructor with a custom class loader.
   *
   * @param aClassLoader
   *        The class loader to use. May be <code>null</code>.
   */
  public SimpleLSResourceResolver (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = new WeakReference <> (aClassLoader);
  }

  /**
   * @return The class loader to use. May be <code>null</code> if the weak
   *         reference has been cleared.
   */
  @Nullable
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader.get ();
  }

  /**
   * @return A mutable copy of the set of remote URL schemes (all lower case, e.g. "http") that are
   *         allowed to be resolved. Empty by default, meaning that only local (class path or
   *         <code>file</code> based) resources may be resolved. Never <code>null</code>.
   * @since 12.3.2
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsSet <String> getAllAllowedRemoteSchemes ()
  {
    return m_aAllowedRemoteSchemes.getClone ();
  }

  /**
   * Set the remote URL schemes that are allowed to be resolved. By default no remote scheme is
   * allowed, to prevent Server Side Request Forgery (SSRF) via XML Schema <code>xs:import</code>,
   * <code>xs:include</code> or external entities referencing remote locations. Local resources
   * (class path or <code>file</code> based) are always resolved regardless of this setting.
   *
   * @param aAllowedRemoteSchemes
   *        The remote schemes to allow (e.g. "http", "https"). May be <code>null</code> or empty to
   *        deny all remote schemes.
   * @return this for chaining
   * @since 12.3.2
   */
  @NonNull
  public final SimpleLSResourceResolver setAllowedRemoteSchemes (@Nullable final String... aAllowedRemoteSchemes)
  {
    m_aAllowedRemoteSchemes.clear ();
    if (aAllowedRemoteSchemes != null)
      for (final String sScheme : aAllowedRemoteSchemes)
        if (StringHelper.isNotEmpty (sScheme))
          m_aAllowedRemoteSchemes.add (sScheme.toLowerCase (Locale.ROOT));
    return this;
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
  protected IReadableResource internalResolveResource (@NonNull @Nonempty final String sType,
                                                       @Nullable final String sNamespaceURI,
                                                       @Nullable final String sPublicId,
                                                       @Nullable final String sSystemId,
                                                       @Nullable final String sBaseURI) throws Exception
  {
    if (DEBUG_RESOLVE)
      LOGGER.info ("internalResolveResource (" +
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
  public final LSInput mainResolveResource (@NonNull @Nonempty final String sType,
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
      if (aResolvedResource == null)
        return null;

      // Check the scheme to prevent SSRF via remote xs:import/xs:include etc.
      if (!XMLResourceSchemeHelper.isResourceAccessAllowed (aResolvedResource, m_aAllowedRemoteSchemes))
      {
        LOGGER.warn ("Blocked resolution of resource '" +
                     sSystemId +
                     "' (base '" +
                     sBaseURI +
                     "') because its URL scheme is not in the list of allowed remote schemes " +
                     m_aAllowedRemoteSchemes);
        // Returning null lets the parser apply its own (restricted) default resolution
        return null;
      }

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
  }
}
