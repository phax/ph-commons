/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.util.HashSet;
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
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.typeconvert.rule.TypeConverterRuleAnySourceFixedDestination;

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
   * <li>ArrayList</li>
   * <li>Vector</li>
   * <li>LinkedList</li>
   * <li>CopyOnWriteArrayList</li>
   * <li>List</li>
   * <li>HashSet</li>
   * <li>TreeSet</li>
   * <li>LinkedHashSet</li>
   * <li>CopyOnWriteArraySet</li>
   * <li>Set</li>
   * </ul>
   */
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // to ArrayList<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (ArrayList.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new ArrayList <Object> ((Collection <?>) aSource);
      final ArrayList <Object> ret = new ArrayList <Object> (1);
      ret.add (aSource);
      return ret;
    }));

    // to Vector<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (Vector.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new Vector <Object> ((Collection <?>) aSource);
      final Vector <Object> ret = new Vector <Object> (1);
      ret.add (aSource);
      return ret;
    }));

    // to LinkedList<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (LinkedList.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new LinkedList <Object> ((Collection <?>) aSource);
      final LinkedList <Object> ret = new LinkedList <Object> ();
      ret.add (aSource);
      return ret;
    }));

    // to CopyOnWriteArrayList<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (CopyOnWriteArrayList.class,
                                                                                         aSource -> {
                                                                                           if (aSource instanceof Collection <?>)
                                                                                             return new CopyOnWriteArrayList <Object> ((Collection <?>) aSource);
                                                                                           final CopyOnWriteArrayList <Object> ret = new CopyOnWriteArrayList <Object> ();
                                                                                           ret.add (aSource);
                                                                                           return ret;
                                                                                         }));

    // to List<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (List.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return CollectionHelper.newList ((Collection <?>) aSource);
      return CollectionHelper.newList (aSource);
    }));

    // to TreeSet<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (TreeSet.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return new TreeSet <Object> ((Collection <?>) aSource);
      final TreeSet <Object> ret = new TreeSet <Object> ();
      ret.add (aSource);
      return ret;
    }));

    // to LinkedHashSet<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (LinkedHashSet.class,
                                                                                         aSource -> {
                                                                                           if (aSource instanceof Collection <?>)
                                                                                             return new LinkedHashSet <Object> ((Collection <?>) aSource);
                                                                                           final LinkedHashSet <Object> ret = new LinkedHashSet <Object> (1);
                                                                                           ret.add (aSource);
                                                                                           return ret;
                                                                                         }));

    // to CopyOnWriteArraySet<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (CopyOnWriteArraySet.class,
                                                                                         aSource -> {
                                                                                           if (aSource instanceof Collection <?>)
                                                                                             return new CopyOnWriteArraySet <Object> ((Collection <?>) aSource);
                                                                                           final CopyOnWriteArraySet <Object> ret = new CopyOnWriteArraySet <Object> ();
                                                                                           ret.add (aSource);
                                                                                           return ret;
                                                                                         }));

    // to Set<?>
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (Set.class, aSource -> {
      if (aSource instanceof Collection <?>)
        return CollectionHelper.newSet ((Collection <?>) aSource);
      return CollectionHelper.newSet (aSource);
    }));

    // boolean[]
    aRegistry.registerTypeConverter (boolean [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newBooleanList ((boolean []) aSource));
    aRegistry.registerTypeConverter (boolean [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newBooleanVector ((boolean []) aSource));
    aRegistry.registerTypeConverter (boolean [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newBooleanSet ((boolean []) aSource));
    aRegistry.registerTypeConverter (boolean [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newBooleanOrderedSet ((boolean []) aSource));
    aRegistry.registerTypeConverter (boolean [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newBooleanSortedSet ((boolean []) aSource));

    // byte[]
    aRegistry.registerTypeConverter (byte [].class, String.class, aSource -> Base64.encodeBytes ((byte []) aSource));
    aRegistry.registerTypeConverter (String.class, byte [].class, aSource -> Base64.safeDecode ((String) aSource));
    aRegistry.registerTypeConverter (byte [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newByteList ((byte []) aSource));
    aRegistry.registerTypeConverter (byte [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newByteVector ((byte []) aSource));
    aRegistry.registerTypeConverter (byte [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newByteSet ((byte []) aSource));
    aRegistry.registerTypeConverter (byte [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newByteOrderedSet ((byte []) aSource));
    aRegistry.registerTypeConverter (byte [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newByteSortedSet ((byte []) aSource));

    // char[]
    aRegistry.registerTypeConverter (char [].class, String.class, aSource -> new String ((char []) aSource));
    aRegistry.registerTypeConverter (String.class, char [].class, aSource -> ((String) aSource).toCharArray ());
    aRegistry.registerTypeConverter (char [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newCharList ((char []) aSource));
    aRegistry.registerTypeConverter (char [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newCharVector ((char []) aSource));
    aRegistry.registerTypeConverter (char [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newCharSet ((char []) aSource));
    aRegistry.registerTypeConverter (char [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newCharOrderedSet ((char []) aSource));
    aRegistry.registerTypeConverter (char [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newCharSortedSet ((char []) aSource));

    // double[]
    aRegistry.registerTypeConverter (double [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newDoubleList ((double []) aSource));
    aRegistry.registerTypeConverter (double [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newDoubleVector ((double []) aSource));
    aRegistry.registerTypeConverter (double [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newDoubleSet ((double []) aSource));
    aRegistry.registerTypeConverter (double [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newDoubleOrderedSet ((double []) aSource));
    aRegistry.registerTypeConverter (double [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newDoubleSortedSet ((double []) aSource));

    // float[]
    aRegistry.registerTypeConverter (float [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newFloatList ((float []) aSource));
    aRegistry.registerTypeConverter (float [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newFloatVector ((float []) aSource));
    aRegistry.registerTypeConverter (float [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newFloatSet ((float []) aSource));
    aRegistry.registerTypeConverter (float [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newFloatOrderedSet ((float []) aSource));
    aRegistry.registerTypeConverter (float [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newFloatSortedSet ((float []) aSource));

    // int[]
    aRegistry.registerTypeConverter (int [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newIntList ((int []) aSource));
    aRegistry.registerTypeConverter (int [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newIntVector ((int []) aSource));
    aRegistry.registerTypeConverter (int [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newIntSet ((int []) aSource));
    aRegistry.registerTypeConverter (int [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newIntOrderedSet ((int []) aSource));
    aRegistry.registerTypeConverter (int [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newIntSortedSet ((int []) aSource));

    // long[]
    aRegistry.registerTypeConverter (long [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newLongList ((long []) aSource));
    aRegistry.registerTypeConverter (long [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newLongVector ((long []) aSource));
    aRegistry.registerTypeConverter (long [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newLongSet ((long []) aSource));
    aRegistry.registerTypeConverter (long [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newLongOrderedSet ((long []) aSource));
    aRegistry.registerTypeConverter (long [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newLongSortedSet ((long []) aSource));

    // short[]
    aRegistry.registerTypeConverter (short [].class,
                                     ArrayList.class,
                                     aSource -> CollectionHelper.newShortList ((short []) aSource));
    aRegistry.registerTypeConverter (short [].class,
                                     Vector.class,
                                     aSource -> CollectionHelper.newShortVector ((short []) aSource));
    aRegistry.registerTypeConverter (short [].class,
                                     HashSet.class,
                                     aSource -> CollectionHelper.newShortSet ((short []) aSource));
    aRegistry.registerTypeConverter (short [].class,
                                     LinkedHashSet.class,
                                     aSource -> CollectionHelper.newShortOrderedSet ((short []) aSource));
    aRegistry.registerTypeConverter (short [].class,
                                     TreeSet.class,
                                     aSource -> CollectionHelper.newShortSortedSet ((short []) aSource));

    // To array
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (boolean [].class, aSource -> {
      boolean [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to boolean[]
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
          // Collection to boolean[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new boolean [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToBoolean (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new boolean [1];
          ret[0] = TypeConverter.convertToBoolean (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (byte [].class, aSource -> {
      byte [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to byte[]
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
          // Collection to byte[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new byte [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToByte (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new byte [1];
          ret[0] = TypeConverter.convertToByte (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (char [].class, aSource -> {
      char [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to char[]
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
          // Collection to char[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new char [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToChar (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new char [1];
          ret[0] = TypeConverter.convertToChar (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (double [].class, aSource -> {
      double [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to double[]
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
          // Collection to double[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new double [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToDouble (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new double [1];
          ret[0] = TypeConverter.convertToDouble (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (float [].class, aSource -> {
      float [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to float[]
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
          // Collection to float[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new float [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToFloat (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new float [1];
          ret[0] = TypeConverter.convertToFloat (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (int [].class, aSource -> {
      int [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to int[]
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
          // Collection to int[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new int [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToInt (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new int [1];
          ret[0] = TypeConverter.convertToInt (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (long [].class, aSource -> {
      long [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to long[]
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
          // Collection to long[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new long [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToLong (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new long [1];
          ret[0] = TypeConverter.convertToLong (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (short [].class, aSource -> {
      short [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to short[]
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
          // Collection to short[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new short [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertToShort (aSourceElement);
        }
        else
        {
          // Use object as is
          ret = new short [1];
          ret[0] = TypeConverter.convertToShort (aSource);
        }
      return ret;
    }));

    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (String [].class, aSource -> {
      String [] ret;
      final Class <?> aSourceClass = aSource.getClass ();
      if (aSourceClass.isArray ())
      {
        // Array to String[]
        final int nLength = Array.getLength (aSource);
        ret = new String [nLength];
        for (int i = 0; i < nLength; ++i)
        {
          final Object aSourceElement = Array.get (aSource, i);
          ret[i] = TypeConverter.convertIfNecessary (aSourceElement, String.class);
        }
      }
      else
        if (aSource instanceof Collection <?>)
        {
          // Collection to String[]
          final Collection <?> aSourceCollection = (Collection <?>) aSource;
          ret = new String [aSourceCollection.size ()];
          int nIndex = 0;
          for (final Object aSourceElement : aSourceCollection)
            ret[nIndex++] = TypeConverter.convertIfNecessary (aSourceElement, String.class);
        }
        else
        {
          // Use object as is
          ret = new String [1];
          ret[0] = TypeConverter.convertIfNecessary (aSource, String.class);
        }
      return ret;
    }));
  }
}
