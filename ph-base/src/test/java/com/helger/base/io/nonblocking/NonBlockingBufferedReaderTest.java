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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * Test class for class {@link NonBlockingBufferedReader}.
 *
 * @author Philip Helger
 */
public final class NonBlockingBufferedReaderTest
{
  private static final String TEST_TEXT = "Hello World!\nThis is line 2\r\nLine 3 with CRLF\rLine 4 with CR only\nFinal line";
  private static final String SIMPLE_TEXT = "abcdefghijklmnopqrstuvwxyz";

  @Test
  public void testConstructorWithReader ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Constructor should work without issues
      assertTrue (reader.markSupported ());
    }
    catch (final IOException ex)
    {
      fail ("Constructor should not throw IOException: " + ex.getMessage ());
    }
  }

  @Test
  public void testConstructorWithReaderAndBufferSize ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 1024))
    {
      // Constructor should work without issues
      assertTrue (reader.markSupported ());
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
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    // Should throw IllegalArgumentException for buffer size <= 0
    new NonBlockingBufferedReader (source, 0);
  }

  @SuppressWarnings ("resource")
  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithNegativeBufferSize ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    // Should throw IllegalArgumentException for negative buffer size
    new NonBlockingBufferedReader (source, -1);
  }

  @Test
  public void testReadSingleCharacter () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Read first character
      final int firstChar = reader.read ();
      assertEquals ('a', firstChar);

      // Read second character
      final int secondChar = reader.read ();
      assertEquals ('b', secondChar);
    }
  }

  @Test
  public void testReadUntilEndOfStream () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      int charCount = 0;
      int c;
      while ((c = reader.read ()) != -1)
      {
        assertEquals (SIMPLE_TEXT.charAt (charCount), (char) c);
        charCount++;
      }
      assertEquals (SIMPLE_TEXT.length (), charCount);

      // Further reads should return -1
      assertEquals (-1, reader.read ());
    }
  }

  @Test
  public void testReadCharacterArray () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [10];
      final int charsRead = reader.read (buffer);

      assertEquals (10, charsRead);
      for (int i = 0; i < 10; i++)
      {
        assertEquals (SIMPLE_TEXT.charAt (i), buffer[i]);
      }
    }
  }

  @Test
  public void testReadCharacterArrayWithOffset () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [20];
      final int charsRead = reader.read (buffer, 5, 10);

      assertEquals (10, charsRead);
      for (int i = 0; i < 10; i++)
      {
        assertEquals (SIMPLE_TEXT.charAt (i), buffer[5 + i]);
      }
    }
  }

  @Test
  public void testReadZeroLengthArray () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [10];
      final int charsRead = reader.read (buffer, 0, 0);
      assertEquals (0, charsRead);
    }
  }

  @Test
  public void testReadLargerThanBuffer () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    // Use small buffer size to test reading more than buffer capacity
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 8))
    {
      final char [] buffer = new char [SIMPLE_TEXT.length ()];
      final int charsRead = reader.read (buffer);

      assertEquals (SIMPLE_TEXT.length (), charsRead);
      for (int i = 0; i < SIMPLE_TEXT.length (); i++)
      {
        assertEquals (SIMPLE_TEXT.charAt (i), buffer[i]);
      }
    }
  }

  @Test
  public void testReadLine () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      assertEquals ("Hello World!", reader.readLine ());
      assertEquals ("This is line 2", reader.readLine ());
      assertEquals ("Line 3 with CRLF", reader.readLine ());
      assertEquals ("Line 4 with CR only", reader.readLine ());
      assertEquals ("Final line", reader.readLine ());
      assertNull (reader.readLine ());
    }
  }

  @Test
  public void testReadLineWithDifferentLineEndings () throws IOException
  {
    final String testData = "Line1\nLine2\r\nLine3\rLine4";
    final NonBlockingStringReader source = new NonBlockingStringReader (testData);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      assertEquals ("Line1", reader.readLine ());
      assertEquals ("Line2", reader.readLine ());
      assertEquals ("Line3", reader.readLine ());
      assertEquals ("Line4", reader.readLine ());
      assertNull (reader.readLine ());
    }
  }

  @Test
  public void testReadLineEmptyLines () throws IOException
  {
    final String testData = "Line1\n\nLine3\r\n\r\nLine6";
    final NonBlockingStringReader source = new NonBlockingStringReader (testData);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      assertEquals ("Line1", reader.readLine ());
      assertEquals ("", reader.readLine ());
      assertEquals ("Line3", reader.readLine ());
      assertEquals ("", reader.readLine ());
      assertEquals ("Line6", reader.readLine ());
      assertNull (reader.readLine ());
    }
  }

  @Test
  public void testReadLineOnlyLineEndings () throws IOException
  {
    final String testData = "\n\r\n\r";
    final NonBlockingStringReader source = new NonBlockingStringReader (testData);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      assertEquals ("", reader.readLine ());
      assertEquals ("", reader.readLine ());
      assertEquals ("", reader.readLine ());
      assertNull (reader.readLine ());
    }
  }

  @Test
  public void testSkip () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Skip first 6 characters ("abcdef")
      final long skipped = reader.skip (6);
      assertEquals (6, skipped);

      // Next read should be 'g'
      final int nextChar = reader.read ();
      assertEquals ('g', nextChar);
    }
  }

  @Test
  public void testSkipZeroCharacters () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final long skipped = reader.skip (0);
      assertEquals (0, skipped);

      // Next read should still be first character
      final int nextChar = reader.read ();
      assertEquals ('a', nextChar);
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testSkipNegativeCharacters () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      reader.skip (-5);
    }
  }

  @Test
  public void testSkipMoreThanAvailable () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final long skipped = reader.skip (SIMPLE_TEXT.length () + 100);
      assertEquals (SIMPLE_TEXT.length (), skipped);

      // Stream should be at end
      assertEquals (-1, reader.read ());
    }
  }

  @Test
  public void testReady () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Should be ready initially
      assertTrue (reader.ready ());

      // Read some characters
      reader.read ();
      reader.read ();

      // Should still be ready
      assertTrue (reader.ready ());
    }
  }

  @Test
  public void testMarkSupported ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      assertTrue (reader.markSupported ());
    }
    catch (final IOException ex)
    {
      fail ("Should not throw IOException: " + ex.getMessage ());
    }
  }

  @Test
  public void testMarkAndReset () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Read first two characters
      final int firstChar = reader.read ();
      final int secondChar = reader.read ();
      assertEquals ('a', firstChar);
      assertEquals ('b', secondChar);

      // Mark current position
      reader.mark (100);

      // Read more characters
      final int thirdChar = reader.read ();
      final int fourthChar = reader.read ();
      assertEquals ('c', thirdChar);
      assertEquals ('d', fourthChar);

      // Reset to marked position
      reader.reset ();

      // Should read same characters again
      assertEquals ('c', reader.read ());
      assertEquals ('d', reader.read ());
    }
  }

  @Test
  public void testMarkWithZeroReadAheadLimit () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Mark with zero read ahead limit
      reader.mark (0);

      // Should be able to reset immediately
      reader.reset ();

      // Should read first character
      assertEquals ('a', reader.read ());
    }
  }

  @Test
  public void testMarkWithLimitExceeded () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 8))
    {
      // Mark with small limit
      reader.mark (5);

      // Read first character
      assertEquals ('a', reader.read ());

      // Read more characters than the mark limit
      final char [] buffer = new char [10];
      final int charsRead = reader.read (buffer);
      assertEquals (10, charsRead);

      try
      {
        // Reset should fail because mark limit was exceeded
        reader.reset ();
        fail ("Reset should have failed due to exceeded mark limit");
      }
      catch (final IOException ex)
      {
        // Expected behavior
        assertTrue (ex.getMessage ().contains ("invalid") || ex.getMessage ().contains ("Mark invalid"));
      }
    }
  }

  @Test (expected = IOException.class)
  public void testResetWithoutMark () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Try to reset without setting a mark
      reader.reset ();
    }
  }

  @Test
  public void testCloseStream () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    @SuppressWarnings ("resource")
    final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source);

    // Close the stream
    reader.close ();

    // Operations on closed stream should throw IOException
    try
    {
      reader.read ();
      fail ("Read on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.read (new char [10]);
      fail ("Read on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.readLine ();
      fail ("ReadLine on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.skip (10);
      fail ("Skip on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.ready ();
      fail ("Ready on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.mark (10);
      fail ("Mark on closed stream should throw IOException");
    }
    catch (final IOException ex)
    {
      assertTrue (ex.getMessage ().contains ("closed"));
    }

    try
    {
      reader.reset ();
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
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    @SuppressWarnings ("resource")
    final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source);

    // Multiple closes should not cause issues
    reader.close ();
    reader.close ();
    reader.close ();
  }

  @Test
  public void testLargeDataReading () throws IOException
  {
    // Create large test data
    final StringBuilder largeData = new StringBuilder ();
    for (int i = 0; i < 10000; i++)
    {
      largeData.append (SIMPLE_TEXT);
    }

    final NonBlockingStringReader source = new NonBlockingStringReader (largeData.toString ());
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 512))
    {
      final char [] readBuffer = new char [largeData.length ()];
      int totalRead = 0;

      while (totalRead < largeData.length ())
      {
        final int charsRead = reader.read (readBuffer, totalRead, largeData.length () - totalRead);
        if (charsRead == -1)
          break;
        totalRead += charsRead;
      }

      assertEquals (largeData.length (), totalRead);
      assertEquals (largeData.toString (), new String (readBuffer));
    }
  }

  @Test
  public void testSmallBufferSize () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    // Use very small buffer to test buffer management
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 1))
    {
      final char [] readData = new char [SIMPLE_TEXT.length ()];
      int totalRead = 0;

      while (totalRead < SIMPLE_TEXT.length ())
      {
        final int charsRead = reader.read (readData, totalRead, SIMPLE_TEXT.length () - totalRead);
        if (charsRead == -1)
          break;
        totalRead += charsRead;
      }

      assertEquals (SIMPLE_TEXT.length (), totalRead);
      assertEquals (SIMPLE_TEXT, new String (readData));
    }
  }

  @Test
  public void testMarkAndResetWithBufferGrowth () throws IOException
  {
    // Create data larger than default buffer
    final StringBuilder largeData = new StringBuilder ();
    for (int i = 0; i < 1000; i++)
    {
      largeData.append (SIMPLE_TEXT);
    }

    final NonBlockingStringReader source = new NonBlockingStringReader (largeData.toString ());
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 1024))
    {
      // Mark at beginning with large read ahead limit
      reader.mark (15000);

      // Read substantial amount of data
      final char [] buffer = new char [10000];
      final int charsRead = reader.read (buffer);
      assertEquals (10000, charsRead);

      // Reset and verify we can read the same data again
      reader.reset ();
      final char [] buffer2 = new char [10000];
      final int charsRead2 = reader.read (buffer2);
      assertEquals (10000, charsRead2);

      for (int i = 0; i < 10000; i++)
      {
        assertEquals (buffer[i], buffer2[i]);
      }
    }
  }

  @Test
  public void testLinesStream () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (TEST_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final List <String> lines = reader.lines ().collect (Collectors.toList ());

      assertEquals (5, lines.size ());
      assertEquals ("Hello World!", lines.get (0));
      assertEquals ("This is line 2", lines.get (1));
      assertEquals ("Line 3 with CRLF", lines.get (2));
      assertEquals ("Line 4 with CR only", lines.get (3));
      assertEquals ("Final line", lines.get (4));
    }
  }

  @Test
  public void testLinesStreamEmpty () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader ("");
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final List <String> lines = reader.lines ().collect (Collectors.toList ());
      assertEquals (0, lines.size ());
    }
  }

  @Test
  public void testLinesStreamWithEmptyLines () throws IOException
  {
    final String testData = "Line1\n\nLine3\n";
    final NonBlockingStringReader source = new NonBlockingStringReader (testData);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final List <String> lines = reader.lines ().collect (Collectors.toList ());
      assertEquals (3, lines.size ());
      assertEquals ("Line1", lines.get (0));
      assertEquals ("", lines.get (1));
      assertEquals ("Line3", lines.get (2));
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithInvalidArrayParameters ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [10];
      // Should throw IllegalArgumentException for invalid offset/length combination
      reader.read (buffer, -1, 5);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithInvalidLength ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [10];
      // Should throw IllegalArgumentException for negative length
      reader.read (buffer, 0, -1);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testReadWithOffsetPlusLengthExceedsArraySize ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      final char [] buffer = new char [10];
      // Should throw IllegalArgumentException when offset + length > array length
      reader.read (buffer, 5, 10);
    }
    catch (final IOException ex)
    {
      fail ("Should throw IllegalArgumentException, not IOException");
    }
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMarkWithNegativeReadAheadLimit () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Should throw IllegalArgumentException for negative read ahead limit
      reader.mark (-1);
    }
  }

  @Test
  public void testReadyAfterPartialRead () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 16))
    {
      // Read some data
      final char [] buffer = new char [10];
      reader.read (buffer);

      // Should still be ready if there's more data
      assertTrue (reader.ready ());
    }
  }

  @Test
  public void testSkipWithMark () throws IOException
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source))
    {
      // Set mark
      reader.mark (50);

      // Skip some characters
      final long skipped = reader.skip (10);
      assertEquals (10, skipped);

      // Reset should work
      reader.reset ();

      // Should be back at beginning
      assertEquals ('a', reader.read ());
    }
  }

  @Test
  public void testReadLineWithLongLine () throws IOException
  {
    final StringBuilder longLine = new StringBuilder ();
    for (int i = 0; i < 10000; i++)
    {
      longLine.append ('x');
    }

    final NonBlockingStringReader source = new NonBlockingStringReader (longLine.toString ());
    try (final NonBlockingBufferedReader reader = new NonBlockingBufferedReader (source, 80))
    {
      final String result = reader.readLine ();
      assertEquals (longLine.toString (), result);
    }
  }

  @SuppressWarnings ("resource")
  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidBufferSizeResource ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    // Should throw IllegalArgumentException for buffer size <= 0
    new NonBlockingBufferedReader (source, 0);
  }

  @SuppressWarnings ("resource")
  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithNegativeBufferSizeResource ()
  {
    final NonBlockingStringReader source = new NonBlockingStringReader (SIMPLE_TEXT);
    // Should throw IllegalArgumentException for negative buffer size
    new NonBlockingBufferedReader (source, -1);
  }
}
