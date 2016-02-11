package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.random.VerySecureRandom;

/**
 * Test class for class {@link ICodec}
 *
 * @author Philip Helger
 */
public final class IByteArrayCodecTest
{
  private void _testCodec (@Nonnull final IByteArrayCodec c, @Nonnull final byte [] aSrcBytes)
  {
    // all
    {
      // Encode
      final byte [] aEncoded = c.getEncoded (aSrcBytes);
      assertNotNull (c.getClass ().getName () + "", aEncoded);

      // Decode
      final byte [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare with source
      assertArrayEquals (c.getClass ().getName (), aSrcBytes, aDecoded);
    }

    // Encode partial
    final int nBytes = aSrcBytes.length;
    if (nBytes >= 4)
    {
      // Encode
      final byte [] aEncoded = c.getEncoded (aSrcBytes, 1, nBytes - 2);
      assertNotNull (c.getClass ().getName () + "", aEncoded);

      // Decode all (of partial)
      final byte [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare all with source
      assertArrayEquals (c.getClass ().getName (), ArrayHelper.getCopy (aSrcBytes, 1, nBytes - 2), aDecoded);
    }
  }

  private void _testCodec (@Nonnull final IByteArrayCodec c)
  {
    _testCodec (c, new byte [0]);
    _testCodec (c, CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_ISO_8859_1_OBJ));
    _testCodec (c, CharsetManager.getAsBytes ("Hallo JÜnit", CCharset.CHARSET_UTF_8_OBJ));

    // Get random bytes
    final byte [] aRandomBytes = new byte [256];
    VerySecureRandom.getInstance ().nextBytes (aRandomBytes);
    _testCodec (c, aRandomBytes);

    for (int i = 0; i < 256; ++i)
    {
      final byte [] aBuf = new byte [i];

      // build ascending identity field
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) j;
      _testCodec (c, aBuf);

      // build constant field with all the same byte
      for (int j = 0; j < i; ++j)
        aBuf[j] = (byte) i;
      _testCodec (c, aBuf);
    }
  }

  @Test
  public void testArbitraryCodecs ()
  {
    _testCodec (new Base32Codec (true));
    _testCodec (new Base32Codec (false));
    _testCodec (new Base64Codec ());
    _testCodec (new FlateCodec ());
    _testCodec (new LZWCodec ());
    _testCodec (new QuotedPrintableCodec ());
    _testCodec (new URLCodec ());
  }
}
