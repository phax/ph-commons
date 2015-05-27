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

import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.base64.Base64;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleAnySourceFixedDestination;

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
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (ArrayList.class)
    {
      public ArrayList <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new ArrayList <Object> ((Collection <?>) aSource);
        final ArrayList <Object> ret = new ArrayList <Object> (1);
        ret.add (aSource);
        return ret;
      }
    });

    // to Vector<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Vector.class)
    {
      public Vector <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new Vector <Object> ((Collection <?>) aSource);
        final Vector <Object> ret = new Vector <Object> (1);
        ret.add (aSource);
        return ret;
      }
    });

    // to LinkedList<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (LinkedList.class)
    {
      public LinkedList <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new LinkedList <Object> ((Collection <?>) aSource);
        final LinkedList <Object> ret = new LinkedList <Object> ();
        ret.add (aSource);
        return ret;
      }
    });

    // to CopyOnWriteArrayList<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (CopyOnWriteArrayList.class)
    {
      public CopyOnWriteArrayList <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new CopyOnWriteArrayList <Object> ((Collection <?>) aSource);
        final CopyOnWriteArrayList <Object> ret = new CopyOnWriteArrayList <Object> ();
        ret.add (aSource);
        return ret;
      }
    });

    // to List<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (List.class)
    {
      public List <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return CollectionHelper.newList ((Collection <?>) aSource);
        return CollectionHelper.newList (aSource);
      }
    });

    // to TreeSet<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (TreeSet.class)
    {
      public TreeSet <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new TreeSet <Object> ((Collection <?>) aSource);
        final TreeSet <Object> ret = new TreeSet <Object> ();
        ret.add (aSource);
        return ret;
      }
    });

    // to LinkedHashSet<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (LinkedHashSet.class)
    {
      public LinkedHashSet <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new LinkedHashSet <Object> ((Collection <?>) aSource);
        final LinkedHashSet <Object> ret = new LinkedHashSet <Object> (1);
        ret.add (aSource);
        return ret;
      }
    });

    // to CopyOnWriteArraySet<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (CopyOnWriteArraySet.class)
    {
      public CopyOnWriteArraySet <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return new CopyOnWriteArraySet <Object> ((Collection <?>) aSource);
        final CopyOnWriteArraySet <Object> ret = new CopyOnWriteArraySet <Object> ();
        ret.add (aSource);
        return ret;
      }
    });

    // to Set<?>
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Set.class)
    {
      public Set <?> convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof Collection <?>)
          return CollectionHelper.newSet ((Collection <?>) aSource);
        return CollectionHelper.newSet (aSource);
      }
    });

    // boolean[]
    aRegistry.registerTypeConverter (boolean [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Boolean> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newBooleanList ((boolean []) aSource);
      }
    });
    aRegistry.registerTypeConverter (boolean [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Boolean> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newBooleanVector ((boolean []) aSource);
      }
    });
    aRegistry.registerTypeConverter (boolean [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Boolean> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newBooleanSet ((boolean []) aSource);
      }
    });
    aRegistry.registerTypeConverter (boolean [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Boolean> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newBooleanOrderedSet ((boolean []) aSource);
      }
    });
    aRegistry.registerTypeConverter (boolean [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Boolean> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newBooleanSortedSet ((boolean []) aSource);
      }
    });

    // byte[]
    aRegistry.registerTypeConverter (byte [].class, String.class, new ITypeConverter ()
    {
      public String convert (@Nonnull final Object aSource)
      {
        return Base64.encodeBytes ((byte []) aSource);
      }
    });
    aRegistry.registerTypeConverter (String.class, byte [].class, new ITypeConverter ()
    {
      public byte [] convert (@Nonnull final Object aSource)
      {
        return Base64.safeDecode ((String) aSource);
      }
    });
    aRegistry.registerTypeConverter (byte [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Byte> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newByteList ((byte []) aSource);
      }
    });
    aRegistry.registerTypeConverter (byte [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Byte> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newByteVector ((byte []) aSource);
      }
    });
    aRegistry.registerTypeConverter (byte [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Byte> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newByteSet ((byte []) aSource);
      }
    });
    aRegistry.registerTypeConverter (byte [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Byte> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newByteOrderedSet ((byte []) aSource);
      }
    });
    aRegistry.registerTypeConverter (byte [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Byte> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newByteSortedSet ((byte []) aSource);
      }
    });

    // char[]
    aRegistry.registerTypeConverter (char [].class, String.class, new ITypeConverter ()
    {
      public String convert (@Nonnull final Object aSource)
      {
        return new String ((char []) aSource);
      }
    });
    aRegistry.registerTypeConverter (String.class, char [].class, new ITypeConverter ()
    {
      public char [] convert (@Nonnull final Object aSource)
      {
        return ((String) aSource).toCharArray ();
      }
    });
    aRegistry.registerTypeConverter (char [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Character> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newCharList ((char []) aSource);
      }
    });
    aRegistry.registerTypeConverter (char [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Character> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newCharVector ((char []) aSource);
      }
    });
    aRegistry.registerTypeConverter (char [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Character> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newCharSet ((char []) aSource);
      }
    });
    aRegistry.registerTypeConverter (char [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Character> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newCharOrderedSet ((char []) aSource);
      }
    });
    aRegistry.registerTypeConverter (char [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Character> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newCharSortedSet ((char []) aSource);
      }
    });

    // double[]
    aRegistry.registerTypeConverter (double [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Double> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newDoubleList ((double []) aSource);
      }
    });
    aRegistry.registerTypeConverter (double [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Double> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newDoubleVector ((double []) aSource);
      }
    });
    aRegistry.registerTypeConverter (double [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Double> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newDoubleSet ((double []) aSource);
      }
    });
    aRegistry.registerTypeConverter (double [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Double> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newDoubleOrderedSet ((double []) aSource);
      }
    });
    aRegistry.registerTypeConverter (double [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Double> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newDoubleSortedSet ((double []) aSource);
      }
    });

    // float[]
    aRegistry.registerTypeConverter (float [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Float> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newFloatList ((float []) aSource);
      }
    });
    aRegistry.registerTypeConverter (float [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Float> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newFloatVector ((float []) aSource);
      }
    });
    aRegistry.registerTypeConverter (float [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Float> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newFloatSet ((float []) aSource);
      }
    });
    aRegistry.registerTypeConverter (float [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Float> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newFloatOrderedSet ((float []) aSource);
      }
    });
    aRegistry.registerTypeConverter (float [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Float> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newFloatSortedSet ((float []) aSource);
      }
    });

    // int[]
    aRegistry.registerTypeConverter (int [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Integer> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newIntList ((int []) aSource);
      }
    });
    aRegistry.registerTypeConverter (int [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Integer> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newIntVector ((int []) aSource);
      }
    });
    aRegistry.registerTypeConverter (int [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Integer> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newIntSet ((int []) aSource);
      }
    });
    aRegistry.registerTypeConverter (int [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Integer> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newIntOrderedSet ((int []) aSource);
      }
    });
    aRegistry.registerTypeConverter (int [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Integer> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newIntSortedSet ((int []) aSource);
      }
    });

    // long[]
    aRegistry.registerTypeConverter (long [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Long> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newLongList ((long []) aSource);
      }
    });
    aRegistry.registerTypeConverter (long [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Long> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newLongVector ((long []) aSource);
      }
    });
    aRegistry.registerTypeConverter (long [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Long> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newLongSet ((long []) aSource);
      }
    });
    aRegistry.registerTypeConverter (long [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Long> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newLongOrderedSet ((long []) aSource);
      }
    });
    aRegistry.registerTypeConverter (long [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Long> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newLongSortedSet ((long []) aSource);
      }
    });

    // short[]
    aRegistry.registerTypeConverter (short [].class, ArrayList.class, new ITypeConverter ()
    {
      public List <Short> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newShortList ((short []) aSource);
      }
    });
    aRegistry.registerTypeConverter (short [].class, Vector.class, new ITypeConverter ()
    {
      public Vector <Short> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newShortVector ((short []) aSource);
      }
    });
    aRegistry.registerTypeConverter (short [].class, HashSet.class, new ITypeConverter ()
    {
      public Set <Short> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newShortSet ((short []) aSource);
      }
    });
    aRegistry.registerTypeConverter (short [].class, LinkedHashSet.class, new ITypeConverter ()
    {
      public Set <Short> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newShortOrderedSet ((short []) aSource);
      }
    });
    aRegistry.registerTypeConverter (short [].class, TreeSet.class, new ITypeConverter ()
    {
      public Set <Short> convert (@Nonnull final Object aSource)
      {
        return CollectionHelper.newShortSortedSet ((short []) aSource);
      }
    });

    // To array
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (boolean [].class)
    {
      public boolean [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (byte [].class)
    {
      public byte [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (char [].class)
    {
      public char [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (double [].class)
    {
      public double [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (float [].class)
    {
      public float [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (int [].class)
    {
      public int [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (long [].class)
    {
      public long [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (short [].class)
    {
      public short [] convert (@Nonnull final Object aSource)
      {
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
      }
    });

    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (String [].class)
    {
      public String [] convert (@Nonnull final Object aSource)
      {
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
      }
    });
  }
}
