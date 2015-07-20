package com.helger.commons.xml.ls;

import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLHelper;

/**
 * Special LS resource resolver that tries to find resources in
 *
 * @author Philip Helger
 */
public class OSGIBundleLSResourceResolver extends AbstractLSResourceResolver
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (OSGIBundleLSResourceResolver.class);
  /** Internal debug flag for console debugging */
  private static final boolean DEBUG_RESOLVE = false;

  private final BundleContext m_aBundleContext;

  public OSGIBundleLSResourceResolver (@Nonnull final BundleContext aBundleContext)
  {
    m_aBundleContext = ValueEnforcer.notNull (aBundleContext, "BundleContext");
  }

  /**
   * Converts a revision identifier to a bundle identifier. Revision IDs are
   * typically <tt>&lt;bundle-id&gt;.&lt;revision&gt;</tt>;
   *
   * @param sHost
   *        Source revision ID
   * @return only the portion corresponding to the bundle ID or -1 in case of an
   *         error
   **/
  public static long getBundleIDFromRevisionID (@Nonnull final String sHost)
  {
    final int nIndex = sHost.indexOf ('.');
    final String sBundleID = nIndex >= 0 ? sHost.substring (0, nIndex) : sHost;
    return StringParser.parseLong (sBundleID, -1L);
  }

  // URL Layout:
  // bundle://<revision-id>:<bundle-classpath-index>/<resource-path>

  // Example:
  // SystemID
  // ../common/UBL-CommonAggregateComponents-2.1.xsd
  // BaseURI
  // bundle://23.0:1/schemas/ubl21/maindoc/UBL-ApplicationResponse-2.1.xsd
  @Override
  @Nullable
  public LSInput mainResolveResource (@Nonnull final String sType,
                                      @Nullable final String sNamespaceURI,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId,
                                      @Nullable final String sBaseURI)
  {
    if (DEBUG_RESOLVE)
      s_aLogger.info ("mainResolveResource (" +
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

    if (StringHelper.hasText (sBaseURI))
    {
      // Try whether the base is a URI
      final URL aBaseURL = URLHelper.getAsURL (sBaseURI);
      // OSGI special handling
      if (aBaseURL != null && aBaseURL.getProtocol ().equals ("bundle"))
      {
        // Verify that the resource pointed to by the URL exists.
        // The URL is constructed like this:
        // bundle://<revision-id>:<bundle-classpath-index>/<resource-path>
        // Where <revision-id> = <bundle-id>.<revision>
        final long nBundleID = getBundleIDFromRevisionID (aBaseURL.getHost ());
        final Bundle aBundle = m_aBundleContext.getBundle (nBundleID);
        if (aBundle == null)
        {
          s_aLogger.error ("No bundle associated with resource '" +
                           aBaseURL.toExternalForm () +
                           "' - no such bundle ID " +
                           nBundleID);
          return null;
        }

        // Take only the path
        String sBasePath = aBaseURL.getPath ();

        // Heuristics to check if the base URI is a file
        // This is not ideal but should do the trick
        final String sBaseFilename = FilenameHelper.getWithoutPath (sBasePath);
        if (sBaseFilename != null && sBaseFilename.indexOf ('.') >= 0)
        {
          sBasePath = FilenameHelper.getPath (sBasePath);
          // For the example this results in:
          // bundle://23.0:1/schemas/ubl21/maindoc/
        }

        // Build combined URL
        String sNewPath = FilenameHelper.getCleanConcatenatedUrlPath (sBasePath, sSystemId);

        // Important: no leading slash for ClassLoader resolution
        sNewPath = StringHelper.trimStart (sNewPath, '/');

        // Resolve
        final URL aBundleURL = aBundle.getResource (sNewPath);
        if (aBundleURL == null)
        {
          s_aLogger.warn ("Failed to resolve '" +
                          sNewPath +
                          "' based on '" +
                          sBaseURI +
                          "' and '" +
                          sSystemId +
                          "' for bundle " +
                          aBundle);
          return null;
        }

        final URLResource ret = new URLResource (aBundleURL);

        if (DEBUG_RESOLVE)
          s_aLogger.info ("  resolved base + system URL to " + ret);

        return new ResourceLSInput (ret);
      }
    }

    return null;
  }
}
