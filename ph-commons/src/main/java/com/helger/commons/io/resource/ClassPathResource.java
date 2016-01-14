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
package com.helger.commons.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.url.URLHelper;

/**
 * Wraps a class path object as a readable resource.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ClassPathResource implements IReadableResource, IHasClassLoader
{
  /** Use this prefix to uniquely identify classpath resources */
  public static final String CLASSPATH_PREFIX_LONG = "classpath:";
  /** Use this prefix to uniquely identify classpath resources - alternative */
  public static final String CLASSPATH_PREFIX_SHORT = "cp:";

  /** Internal debug logging flag */
  private static final boolean DEBUG_GET_IS = false;

  private String m_sPath;
  private final WeakReference <ClassLoader> m_aClassLoader;
  private boolean m_bURLResolved = false;
  private URL m_aURL;

  /**
   * Create a new class path resource, using the specified URL. Class loader
   * handling is automatic.
   *
   * @param aURL
   *        The URL to be used. May not be <code>null</code>.
   */
  public ClassPathResource (@Nonnull final URL aURL)
  {
    this (aURL, null);
  }

  /**
   * Create a new class path resource using the specified URL and class loader
   * (optional).
   *
   * @param aURL
   *        The URL to be used. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use. May be <code>null</code> indicating that
   *        automatic class loader handling should be applied.
   */
  public ClassPathResource (@Nonnull final URL aURL, @Nullable final ClassLoader aClassLoader)
  {
    this (aURL.toExternalForm (), aClassLoader);
  }

  /**
   * Create a new class path resource, using the specified path. Class loader
   * handling is automatic.
   *
   * @param sPath
   *        The path to be used. May neither be <code>null</code> nor empty.
   */
  public ClassPathResource (@Nonnull @Nonempty final String sPath)
  {
    this (sPath, null);
  }

  /**
   * Create a new class path resource using the specified path and class loader
   * (optional).
   *
   * @param sPath
   *        The path to be used. May neither be <code>null</code> nor empty.
   * @param aClassLoader
   *        The class loader to use. May be <code>null</code> indicating that
   *        automatic class loader handling should be applied.
   */
  public ClassPathResource (@Nonnull @Nonempty final String sPath, @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notEmpty (sPath, "Path");

    m_sPath = getWithoutClassPathPrefix (sPath);

    // In case something was cut...
    if (StringHelper.hasNoText (m_sPath))
      throw new IllegalArgumentException ("No path specified after prefix: " + sPath);

    // Ensure the ClassLoader can be garbage collected if necessary
    m_aClassLoader = aClassLoader == null ? null : new WeakReference <ClassLoader> (aClassLoader);
  }

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    if (m_aClassLoader != null)
      throw new IOException ("Cannot serialize a ClassPathResource that has a specific ClassLoader!");
    aOOS.writeUTF (m_sPath);
    // Don't write the rest! After serialization the URL must be resolved again!
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException
  {
    m_sPath = aOIS.readUTF ();
  }

  /**
   * Remove any leading explicit classpath resource prefixes.
   *
   * @param sPath
   *        The source path to strip the class path prefixes from. May be
   *        <code>null</code>.
   * @return <code>null</code> if the parameter was <code>null</code>.
   * @see #CLASSPATH_PREFIX_LONG
   * @see #CLASSPATH_PREFIX_SHORT
   */
  @Nullable
  public static String getWithoutClassPathPrefix (@Nullable final String sPath)
  {
    if (StringHelper.startsWith (sPath, CLASSPATH_PREFIX_LONG))
      return sPath.substring (CLASSPATH_PREFIX_LONG.length ());
    if (StringHelper.startsWith (sPath, CLASSPATH_PREFIX_SHORT))
      return sPath.substring (CLASSPATH_PREFIX_SHORT.length ());
    return sPath;
  }

  /**
   * Check if the passed resource name is an explicit classpath resource. This
   * is the case, if the name starts either with {@link #CLASSPATH_PREFIX_LONG}
   * or {@link #CLASSPATH_PREFIX_SHORT}.
   *
   * @param sName
   *        The name to check. May be <code>null</code>.
   * @return <code>true</code> if the passed name is not <code>null</code> and
   *         an explicit classpath resource.
   */
  public static boolean isExplicitClassPathResource (@Nullable final String sName)
  {
    return StringHelper.startsWith (sName, CLASSPATH_PREFIX_LONG) ||
           StringHelper.startsWith (sName, CLASSPATH_PREFIX_SHORT);
  }

  @Nullable
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader == null ? null : m_aClassLoader.get ();
  }

  @Nonnull
  public String getResourceID ()
  {
    final URL aURL = getAsURL ();
    return aURL == null ? m_sPath : aURL.toExternalForm ();
  }

  @Nonnull
  @Nonempty
  public String getPath ()
  {
    return m_sPath;
  }

  @SuppressWarnings ("resource")
  @Nullable
  private static InputStream _getInputStream (@Nonnull @Nonempty final String sPath,
                                              @Nullable final URL aURL,
                                              @Nullable final ClassLoader aClassLoader)
  {
    if (DEBUG_GET_IS)
      LoggerFactory.getLogger (ClassPathResource.class)
                   .info ("_getInputStream ('" + sPath + "', " + aURL + ", " + aClassLoader + ")");

    // Simple version
    InputStream ret = null;
    if (aClassLoader != null)
    {
      // An explicit ClassLoader was provided - use it
      ret = ClassLoaderHelper.getResourceAsStream (aClassLoader, sPath);
    }
    else
      if (aURL != null)
      {
        // Resolve from URL
        ret = URLResource.getInputStream (aURL);
      }

    if (DEBUG_GET_IS)
      LoggerFactory.getLogger (ClassPathResource.class).info ("  returning " + ret);

    return ret;
  }

  /**
   * Get the input stream for the specified path using automatic class loader
   * handling. The class loaders are iterated in the following order:
   * <ol>
   * <li>Default class loader (usually the context class loader)</li>
   * <li>The class loader of this class</li>
   * <li>The system class loader</li>
   * </ol>
   *
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if the path could not be resolved.
   */
  @Nullable
  public static InputStream getInputStream (@Nonnull @Nonempty final String sPath)
  {
    final URL aURL = URLHelper.getClassPathURL (sPath);
    return _getInputStream (sPath, aURL, (ClassLoader) null);
  }

  /**
   * Get the input stream of the passed resource using the specified class
   * loader only.
   *
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  public static InputStream getInputStream (@Nonnull @Nonempty final String sPath,
                                            @Nonnull final ClassLoader aClassLoader)
  {
    final URL aURL = ClassLoaderHelper.getResource (aClassLoader, sPath);
    return _getInputStream (sPath, aURL, (ClassLoader) null);
  }

  /**
   * Get the input stream for the specified path using automatic class loader
   * handling. If no class loader was specified in the constructor, the class
   * loaders are iterated in the following order:
   * <ol>
   * <li>Default class loader (usually the context class loader)</li>
   * <li>The class loader of this class</li>
   * <li>The system class loader</li>
   * </ol>
   *
   * @return <code>null</code> if no such resource exists.
   */
  @Nullable
  public InputStream getInputStream ()
  {
    final URL aURL = getAsURL ();
    return _getInputStream (m_sPath, aURL, getClassLoader ());
  }

  /**
   * Get the input stream to the this resource, using the passed class loader
   * only.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @return <code>null</code> if the path could not be resolved.
   */
  @Nullable
  public InputStream getInputStreamNoCache (@Nonnull final ClassLoader aClassLoader)
  {
    final URL aURL = getAsURLNoCache (aClassLoader);
    return _getInputStream (m_sPath, aURL, aClassLoader);
  }

  @Nullable
  public Reader getReader (@Nonnull final Charset aCharset)
  {
    return StreamHelper.createReader (getInputStream (), aCharset);
  }

  /**
   * Create a {@link Reader} of this resource, using the specified class loader
   * only.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param aCharset
   *        The charset to be used for the {@link Reader}. May not be
   *        <code>null</code>.
   * @return <code>null</code> if the path could not be resolved.
   */
  @Nullable
  public Reader getReaderNoCache (@Nonnull final ClassLoader aClassLoader, @Nonnull final Charset aCharset)
  {
    return StreamHelper.createReader (getInputStreamNoCache (aClassLoader), aCharset);
  }

  public boolean exists ()
  {
    // Uses a cached already resolved URL
    return getAsURL () != null;
  }

  @Nullable
  private URL _getAsURL ()
  {
    final ClassLoader aClassLoader = getClassLoader ();
    if (aClassLoader == null)
      return URLHelper.getClassPathURL (m_sPath);
    final String sPath = m_sPath;
    return ClassLoaderHelper.getResource (aClassLoader, sPath);
  }

  public boolean existsNoCacheUsage ()
  {
    // Resolve the URL again
    return _getAsURL () != null;
  }

  @Nullable
  public URL getAsURL ()
  {
    if (!m_bURLResolved)
    {
      m_aURL = _getAsURL ();

      // Remember that we tried to resolve the URL - avoid retry
      m_bURLResolved = true;
    }
    return m_aURL;
  }

  /**
   * Convert the path to a URL without using caching. Otherwise the resolution
   * of {@link #getAsURL()} using the constructor supplied class loader would
   * possibly contradict with this resolution.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @return <code>null</code> if the path could not be resolved to a URL
   */
  @Nullable
  public URL getAsURLNoCache (@Nonnull final ClassLoader aClassLoader)
  {
    final String sPath = m_sPath;
    return ClassLoaderHelper.getResource (aClassLoader, sPath);
  }

  /**
   * Get the file for the specified path using automatic class loader handling.
   * The class loaders are iterated in the following order:
   * <ol>
   * <li>Default class loader (usually the context class loader)</li>
   * <li>The class loader of this class</li>
   * <li>The system class loader</li>
   * </ol>
   *
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if the path could not be resolved.
   */
  @Nullable
  public static File getAsFile (@Nonnull @Nonempty final String sPath)
  {
    final URL aURL = URLHelper.getClassPathURL (sPath);
    return URLHelper.getAsFileOrNull (aURL);
  }

  @Nullable
  public static File getAsFile (@Nonnull @Nonempty final String sPath, @Nonnull final ClassLoader aClassLoader)
  {
    final URL aURL = ClassLoaderHelper.getResource (aClassLoader, sPath);
    return URLHelper.getAsFileOrNull (aURL);
  }

  @Nullable
  public File getAsFile ()
  {
    // Try to use the cached URL here - avoid double resolution
    final URL aURL = getAsURL ();
    return URLHelper.getAsFileOrNull (aURL);
  }

  @Nullable
  public File getAsFileNoCache (@Nonnull final ClassLoader aClassLoader)
  {
    final URL aURL = getAsURLNoCache (aClassLoader);
    return URLHelper.getAsFileOrNull (aURL);
  }

  public boolean canRead ()
  {
    return getAsURL () != null;
  }

  public boolean canReadNoCache (@Nonnull final ClassLoader aClassLoader)
  {
    return getAsURLNoCache (aClassLoader) != null;
  }

  @Nonnull
  public ClassPathResource getReadableCloneForPath (@Nonnull final String sPath)
  {
    return new ClassPathResource (sPath, getClassLoader ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ClassPathResource rhs = (ClassPathResource) o;
    // URL and URLresolved are state variables
    return EqualsHelper.equals (m_sPath, rhs.m_sPath) && EqualsHelper.equals (getClassLoader (), rhs.getClassLoader ());
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPath).append (m_aClassLoader).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("cpPath", m_sPath)
                                       .appendIfNotNull ("classLoader", getClassLoader ())
                                       .append ("urlResolved", m_bURLResolved)
                                       .append ("URL", m_aURL)
                                       .toString ();
  }
}
