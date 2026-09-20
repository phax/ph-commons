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
package com.helger.io.channel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.state.ESuccess;

/**
 * Test class for class {@link ChannelHelper}.
 *
 * @author Philip Helger
 */
public final class ChannelHelperTest
{
  private static final byte [] PAYLOAD = "Hello World - this is the payload".getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testChannelCopy () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final ReadableByteChannel aSrc = Channels.newChannel (new NonBlockingByteArrayInputStream (PAYLOAD));
         final WritableByteChannel aDest = Channels.newChannel (aBAOS))
    {
      assertEquals (PAYLOAD.length, ChannelHelper.channelCopy (aSrc, aDest));
      assertArrayEquals (PAYLOAD, aBAOS.toByteArray ());
    }
  }

  @Test
  public void testChannelCopyInvalidParams () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final ReadableByteChannel aSrc = Channels.newChannel (new NonBlockingByteArrayInputStream (PAYLOAD));
         final WritableByteChannel aDest = Channels.newChannel (aBAOS))
    {
      try
      {
        ChannelHelper.channelCopy (null, aDest);
        fail ();
      }
      catch (final NullPointerException | IllegalArgumentException ex)
      {
        // expected
      }
      try
      {
        ChannelHelper.channelCopy (aSrc, null);
        fail ();
      }
      catch (final NullPointerException | IllegalArgumentException ex)
      {
        // expected
      }

      // A closed channel is not accepted
      @SuppressWarnings ("resource")
      final ReadableByteChannel aClosed = Channels.newChannel (new NonBlockingByteArrayInputStream (PAYLOAD));
      aClosed.close ();
      try
      {
        ChannelHelper.channelCopy (aClosed, aDest);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testClose ()
  {
    assertSame (ESuccess.FAILURE, ChannelHelper.close (null));

    final ReadableByteChannel aChannel = Channels.newChannel (new NonBlockingByteArrayInputStream (PAYLOAD));
    assertSame (ESuccess.SUCCESS, ChannelHelper.close (aChannel));
    // Already closed
    assertSame (ESuccess.FAILURE, ChannelHelper.close (aChannel));
  }

  @Test
  public void testRelease () throws IOException
  {
    assertSame (ESuccess.FAILURE, ChannelHelper.release (null));

    final Path aPath = Path.of ("target", "junittest-channelhelper.txt");
    Files.createDirectories (aPath.getParent ());
    try
    {
      try (final FileChannel aChannel = FileChannel.open (aPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
        final FileLock aLock = aChannel.lock ();
        assertSame (ESuccess.SUCCESS, ChannelHelper.release (aLock));
        // Releasing an already released lock is a no-op
        assertSame (ESuccess.SUCCESS, ChannelHelper.release (aLock));
      }

      // Releasing a lock of a closed channel fails
      final FileLock aLock;
      try (final FileChannel aChannel = FileChannel.open (aPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
        aLock = aChannel.lock ();
      }
      assertSame (ESuccess.FAILURE, ChannelHelper.release (aLock));
    }
    finally
    {
      Files.deleteIfExists (aPath);
    }
  }
}
