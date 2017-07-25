package com.helger.commons.traits;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.CGlobal;

public interface IGenericAdderTrait <DSTTYPE, IMPLTYPE extends IGenericAdderTrait <DSTTYPE, IMPLTYPE>> extends
                                    IGenericImplTrait <IMPLTYPE>
{
  @Nonnull
  IPrimitiveConverterTo <DSTTYPE> getPrimitiveConverterTo ();

  @Nonnull
  default IMPLTYPE addNative (final DSTTYPE aValue)
  {
    return addNativeAt (CGlobal.ILLEGAL_UINT, aValue);
  }

  @Nonnull
  IMPLTYPE addNativeAt (@CheckForSigned int nIndex, DSTTYPE aValue);

  @Nonnull
  default IMPLTYPE addNativeIf (@Nonnull final DSTTYPE aValue, @Nonnull final Predicate <? super DSTTYPE> aFilter)
  {
    if (aFilter.test (aValue))
      addNative (aValue);
    return thisAsT ();
  }

  @Nonnull
  default IMPLTYPE addNativeIfNotNull (@Nullable final DSTTYPE aValue)
  {
    if (aValue != null)
      addNative (aValue);
    return thisAsT ();
  }

  /**
   * Add using the converter
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   * @return thisAsT ()
   */
  @Nonnull
  default IMPLTYPE add (@Nullable final Object aValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (aValue));
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
    return addNative (getPrimitiveConverterTo ().convert (bValue));
  }

  @Nonnull
  default IMPLTYPE add (final byte nValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final char cValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (cValue));
  }

  @Nonnull
  default IMPLTYPE add (final double dValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (dValue));
  }

  @Nonnull
  default IMPLTYPE add (final float fValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (fValue));
  }

  @Nonnull
  default IMPLTYPE add (final int nValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final long nValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE add (final short nValue)
  {
    return addNative (getPrimitiveConverterTo ().convert (nValue));
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
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (aValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final boolean bValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (bValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final byte nValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final char cValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (cValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final double dValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (dValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final float fValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (fValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final int nValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final long nValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
  }

  @Nonnull
  default IMPLTYPE addAt (@Nonnegative final int nIndex, final short nValue)
  {
    return addNativeAt (nIndex, getPrimitiveConverterTo ().convert (nValue));
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
                                     @Nonnull final Function <? super T, ? extends DSTTYPE> aMapper)
  {
    if (aValues != null)
      for (final T aValue : aValues)
        addNative (aMapper.apply (aValue));
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
                                     @Nonnull final Function <? super T, ? extends DSTTYPE> aMapper)
  {
    if (aValues != null)
      for (final T aItem : aValues)
        addNative (aMapper.apply (aItem));
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
                                       @Nonnull final Function <? super T, ? extends DSTTYPE> aMapper)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final T aItem : aValues)
      {
        addNativeAt (nRealIndex, aMapper.apply (aItem));
        nRealIndex++;
      }
    }
    return thisAsT ();
  }

  @Nonnull
  default <T> IMPLTYPE addAllMappedAt (@Nonnegative final int nIndex,
                                       @Nullable final Iterable <? extends T> aValues,
                                       @Nonnull final Function <? super T, ? extends DSTTYPE> aMapper)
  {
    if (aValues != null)
    {
      int nRealIndex = nIndex;
      for (final T aItem : aValues)
      {
        addNativeAt (nRealIndex, aMapper.apply (aItem));
        nRealIndex++;
      }
    }
    return thisAsT ();
  }
}
