package com.helger.commons.codec;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.string.StringHelper;

/**
 * Base16 encoder and decoder.
 *
 * @author Philip Helger
 */
public class Base16Codec implements IByteArrayCodec, IByteArrayStreamEncoder, IByteArrayStreamDecoder
{
  /**
   * Creates a Base16 codec used for decoding and encoding.
   */
  public Base16Codec ()
  {}

  public int getEncodedLength (final int nLen)
  {
    return nLen * 2;
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aDecodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; ++i)
      {
        final byte b = aDecodedBuffer[nOfs + i];
        aOS.write (StringHelper.getHexChar ((b & 0xf0) >> 4));
        aOS.write (StringHelper.getHexChar (b & 0x0f));
      }
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode Base16", ex);
    }
  }

  public int getDecodedLength (final int nLen)
  {
    return nLen / 2;
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null || nLen == 0)
      return;

    try
    {
      for (int i = 0; i < nLen; ++i)
      {
        if (i >= nLen - 1)
          throw new DecodeException ("Invalid Base16 encoding. Premature end of input");
        final char cHigh = (char) aEncodedBuffer[nOfs + i];
        final char cLow = (char) aEncodedBuffer[nOfs + i + 1];
        i++;
        final int nDecodedValue = StringHelper.getHexByte (cHigh, cLow);
        if (nDecodedValue < 0)
          throw new DecodeException ("Invalid Base16 encoding for " + (int) cHigh + " and " + (int) cLow);

        aOS.write (nDecodedValue);
      }
    }
    catch (final IOException ex)
    {
      throw new DecodeException ("Failed to decode Base16", ex);
    }
  }
}
