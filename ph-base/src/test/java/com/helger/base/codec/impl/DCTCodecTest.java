/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.codec.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.helger.base.codec.DecodeException;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;

/**
 * Test class for class {@link DCTCodec}.
 *
 * @author Philip Helger
 */
public final class DCTCodecTest
{
  private static final int WIDTH = 4;
  private static final int HEIGHT = 3;

  private static byte [] _createImage (final String sFormat) throws IOException
  {
    final BufferedImage aImg = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < HEIGHT; ++y)
      for (int x = 0; x < WIDTH; ++x)
        aImg.setRGB (x, y, 0x123456);

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      if (!ImageIO.write (aImg, sFormat, aBAOS))
        throw new IOException ("Failed to write image of type " + sFormat);
      return aBAOS.toByteArray ();
    }
  }

  @Test
  public void testDecodeJPEG () throws IOException
  {
    final byte [] aEncoded = _createImage ("jpg");

    final DCTCodec aCodec = new DCTCodec ();
    final byte [] aDecoded = aCodec.getDecoded (aEncoded);
    assertNotNull (aDecoded);
    // 3 bytes per pixel
    assertEquals (WIDTH * HEIGHT * 3, aDecoded.length);

    // The static version delivers the same result
    assertEquals (aDecoded.length, DCTCodec.getDecodedDCT (aEncoded, 0, aEncoded.length).length);
  }

  @Test
  public void testDecodePNG () throws IOException
  {
    // A PNG is not a DCT encoded image, but ImageIO reads it as well
    final byte [] aEncoded = _createImage ("png");
    final byte [] aDecoded = new DCTCodec ().getDecoded (aEncoded);
    assertNotNull (aDecoded);
    assertEquals (WIDTH * HEIGHT * 3, aDecoded.length);
  }

  @Test
  public void testDecodeNull ()
  {
    assertNull (new DCTCodec ().getDecoded ((byte []) null));
    assertNull (DCTCodec.getDecodedDCT (null, 0, 0));
  }

  @Test
  public void testDecodeInvalid ()
  {
    final byte [] aNotAnImage = "This is not an image".getBytes (StandardCharsets.ISO_8859_1);
    try
    {
      new DCTCodec ().getDecoded (aNotAnImage);
      fail ();
    }
    catch (final DecodeException ex)
    {
      // expected
    }
  }
}
