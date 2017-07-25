package com.helger.commons.traits;

import javax.annotation.Nonnull;

/**
 * An object having or requiring a converter from primitive to something else.
 *
 * @author Philip Helger
 * @param <DSTTYPE>
 *        The element type to be converted to.
 */
public interface IHasPrimitiveConverter <DSTTYPE>
{
  @Nonnull
  IPrimitiveConverterTo <DSTTYPE> getPrimitiveConverterTo ();
}
