package com.helger.commons.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.random.VerySecureRandom;

/**
 * Test class for class {@link ICodec}
 *
 * @author Philip Helger
 */
public final class ICodecTest
{

  private void _testCodec (final IByteArrayCodec c)
  {
    final int nBytes = 256;

    // Get random bytes
    final byte [] aSrcBytes = new byte [nBytes];
    VerySecureRandom.getInstance ().nextBytes (aSrcBytes);

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
    {
      // Encode
      final byte [] aEncoded = c.getEncoded (aSrcBytes, 5, nBytes - 20);
      assertNotNull (c.getClass ().getName () + "", aEncoded);

      // Decode all (of partial)
      final byte [] aDecoded = c.getDecoded (aEncoded);
      assertNotNull (c.getClass ().getName (), aDecoded);

      // Compare all with source
      assertArrayEquals (c.getClass ().getName (), ArrayHelper.getCopy (aSrcBytes, 5, nBytes - 20), aDecoded);
    }
  }

  @Test
  public void testArbitraryCodecs ()
  {
    _testCodec (new RFC1522BCodec ());
    _testCodec (new RFC1522QCodec ());
    _testCodec (new Base32Codec ());
    _testCodec (new Base64Codec ());
    _testCodec (new FlateCodec ());
    _testCodec (new LZWCodec ());
    _testCodec (new QuotedPrintableCodec ());
    _testCodec (new URLCodec ());
  }
}
