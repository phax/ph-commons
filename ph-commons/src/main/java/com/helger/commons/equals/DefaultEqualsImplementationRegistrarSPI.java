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
package com.helger.commons.equals;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Path;
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
  public void registerEqualsImplementations (@Nonnull final IEqualsImplementationRegistry aRegistry)
  {
    /**
     * Special equals implementation for BigDecimal because
     * <code>BigDecimal.equals</code> returns <code>false</code> if they have a
     * different scale so that "5.5" is not equal "5.50".
     */
    aRegistry.registerEqualsImplementation (BigDecimal.class, (aObj1, aObj2) -> {
      // Compare is ~15% quicker than the setScale version
      if (true)
        return aObj1.compareTo (aObj2) == 0;

      final int nMaxScale = Math.max (aObj1.scale (), aObj2.scale ());
      // Use the same rounding mode for both
      return aObj1.setScale (nMaxScale, RoundingMode.HALF_UP).equals (aObj2.setScale (nMaxScale, RoundingMode.HALF_UP));
    });

    // Special overload for "Double" required!
    aRegistry.registerEqualsImplementation (Double.class, (aObj1, aObj2) -> aObj1.compareTo (aObj2) == 0);

    // Special overload for "Float" required!
    aRegistry.registerEqualsImplementation (Float.class, (aObj1, aObj2) -> aObj1.compareTo (aObj2) == 0);

    // StringBuffer does not implement equals!
    aRegistry.registerEqualsImplementation (StringBuffer.class,
                                            (aObj1, aObj2) -> aObj1.toString ().equals (aObj2.toString ()));

    // StringBuilder does not implement equals!
    aRegistry.registerEqualsImplementation (StringBuilder.class,
                                            (aObj1, aObj2) -> aObj1.toString ().equals (aObj2.toString ()));

    // Node does not implement equals
    aRegistry.registerEqualsImplementation (Node.class, (aObj1, aObj2) -> {
      if (aObj1.getNodeType () != aObj2.getNodeType ())
        return false;
      if (!EqualsImplementationRegistry.areEqual (aObj1.getNodeName (), aObj2.getNodeName ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aObj1.getLocalName (), aObj2.getLocalName ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aObj1.getNamespaceURI (), aObj2.getNamespaceURI ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aObj1.getPrefix (), aObj2.getPrefix ()))
        return false;
      if (!EqualsImplementationRegistry.areEqual (aObj1.getNodeValue (), aObj2.getNodeValue ()))
        return false;

      // For all children
      final NodeList aNL1 = aObj1.getChildNodes ();
      final NodeList aNL2 = aObj2.getChildNodes ();

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
    });

    /**
     * Special equals implementation for URLs because <code>URL.equals</code>
     * performs a host lookup.<br>
     * <a href=
     * "http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html"
     * >Click here for details</a>
     */
    aRegistry.registerEqualsImplementation (URL.class,
                                            (aObj1, aObj2) -> aObj1.toExternalForm ().equals (aObj2.toExternalForm ()));

    // AtomicBoolean does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicBoolean.class, (aObj1, aObj2) -> aObj1.get () == aObj2.get ());

    // AtomicInteger does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicInteger.class, (aObj1, aObj2) -> aObj1.get () == aObj2.get ());

    // AtomicLong does not implement equals!
    aRegistry.registerEqualsImplementation (AtomicLong.class, (aObj1, aObj2) -> aObj1.get () == aObj2.get ());

    // Default array implementations
    // (Object[].class is handled specially!)
    aRegistry.registerEqualsImplementation (boolean [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (byte [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (char [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (double [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (float [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (int [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (long [].class, Arrays::equals);
    aRegistry.registerEqualsImplementation (short [].class, Arrays::equals);

    // Special handling for Map
    aRegistry.registerEqualsImplementation (Map.class, (aObj1, aObj2) -> {
      // Size check
      if (aObj1.size () != aObj2.size ())
        return false;

      // Content check
      for (final Object aEntry1 : aObj1.entrySet ())
      {
        final Object aKey1 = ((Map.Entry <?, ?>) aEntry1).getKey ();
        final Object aValue1 = ((Map.Entry <?, ?>) aEntry1).getValue ();
        if (aValue1 == null)
        {
          // Second map must also contain null value
          if (!(aObj2.get (aKey1) == null && aObj2.containsKey (aKey1)))
            return false;
        }
        else
        {
          // Check value
          final Object aValue2 = aObj2.get (aKey1);
          if (!EqualsImplementationRegistry.areEqual (aValue1, aValue2))
            return false;
        }
      }
      return true;
    });

    // Special handling for Collection
    aRegistry.registerEqualsImplementation (Collection.class, (aObj1, aObj2) -> {
      // Size check
      if (aObj1.size () != aObj2.size ())
        return false;

      // Content check
      final Object [] aData1 = aObj1.toArray ();
      final Object [] aData2 = aObj2.toArray ();
      return EqualsImplementationRegistry.areEqual (aData1, aData2);
    });

    // Special handling for Iterator
    aRegistry.registerEqualsImplementation (Iterator.class, (aObj1, aObj2) -> {
      while (aObj1.hasNext ())
      {
        if (!aObj2.hasNext ())
        {
          // Second iterator is shorter
          return false;
        }
        final Object aChild1 = aObj1.next ();
        final Object aChild2 = aObj2.next ();
        if (!EqualsImplementationRegistry.areEqual (aChild1, aChild2))
          return false;
      }
      // Second iterator should not be longer
      return !aObj2.hasNext ();
    });

    // Special handling for Enumeration
    aRegistry.registerEqualsImplementation (Enumeration.class, (aObj1, aObj2) -> {
      while (aObj1.hasMoreElements ())
      {
        if (!aObj2.hasMoreElements ())
        {
          // Second enumeration is shorter
          return false;
        }
        final Object aChild1 = aObj1.nextElement ();
        final Object aChild2 = aObj2.nextElement ();
        if (!EqualsImplementationRegistry.areEqual (aChild1, aChild2))
          return false;
      }
      // Second enumeration should not be longer
      return !aObj2.hasMoreElements ();
    });

    // Special handling for File
    aRegistry.registerEqualsImplementation (File.class,
                                            (aObj1,
                                             aObj2) -> FilenameHelper.getCleanPath (aObj1.getAbsoluteFile ())
                                                                     .equals (FilenameHelper.getCleanPath (aObj2.getAbsoluteFile ())));

    aRegistry.registerEqualsImplementation (Path.class, new IEqualsImplementation <Path> ()
    {
      public boolean areEqual (final Path aObj1, final Path aObj2)
      {
        try
        {
          return aObj1.toRealPath ().equals (aObj2.toRealPath ());
        }
        catch (final IOException ex)
        {
          return aObj1.equals (aObj2);
        }
      }

      @Override
      public boolean implementationEqualsOverridesInterface ()
      {
        return false;
      }
    });

    // Special handling for Locale in JDK >= 1.7
    aRegistry.registerEqualsImplementation (Locale.class,
                                            (aObj1, aObj2) -> aObj1.toString ().equals (aObj2.toString ()));
  }
}
