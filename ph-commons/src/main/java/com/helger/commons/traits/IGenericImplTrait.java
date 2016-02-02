package com.helger.commons.traits;

import javax.annotation.Nonnull;

import com.helger.commons.lang.GenericReflection;

/**
 * A trait to convert this to a generic implementation.
 * 
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The real implementation type.
 */
public interface IGenericImplTrait <IMPLTYPE extends IGenericImplTrait <IMPLTYPE>>
{
  @Nonnull
  default IMPLTYPE thisAsT ()
  {
    return GenericReflection.uncheckedCast (this);
  }
}
