package com.helger.commons.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.string.StringHelper;
import com.helger.commons.text.util.ABNF;

/**
 * Codec for RFC 2616 HTTP header values.
 *
 * @author Philip Helger
 * @since 9.3.6
 */
public class RFC2616Codec implements IByteArrayCodec
{
  private static final byte QUOTE_CHAR = '"';
  private static final byte ESCAPE_CHAR = '\\';

  // Non-token chars according to RFC 2616
  private static final BitSet NON_TOKEN_RFC2616 = new BitSet (256);

  static
  {
    for (int i = ABNF.CHECK_RANGE_MIN_INCL; i <= ABNF.CHECK_RANGE_MAX_INCL; ++i)
      if (ABNF.isCtl (i) || ABNF.isSP (i) || ABNF.isHTab (i))
        NON_TOKEN_RFC2616.set (i);
    NON_TOKEN_RFC2616.set ('(');
    NON_TOKEN_RFC2616.set (')');
    NON_TOKEN_RFC2616.set ('<');
    NON_TOKEN_RFC2616.set ('>');
    NON_TOKEN_RFC2616.set ('@');
    NON_TOKEN_RFC2616.set (',');
    NON_TOKEN_RFC2616.set (';');
    NON_TOKEN_RFC2616.set (':');
    NON_TOKEN_RFC2616.set ('\\');
    NON_TOKEN_RFC2616.set ('"');
    NON_TOKEN_RFC2616.set ('/');
    NON_TOKEN_RFC2616.set ('[');
    NON_TOKEN_RFC2616.set (']');
    NON_TOKEN_RFC2616.set ('?');
    NON_TOKEN_RFC2616.set ('=');
    NON_TOKEN_RFC2616.set ('{');
    NON_TOKEN_RFC2616.set ('}');
  }

  public static boolean isToken (@Nullable final String s)
  {
    // May not be empty
    if (StringHelper.hasNoText (s))
      return false;

    // No forbidden chars may be present
    for (final char c : s.toCharArray ())
      if (NON_TOKEN_RFC2616.get (c))
        return false;
    return true;
  }

  public RFC2616Codec ()
  {}

  public int getEncodedLength (@Nonnegative final int nDecodedLen)
  {
    // Worst case: each char needs quoting
    return 1 + 2 * nDecodedLen + 1;
  }

  public void encode (@Nullable final byte [] aDecodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    // Length 0 is okay, because it results in an empty string
    if (aDecodedBuffer == null)
      return;

    try
    {
      // Opening quote
      aOS.write (QUOTE_CHAR);

      for (int i = 0; i < nLen; ++i)
      {
        final int b = aDecodedBuffer[nOfs + i] & 0xff;
        if (b == ESCAPE_CHAR || b == QUOTE_CHAR)
          aOS.write (ESCAPE_CHAR);
        aOS.write (b);
      }

      // closing quote
      aOS.write (QUOTE_CHAR);
    }
    catch (final IOException ex)
    {
      throw new EncodeException ("Failed to encode RFC2616", ex);
    }
  }

  public void decode (@Nullable final byte [] aEncodedBuffer,
                      @Nonnegative final int nOfs,
                      @Nonnegative final int nLen,
                      @Nonnull @WillNotClose final OutputStream aOS)
  {
    if (aEncodedBuffer == null)
      return;
    // TODO

  }
}
