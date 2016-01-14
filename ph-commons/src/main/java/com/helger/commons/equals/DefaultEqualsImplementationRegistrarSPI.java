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
package com.helger.commons.equals;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.system.EJavaVersion;

/**
 * This class registers the default equals implementations. The implementations
 * in here should be aligned with the implementations in the
 * {@link com.helger.commons.hashcode.DefaultHashCodeImplementationRegistrarSPI}
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class DefaultEqualsImplementationRegistrarSPI implements IEqualsImplementationRegistrarSPI
{
  private static final class EqualsImplementationFile implements IEqualsImplementation
  {
    public boolean areEqual (@Nonnull final Object aObj1, @Nonnull final Object aObj2)
    {
      final File aFile1 = (File) aObj1;
      final File aFile2 = (File) aObj2;
      return FilenameHelper.getCleanPath (aFile1.getAbsoluteFile ())
                           .equals (FilenameHelper.getCleanPath (aFile2.getAbsoluteFile ()));
    }
  }

  private static final class EqualsImplementationEnumeration implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Enumeration <?> aRealObj1 = (Enumeration <?>) aObj1;
      final Enumeration <?> aRealObj2 = (Enumeration <?>) aObj2;

      while (aRealObj1.hasMoreElements ())
      {
        if (!aRealObj2.hasMoreElements ())
        {
          // Second enumeration is shorter
          return false;
        }
        final Object aChild1 = aRealObj1.nextElement ();
        final Object aChild2 = aRealObj2.nextElement ();
        if (!EqualsImplementationRegistry.areEqual (aChild1, aChild2))
          return false;
      }
      if (aRealObj2.hasMoreElements ())
      {
        // Second enumeration is long
        return false;
      }
      return true;
    }
  }

  private static final class EqualsImplementationIterator implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Iterator <?> aRealObj1 = (Iterator <?>) aObj1;
      final Iterator <?> aRealObj2 = (Iterator <?>) aObj2;

      while (aRealObj1.hasNext ())
      {
        if (!aRealObj2.hasNext ())
        {
          // Second iterator is shorter
          return false;
        }
        final Object aChild1 = aRealObj1.next ();
        final Object aChild2 = aRealObj2.next ();
        if (!EqualsImplementationRegistry.areEqual (aChild1, aChild2))
          return false;
      }
      if (aRealObj2.hasNext ())
      {
        // Second iterator is longer
        return false;
      }
      return true;
    }
  }

  private static final class EqualsImplementationCollection implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Collection <?> aRealObj1 = (Collection <?>) aObj1;
      final Collection <?> aRealObj2 = (Collection <?>) aObj2;

      // Size check
      if (aRealObj1.size () != aRealObj2.size ())
        return false;

      // Content check
      final Object [] aData1 = aRealObj1.toArray ();
      final Object [] aData2 = aRealObj2.toArray ();
      return EqualsImplementationRegistry.areEqual (aData1, aData2);
    }
  }

  private static final class EqualsImplementationMap implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Map <?, ?> aRealObj1 = (Map <?, ?>) aObj1;
      final Map <?, ?> aRealObj2 = (Map <?, ?>) aObj2;

      // Size check
      if (aRealObj1.size () != aRealObj2.size ())
        return false;

      // Content check
      for (final Map.Entry <?, ?> aEntry1 : aRealObj1.entrySet ())
      {
        final Object aKey1 = aEntry1.getKey ();
        final Object aValue1 = aEntry1.getValue ();
        if (aValue1 == null)
        {
          // Second map must also contain null value
          if (!(aRealObj2.get (aKey1) == null && aRealObj2.containsKey (aKey1)))
            return false;
        }
        else
        {
          // Check value
          final Object aValue2 = aRealObj2.get (aKey1);
          if (!EqualsImplementationRegistry.areEqual (aValue1, aValue2))
            return false;
        }
      }
      return true;
    }
  }

  private static final class EqualsImplementationArrayShort implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((short []) aObj1, (short []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayLong implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((long []) aObj1, (long []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayInt implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((int []) aObj1, (int []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayFloat implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((float []) aObj1, (float []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayDouble implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((double []) aObj1, (double []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayChar implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((char []) aObj1, (char []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayByte implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((byte []) aObj1, (byte []) aObj2);
    }
  }

  private static final class EqualsImplementationArrayBoolean implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return Arrays.equals ((boolean []) aObj1, (boolean []) aObj2);
    }
  }

  private static final class EqualsImplementationAtomicLong implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final AtomicLong aRealObj1 = (AtomicLong) aObj1;
      final AtomicLong aRealObj2 = (AtomicLong) aObj2;
      return aRealObj1.get () == aRealObj2.get ();
    }
  }

  private static final class EqualsImplementationAtomicInteger implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final AtomicInteger aRealObj1 = (AtomicInteger) aObj1;
      final AtomicInteger aRealObj2 = (AtomicInteger) aObj2;
      return aRealObj1.get () == aRealObj2.get ();
    }
  }

  private static final class EqualsImplementationAtomicBoolean implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final AtomicBoolean aRealObj1 = (AtomicBoolean) aObj1;
      final AtomicBoolean aRealObj2 = (AtomicBoolean) aObj2;
      return aRealObj1.get () == aRealObj2.get ();
    }
  }

  private static final class EqualsImplementationURL implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final URL aRealObj1 = (URL) aObj1;
      final URL aRealObj2 = (URL) aObj2;
      return aRealObj1.toExternalForm ().equals (aRealObj2.toExternalForm ());
    }
  }

  private static final class EqualsImplementationNode implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Node aRealObj1 = (Node) aObj1;
      final Node aRealObj2 = (Node) aObj2;
      if (aRealObj1.getNodeType () != aRealObj2.getNodeType ())
        return false;
      if (!EqualsImplementationRegistry.areEqual (aRealObj1.getNodeName (), aRealObj2.getNodeName ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aRealObj1.getLocalName (), aRealObj2.getLocalName ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aRealObj1.getNamespaceURI (), aRealObj2.getNamespaceURI ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aRealObj1.getPrefix (), aRealObj2.getPrefix ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aRealObj1.getNodeValue (), aRealObj2.getNodeValue ()))
        return false;

      // For all children
      final NodeList aNL1 = aRealObj1.getChildNodes ();
      final NodeList aNL2 = aRealObj2.getChildNodes ();

      final int nLength = aNL1.getLength ();
      if (nLength != aNL2.getLength ())
        return false;

      for (int i = 0; i < nLength; ++i)
      {
        final Node aChild1 = aNL1.item (i);
        final Node aChild2 = aNL2.item (i);
        if (!EqualsImplementationRegistry.areEqual (aChild1, aChild2))
          return false;
      }

      return true;
    }
  }

  private static final class EqualsImplementationStringBuilder implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return aObj1.toString ().equals (aObj2.toString ());
    }
  }

  private static final class EqualsImplementationStringBuffer implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      return aObj1.toString ().equals (aObj2.toString ());
    }
  }

  private static final class EqualsImplementationFloat implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Float aRealObj1 = (Float) aObj1;
      final Float aRealObj2 = (Float) aObj2;
      return aRealObj1.compareTo (aRealObj2) == 0;
    }
  }

  private static final class EqualsImplementationDouble implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Double aRealObj1 = (Double) aObj1;
      final Double aRealObj2 = (Double) aObj2;
      return aRealObj1.compareTo (aRealObj2) == 0;
    }
  }

  private static final class EqualsImplementationBigDecimal implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final BigDecimal aRealObj1 = (BigDecimal) aObj1;
      final BigDecimal aRealObj2 = (BigDecimal) aObj2;
      // Compare is ~15% quicker than the setScale version
      if (true)
        return aRealObj1.compareTo (aRealObj2) == 0;

      final int nMaxScale = Math.max (aRealObj1.scale (), aRealObj2.scale ());
      // Use the same rounding mode for both
      return aRealObj1.setScale (nMaxScale, RoundingMode.HALF_UP)
                      .equals (aRealObj2.setScale (nMaxScale, RoundingMode.HALF_UP));
    }
  }

  private static final class EqualsImplementationLocale implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Locale aRealObj1 = (Locale) aObj1;
      final Locale aRealObj2 = (Locale) aObj2;
      return aRealObj1.toString ().equals (aRealObj2.toString ());
    }
  }

  public void registerEqualsImplementations (@Nonnull final IEqualsImplementationRegistry aRegistry)
  {
    /**
     * Special equals implementation for BigDecimal because
     * <code>BigDecimal.equals</code> returns <code>false</code> if they have a
     * different scale so that "5.5" is not equal "5.50".
     */
    aRegistry.registerEqualsImplementation (BigDecimal.class, new EqualsImplementationBigDecimal ());

    // Special overload for "Double" required!
    aRegistry.registerEqualsImplementation (Double.class, new EqualsImplementationDouble ());

    // Special overload for "Float" required!
    aRegistry.registerEqualsImplementation (Float.class, new EqualsImplementationFloat ());

    // StringBuffer does not implement equals!
    aRegistry.registerEqualsImplementation (StringBuffer.class, new EqualsImplementationStringBuffer ());

    // StringBuilder does not implement equals!
    aRegistry.registerEqualsImplementation (StringBuilder.class, new EqualsImplementationStringBuilder ());

    // Node does not implement equals
    aRegistry.registerEqualsImplementation (Node.class, new EqualsImplementationNode ());

    /**
     * Special equals implementation for URLs because <code>URL.equals</code>
     * performs a host lookup.<br>
     * <a href=
     * "http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html"
     * >Click here for details</a>
     */
    aRegistry.registerEqualsImplementation (URL.class, new EqualsImplementationURL ());

    // AtomicBoolean does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicBoolean.class, new EqualsImplementationAtomicBoolean ());

    // AtomicInteger does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicInteger.class, new EqualsImplementationAtomicInteger ());

    // AtomicLong does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicLong.class, new EqualsImplementationAtomicLong ());

    // Default array implementations
    // (Object[].class is handled specially!)
    aRegistry.registerEqualsImplementation (boolean [].class, new EqualsImplementationArrayBoolean ());
    aRegistry.registerEqualsImplementation (byte [].class, new EqualsImplementationArrayByte ());
    aRegistry.registerEqualsImplementation (char [].class, new EqualsImplementationArrayChar ());
    aRegistry.registerEqualsImplementation (double [].class, new EqualsImplementationArrayDouble ());
    aRegistry.registerEqualsImplementation (float [].class, new EqualsImplementationArrayFloat ());
    aRegistry.registerEqualsImplementation (int [].class, new EqualsImplementationArrayInt ());
    aRegistry.registerEqualsImplementation (long [].class, new EqualsImplementationArrayLong ());
    aRegistry.registerEqualsImplementation (short [].class, new EqualsImplementationArrayShort ());

    // Special handling for Map
    aRegistry.registerEqualsImplementation (Map.class, new EqualsImplementationMap ());

    // Special handling for Collection
    aRegistry.registerEqualsImplementation (Collection.class, new EqualsImplementationCollection ());

    // Special handling for Iterator
    aRegistry.registerEqualsImplementation (Iterator.class, new EqualsImplementationIterator ());

    // Special handling for Enumeration
    aRegistry.registerEqualsImplementation (Enumeration.class, new EqualsImplementationEnumeration ());

    // Special handling for File
    aRegistry.registerEqualsImplementation (File.class, new EqualsImplementationFile ());

    // Special handling for Locale in JDK >= 1.7
    if (EJavaVersion.JDK_17.isSupportedVersion ())
      aRegistry.registerEqualsImplementation (Locale.class, new EqualsImplementationLocale ());
  }
}
