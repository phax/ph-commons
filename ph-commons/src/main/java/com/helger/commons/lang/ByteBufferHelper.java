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
package com.helger.commons.lang;

import java.nio.ByteBuffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Contains some {@link ByteBuffer} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ByteBufferHelper
{
  @PresentForCodeCoverage
  private static final ByteBufferHelper s_aInstance = new ByteBufferHelper ();

  private ByteBufferHelper ()
  {}

  @Nonnegative
  private static int _doTransfer (@Nonnull final ByteBuffer aSrcBuffer, @Nonnull final ByteBuffer aDstBuffer)
  {
    final int nSrcRemaining = aSrcBuffer.remaining ();
    final int nDstRemaining = aDstBuffer.remaining ();
    if (nDstRemaining >= nSrcRemaining)
    {
      // Dest buffer is large enough to hold complete source buffer content
      aDstBuffer.put (aSrcBuffer);
      return nSrcRemaining;
    }

    final int nSrcLimit = aSrcBuffer.limit ();
    final int nSrcPos = aSrcBuffer.position ();
    aSrcBuffer.limit (nSrcPos + nDstRemaining);
    aDstBuffer.put (aSrcBuffer);
    aSrcBuffer.limit (nSrcLimit);
    return nDstRemaining;
  }

  /**
   * Transfer as much as possible from source to dest buffer.
   *
   * @param aSrcBuffer
   *        Source buffer. May not be <code>null</code>.
   * @param aDstBuffer
   *        Destination buffer. May not be <code>null</code>.
   * @param bNeedsFlip
   *        whether or not to flip src
   * @return The amount of data transferred. Always &ge; 0.
   */
  @Nonnegative
  public static int transfer (@Nonnull final ByteBuffer aSrcBuffer,
                              @Nonnull final ByteBuffer aDstBuffer,
                              final boolean bNeedsFlip)
  {
    ValueEnforcer.notNull (aSrcBuffer, "SourceBuffer");
    ValueEnforcer.notNull (aDstBuffer, "DestinationBuffer");

    int nRead = 0;
    if (bNeedsFlip)
    {
      if (aSrcBuffer.position () > 0)
      {
        aSrcBuffer.flip ();
        nRead = _doTransfer (aSrcBuffer, aDstBuffer);
        if (aSrcBuffer.hasRemaining ())
          aSrcBuffer.compact ();
        else
          aSrcBuffer.clear ();
      }
    }
    else
    {
      if (aSrcBuffer.hasRemaining ())
        nRead = _doTransfer (aSrcBuffer, aDstBuffer);
    }
    return nRead;
  }
}
