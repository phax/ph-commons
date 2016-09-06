package com.helger.commons.error;

import javax.annotation.Nullable;

import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.string.StringHelper;

/**
 * @deprecated Use {@link IErrorLocation} instead.
 * @author Philip Helger
 */
@Deprecated
public interface IResourceLocation extends IErrorLocation
{

  /**
   * @return The field where the error occurred. Sometimes this field is
   *         available instead of the line- and column numbers. May be
   *         <code>null</code>.
   */
  @Nullable
  String getField ();

  default boolean hasField ()
  {
    return StringHelper.hasText (getField ());
  }
}
