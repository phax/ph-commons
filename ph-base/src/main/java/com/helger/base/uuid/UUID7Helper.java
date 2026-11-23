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

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;

/**
 * This class contains static methods to create version-7 UUIDs as defined in RFC 9562. A UUID v7
 * starts with a 48 bit Unix epoch timestamp in milliseconds, followed by the 4 bit version, 12 bits
 * "rand_a", the 2 bit variant and 62 bits "rand_b". UUIDs created in different milliseconds
 * therefore sort by creation time.
 * <p>
 * This is implemented here, because <code>UUID.ofEpochMillis</code> is only available from Java 26
 * onwards.
 * <p>
 * Note: {@link UUID#compareTo(UUID)} compares the two halves of a UUID as <b>signed</b> longs.
 * Timestamps from 2^47 milliseconds onwards (and all random bits with the top bit set) are therefore
 * sorted incorrectly. Use {@link #UNSIGNED_COMPARATOR} to sort UUID v7 objects by their creation
 * time.
 *
 * @author Philip Helger
 * @since v12.8.0
 */
@Immutable
public final class UUID7Helper
{
  /** The maximum timestamp that fits into 48 bits */
  public static final long MAX_TIMESTAMP_MILLIS = 0xffff_ffff_ffffL;

  /**
   * A comparator that compares UUIDs by their unsigned 128 bit value. For UUID v7 objects this is
   * the order of creation. {@link UUID#compareTo(UUID)} must not be used for this purpose, because
   * it uses a signed comparison.
   */
  public static final Comparator <UUID> UNSIGNED_COMPARATOR = (aUUID1, aUUID2) -> {
    final int ret = Long.compareUnsigned (aUUID1.getMostSignificantBits (), aUUID2.getMostSignificantBits ());
    if (ret != 0)
      return ret;
    return Long.compareUnsigned (aUUID1.getLeastSignificantBits (), aUUID2.getLeastSignificantBits ());
  };

  private static final SecureRandom RANDOM = new SecureRandom ();

  // The last state of the monotonic creation: timestamp (48 bits) << 12 | counter (12 bits)
  private static final AtomicLong LAST_MONOTONIC = new AtomicLong (0);

  private UUID7Helper ()
  {}

  /**
   * Helper method to build a UUID v7 from its parts.
   *
   * @param nEpochMillis
   *        The timestamp. Must fit into 48 bits.
   * @param nRandA
   *        The 12 bits of "rand_a". Higher bits are ignored.
   * @param nRandB
   *        The 62 bits of "rand_b". Higher bits are ignored.
   * @return A UUID object
   */
  @NonNull
  private static UUID _makeUUID7 (final long nEpochMillis, final long nRandA, final long nRandB)
  {
    // timestamp (48 bits) | version 7 (4 bits) | rand_a (12 bits)
    final long nMostSigBits = (nEpochMillis << 16) | 0x7000L | (nRandA & 0x0fffL);
    // IETF variant (10b) | rand_b (62 bits)
    final long nLeastSigBits = 0x8000_0000_0000_0000L | (nRandB & 0x3fff_ffff_ffff_ffffL);
    return new UUID (nMostSigBits, nLeastSigBits);
  }

  /**
   * Create a new UUID v7 with the current time and random bits.
   *
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID create ()
  {
    return create (System.currentTimeMillis ());
  }

  /**
   * Create a new UUID v7 with the provided timestamp and random bits.
   *
   * @param nEpochMillis
   *        The milliseconds since the Unix epoch. Must be &ge; 0 and &le;
   *        {@link #MAX_TIMESTAMP_MILLIS}.
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID create (@Nonnegative final long nEpochMillis)
  {
    ValueEnforcer.isBetweenInclusive (nEpochMillis, "EpochMillis", 0, MAX_TIMESTAMP_MILLIS);

    return _makeUUID7 (nEpochMillis, RANDOM.nextInt (), RANDOM.nextLong ());
  }

  /**
   * Create a new UUID v7 that is strictly greater (according to {@link #UNSIGNED_COMPARATOR}) than
   * all UUIDs previously created with this method in this JVM. If the clock has not advanced (or
   * went backwards) since the last call, the 12 bit "rand_a" field is used as a counter. If the
   * counter overflows, the timestamp of the last UUID plus one is used. This method is thread-safe.
   *
   * @return the UUID object and never <code>null</code>.
   */
  @NonNull
  public static UUID createMonotonic ()
  {
    final long nNowState = System.currentTimeMillis () << 12;
    final long nState = LAST_MONOTONIC.updateAndGet (nLast -> Math.max (nLast + 1, nNowState));
    final long nEpochMillis = nState >>> 12;
    ValueEnforcer.isTrue (nEpochMillis <= MAX_TIMESTAMP_MILLIS, "The UUID v7 timestamp overflowed");

    return _makeUUID7 (nEpochMillis, nState, RANDOM.nextLong ());
  }

  /**
   * Check if the provided UUID is a UUID v7.
   *
   * @param aUUID
   *        The UUID to check. May not be <code>null</code>.
   * @return <code>true</code> if it has version 7 and the IETF variant, <code>false</code>
   *         otherwise.
   */
  public static boolean isUUID7 (@NonNull final UUID aUUID)
  {
    ValueEnforcer.notNull (aUUID, "UUID");

    return aUUID.version () == 7 && aUUID.variant () == 2;
  }

  /**
   * Extract the timestamp from a UUID v7.
   *
   * @param aUUID
   *        The UUID v7 to extract the timestamp from. May not be <code>null</code>.
   * @return The milliseconds since the Unix epoch.
   * @throws IllegalArgumentException
   *         if the UUID is not a UUID v7
   */
  @Nonnegative
  public static long getTimestampMillis (@NonNull final UUID aUUID)
  {
    ValueEnforcer.isTrue (isUUID7 (aUUID), () ->"The UUID '" + aUUID + "' is not a UUID v7");

    return aUUID.getMostSignificantBits () >>> 16;
  }
}
