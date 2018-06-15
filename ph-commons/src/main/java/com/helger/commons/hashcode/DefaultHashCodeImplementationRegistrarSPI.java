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
package com.helger.commons.hashcode;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
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
 * This class registers the default hash code implementations. The
 * implementations in here should be aligned with the implementations in the
 * {@link com.helger.commons.equals.DefaultEqualsImplementationRegistrarSPI}
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class DefaultHashCodeImplementationRegistrarSPI implements IHashCodeImplementationRegistrarSPI
{
  public void registerHashCodeImplementations (@Nonnull final IHashCodeImplementationRegistry aRegistry)
  {
    // StringBuffer does not implement hashCode!
    aRegistry.registerHashCodeImplementation (StringBuffer.class, x -> x.toString ().hashCode ());

    // StringBuilder does not implement hashCode!
    aRegistry.registerHashCodeImplementation (StringBuilder.class, x -> x.toString ().hashCode ());

    // Node does not implement hashCode
    aRegistry.registerHashCodeImplementation (Node.class, x -> {
      HashCodeGenerator aHC = new HashCodeGenerator (x).append (x.getNodeType ())
                                                       .append (x.getNodeName ())
                                                       .append (x.getLocalName ())
                                                       .append (x.getNamespaceURI ())
                                                       .append (x.getPrefix ())
                                                       .append (x.getNodeValue ());

      // For all children
      final NodeList aNL = x.getChildNodes ();
      final int nLength = aNL.getLength ();
      aHC = aHC.append (nLength);
      for (int i = 0; i < nLength; ++i)
        aHC = aHC.append (aNL.item (i));
      return aHC.getHashCode ();
    });

    // AtomicBoolean does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicBoolean.class, x -> HashCodeCalculator.append (0, x.get ()));

    // AtomicInteger does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicInteger.class, x -> HashCodeCalculator.append (0, x.get ()));

    // AtomicLong does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicLong.class, x -> HashCodeCalculator.append (0, x.get ()));

    // Special handling for arrays
    // (Object[] is handled internally)
    aRegistry.registerHashCodeImplementation (boolean [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (byte [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (char [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (double [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (float [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (int [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (long [].class, Arrays::hashCode);
    aRegistry.registerHashCodeImplementation (short [].class, Arrays::hashCode);

    // Special handling for Map
    aRegistry.registerHashCodeImplementation (Map.class, x -> {
      HashCodeGenerator aHC = new HashCodeGenerator (x).append (x.size ());
      for (final Object aEntry : x.entrySet ())
      {
        final Map.Entry <?, ?> aRealEntry = (Map.Entry <?, ?>) aEntry;
        aHC = aHC.append (aRealEntry.getKey ()).append (aRealEntry.getValue ());
      }
      return aHC.getHashCode ();
    });

    // Special handling for Collection
    aRegistry.registerHashCodeImplementation (Collection.class, x -> {
      HashCodeGenerator aHC = new HashCodeGenerator (x).append (x.size ());
      for (final Object aMember : x)
        aHC = aHC.append (aMember);
      return aHC.getHashCode ();
    });

    // Special handling for Iterator
    aRegistry.registerHashCodeImplementation (Iterator.class, x -> {
      HashCodeGenerator aHC = new HashCodeGenerator (x);
      while (x.hasNext ())
        aHC = aHC.append (x.next ());
      return aHC.getHashCode ();
    });

    // Special handling for Enumeration
    aRegistry.registerHashCodeImplementation (Enumeration.class, x -> {
      HashCodeGenerator aHC = new HashCodeGenerator (x);
      while (x.hasMoreElements ())
        aHC = aHC.append (x.nextElement ());
      return aHC.getHashCode ();
    });

    // Special handling for File
    aRegistry.registerHashCodeImplementation (File.class,
                                              x -> FilenameHelper.getCleanPath (x.getAbsoluteFile ()).hashCode ());
  }
}
