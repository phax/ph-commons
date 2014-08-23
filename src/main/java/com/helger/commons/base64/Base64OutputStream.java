package com.helger.commons.base64;

import java.io.IOException;

/**
 * A {@link Base64OutputStream} will write data to another
 * <tt>java.io.OutputStream</tt>, given in the constructor, and encode/decode
 * to/from Base64 notation on the fly.
 *
 * @see Base64
 * @since 1.3
 */
public class Base64OutputStream extends java.io.FilterOutputStream
{
  private final boolean m_bEncode;
  private int m_nPosition;
  private byte [] m_aBuffer;
  private final int m_nBufferLength;
  private int m_nLineLength;
  private final boolean m_bBreakLines;
  private final byte [] m_aB4; // Scratch used in a few places
  private boolean m_bSuspendEncoding;
  private final int m_nOptions; // Record for later
  private final byte [] m_aDecodabet; // Local copies to avoid extra method
                                      // calls

  /**
   * Constructs a {@link Base64OutputStream} in ENCODE mode.
   *
   * @param pout
   *        the <tt>java.io.OutputStream</tt> to which data will be written.
   * @since 1.3
   */
  public Base64OutputStream (final java.io.OutputStream pout)
  {
    this (pout, Base64.ENCODE);
  }

  /**
   * Constructs a {@link Base64OutputStream} in either ENCODE or DECODE mode.
   * <p>
   * Valid options:
   *
   * <pre>
   *   ENCODE or DECODE: Encode or Decode as data is read.
   *   DO_BREAK_LINES: don't break lines at 76 characters
   *     (only meaningful when encoding)</i>
   * </pre>
   * <p>
   * Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
   *
   * @param pout
   *        the <tt>java.io.OutputStream</tt> to which data will be written.
   * @param poptions
   *        Specified options.
   * @see Base64#ENCODE
   * @see Base64#DECODE
   * @see Base64#DO_BREAK_LINES
   * @since 1.3
   */
  public Base64OutputStream (final java.io.OutputStream pout, final int poptions)
  {
    super (pout);
    this.m_bBreakLines = (poptions & Base64.DO_BREAK_LINES) != 0;
    this.m_bEncode = (poptions & Base64.ENCODE) != 0;
    this.m_nBufferLength = m_bEncode ? 3 : 4;
    this.m_aBuffer = new byte [m_nBufferLength];
    this.m_nPosition = 0;
    this.m_nLineLength = 0;
    this.m_bSuspendEncoding = false;
    this.m_aB4 = new byte [4];
    this.m_nOptions = poptions;
    this.m_aDecodabet = Base64._getDecodabet (poptions);
  }

  /**
   * Writes the byte to the output stream after converting to/from Base64
   * notation. When encoding, bytes are buffered three at a time before the
   * output stream actually gets a write() call. When decoding, bytes are
   * buffered four at a time.
   *
   * @param theByte
   *        the byte to write
   * @since 1.3
   */
  @Override
  public void write (final int theByte) throws IOException
  {
    // Encoding suspended?
    if (m_bSuspendEncoding)
    {
      this.out.write (theByte);
      return;
    }

    // Encode?
    if (m_bEncode)
    {
      m_aBuffer[m_nPosition++] = (byte) theByte;
      if (m_nPosition >= m_nBufferLength)
      { // Enough to encode.

        this.out.write (Base64._encode3to4 (m_aB4, m_aBuffer, m_nBufferLength, m_nOptions));

        m_nLineLength += 4;
        if (m_bBreakLines && m_nLineLength >= Base64.MAX_LINE_LENGTH)
        {
          this.out.write (Base64.NEW_LINE);
          m_nLineLength = 0;
        }
        m_nPosition = 0;
      }
    }
    // Else, Decoding
    else
    {
      // Meaningful Base64 character?
      if (m_aDecodabet[theByte & 0x7f] > Base64.WHITE_SPACE_ENC)
      {
        m_aBuffer[m_nPosition++] = (byte) theByte;
        if (m_nPosition >= m_nBufferLength)
        { // Enough to output.

          final int len = Base64._decode4to3 (m_aBuffer, 0, m_aB4, 0, m_nOptions);
          out.write (m_aB4, 0, len);
          m_nPosition = 0;
        }
      }
      else
        if (m_aDecodabet[theByte & 0x7f] != Base64.WHITE_SPACE_ENC)
        {
          throw new IOException ("Invalid character in Base64 data.");
        }
    }
  }

  /**
   * Calls {@link #write(int)} repeatedly until <var>len</var> bytes are
   * written.
   *
   * @param theBytes
   *        array from which to read bytes
   * @param off
   *        offset for array
   * @param len
   *        max number of bytes to read into array
   * @since 1.3
   */
  @Override
  public void write (final byte [] theBytes, final int off, final int len) throws IOException
  {
    // Encoding suspended?
    if (m_bSuspendEncoding)
    {
      this.out.write (theBytes, off, len);
      return;
    }

    for (int i = 0; i < len; i++)
      write (theBytes[off + i]);
  }

  /**
   * Method added by PHIL. [Thanks, PHIL. -Rob] This pads the buffer without
   * closing the stream.
   *
   * @throws IOException
   *         if there's an error.
   */
  public void flushBase64 () throws IOException
  {
    if (m_nPosition > 0)
    {
      if (m_bEncode)
      {
        out.write (Base64._encode3to4 (m_aB4, m_aBuffer, m_nPosition, m_nOptions));
        m_nPosition = 0;
      }
      else
      {
        throw new IOException ("Base64 input not properly padded.");
      }
    }
  }

  /**
   * Flushes and closes (I think, in the superclass) the stream.
   *
   * @since 1.3
   */
  @Override
  public void close () throws IOException
  {
    // 1. Ensure that pending characters are written
    flushBase64 ();

    // 2. Actually close the stream
    // Base class both flushes and closes.
    if (out != null)
      super.close ();

    m_aBuffer = null;
    out = null;
  }

  /**
   * Suspends encoding of the stream. May be helpful if you need to embed a
   * piece of base64-encoded data in a stream.
   *
   * @throws IOException
   *         if there's an error flushing
   * @since 1.5.1
   */
  public void suspendEncoding () throws IOException
  {
    flushBase64 ();
    this.m_bSuspendEncoding = true;
  }

  /**
   * Resumes encoding of the stream. May be helpful if you need to embed a
   * piece of base64-encoded data in a stream.
   *
   * @since 1.5.1
   */
  public void resumeEncoding ()
  {
    this.m_bSuspendEncoding = false;
  }
}