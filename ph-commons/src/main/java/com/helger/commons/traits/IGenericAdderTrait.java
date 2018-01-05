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
package com.helger.commons.traits;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.CGlobal;

/**
 * Add arbitrary objects to this
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The element type to be added. Must implement Serializable as a hack,
 *        so that the APIs <code>add(Object)</code> and
 *        <code>add(ELEMENTTYPE)</code> can co-exist. Otherwise there would be a
 *        problem with type erasure.
 * @param <IMPLTYPE>
 *        The implementation type for chaining API
 */
public interface IGenericAdderTrait <ELEMENTTYPE extends Serializable, IMPLTYPE extends IGenericAdderTrait <ELEMENTTYPE, IMPLTYPE>>
                                    extends
                                    IHasPrimitiveConverter <ELEMENTTYPE>,
                                    IGenericImplTrait <IMPLTYPE>
{
  @Nonnull
  default IMPLTYPE add (final ELEMENTTYPE aValue)
  {
    return addAt (CGlobal.ILLEGAL_UINT, aValue);
  }

  @Nonnull
  IMPLTYPE addAt (@CheckForSigned int nIndex, ELEMENTTYPE aValue);

  @Nonnull
  default IMPLTYPE addIf (@Nonnull final ELEMENTTYPE aValue, @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter.test (aValue))
      add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addIfNotNull (@Nullable final ELEMENTTYPE aValue)
  {
    if (aValue != null)
      add (aValue);
    return thisAsT ();
  }

  /**
   * Add using the converter
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  default IMPLTYPE add (@Nullable final Object aValue)
  {
    return add (getPrimitiveConverterTo ().convert (aValue));
  }

  @Nonnull
  default <T> IMPLTYPE addIf (@Nullable final T aValue, @Nonnull final Predicate <? super T> aFilter)
  {
    if (aFilter.test (aValue))
      add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE add (final boolean bValue)
  {
    return add (getPrimitiveConverterTo ().convert (bValue));
  }

  @Nonnull
  default IMPLTYPE add (final byte nValue)
  {
    return add (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final char cValue)
  {
    return add (getPrimitiveConverterTo ().convert (cValue));
  }

  @Nonnull
  default IMPLTYPE add (final double dValue)
  {
    return add (getPrimitiveConverterTo ().convert (dValue));
  }

  @Nonnull
  default IMPLTYPE add (final float fValue)
  {
    return add (getPrimitiveConverterTo ().convert (fValue));
  }

  @Nonnull
  default IMPLTYPE add (final int nValue)
  {
    return add (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final long nValue)
  {
    return add (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final short nValue)
  {
    return add (getPrimitiveConverterTo ().convert (nValue));
  }

  /**
   * Add at the specified index using the converter
   *
   * @param nIndex
   *        The index where the item should be added. Must be &ge; 0.
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return thisAsT ()
   */
  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, @Nullable final Object aValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (aValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final boolean bValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (bValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final byte nValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final char cValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (cValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final double dValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (dValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final float fValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (fValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final int nValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final long nValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final short nValue)
  {
    return addAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final boolean... aValues)
  {
    if (aValues != null)
      for (final boolean aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final byte... aValues)
  {
    if (aValues != null)
      for (final byte aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final char... aValues)
  {
    if (aValues != null)
      for (final char aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final double... aValues)
  {
    if (aValues != null)
      for (final double aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final float... aValues)
  {
    if (aValues != null)
      for (final float aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final int... aValues)
  {
    if (aValues != null)
      for (final int aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final long... aValues)
  {
    if (aValues != null)
      for (final long aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final short... aValues)
  {
    if (aValues != null)
      for (final short aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Object... aValues)
  {
    if (aValues != null)
      for (final Object aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addAllMapped (@Nullable final T [] aValues,
                                     @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    if (aValues != null)
      for (final T aValue : aValues)
        add (aMapper.apply (aValue));
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAll (@Nullable final Iterable <?> aValues)
  {
    if (aValues != null)
      for (final Object aValue : aValues)
        add (aValue);
    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addAllMapped (@Nullable final Iterable <? extends T> aValues,
                                     @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    if (aValues != null)
      for (final T aItem : aValues)
        add (aMapper.apply (aItem));
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final boolean... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final boolean aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final byte... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final byte aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final char... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final char aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final double... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final double aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final float... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final float aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final int... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final int aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final long... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final long aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final short... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final short aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final Object... aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final Object aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addAllAt (@Nonnegative final int nIndex, @Nullable final Iterable <?> aValues)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final Object aValue : aValues)
      {
        addAt (nRealIndex, aValue);
        nRealIndex++;
      }
    }

    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addAllMappedAt (@Nonnegative final int nIndex,
                                       @Nullable final T [] aValues,
                                       @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final T aItem : aValues)
      {
        addAt (nRealIndex, aMapper.apply (aItem));
        nRealIndex++;
      }
    }
    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addAllMappedAt (@Nonnegative final int nIndex,
                                       @Nullable final Iterable <? extends T> aValues,
                                       @Nonnull final Function <? super T, ? extends ELEMENTTYPE> aMapper)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final T aItem : aValues)
      {
        addAt (nRealIndex, aMapper.apply (aItem));
        nRealIndex++;
      }
    }
    return thisAsT ();
  }
}
