/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.header;

import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;

/**
 * Represents a base class for all QValue'd stuff
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The key type for the map.
 */
public abstract class AbstractQValueList <KEYTYPE>
{
  // Maps something to quality
  private final ICommonsOrderedMap <KEYTYPE, QValue> m_aMap = new CommonsLinkedHashMap <> ();

  public AbstractQValueList ()
  {}

  @NonNull
  @ReturnsMutableCopy
  protected final ICommonsOrderedMap <KEYTYPE, QValue> qvalueMap ()
  {
    return m_aMap;
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <KEYTYPE, QValue> getAllQValues ()
  {
    return m_aMap.getClone ();
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <KEYTYPE, QValue> getAllQValuesLowerThan (final double dQuality)
  {
    final ICommonsOrderedMap <KEYTYPE, QValue> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <KEYTYPE, QValue> aEntry : m_aMap.entrySet ())
    {
      final QValue aQValue = aEntry.getValue ();
      if (aQValue.getQuality () < dQuality)
        ret.put (aEntry.getKey (), aQValue);
    }
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <KEYTYPE, QValue> getAllQValuesLowerOrEqual (final double dQuality)
  {
    final ICommonsOrderedMap <KEYTYPE, QValue> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <KEYTYPE, QValue> aEntry : m_aMap.entrySet ())
    {
      final QValue aQValue = aEntry.getValue ();
      if (aQValue.getQuality () <= dQuality)
        ret.put (aEntry.getKey (), aQValue);
    }
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <KEYTYPE, QValue> getAllQValuesGreaterThan (final double dQuality)
  {
    final ICommonsOrderedMap <KEYTYPE, QValue> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <KEYTYPE, QValue> aEntry : m_aMap.entrySet ())
    {
      final QValue aQValue = aEntry.getValue ();
      if (aQValue.getQuality () > dQuality)
        ret.put (aEntry.getKey (), aQValue);
    }
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public final ICommonsOrderedMap <KEYTYPE, QValue> getAllQValuesGreaterOrEqual (final double dQuality)
  {
    final ICommonsOrderedMap <KEYTYPE, QValue> ret = new CommonsLinkedHashMap <> ();
    for (final Map.Entry <KEYTYPE, QValue> aEntry : m_aMap.entrySet ())
    {
      final QValue aQValue = aEntry.getValue ();
      if (aQValue.getQuality () >= dQuality)
        ret.put (aEntry.getKey (), aQValue);
    }
    return ret;
  }

  /**
   * @return The string representation of this list e.g. for usage in HTTP
   *         headers.
   * @since 9.3.5
   */
  @NonNull
  public abstract String getAsHttpHeaderValue ();

  /**
   * Abstract implementation that requires a converter from the KEYTYPE generic
   * parameter to String.
   *
   * @param aKeyTransformer
   *        The transformer from KEYTYPE to String. May not be
   *        <code>null</code>.
   * @return The string representation of this list e.g. for usage in HTTP
   *         headers.
   * @since 9.3.5
   */
  @NonNull
  protected String getAsHttpHeaderValue (@NonNull final Function <KEYTYPE, String> aKeyTransformer)
  {
    return getAsHttpHeaderValue (m_aMap, aKeyTransformer);
  }

  /**
   * Static implementation of converting a map to String.
   *
   * @param aMap
   *        The map to be converted. May not be <code>null</code>.
   * @param aKeyTransformer
   *        The transformer from T to String. May not be <code>null</code>.
   * @return The string representation of this list e.g. for usage in HTTP
   *         headers.
   * @param <T>
   *        Key type
   * @since 9.3.5
   */
  @NonNull
  public static <T> String getAsHttpHeaderValue (@NonNull final ICommonsOrderedMap <T, QValue> aMap,
                                                 @NonNull final Function <T, String> aKeyTransformer)
  {
    ValueEnforcer.notNull (aMap, "Map");
    ValueEnforcer.notNull (aKeyTransformer, "KeyTransformer");

    final StringBuilder aSB = new StringBuilder ();
    for (final Map.Entry <T, QValue> aEntry : aMap.entrySet ())
    {
      if (aSB.length () > 0)
        aSB.append (", ");
      aSB.append (aKeyTransformer.apply (aEntry.getKey ())).append ("; q=").append (aEntry.getValue ().getQuality ());
    }
    return aSB.toString ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractQValueList <?> rhs = (AbstractQValueList <?>) o;
    return m_aMap.equals (rhs.m_aMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aMap).getToString ();
  }
}
