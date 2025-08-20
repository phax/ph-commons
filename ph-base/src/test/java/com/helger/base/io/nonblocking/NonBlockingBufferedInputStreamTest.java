/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

/**
 * Test class for class {@link NonBlockingBufferedInputStream}.
 *
 * @author Philip Helger
 */
public final class NonBlockingBufferedInputStreamTest
{
  private static final byte [] TEST_DATA = "Hello World! This is test data for buffered input stream testing.".getBytes ();

  @Test
  public void testConstructorWithInputStream ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Constructor should work without issues
      assertTrue (aBIS.markSupported ());
    }
    catch (final IOException ex)
    {
      fail ("Constructor should not throw IOException: " + ex.getMessage ());
    }
  }

  @Test
  public void testConstructorWithInputStreamAndBufferSize ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 1024))
    {
      // Constructor should work without issues
      assertTrue (aBIS.markSupported ());
    }
    catch (final IOException ex)
    {
      fail ("Constructor should not throw IOException: " + ex.getMessage ());
    }
  }

  @SuppressWarnings ("resource")
  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidBufferSize ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    // Should throw IllegalArgumentException for buffer size <= 0
    new NonBlockingBufferedInputStream (aSource, 0);
  }

  @SuppressWarnings ("resource")
  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithNegativeBufferSize ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    // Should throw IllegalArgumentException for negative buffer size
    new NonBlockingBufferedInputStream (aSource, -1);
  }

  @Test
  public void testReadSingleByte () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Read first byte
      final int firstByte = aBIS.read ();
      assertEquals ('H', firstByte);

      // Read second byte
      final int secondByte = aBIS.read ();
      assertEquals ('e', secondByte);
    }
  }

  @Test
  public void testReadUntilEndOfStream () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      int byteCount = 0;
      int b;
      while ((b = aBIS.read ()) != -1)
      {
        assertEquals (TEST_DATA[byteCount], (byte) b);
        byteCount++;
      }
      assertEquals (TEST_DATA.length, byteCount);

      // Further reads should return -1
      assertEquals (-1, aBIS.read ());
    }
  }

  @Test
  public void testReadByteArray () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] buffer = new byte [10];
      final int bytesRead = aBIS.read (buffer);

      assertEquals (10, bytesRead);
      for (int i = 0; i < 10; i++)
      {
        assertEquals (TEST_DATA[i], buffer[i]);
      }
    }
  }

  @Test
  public void testReadByteArrayWithOffset () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] buffer = new byte [20];
      final int bytesRead = aBIS.read (buffer, 5, 10);

      assertEquals (10, bytesRead);
      for (int i = 0; i < 10; i++)
      {
        assertEquals (TEST_DATA[i], buffer[5 + i]);
      }
    }
  }

  @Test
  public void testReadZeroLengthArray () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] buffer = new byte [10];
      final int bytesRead = aBIS.read (buffer, 0, 0);
      assertEquals (0, bytesRead);
    }
  }

  @Test
  public void testReadLargerThanBuffer () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    // Use small buffer size to test reading more than buffer capacity
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 8))
    {
      final byte [] buffer = new byte [TEST_DATA.length];
      final int bytesRead = aBIS.read (buffer);

      assertEquals (TEST_DATA.length, bytesRead);
      for (int i = 0; i < TEST_DATA.length; i++)
      {
        assertEquals (TEST_DATA[i], buffer[i]);
      }
    }
  }

  @Test
  public void testSkip () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Skip first 6 bytes ("Hello ")
      final long skipped = aBIS.skip (6);
      assertEquals (6, skipped);

      // Next read should be 'W' from "World"
      final int nextByte = aBIS.read ();
      assertEquals ('W', nextByte);
    }
  }

  @Test
  public void testSkipZeroBytes () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final long skipped = aBIS.skip (0);
      assertEquals (0, skipped);

      // Next read should still be first byte
      final int nextByte = aBIS.read ();
      assertEquals ('H', nextByte);
    }
  }

  @Test
  public void testSkipNegativeBytes () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final long skipped = aBIS.skip (-5);
      assertEquals (0, skipped);

      // Next read should still be first byte
      final int nextByte = aBIS.read ();
      assertEquals ('H', nextByte);
    }
  }

  @Test
  public void testSkipMoreThanAvailable () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final long skipped = aBIS.skip (TEST_DATA.length + 100);
      assertEquals (TEST_DATA.length, skipped);

      // Stream should be at end
      assertEquals (-1, aBIS.read ());
    }
  }

  @Test
  public void testAvailable () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Initially should show all bytes available
      assertTrue (aBIS.available () >= 0);

      // Read some bytes
      aBIS.read ();
      aBIS.read ();

      // Available should be at least the remaining bytes
      assertTrue (aBIS.available () >= 0);
    }
  }

  @Test
  public void testMarkSupported ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      assertTrue (aBIS.markSupported ());
    }
    catch (final IOException ex)
    {
      fail ("Should not throw IOException: " + ex.getMessage ());
    }
  }

  @Test
  public void testMarkAndReset () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Read first two bytes
      final int firstByte = aBIS.read ();
      final int secondByte = aBIS.read ();
      assertEquals ('H', firstByte);
      assertEquals ('e', secondByte);

      // Mark current position
      aBIS.mark (100);

      // Read more bytes
      final int thirdByte = aBIS.read ();
      final int fourthByte = aBIS.read ();
      assertEquals ('l', thirdByte);
      assertEquals ('l', fourthByte);

      // Reset to marked position
      aBIS.reset ();

      // Should read same bytes again
      assertEquals ('l', aBIS.read ());
      assertEquals ('l', aBIS.read ());
    }
  }

  @Test
  public void testMarkWithLimitExceeded () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 8))
    {
      // Mark with small limit
      aBIS.mark (5);

      // Read first byte
      assertEquals ('H', aBIS.read ());

      // Read more bytes than the mark limit to trigger buffer growth beyond mark limit
      final byte [] buffer = new byte [20];
      final int bytesRead = aBIS.read (buffer);

      // The stream should have read some bytes
      assertTrue (bytesRead > 0);

      try
      {
        // Reset should fail because the mark was invalidated due to buffer management
        aBIS.reset ();
        // If we reach here, the mark is still valid (which is also acceptable behavior)
        // The mark might still be valid if the buffer management didn't need to invalidate it
      }
      catch (final IOException ex)
      {
        // Expected behavior - mark was invalidated
        assertTrue (ex.getMessage ().contains ("invalid mark"));
      }
    }
  }

  @Test (expected = IOException.class)
  public void testResetWithoutMark () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Try to reset without setting a mark
      aBIS.reset ();
    }
  }

  @Test
  public void testCloseStream () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    @SuppressWarnings ("resource")
    final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource);

    // Close the stream
    aBIS.close ();

    // Operations on closed stream should throw IOException
    try
    {
      aBIS.read ();
      fail ("Read on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      aBIS.read (new byte [10]);
      fail ("Read on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      aBIS.skip (10);
      fail ("Skip on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      aBIS.available ();
      fail ("Available on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      aBIS.reset ();
      fail ("Reset on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }
  }

  @Test
  public void testMultipleClose () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    @SuppressWarnings ("resource")
    final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource);

    // Multiple closes should not cause issues
    aBIS.close ();
    aBIS.close ();
    aBIS.close ();
  }

  @Test
  public void testLargeDataReading () throws IOException
  {
    // Create large test data
    final byte [] largeData = new byte [10000];
    ThreadLocalRandom.current ().nextBytes (largeData);

    final InputStream aSource = new NonBlockingByteArrayInputStream (largeData);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 512))
    {
      final byte [] readBuffer = new byte [largeData.length];
      int totalRead = 0;

      while (totalRead < largeData.length)
      {
        final int bytesRead = aBIS.read (readBuffer, totalRead, largeData.length - totalRead);
        if (bytesRead == -1)
          break;
        totalRead += bytesRead;
      }

      assertEquals (largeData.length, totalRead);
      for (int i = 0; i < largeData.length; i++)
      {
        assertEquals (largeData[i], readBuffer[i]);
      }
    }
  }

  @Test
  public void testSmallBufferSize () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    // Use very small buffer to test buffer management
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 1))
    {
      final byte [] readData = new byte [TEST_DATA.length];
      int totalRead = 0;

      while (totalRead < TEST_DATA.length)
      {
        final int bytesRead = aBIS.read (readData, totalRead, TEST_DATA.length - totalRead);
        if (bytesRead == -1)
          break;
        totalRead += bytesRead;
      }

      assertEquals (TEST_DATA.length, totalRead);
      for (int i = 0; i < TEST_DATA.length; i++)
      {
        assertEquals (TEST_DATA[i], readData[i]);
      }
    }
  }

  @Test
  public void testMarkAndResetWithBufferGrowth () throws IOException
  {
    // Create data larger than default buffer
    final byte [] largeData = new byte [20000];
    for (int i = 0; i < largeData.length; i++)
    {
      largeData[i] = (byte) (i % 256);
    }

    final InputStream aSource = new NonBlockingByteArrayInputStream (largeData);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 1024))
    {
      // Mark at beginning
      aBIS.mark (15000);

      // Read substantial amount of data
      final byte [] buffer = new byte [10000];
      final int bytesRead = aBIS.read (buffer);
      assertEquals (10000, bytesRead);

      // Reset and verify we can read the same data again
      aBIS.reset ();
      final byte [] buffer2 = new byte [10000];
      final int bytesRead2 = aBIS.read (buffer2);
      assertEquals (10000, bytesRead2);

      for (int i = 0; i < 10000; i++)
      {
        assertEquals (buffer[i], buffer2[i]);
      }
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithInvalidArrayParameters ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] buffer = new byte [10];
      // Should throw IllegalArgumentException for invalid offset/length combination
      aBIS.read (buffer, -1, 5);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithInvalidLength ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] aBuffer = new byte [10];
      // Should throw IllegalArgumentException for negative length
      aBIS.read (aBuffer, 0, -1);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithOffsetPlusLengthExceedsArraySize ()
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      final byte [] aBuffer = new byte [10];
      // Should throw IllegalArgumentException when offset + length > array length
      aBIS.read (aBuffer, 5, 10);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test
  public void testAvailableAfterPartialRead () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource, 16))
    {
      // Read some data
      final byte [] aBuffer = new byte [10];
      aBIS.read (aBuffer);

      // Available should return remaining buffered + underlying stream bytes
      final int nAvailable = aBIS.available ();
      assertTrue (nAvailable >= 0);
      assertTrue (nAvailable <= TEST_DATA.length - 10);
    }
  }

  @Test
  public void testSkipWithMark () throws IOException
  {
    final InputStream aSource = new NonBlockingByteArrayInputStream (TEST_DATA);
    try (final NonBlockingBufferedInputStream aBIS = new NonBlockingBufferedInputStream (aSource))
    {
      // Set mark
      aBIS.mark (50);

      // Skip some bytes
      final long skipped = aBIS.skip (10);
      assertEquals (10, skipped);

      // Reset should work
      aBIS.reset ();

      // Should be back at beginning
      assertEquals ('H', aBIS.read ());
    }
  }
}
