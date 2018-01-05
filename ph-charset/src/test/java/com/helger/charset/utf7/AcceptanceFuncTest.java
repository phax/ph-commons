/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.charset.utf7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.junit.Before;
import org.junit.Test;

public final class AcceptanceFuncTest
{
  private UTF7CharsetProviderSPI provider;
  private Charset charset;
  private CharsetDecoder decoder;
  private CharsetEncoder encoder;

  @Before
  public void setUp () throws Exception
  {
    provider = new UTF7CharsetProviderSPI ();
  }

  private void _init (final String init)
  {
    charset = provider.charsetForName (init);
    decoder = charset.newDecoder ();
    encoder = charset.newEncoder ();
  }

  @Test
  public void testUTF7 () throws Exception
  {
    _init ("UTF-7");
    assertEquals ("A+ImIDkQ.", _encodeGetBytes ("A\u2262\u0391."));
    assertEquals ("A+ImIDkQ.", _encodeCharsetEncode ("A\u2262\u0391."));
    assertEquals ("+ACEAIgAj-", _encodeGetBytes ("!\"#"));
    _verifyAll ();
  }

  @Test
  public void testUTF7o () throws Exception
  {
    _init ("X-UTF-7-OPTIONAL");
    assertEquals ("A+ImIDkQ.", _encodeGetBytes ("A\u2262\u0391."));
    assertEquals ("A+ImIDkQ.", _encodeCharsetEncode ("A\u2262\u0391."));
    assertEquals ("!\"#", _encodeGetBytes ("!\"#"));
    _verifyAll ();
  }

  @Test
  public void testModifiedUTF7 () throws Exception
  {
    _init ("x-IMAP4-MODIFIED-UTF7");
    assertEquals ("A&ImIDkQ-.", _encodeGetBytes ("A\u2262\u0391."));
    assertEquals ("A&ImIDkQ-.", _encodeCharsetEncode ("A\u2262\u0391."));
    _verifyAll ();
  }

  private void _verifyAll () throws Exception
  {
    _verifySymmetrical ("������������������������");
    _verifySymmetrical ("a�b�c�d�e�f�g�h�i�j�k�l�m�n�o�p�q�r�s�t�u�v�w�x�y�z");
    _verifySymmetrical ("abc���def���ghi���jkl���mno���pqr���stu���vwx���yz�");
    _verifySymmetrical ("abcdefghijklmnopqrstuvwyxz������������������������abcdefghijklmnopqrstuvwyxz");
    _verifySymmetrical ("a�b+�c�+-d�e-�f�-+g�h+�i�+-j�k-�l�-+m�n+�o�+-p�q-�r�-+s�t+�u�+-v�w�-x�y-+�z+");
    _verifySymmetrical ("�+��+���+���++�++��++���+++�+++��+++���+++���");
    _verifySymmetrical ("�+-��+-���+-���++-�++-��++-���+++-�+++-��+++-���+++-���");
    _verifySymmetrical ("++++++++");
    _verifySymmetrical ("+-++--+++---++");
    _verifySymmetrical ("+���+");
    _verifySymmetrical ("`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?\u0000\r\n\t\b\f�");
    _verifySymmetrical ("#a�a#�#��#���#");
  }

  private void _verifySymmetrical (final String s) throws Exception
  {
    final String encoded = _encodeGetBytes (s);
    assertEquals (encoded, _encodeCharsetEncode (s));
    assertEquals ("problem decoding " + encoded, s, _decode (encoded));
    for (int i = 4; i < encoded.length (); i++)
    {
      final ByteBuffer in = CharsetTestHelper.wrap (encoded);
      decoder.reset ();
      _verifyChunkedOutDecode (i, in, s);
    }
    for (int i = 10; i < encoded.length (); i++)
    {
      final CharBuffer in = CharBuffer.wrap (s);
      encoder.reset ();
      _verifyChunkedOutEncode (i, in, encoded);
    }
    for (int i = 10; i < encoded.length (); i++)
    {
      decoder.reset ();
      _verifyChunkedInDecode (i, encoded, s);
    }
    for (int i = 4; i < encoded.length (); i++)
    {
      encoder.reset ();
      _verifyChunkedInEncode (i, s, encoded);
    }
  }

  private String _encodeCharsetEncode (final String string) throws UnsupportedEncodingException
  {
    final String charsetEncode = CharsetTestHelper.asString (charset.encode (string));
    return charsetEncode;
  }

  /*
   * simulate what is done in String.getBytes (cannot be used directly since
   * Charset is not installed while testing)
   */
  private String _encodeGetBytes (final String string) throws CharacterCodingException, UnsupportedEncodingException
  {
    final ByteBuffer bb = ByteBuffer.allocate ((int) (encoder.maxBytesPerChar () * string.length ()));
    final CharBuffer cb = CharBuffer.wrap (string);
    encoder.reset ();
    CoderResult cr = encoder.encode (cb, bb, true);
    if (!cr.isUnderflow ())
      cr.throwException ();
    cr = encoder.flush (bb);
    if (!cr.isUnderflow ())
      cr.throwException ();
    bb.flip ();
    final String stringGetBytes = CharsetTestHelper.asString (bb);
    return stringGetBytes;
  }

  private String _decode (final String string) throws UnsupportedEncodingException
  {
    final ByteBuffer buffer = CharsetTestHelper.wrap (string);
    final CharBuffer decoded = charset.decode (buffer);
    return decoded.toString ();
  }

  private void _verifyChunkedInDecode (final int i,
                                       final String encoded,
                                       final String decoded) throws UnsupportedEncodingException
  {
    final ByteBuffer in = ByteBuffer.allocate (i);
    final CharBuffer out = CharBuffer.allocate (decoded.length () + 5);
    int pos = 0;
    CoderResult result = CoderResult.UNDERFLOW;
    while (pos < encoded.length ())
    {
      final int end = Math.min (encoded.length (), pos + i);
      in.put (CharsetTestHelper.wrap (encoded.substring (pos + in.position (), end)));
      in.flip ();
      result = decoder.decode (in, out, false);
      assertEquals ("at position: " + pos, CoderResult.UNDERFLOW, result);
      assertTrue ("no progress after " + pos + " of " + encoded.length (), in.position () > 0);
      pos += in.position ();
      in.compact ();
    }
    in.limit (0);
    result = decoder.decode (in, out, true);
    assertEquals (CoderResult.UNDERFLOW, result);
    result = decoder.flush (out);
    assertEquals (CoderResult.UNDERFLOW, result);
    assertEquals (encoded.length (), pos);
    assertEquals (decoded.length (), out.position ());
    out.flip ();
    assertEquals ("for length: " + i, decoded, out.toString ());
  }

  private void _verifyChunkedInEncode (final int i,
                                       final String decoded,
                                       final String encoded) throws UnsupportedEncodingException
  {
    final CharBuffer in = CharBuffer.allocate (i);
    final ByteBuffer out = ByteBuffer.allocate (encoded.length () + 40);
    int pos = 0;
    CoderResult result = CoderResult.UNDERFLOW;
    while (pos < decoded.length ())
    {
      final int end = Math.min (decoded.length (), pos + i);
      in.put (decoded.substring (pos + in.position (), end));
      in.flip ();
      assertTrue ("unexpected end at " + pos, in.limit () > 0);
      result = encoder.encode (in, out, false);
      if (result.isUnderflow ())
        assertTrue ("no progress after " + pos + " of " + decoded.length () + " in " + decoded, in.position () > 0);
      pos += in.position ();
      in.compact ();
    }
    pos += in.position ();
    in.limit (0);
    result = encoder.encode (in, out, true);
    result = encoder.flush (out);
    assertEquals (CoderResult.UNDERFLOW, result);
    out.flip ();
    assertEquals ("for length: " + i, encoded, CharsetTestHelper.asString (out));
  }

  private void _verifyChunkedOutEncode (final int i,
                                        final CharBuffer in,
                                        final String encoded) throws UnsupportedEncodingException
  {
    final ByteBuffer out = ByteBuffer.allocate (i);
    int encodeCount = 0;
    final StringBuffer sb = new StringBuffer ();
    CoderResult result;
    while (in.hasRemaining ())
    {
      result = encoder.encode (in, out, false);
      encodeCount += out.position ();
      if (in.hasRemaining ())
      {
        assertEquals ("at position: " + encodeCount, CoderResult.OVERFLOW, result);
        assertTrue ("at position: " + encodeCount, out.position () > 0);
      }
      CharsetTestHelper.outToSB (out, sb);
    }
    result = encoder.encode (in, out, true);
    assertFalse (!result.isOverflow () && in.hasRemaining ());
    CharsetTestHelper.outToSB (out, sb);
    result = encoder.flush (out);
    CharsetTestHelper.outToSB (out, sb);
    assertEquals (encoded, sb.toString ());
    in.rewind ();
  }

  private void _verifyChunkedOutDecode (final int i, final ByteBuffer in, final String decoded)
  {
    final CharBuffer out = CharBuffer.allocate (i);
    int decodeCount = 0;
    final StringBuffer sb = new StringBuffer ();
    CoderResult result = CoderResult.OVERFLOW;
    while (decodeCount < decoded.length ())
    {
      assertEquals ("at position: " + decodeCount, CoderResult.OVERFLOW, result);
      result = decoder.decode (in, out, true);
      assertTrue (out.position () > 0);
      decodeCount += out.position ();
      out.flip ();
      sb.append (out.toString ());
      out.clear ();
    }
    assertEquals (decoded, sb.toString ());
    in.rewind ();
  }
}
