/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.codec;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;

/**
 * Decoder for Discrete Cosinus Transformation (DCT)
 *
 * @author Philip Helger
 */
public class DCTCodec implements IByteArrayDecoder
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DCTCodec.class);

  public DCTCodec ()
  {}

  @Nullable
  @ReturnsMutableCopy
  public byte [] getDecoded (@Nullable final byte [] aEncodedBuffer)
  {
    return getDecodedDCT (aEncodedBuffer);
  }

  @Nullable
  @ReturnsMutableCopy
  public static byte [] getDecodedDCT (@Nullable final byte [] aEncodedBuffer)
  {
    if (aEncodedBuffer == null)
      return null;

    Image aImg;
    try
    {
      aImg = ImageIO.read (new NonBlockingByteArrayInputStream (aEncodedBuffer));
      if (aImg == null)
        throw new DecodeException ("Failed to read image");
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Read DCT encoded image with " + aEncodedBuffer.length + " bytes");
    }
    catch (final Throwable t)
    {
      throw new DecodeException ("Failed to read image", t);
    }

    final int nWidth = aImg.getWidth (null);
    final int nHeight = aImg.getHeight (null);
    final int [] aPixels = new int [nWidth * nHeight];
    final PixelGrabber aGrabber = new PixelGrabber (aImg, 0, 0, nWidth, nHeight, aPixels, 0, nWidth);
    try
    {
      if (!aGrabber.grabPixels ())
        throw new DecodeException ("Failed to grab pixels!");
    }
    catch (final InterruptedException ex)
    {
      throw new DecodeException (ex);
    }

    final byte [] ret = new byte [aPixels.length * 3];
    for (int i = 0; i < aPixels.length; ++i)
    {
      ret[i * 3] = (byte) ((aPixels[i] >> 24) & 0xff);
      ret[i * 3 + 1] = (byte) ((aPixels[i] >> 16) & 0xff);
      ret[i * 3 + 2] = (byte) ((aPixels[i] >> 8) & 0xff);
    }

    return ret;
  }
}
