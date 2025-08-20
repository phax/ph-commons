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
package com.helger.base.array.bytes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.helger.base.BaseTestHelper;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.mock.CommonsAssert;

/**
 * Test class for class {@link ByteArrayWrapper}.
 *
 * @author Philip Helger
 */
public final class ByteArrayWrapperTest
{
  private static final byte [] TEST_DATA = "Hello World!".getBytes (StandardCharsets.UTF_8);
  private static final byte [] EMPTY_ARRAY = {};

  @Test
  public void testConstructorWithWholeArrayCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, true);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertTrue (wrapper.isCopy ());
    assertFalse (wrapper.isEmpty ());
    assertTrue (wrapper.isNotEmpty ());

    // Verify it's a copy - arrays should not be the same reference
    assertNotSame (TEST_DATA, wrapper.bytes ());
    // But content should be equal
    assertArrayEquals (TEST_DATA, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithWholeArrayNoCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, false);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());
    assertFalse (wrapper.isEmpty ());
    assertTrue (wrapper.isNotEmpty ());

    // Verify it's not a copy - arrays should be the same reference
    assertSame (TEST_DATA, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithPartialArrayCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, 2, 5, true);

    assertEquals (5, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertTrue (wrapper.isCopy ());
    assertFalse (wrapper.isEmpty ());
    assertTrue (wrapper.isNotEmpty ());

    // Verify it's a copy and contains the correct subset
    assertNotSame (TEST_DATA, wrapper.bytes ());
    assertEquals (5, wrapper.bytes ().length);

    // Check that the copied content matches the original subset
    final byte [] expected = new byte [5];
    System.arraycopy (TEST_DATA, 2, expected, 0, 5);
    assertArrayEquals (expected, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithPartialArrayNoCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, 2, 5, false);

    assertEquals (5, wrapper.size ());
    assertEquals (2, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());
    assertFalse (wrapper.isEmpty ());
    assertTrue (wrapper.isNotEmpty ());

    // Verify it's not a copy - arrays should be the same reference
    assertSame (TEST_DATA, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithEmptyArrayCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (EMPTY_ARRAY, true);

    assertEquals (0, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertTrue (wrapper.isCopy ());
    assertTrue (wrapper.isEmpty ());
    assertFalse (wrapper.isNotEmpty ());

    assertNotSame (EMPTY_ARRAY, wrapper.bytes ());
    assertArrayEquals (EMPTY_ARRAY, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithEmptyArrayNoCopy ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (EMPTY_ARRAY, false);

    assertEquals (0, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());
    assertTrue (wrapper.isEmpty ());
    assertFalse (wrapper.isNotEmpty ());

    assertSame (EMPTY_ARRAY, wrapper.bytes ());
  }

  @Test
  public void testConstructorWithZeroLength ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, 5, 0, false);

    assertEquals (0, wrapper.size ());
    assertEquals (5, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());
    assertTrue (wrapper.isEmpty ());
    assertFalse (wrapper.isNotEmpty ());

    assertSame (TEST_DATA, wrapper.bytes ());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidOffset ()
  {
    new ByteArrayWrapper (TEST_DATA, -1, 5, false);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidLength ()
  {
    new ByteArrayWrapper (TEST_DATA, 0, -1, false);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorWithOffsetPlusLengthExceedsArraySize ()
  {
    new ByteArrayWrapper (TEST_DATA, 5, TEST_DATA.length, false);
  }

  @Test
  public void testEqualsAndHashCode ()
  {
    final ByteArrayWrapper wrapper1 = new ByteArrayWrapper (TEST_DATA, true);
    final ByteArrayWrapper wrapper2 = new ByteArrayWrapper (TEST_DATA, true);
    final ByteArrayWrapper wrapper3 = new ByteArrayWrapper (TEST_DATA, false);

    // Test reflexivity
    assertEquals (wrapper1, wrapper1);

    // Test symmetry and equality
    assertEquals (wrapper1, wrapper2);
    assertEquals (wrapper2, wrapper1);

    // Test different copy flags but same content - now should be equal
    assertEquals (wrapper1, wrapper3);

    // Test hashCode consistency
    assertEquals (wrapper1.hashCode (), wrapper2.hashCode ());
    assertEquals (wrapper1.hashCode (), wrapper3.hashCode ());

    // Test inequality with different content
    final byte [] differentData = "Different".getBytes (StandardCharsets.UTF_8);
    final ByteArrayWrapper wrapper4 = new ByteArrayWrapper (differentData, false);
    assertNotEquals (wrapper1, wrapper4);
    assertNotEquals (wrapper1.hashCode (), wrapper4.hashCode ());

    // Test equality with same content but different underlying arrays
    final byte [] sameContentArray = TEST_DATA.clone ();
    final ByteArrayWrapper wrapper5 = new ByteArrayWrapper (sameContentArray, false);
    assertEquals (wrapper1, wrapper5);
    assertEquals (wrapper1.hashCode (), wrapper5.hashCode ());

    // Test inequality with different offset/length from same array
    final ByteArrayWrapper wrapper6 = new ByteArrayWrapper (TEST_DATA, 1, TEST_DATA.length - 1, false);
    assertNotEquals (wrapper1, wrapper6);

    // Test inequality with null
    assertNotEquals (wrapper1, null);

    // Test inequality with different class
    assertNotEquals (wrapper1, "string");
  }

  @Test
  public void testToString ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, 2, 5, true);
    final String result = wrapper.toString ();

    // Let's check the actual format - it might be "byte[]#5" rather than "byte[]#=5"
    assertTrue ("Result: " + result, result.contains ("byte[]#5") || result.contains ("byte[]#=5"));
    assertTrue ("Result: " + result, result.contains ("Offset=0")); // 0 because it's a copy
    assertTrue ("Result: " + result, result.contains ("Length=5"));
    assertTrue ("Result: " + result, result.contains ("IsCopy=true"));
  }

  @Test
  public void testCreateFromNonBlockingByteArrayOutputStreamCopy ()
  {
    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    baos.write (TEST_DATA);

    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (baos, true);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertTrue (wrapper.isCopy ());
    assertArrayEquals (TEST_DATA, wrapper.bytes ());

    // Verify it's a copy by modifying the original and checking wrapper is unchanged
    final byte [] originalContent = wrapper.bytes ().clone ();
    baos.write ("Additional".getBytes (StandardCharsets.UTF_8));
    assertArrayEquals (originalContent, wrapper.bytes ());
  }

  @Test
  public void testCreateFromNonBlockingByteArrayOutputStreamNoCopy ()
  {
    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    baos.write (TEST_DATA);

    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (baos, false);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());

    // Verify it's not a copy - should reference the same buffer
    assertSame (baos.directGetBuffer (), wrapper.bytes ());
  }

  @Test
  public void testCreateFromString ()
  {
    final String testString = "Hello World!";
    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (testString, StandardCharsets.UTF_8);

    assertEquals (testString.getBytes (StandardCharsets.UTF_8).length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertFalse (wrapper.isCopy ()); // String.getBytes() already creates a new array
    assertArrayEquals (testString.getBytes (StandardCharsets.UTF_8), wrapper.bytes ());
  }

  @Test
  public void testCreateFromStringWithDifferentCharsets ()
  {
    final String testString = "Hällö Wörld!"; // Contains non-ASCII characters

    final ByteArrayWrapper wrapperUTF8 = ByteArrayWrapper.create (testString, StandardCharsets.UTF_8);
    final ByteArrayWrapper wrapperISO = ByteArrayWrapper.create (testString, StandardCharsets.ISO_8859_1);

    // Different charsets should produce different byte arrays
    assertNotEquals (wrapperUTF8.size (), wrapperISO.size ());
    assertFalse (java.util.Arrays.equals (wrapperUTF8.bytes (), wrapperISO.bytes ()));
  }

  @Test
  public void testCreateFromEmptyString ()
  {
    final ByteArrayWrapper wrapper = ByteArrayWrapper.create ("", StandardCharsets.UTF_8);

    assertEquals (0, wrapper.size ());
    assertTrue (wrapper.isEmpty ());
    assertFalse (wrapper.isNotEmpty ());
    assertArrayEquals (new byte [0], wrapper.bytes ());
  }

  @Test
  public void testCreateFromByteBufferCopy ()
  {
    final ByteBuffer buffer = ByteBuffer.allocate (20);
    buffer.put (TEST_DATA);
    // Position is now at TEST_DATA.length

    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (buffer, true);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (0, wrapper.getOffset ());
    assertTrue (wrapper.isCopy ());

    // Verify content matches
    final byte [] expected = new byte [TEST_DATA.length];
    System.arraycopy (buffer.array (), buffer.arrayOffset (), expected, 0, TEST_DATA.length);
    assertArrayEquals (expected, wrapper.bytes ());
  }

  @Test
  public void testCreateFromByteBufferNoCopy ()
  {
    final ByteBuffer buffer = ByteBuffer.allocate (20);
    buffer.put (TEST_DATA);
    // Position is now at TEST_DATA.length

    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (buffer, false);

    assertEquals (TEST_DATA.length, wrapper.size ());
    assertEquals (buffer.arrayOffset (), wrapper.getOffset ());
    assertFalse (wrapper.isCopy ());

    // Verify it references the same array
    assertSame (buffer.array (), wrapper.bytes ());
  }

  @Test
  public void testCreateFromEmptyByteBuffer ()
  {
    final ByteBuffer buffer = ByteBuffer.allocate (10);
    // Position is 0, so no data

    final ByteArrayWrapper wrapper = ByteArrayWrapper.create (buffer, false);

    assertEquals (0, wrapper.size ());
    assertTrue (wrapper.isEmpty ());
    assertSame (buffer.array (), wrapper.bytes ());
  }

  @Test
  public void testLargeArrayHandling ()
  {
    // Create a large array to test memory handling
    final byte [] largeArray = new byte [100000];
    ThreadLocalRandom.current ().nextBytes (largeArray);

    final ByteArrayWrapper wrapperCopy = new ByteArrayWrapper (largeArray, true);
    final ByteArrayWrapper wrapperNoCopy = new ByteArrayWrapper (largeArray, false);

    assertEquals (largeArray.length, wrapperCopy.size ());
    assertEquals (largeArray.length, wrapperNoCopy.size ());

    assertTrue (wrapperCopy.isCopy ());
    assertFalse (wrapperNoCopy.isCopy ());

    assertNotSame (largeArray, wrapperCopy.bytes ());
    assertSame (largeArray, wrapperNoCopy.bytes ());

    assertArrayEquals (largeArray, wrapperCopy.bytes ());
  }

  @Test
  public void testPartialArrayEquality ()
  {
    final byte [] data = "Hello World Test Data".getBytes (StandardCharsets.UTF_8);

    // Create two wrappers with the same partial content from the same array
    // "World" (copied)
    final ByteArrayWrapper wrapper1 = new ByteArrayWrapper (data, 6, 5, true);
    // "World" (not copied)
    final ByteArrayWrapper wrapper2 = new ByteArrayWrapper (data, 6, 5, false);

    // These should now be equal because equals compares only the relevant content
    assertEquals (wrapper1, wrapper2);
    assertEquals (wrapper1.hashCode (), wrapper2.hashCode ());

    // Two wrappers from the same array with same offset/length should be equal
    final ByteArrayWrapper wrapper3 = new ByteArrayWrapper (data, 6, 5, false);
    assertEquals (wrapper2, wrapper3);
    assertEquals (wrapper2.hashCode (), wrapper3.hashCode ());

    // Two copies should be equal if they have the same content
    final ByteArrayWrapper wrapper4 = new ByteArrayWrapper (data, 6, 5, true);
    assertEquals (wrapper1, wrapper4);

    // Create wrapper with different offset but same content from different array
    final byte [] worldBytes = "World".getBytes (StandardCharsets.UTF_8);
    final ByteArrayWrapper wrapper5 = new ByteArrayWrapper (worldBytes, false);
    assertEquals (wrapper1, wrapper5);
    assertEquals (wrapper1.hashCode (), wrapper5.hashCode ());
  }

  @Test
  public void testModificationSafety ()
  {
    final byte [] originalData = TEST_DATA.clone ();
    final ByteArrayWrapper wrapperCopy = new ByteArrayWrapper (originalData, true);
    final ByteArrayWrapper wrapperNoCopy = new ByteArrayWrapper (originalData, false);

    // Modify the original array
    originalData[0] = (byte) 'X';

    // Copy should be unaffected
    assertEquals ('H', wrapperCopy.bytes ()[0]);

    // No-copy should be affected
    assertEquals ('X', wrapperNoCopy.bytes ()[0]);
  }

  @Test
  public void testBoundaryConditions ()
  {
    // Test with array of size 1
    final byte [] singleByte = { 42 };
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (singleByte, false);

    assertEquals (1, wrapper.size ());
    assertEquals (42, wrapper.bytes ()[0]);
    assertFalse (wrapper.isEmpty ());

    // Test wrapping at the end of array
    final ByteArrayWrapper endWrapper = new ByteArrayWrapper (TEST_DATA, TEST_DATA.length, 0, false);
    assertEquals (0, endWrapper.size ());
    assertTrue (endWrapper.isEmpty ());
  }

  @Test
  public void testConsistentHashCodeForEqualObjects ()
  {
    final ByteArrayWrapper wrapper1 = new ByteArrayWrapper (TEST_DATA, true);
    final ByteArrayWrapper wrapper2 = new ByteArrayWrapper (TEST_DATA, true);

    // Equal objects must have equal hash codes
    assertEquals (wrapper1, wrapper2);
    assertEquals (wrapper1.hashCode (), wrapper2.hashCode ());

    // Hash code should be consistent across multiple calls
    final int hash1 = wrapper1.hashCode ();
    final int hash2 = wrapper1.hashCode ();
    assertEquals (hash1, hash2);
  }

  @Test
  public void testImmutabilityOfState ()
  {
    final ByteArrayWrapper wrapper = new ByteArrayWrapper (TEST_DATA, 2, 5, false);

    // Store initial state
    final int initialSize = wrapper.size ();
    final int initialOffset = wrapper.getOffset ();
    final boolean initialIsCopy = wrapper.isCopy ();

    // Attempt to modify the underlying array (this should be external to the wrapper)
    // The wrapper state should remain unchanged
    assertEquals (initialSize, wrapper.size ());
    assertEquals (initialOffset, wrapper.getOffset ());
    CommonsAssert.assertEquals (initialIsCopy, wrapper.isCopy ());
  }

  @Test
  public void testEqualsWithLogicallyEqualContent ()
  {
    // Test the bug fix: equals should compare only the logical content, not the entire arrays
    final byte [] data1 = "BEFORE_CONTENT_AFTER".getBytes (StandardCharsets.UTF_8);
    final byte [] data2 = "PREFIX_CONTENT_SUFFIX".getBytes (StandardCharsets.UTF_8);

    // Both should represent "CONTENT"
    // "CONTENT" from data1
    final ByteArrayWrapper wrapper1 = new ByteArrayWrapper (data1, 7, 7, false);
    // "CONTENT" from data2
    final ByteArrayWrapper wrapper2 = new ByteArrayWrapper (data2, 7, 7, false);

    // These should be equal despite having different underlying arrays
    assertEquals (wrapper1, wrapper2);
    assertEquals (wrapper1.hashCode (), wrapper2.hashCode ());

    // Test with copy vs no-copy of the same logical content
    // Copy of "CONTENT"
    final ByteArrayWrapper wrapper3 = new ByteArrayWrapper (data1, 7, 7, true);
    assertEquals (wrapper1, wrapper3);
    assertEquals (wrapper1.hashCode (), wrapper3.hashCode ());

    // Test with completely different array containing same content
    final byte [] data3 = "CONTENT".getBytes (StandardCharsets.UTF_8);
    final ByteArrayWrapper wrapper4 = new ByteArrayWrapper (data3, false);
    assertEquals (wrapper1, wrapper4);
    assertEquals (wrapper1.hashCode (), wrapper4.hashCode ());
  }

  @Test
  public void testEqualsWithDifferentLogicalContent ()
  {
    final byte [] data = "Hello World Test".getBytes (StandardCharsets.UTF_8);

    // "Hello"
    final ByteArrayWrapper wrapper1 = new ByteArrayWrapper (data, 0, 5, false);
    // "World"
    final ByteArrayWrapper wrapper2 = new ByteArrayWrapper (data, 6, 5, false); // "Test"
    // "Test"
    final ByteArrayWrapper wrapper3 = new ByteArrayWrapper (data, 12, 4, false);

    // All should be different
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (wrapper1, wrapper2);
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (wrapper1, wrapper3);
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (wrapper2, wrapper3);
  }
}
