package com.helger.collection.enumeration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.collection.CollectionHelper;
import com.helger.collection.base.EmptyEnumeration;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class EnumerationHelper
{
  private EnumerationHelper ()
  {}

  public static boolean isEmpty (@Nullable final Enumeration <?> aEnum)
  {
    return aEnum == null || !aEnum.hasMoreElements ();
  }

  public static boolean isNotEmpty (@Nullable final Enumeration <?> aEnum)
  {
    return aEnum != null && aEnum.hasMoreElements ();
  }

  /**
   * Retrieve the size of the passed {@link Enumeration}.
   *
   * @param aEnumeration
   *        Enumeration to check. May be <code>null</code>.
   * @return The number objects or 0 if the passed parameter is <code>null</code>.
   */
  @Nonnegative
  public static int getSize (@Nullable final Enumeration <?> aEnumeration)
  {
    int ret = 0;
    if (aEnumeration != null)
      while (aEnumeration.hasMoreElements ())
      {
        aEnumeration.nextElement ();
        ++ret;
      }
    return ret;
  }

  /**
   * Get an {@link Enumeration} object based on the passed array.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aArray
   *        The array to enumerate.
   * @return an Enumeration object
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null || aArray.length == 0 ? new EmptyEnumeration <> () : getEnumeration (Arrays.asList (aArray));
  }

  /**
   * Get an {@link Enumeration} object based on a {@link Collection} object.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aCont
   *        The container to enumerate.
   * @return an Enumeration object
   */
  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final Iterable <ELEMENTTYPE> aCont)
  {
    return CollectionHelper.isEmpty (aCont) ? new EmptyEnumeration <> () : getEnumeration (aCont.iterator ());
  }

  /**
   * Get an Enumeration object based on an Iterator object.
   *
   * @param <ELEMENTTYPE>
   *        the type of the elements in the container
   * @param aIter
   *        iterator object to use
   * @return an Enumeration object
   */
  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> getEnumeration (@Nullable final Iterator <ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      return new EmptyEnumeration <> ();
    return new EnumerationFromIterator <> (aIter);
  }

  /**
   * Get an Enumeration object based on a Map object.
   *
   * @param <KEYTYPE>
   *        map key type
   * @param <VALUETYPE>
   *        map value type
   * @param aMap
   *        map object to use
   * @return an Enumeration object
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> Enumeration <Map.Entry <KEYTYPE, VALUETYPE>> getEnumeration (@Nullable final Map <KEYTYPE, VALUETYPE> aMap)
  {
    if (aMap == null)
      return new EmptyEnumeration <> ();
    return getEnumeration (aMap.entrySet ());
  }
}
