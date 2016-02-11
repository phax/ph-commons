package com.helger.commons.codec;

import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

public interface IByteArrayEncoderToString extends IByteArrayStreamEncoder
{
  @Nonnull
  default Charset getEncodedCharset ()
  {
    return CCharset.DEFAULT_CHARSET_OBJ;
  }

  @Nullable
  default String getEncodedAsString (@Nullable final byte [] aDecodedBuf)
  {
    if (aDecodedBuf == null)
      return null;

    return getEncodedAsString (aDecodedBuf, 0, aDecodedBuf.length);
  }

  @Nullable
  default String getEncodedAsString (@Nullable final byte [] aDecodedBuf,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    if (aDecodedBuf == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getEncodedLength (nLen)))
    {
      encode (aDecodedBuf, nOfs, nLen, aBAOS);
      return aBAOS.getAsString (getEncodedCharset ());
    }
  }

  /**
   * Encode the passed string and return the result as a String.
   *
   * @param sDecoded
   *        The string to be encoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws EncodeException
   *         In case something goes wrong
   */
  @Nullable
  default public String getEncodedAsString (@Nullable final String sDecoded, @Nonnull final Charset aCharset)
  {
    if (sDecoded == null)
      return null;

    final byte [] aDecoded = CharsetManager.getAsBytes (sDecoded, aCharset);
    return getEncodedAsString (aDecoded, 0, aDecoded.length);
  }
}
