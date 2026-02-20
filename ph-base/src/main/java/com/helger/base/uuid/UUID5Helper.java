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
package com.helger.base.uuid;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;

/**
 * This class contains static methods that leverage {@link java.util.UUID} and
 * {@link java.security.MessageDigest} to create version-5 UUIDs with full namespace support.
 * <p>
 * The UUID class provided by java.util is suitable as a datatype for UUIDs of any version, but
 * lacks methods for creating version 5 (SHA-1 based) UUIDs. Its implementation of version 3 (MD5
 * based) UUIDs also lacks build-in namespace support.
 * <p>
 * This class was inspired by
 * https://github.com/rootsdev/polygenea/blob/master/java/src/org/rootsdev/polygenea/UUID5.java from
 * Luther Tychonievich
 *
 * @author Philip Helger
 * @since v12.1.3
 */
@Immutable
public final class UUID5Helper
{
  private UUID5Helper ()
  {}

  /**
   * Helper method to prepare values for being a UUID v5
   *
   * @param aHashBytes
   *        A 20 byte array to be the basis of the UUID bits
   * @return A UUID object
   */
  @NonNull
  private static UUID _makeUUID5 (final byte @NonNull [] aHashBytes)
  {
    ValueEnforcer.notNull (aHashBytes, "HashBytes");
    ValueEnforcer.isEqual (aHashBytes.length, 20, "Expected 20 bytes from SHA-1");

    // clear version (00001111b)
    aHashBytes[6] &= 0x0f;
    // set to version 5 (01010000b)
    aHashBytes[6] |= 0x50;
    // clear variant (00111111b)
    aHashBytes[8] &= 0x3f;
    // set to IETF variant (10000000b)
    aHashBytes[8] |= 0x80;

    final ByteBuffer aBB = ByteBuffer.wrap (aHashBytes, 0, 16);
    final long nMostSigBits = aBB.getLong ();
    final long nLeastSigBits = aBB.getLong ();
    return new UUID (nMostSigBits, nLeastSigBits);
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5)
   *
   * @param aNameBytes
   *        The bytes to use as the "name" of this hash
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID fromBytes (final byte @NonNull [] aNameBytes)
  {
    ValueEnforcer.notNull (aNameBytes, "Name");

    try
    {
      final MessageDigest aMD = MessageDigest.getInstance ("SHA-1");
      return _makeUUID5 (aMD.digest (aNameBytes));
    }
    catch (final NoSuchAlgorithmException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5) and uses a
   * namespace
   *
   * @param aNamespace
   *        The namespace to use for this UUID. If <code>null</code>, uses
   *        00000000-0000-0000-0000-000000000000
   * @param aNameBytes
   *        The bytes to use as the "name" of this hash. May not be <code>null</code>.
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID fromBytes (@Nullable final UUID aNamespace, final byte @NonNull [] aNameBytes)
  {
    ValueEnforcer.notNull (aNameBytes, "NameBytes");

    try
    {
      final MessageDigest aMD = MessageDigest.getInstance ("SHA-1");
      if (aNamespace == null)
      {
        // Use a 16-byte 0-array
        aMD.update (new byte [16]);
      }
      else
      {
        final ByteBuffer aBB = ByteBuffer.allocate (16);
        aBB.putLong (aNamespace.getMostSignificantBits ());
        aBB.putLong (aNamespace.getLeastSignificantBits ());
        aMD.update (aBB.array ());
      }
      return _makeUUID5 (aMD.digest (aNameBytes));
    }
    catch (final NoSuchAlgorithmException e)
    {
      throw new IllegalStateException (e);
    }
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5)
   *
   * @param sName
   *        The string to be encoded in utf-8 to get the bytes to hash. May not be
   *        <code>null</code>.
   * @return the UUID object
   */
  @NonNull
  public static UUID fromUTF8 (@NonNull final String sName)
  {
    return fromBytes (sName.getBytes (StandardCharsets.UTF_8));
  }

  /**
   * Similar to UUID.nameUUIDFromBytes, but does version 5 (sha-1) not version 3 (md5) and uses a
   * namespace
   *
   * @param aNamespace
   *        The namespace to use for this UUID. If <code>null</code>, uses
   *        00000000-0000-0000-0000-000000000000
   * @param sName
   *        The string to be encoded in utf-8 to get the bytes to hash. May not be
   *        <code>null</code>.
   * @return the UUID object
   */
  @NonNull
  public static UUID fromUTF8 (@Nullable final UUID aNamespace, @NonNull final String sName)
  {
    return fromBytes (aNamespace, sName.getBytes (StandardCharsets.UTF_8));
  }
}
