/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.DevelopersNote;
import com.helger.commons.annotations.UnsupportedOperation;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.streams.NonBlockingBufferedReader;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A simple service-provider loading facility.
 * <p>
 * A <i>service</i> is a well-known set of interfaces and (usually abstract)
 * classes. A <i>service provider</i> is a specific implementation of a service.
 * The classes in a provider typically implement the interfaces and subclass the
 * classes defined in the service itself. Service providers can be installed in
 * an implementation of the Java platform in the form of extensions, that is,
 * jar files placed into any of the usual extension directories. Providers can
 * also be made available by adding them to the application's class path or by
 * some other platform-specific means.
 * </p>
 * <p>
 * For the purpose of loading, a service is represented by a single type, that
 * is, a single interface or abstract class. (A concrete class can be used, but
 * this is not recommended.) A provider of a given service contains one or more
 * concrete classes that extend this <i>service type</i> with data and code
 * specific to the provider. The <i>provider class</i> is typically not the
 * entire provider itself but rather a proxy which contains enough information
 * to decide whether the provider is able to satisfy a particular request
 * together with code that can create the actual provider on demand. The details
 * of provider classes tend to be highly service-specific; no single class or
 * interface could possibly unify them, so no such type is defined here. The
 * only requirement enforced by this facility is that provider classes must have
 * a zero-argument constructor so that they can be instantiated during loading.
 * </p>
 * <p>
 * A service provider is identified by placing a <i>provider-configuration
 * file</i> in the resource directory <tt>META-INF/services</tt>. The file's
 * name is the fully-qualified <a href="../lang/ClassLoader.html#name">binary
 * name</a> of the service's type. The file contains a list of fully-qualified
 * binary names of concrete provider classes, one per line. Space and tab
 * characters surrounding each name, as well as blank lines, are ignored. The
 * comment character is <tt>'#'</tt> ( <tt>'&#92;u0023'</tt> , <font
 * size="-1">NUMBER SIGN</font>); on each line all characters following the
 * first comment character are ignored. The file must be encoded in UTF-8.
 * </p>
 * <p>
 * If a particular concrete provider class is named in more than one
 * configuration file, or is named in the same configuration file more than
 * once, then the duplicates are ignored. The configuration file naming a
 * particular provider need not be in the same jar file or other distribution
 * unit as the provider itself. The provider must be accessible from the same
 * class loader that was initially queried to locate the configuration file;
 * note that this is not necessarily the class loader from which the file was
 * actually loaded.
 * </p>
 * <p>
 * Providers are located and instantiated lazily, that is, on demand. A service
 * loader maintains a cache of the providers that have been loaded so far. Each
 * invocation of the {@link #iterator iterator} method returns an iterator that
 * first yields all of the elements of the cache, in instantiation order, and
 * then lazily locates and instantiates any remaining providers, adding each one
 * to the cache in turn. The cache can be cleared via the {@link #reload reload}
 * method.
 * </p>
 * <p>
 * Service loaders always execute in the security context of the caller. Trusted
 * system code should typically invoke the methods in this class, and the
 * methods of the iterators which they return, from within a privileged security
 * context.
 * </p>
 * <p>
 * Instances of this class are not safe for use by multiple concurrent threads.
 * </p>
 * <p>
 * Unless otherwise specified, passing a <tt>null</tt> argument to any method in
 * this class will cause a {@link NullPointerException} to be thrown.
 * </p>
 * <p>
 * <span style="font-weight: bold; padding-right: 1em">Example</span> Suppose we
 * have a service type <tt>com.example.CodecSet</tt> which is intended to
 * represent sets of encoder/decoder pairs for some protocol. In this case it is
 * an abstract class with two abstract methods:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public abstract Encoder getEncoder (String encodingName);
 * 
 * public abstract Decoder getDecoder (String encodingName);
 * </pre>
 *
 * </blockquote> Each method returns an appropriate object or <tt>null</tt> if
 * the provider does not support the given encoding. Typical providers support
 * more than one encoding.
 * <p>
 * If <tt>com.example.impl.StandardCodecs</tt> is an implementation of the
 * <tt>CodecSet</tt> service then its jar file also contains a file named
 * </p>
 * <blockquote>
 *
 * <pre>
 * META - INF / services / com.example.CodecSet
 * </pre>
 *
 * </blockquote>
 * <p>
 * This file contains the single line:
 * </p>
 * <blockquote>
 *
 * <pre>
 * com.example.impl.StandardCodecs    # Standard codecs
 * </pre>
 *
 * </blockquote>
 * <p>
 * The <tt>CodecSet</tt> class creates and saves a single service instance at
 * initialization:
 * </p>
 * <blockquote>
 *
 * <pre>
 * private static ServiceLoader &lt;CodecSet&gt; codecSetLoader = ServiceLoader.load (CodecSet.class);
 * </pre>
 *
 * </blockquote>
 * <p>
 * To locate an encoder for a given encoding name it defines a static factory
 * method which iterates through the known and available providers, returning
 * only when it has located a suitable encoder or has run out of providers.
 * </p>
 * <blockquote>
 *
 * <pre>
 * public static Encoder getEncoder (String encodingName)
 * {
 *   for (CodecSet cp : codecSetLoader)
 *   {
 *     Encoder enc = cp.getEncoder (encodingName);
 *     if (enc != null)
 *       return enc;
 *   }
 *   return null;
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * A <tt>getDecoder</tt> method is defined similarly.
 * </p>
 * <p>
 * <span style="font-weight: bold; padding-right: 1em">Usage Note</span> If the
 * class path of a class loader that is used for provider loading includes
 * remote network URLs then those URLs will be dereferenced in the process of
 * searching for provider-configuration files.
 * </p>
 * <p>
 * This activity is normal, although it may cause puzzling entries to be created
 * in web-server logs. If a web server is not configured correctly, however,
 * then this activity may cause the provider-loading algorithm to fail
 * spuriously.
 * </p>
 * <p>
 * A web server should return an HTTP 404 (Not Found) response when a requested
 * resource does not exist. Sometimes, however, web servers are erroneously
 * configured to return an HTTP 200 (OK) response along with a helpful HTML
 * error page in such cases. This will cause a {@link ServiceLoaderException} to
 * be thrown when this class attempts to parse the HTML page as a
 * provider-configuration file. The best solution to this problem is to fix the
 * misconfigured web server to return the correct response code (HTTP 404) along
 * with the HTML error page.
 * </p>
 *
 * @param <SPITYPE>
 *        The type of the service to be loaded by this loader
 * @author Mark Reinhold
 * @version 1.10, 06/04/10
 * @since 1.6
 */
// IFJDK5
// ELSE
@Deprecated
// ENDIF
@DevelopersNote ("Required only in JDK5. Use the regular ServiceLoader from JDK 1.6 otherwise.")
public final class ServiceLoaderBackport <SPITYPE> implements Iterable <SPITYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServiceLoaderBackport.class);
  static final String PREFIX = "META-INF/services/";

  // The class or interface representing the service being loaded
  private final Class <SPITYPE> m_aService;

  // The class loader used to locate, load, and instantiate providers
  private final ClassLoader m_aClassLoader;

  // Cached providers, in instantiation order
  private final Map <String, SPITYPE> m_aProviders = new LinkedHashMap <String, SPITYPE> ();

  // The current lazy-lookup iterator
  private LazyIterator m_aLookupIterator;

  /**
   * Clear this loader's provider cache so that all providers will be reloaded.
   * <p>
   * After invoking this method, subsequent invocations of the
   * {@link #iterator() iterator} method will lazily look up and instantiate
   * providers from scratch, just as is done by a newly-created loader.
   * <p>
   * This method is intended for use in situations in which new providers can be
   * installed into a running Java virtual machine.
   */
  public void reload ()
  {
    m_aProviders.clear ();
    m_aLookupIterator = new LazyIterator (m_aService, m_aClassLoader);
  }

  private ServiceLoaderBackport (final Class <SPITYPE> svc, final ClassLoader cl)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Loading all SPI implementations of " + svc.toString ());
    m_aService = svc;
    m_aClassLoader = cl;
    reload ();
  }

  private static void _fail (final Class <?> service, final String msg, final Throwable cause)
  {
    throw new ServiceLoaderException (service.getName () + ": " + msg, cause);
  }

  private static void _fail (final Class <?> service, final String msg)
  {
    throw new ServiceLoaderException (service.getName () + ": " + msg);
  }

  private static void _fail (final Class <?> service, final URL u, final int line, final String msg)
  {
    throw new ServiceLoaderException (service.getName () + ": " + u + ":" + line + ": " + msg);
  }

  // Parse a single line from the given configuration file, adding the name
  // on the line to the names list.
  //
  private int _parseLine (final Class <SPITYPE> aService,
                          final URL u,
                          final NonBlockingBufferedReader r,
                          final int lc,
                          final List <String> names) throws IOException
  {
    String ln = r.readLine ();
    if (ln == null)
      return -1;

    final int ci = ln.indexOf ('#');
    if (ci >= 0)
      ln = ln.substring (0, ci);
    ln = ln.trim ();
    final int n = ln.length ();
    if (n != 0)
    {
      if ((ln.indexOf (' ') >= 0) || (ln.indexOf ('\t') >= 0))
        _fail (aService, u, lc, "Illegal configuration-file syntax");
      int cp = ln.codePointAt (0);
      if (!Character.isJavaIdentifierStart (cp))
        _fail (aService, u, lc, "Illegal provider-class name: " + ln);
      for (int i = Character.charCount (cp); i < n; i += Character.charCount (cp))
      {
        cp = ln.codePointAt (i);
        if (!Character.isJavaIdentifierPart (cp) && (cp != '.'))
          _fail (aService, u, lc, "Illegal provider-class name: " + ln);
      }
      if (!m_aProviders.containsKey (ln) && !names.contains (ln))
        names.add (ln);
    }
    return lc + 1;
  }

  // Parse the content of the given URL as a provider-configuration file.
  //
  // @param service
  // The service type for which providers are being sought;
  // used to construct error detail strings
  //
  // @param u
  // The URL naming the configuration file to be parsed
  //
  // @return A (possibly empty) iterator that will yield the provider-class
  // names in the given configuration file that are not yet members
  // of the returned set
  //
  // @
  // If an I/O error occurs while reading from the given URL, or
  // if a configuration-file format error is detected
  //
  private Iterator <String> _parse (final Class <SPITYPE> aService, final URL u)
  {
    InputStream in = null;
    NonBlockingBufferedReader r = null;
    final ArrayList <String> names = new ArrayList <String> ();
    try
    {
      in = u.openStream ();
      r = new NonBlockingBufferedReader (StreamUtils.createReader (in, CCharset.CHARSET_SERVICE_LOADER_OBJ));
      int lc = 1;
      while ((lc = _parseLine (aService, u, r, lc, names)) >= 0)
      {// NOPMD
       // do nothing
      }
    }
    catch (final IOException x)
    {
      _fail (aService, "Error reading configuration file", x);
    }
    finally
    {
      try
      {
        if (r != null)
          r.close ();
        if (in != null)
          in.close ();
      }
      catch (final IOException y)
      {
        _fail (aService, "Error closing configuration file", y);
      }
    }
    return names.iterator ();
  }

  // Private inner class implementing fully-lazy provider lookup
  @SuppressFBWarnings ("UWF_NULL_FIELD")
  private final class LazyIterator implements Iterator <SPITYPE>
  {
    private final Class <SPITYPE> m_aLIService;
    private final ClassLoader m_aLILoader;
    private Enumeration <URL> m_aLIConfigs = null;
    private Iterator <String> m_aLIPending = null;
    private String m_sLINextName = null;

    public LazyIterator (final Class <SPITYPE> aService, final ClassLoader aLoader)
    {
      m_aLIService = aService;
      m_aLILoader = aLoader;
    }

    public boolean hasNext ()
    {
      if (m_sLINextName != null)
        return true;
      if (m_aLIConfigs == null)
      {
        try
        {
          final String fullName = PREFIX + m_aLIService.getName ();
          if (m_aLILoader == null)
            m_aLIConfigs = ClassLoader.getSystemResources (fullName);
          else
            m_aLIConfigs = m_aLILoader.getResources (fullName);
        }
        catch (final IOException ex)
        {
          _fail (m_aLIService, "Error locating configuration files", ex);
        }
      }
      while ((m_aLIPending == null) || !m_aLIPending.hasNext ())
      {
        if (!m_aLIConfigs.hasMoreElements ())
        {
          return false;
        }
        m_aLIPending = _parse (m_aLIService, m_aLIConfigs.nextElement ());
      }
      m_sLINextName = m_aLIPending.next ();
      return true;
    }

    public SPITYPE next ()
    {
      if (!hasNext ())
        throw new NoSuchElementException ();

      final String sClassName = m_sLINextName;
      m_sLINextName = null;
      try
      {
        final SPITYPE p = m_aLIService.cast (Class.forName (sClassName, true, m_aLILoader).newInstance ());
        m_aProviders.put (sClassName, p);
        return p;
      }
      catch (final ClassNotFoundException x)
      {
        _fail (m_aLIService, "Provider " + sClassName + " not found");
      }
      catch (final Throwable x)
      {
        _fail (m_aLIService, "Provider " + sClassName + " could not be instantiated: " + x, x);
      }
      throw new Error (); // This cannot happen
    }

    @UnsupportedOperation
    public void remove ()
    {
      throw new UnsupportedOperationException ();
    }
  }

  /**
   * Lazily loads the available providers of this loader's service.
   * <p>
   * The iterator returned by this method first yields all of the elements of
   * the provider cache, in instantiation order. It then lazily loads and
   * instantiates any remaining providers, adding each one to the cache in turn.
   * <p>
   * To achieve laziness the actual work of parsing the available
   * provider-configuration files and instantiating providers must be done by
   * the iterator itself. Its {@link java.util.Iterator#hasNext hasNext} and
   * {@link java.util.Iterator#next next} methods can therefore throw a
   * {@link ServiceLoaderException} if a provider-configuration file violates
   * the specified format, or if it names a provider class that cannot be found
   * and instantiated, or if the result of instantiating the class is not
   * assignable to the service type, or if any other kind of exception or error
   * is thrown as the next provider is located and instantiated. To write robust
   * code it is only necessary to catch {@link ServiceLoaderException} when
   * using a service iterator.
   * <p>
   * If such an error is thrown then subsequent invocations of the iterator will
   * make a best effort to locate and instantiate the next available provider,
   * but in general such recovery cannot be guaranteed. <blockquote
   * style="font-size: smaller; line-height: 1.2"><span
   * style="padding-right: 1em; font-weight: bold">Design Note</span> Throwing
   * an error in these cases may seem extreme. The rationale for this behavior
   * is that a malformed provider-configuration file, like a malformed class
   * file, indicates a serious problem with the way the Java virtual machine is
   * configured or is being used. As such it is preferable to throw an error
   * rather than try to recover or, even worse, fail silently.</blockquote>
   * <p>
   * The iterator returned by this method does not support removal. Invoking its
   * {@link java.util.Iterator#remove() remove} method will cause an
   * {@link UnsupportedOperationException} to be thrown.
   *
   * @return An iterator that lazily loads providers for this loader's service
   */
  public Iterator <SPITYPE> iterator ()
  {
    return new Iterator <SPITYPE> ()
    {
      private final Iterator <Map.Entry <String, SPITYPE>> m_aKnownProviders = m_aProviders.entrySet ().iterator ();

      public boolean hasNext ()
      {
        return m_aKnownProviders.hasNext () || m_aLookupIterator.hasNext ();
      }

      public SPITYPE next ()
      {
        if (m_aKnownProviders.hasNext ())
          return m_aKnownProviders.next ().getValue ();
        return m_aLookupIterator.next ();
      }

      @UnsupportedOperation
      public void remove ()
      {
        throw new UnsupportedOperationException ();
      }
    };
  }

  /**
   * Returns a string describing this service.
   *
   * @return A descriptive string
   */
  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("serviceName", m_aService.getName ()).toString ();
  }

  /**
   * Creates a new service loader for the given service type and class loader.
   *
   * @param service
   *        The interface or abstract class representing the service
   * @param loader
   *        The class loader to be used to load provider-configuration files and
   *        provider classes, or <tt>null</tt> if the system class loader (or,
   *        failing that, the bootstrap class loader) is to be used
   * @return A new service loader
   */
  public static <S> ServiceLoaderBackport <S> load (final Class <S> service, final ClassLoader loader)
  {
    return new ServiceLoaderBackport <S> (service, loader);
  }

  /**
   * Creates a new service loader for the given service type, using the current
   * thread's {@linkplain java.lang.Thread#getContextClassLoader context class
   * loader}.
   * <p>
   * An invocation of this convenience method of the form <blockquote>
   *
   * <pre>
   * ServiceLoader.load(&lt;i&gt;service&lt;/i&gt;)
   * </pre>
   *
   * </blockquote> is equivalent to <blockquote>
   *
   * <pre>
   * ServiceLoader.load(&lt;i&gt;service&lt;/i&gt;,
   *                    Thread.currentThread().getContextClassLoader())
   * </pre>
   *
   * </blockquote>
   *
   * @param service
   *        The interface or abstract class representing the service
   * @return A new service loader
   */
  public static <S> ServiceLoaderBackport <S> load (final Class <S> service)
  {
    return load (service, ClassHelper.getDefaultClassLoader ());
  }

  /**
   * Creates a new service loader for the given service type, using the
   * extension class loader.
   * <p>
   * This convenience method simply locates the extension class loader, call it
   * <tt><i>extClassLoader</i></tt>, and then returns <blockquote>
   *
   * <pre>
   * ServiceLoader.load(&lt;i&gt;service&lt;/i&gt;, &lt;i&gt;extClassLoader&lt;/i&gt;)
   * </pre>
   *
   * </blockquote>
   * <p>
   * If the extension class loader cannot be found then the system class loader
   * is used; if there is no system class loader then the bootstrap class loader
   * is used.
   * <p>
   * This method is intended for use when only installed providers are desired.
   * The resulting service will only find and load providers that have been
   * installed into the current Java virtual machine; providers on the
   * application's class path will be ignored.
   *
   * @param service
   *        The interface or abstract class representing the service
   * @return A new service loader
   */
  public static <S> ServiceLoaderBackport <S> loadInstalled (final Class <S> service)
  {
    ClassLoader cl = ClassLoader.getSystemClassLoader ();
    ClassLoader prev = null;
    while (cl != null)
    {
      prev = cl;
      cl = cl.getParent ();
    }
    return ServiceLoaderBackport.load (service, prev);
  }
}
