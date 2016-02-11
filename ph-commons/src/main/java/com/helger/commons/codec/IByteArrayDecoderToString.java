package com.helger.commons.codec;

import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

public interface IByteArrayDecoderToString extends IByteArrayStreamDecoder
{
  @Nonnull
  default Charset getDecodedCharset ()
  {
    return CCharset.DEFAULT_CHARSET_OBJ;
  }

  @Nullable
  default String getDecodedAsString (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    return getDecodedAsString (aEncodedBuffer, 0, aEncodedBuffer.length);
  }

  @Nullable
  default String getDecodedAsString (@Nullable final byte [] aEncodedBuffer,
                                     @Nonnegative final int nOfs,
                                     @Nonnegative final int nLen)
  {
    if (aEncodedBuffer == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (getDecodedLength (nLen)))
    {
      decode (aEncodedBuffer, nOfs, nLen, aBAOS);
      return aBAOS.getAsString (getDecodedCharset ());
    }
  }

  /**
   * Decode the passed string.
   *
   * @param sEncoded
   *        The string to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to be used. May not be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code>.
   * @throws DecodeException
   *         in case something goes wrong
   */
  @Nullable
  default public String getDecodedAsString (@Nullable final String sEncoded, @Nonnull final Charset aCharset)
  {
    if (sEncoded == null)
      return null;

    final byte [] aEncoded = CharsetManager.getAsBytes (sEncoded, aCharset);
    return getDecodedAsString (aEncoded, 0, aEncoded.length);
  }
}
