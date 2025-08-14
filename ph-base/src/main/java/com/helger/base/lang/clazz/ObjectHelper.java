package com.helger.base.lang.clazz;

import com.helger.annotation.Nonempty;
import com.helger.base.string.StringHex;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public final class ObjectHelper
{
  private ObjectHelper ()
  {}

  /**
   * Get the hex representation of the passed object's address. Note that this method makes no
   * differentiation between 32 and 64 bit architectures. The result is always a hexadecimal value
   * preceded by "0x" and followed by exactly 8 characters.
   *
   * @param aObject
   *        The object who's address is to be retrieved. May be <code>null</code>.
   * @return Depending on the current architecture. Always starting with "0x" and than containing
   *         the address.
   * @see System#identityHashCode(Object)
   */
  @Nonnull
  @Nonempty
  public static String getObjectAddress (@Nullable final Object aObject)
  {
    if (aObject == null)
      return "0x00000000";
    return "0x" + StringHex.getHexStringLeadingZero (System.identityHashCode (aObject), 8);
  }

}
