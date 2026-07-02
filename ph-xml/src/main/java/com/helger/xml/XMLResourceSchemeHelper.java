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
package com.helger.xml;

import java.net.URL;
import java.util.Locale;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.URLResource;

/**
 * Helper class to decide whether an externally resolved {@link IReadableResource} may be accessed,
 * based on its URL scheme. This is used to prevent Server Side Request Forgery (SSRF) when
 * resolving external resources (like XSLT includes or XML Schema imports) that point to remote
 * locations. By default all remote network schemes (<code>REMOTE_NETWORK_SCHEMES</code>, i.e.
 * <code>http</code>, <code>https</code>, <code>ftp</code> and <code>ftps</code>) are blocked,
 * unless they are explicitly allowed. Local schemes (like <code>file</code>, <code>jar</code>,
 * <code>classpath</code> or OSGi <code>bundle</code> URLs) are always considered safe, because they
 * do not trigger outbound network requests to attacker-chosen hosts.
 *
 * @author Philip Helger
 * @since 12.3.2
 */
@Immutable
public final class XMLResourceSchemeHelper
{
  /**
   * The set of URL schemes that trigger outbound network requests and are therefore blocked by
   * default. All lower case.
   */
  private static final ICommonsSet <String> REMOTE_NETWORK_SCHEMES = new CommonsHashSet <> ("http",
                                                                                            "https",
                                                                                            "ftp",
                                                                                            "ftps");

  @PresentForCodeCoverage
  private static final XMLResourceSchemeHelper INSTANCE = new XMLResourceSchemeHelper ();

  private XMLResourceSchemeHelper ()
  {}

  /**
   * @return A mutable copy of the URL schemes that are considered remote (and are blocked by
   *         default). Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsSet <String> getAllRemoteNetworkSchemes ()
  {
    return REMOTE_NETWORK_SCHEMES.getClone ();
  }

  /**
   * Check if the passed resolved resource may be accessed. A resource is considered safe if it is
   * not URL based (e.g. a class path or file system resource), if it does not use a remote network
   * scheme (<code>REMOTE_NETWORK_SCHEMES</code>), or if its remote scheme is explicitly contained
   * in the passed set of allowed remote schemes.
   *
   * @param aResource
   *        The resolved resource to check. May be <code>null</code>.
   * @param aAllowedRemoteSchemes
   *        The set of explicitly allowed remote URL schemes (all lower case, e.g. "http"). May not
   *        be <code>null</code> but may be empty to deny all remote network schemes.
   * @return <code>true</code> if the resource may be accessed, <code>false</code> if access to the
   *         remote scheme is not allowed.
   */
  public static boolean isResourceAccessAllowed (@Nullable final IReadableResource aResource,
                                                 @NonNull final Set <String> aAllowedRemoteSchemes)
  {
    ValueEnforcer.notNull (aAllowedRemoteSchemes, "AllowedRemoteSchemes");

    // Only URL based resources can point to a remote location
    if (!(aResource instanceof final URLResource aURLResource))
      return true;

    final URL aURL = aURLResource.getAsURL ();
    final String sProtocol = aURL.getProtocol ();
    if (StringHelper.isEmpty (sProtocol))
      return true;

    final String sProtocolLC = sProtocol.toLowerCase (Locale.ROOT);

    // Local schemes (file, jar, classpath, bundle, ...) are always safe
    if (!REMOTE_NETWORK_SCHEMES.contains (sProtocolLC))
      return true;

    // Remote network scheme - only if explicitly allowed
    return aAllowedRemoteSchemes.contains (sProtocolLC);
  }
}
