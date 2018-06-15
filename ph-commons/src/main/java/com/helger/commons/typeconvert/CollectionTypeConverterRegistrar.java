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
package com.helger.commons.typeconvert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.base64.Base64;
import com.helger.commons.collection.PrimitiveCollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsCopyOnWriteArrayList;
import com.helger.commons.collection.impl.CommonsCopyOnWriteArraySet;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsLinkedList;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.CommonsVector;

/**
 * Register the base type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class CollectionTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  /**
   * Register type converters for the collection types:<br>
   * <ul>
   * <li>CommonsList</li>
   * <li>CommonsVector</li>
   * <li>LinkedList</li>
   * <li>CopyOnWriteArrayList</li>
   * <li>List</li>
   * <li>CommonsHashSet</li>
   * <li>CommonsTreeSet</li>
   * <li>CommonsLinkedHashSet</li>
   * <li>CopyOnWriteArraySet</li>
   * <li>Set</li>
   * </ul>
   */
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // to CommonsList<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (ArrayList.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsArrayList <> ((Collection <?>) aSource);
      return new CommonsArrayList <> (aSource);
    });

    // to CommonsVector<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Vector.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsVector <> ((Collection <?>) aSource);
      return new CommonsVector <> (aSource);
    });

    // to LinkedList<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (LinkedList.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsLinkedList <> ((Collection <?>) aSource);
      return new CommonsLinkedList <> (aSource);
    });

    // to CopyOnWriteArrayList<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (CopyOnWriteArrayList.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsCopyOnWriteArrayList <> ((Collection <?>) aSource);
      return new CommonsCopyOnWriteArrayList <> (aSource);
    });

    // to List<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (List.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsArrayList <> ((Collection <?>) aSource);
      return new CommonsArrayList <> (aSource);
    });

    // to CommonsTreeSet<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (TreeSet.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsTreeSet <> ((Collection <?>) aSource);
      return new CommonsTreeSet <> (aSource);
    });

    // to CommonsLinkedHashSet<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (LinkedHashSet.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsLinkedHashSet <> ((Collection <?>) aSource);
      return new CommonsLinkedHashSet <> (aSource);
    });

    // to CopyOnWriteArraySet<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (CopyOnWriteArraySet.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsCopyOnWriteArraySet <> ((Collection <?>) aSource);
      return new CommonsCopyOnWriteArraySet <> (aSource);
    });

    // to Set<?>
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Set.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new CommonsHashSet <> ((Collection <?>) aSource);
      return new CommonsHashSet <> (aSource);
    });

    // boolean[]
    aRegistry.registerTypeConverter (boolean [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (boolean [].class,
                                     CommonsHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (boolean [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (boolean [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // byte[]
    aRegistry.registerTypeConverter (byte [].class, String.class, Base64::encodeBytes);
    aRegistry.registerTypeConverter (String.class, byte [].class, Base64::safeDecode);
    aRegistry.registerTypeConverter (byte [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (byte [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (byte [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (byte [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // char[]
    aRegistry.registerTypeConverter (char [].class, String.class, String::new);
    aRegistry.registerTypeConverter (String.class, char [].class, String::toCharArray);
    aRegistry.registerTypeConverter (char [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (char [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (char [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (char [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // double[]
    aRegistry.registerTypeConverter (double [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (double [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (double [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (double [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // float[]
    aRegistry.registerTypeConverter (float [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (float [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (float [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (float [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // int[]
    aRegistry.registerTypeConverter (int [].class, CommonsArrayList.class, PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (int [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (int [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (int [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // long[]
    aRegistry.registerTypeConverter (long [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (long [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (long [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (long [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // short[]
    aRegistry.registerTypeConverter (short [].class,
                                     CommonsArrayList.class,
                                     PrimitiveCollectionHelper::newPrimitiveList);
    aRegistry.registerTypeConverter (short [].class, CommonsHashSet.class, PrimitiveCollectionHelper::newPrimitiveSet);
    aRegistry.registerTypeConverter (short [].class,
                                     CommonsLinkedHashSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveOrderedSet);
    aRegistry.registerTypeConverter (short [].class,
                                     CommonsTreeSet.class,
                                     PrimitiveCollectionHelper::newPrimitiveSortedSet);

    // To array
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (boolean [].class, aSource -> {
      boolean [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to boolean[] */
        final int nLength = Array.getLength (aSource);
        ret = new boolean [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToBoolean (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to boolean[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new boolean [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToBoolean (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new boolean [1];
          ret[0] = TypeConverter.convertToBoolean (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (byte [].class, aSource -> {
      byte [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to byte[] */
        final int nLength = Array.getLength (aSource);
        ret = new byte [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToByte (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to byte[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new byte [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToByte (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new byte [1];
          ret[0] = TypeConverter.convertToByte (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (char [].class, aSource -> {
      char [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to char[] */
        final int nLength = Array.getLength (aSource);
        ret = new char [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToChar (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to char[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new char [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToChar (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new char [1];
          ret[0] = TypeConverter.convertToChar (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (double [].class, aSource -> {
      double [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to double[] */
        final int nLength = Array.getLength (aSource);
        ret = new double [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToDouble (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to double[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new double [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToDouble (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new double [1];
          ret[0] = TypeConverter.convertToDouble (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (float [].class, aSource -> {
      float [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to float[] */
        final int nLength = Array.getLength (aSource);
        ret = new float [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToFloat (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to float[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new float [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToFloat (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new float [1];
          ret[0] = TypeConverter.convertToFloat (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (int [].class, aSource -> {
      int [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to int[] */
        final int nLength = Array.getLength (aSource);
        ret = new int [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToInt (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to int[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new int [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToInt (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new int [1];
          ret[0] = TypeConverter.convertToInt (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (long [].class, aSource -> {
      long [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to long[] */
        final int nLength = Array.getLength (aSource);
        ret = new long [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToLong (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to long[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new long [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToLong (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new long [1];
          ret[0] = TypeConverter.convertToLong (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (short [].class, aSource -> {
      short [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to short[] */
        final int nLength = Array.getLength (aSource);
        ret = new short [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertToShort (aSourceElement);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to short[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new short [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToShort (aSourceElement);
        }
        else
        {
          /* Use object as is */
          ret = new short [1];
          ret[0] = TypeConverter.convertToShort (aSource);
        }
      return ret;
    });

    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (String [].class, aSource -> {
      String [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        /* Array to String[] */
        final int nLength = Array.getLength (aSource);
        ret = new String [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convert (aSourceElement, String.class);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          /* Collection to String[] */
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new String [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convert (aSourceElement, String.class);
        }
        else
        {
          /* Use object as is */
          ret = new String [1];
          ret[0] = TypeConverter.convert (aSource, String.class);
        }
      return ret;
    });
  }
}
