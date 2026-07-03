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
package com.helger.collection.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.MapEntry;

/**
 * Test cases documenting the thread safety characteristics of {@link AbstractSoftMap},
 * {@link SoftHashMap} and {@link SoftLinkedHashMap}.<br>
 * The deterministic tests document the "reads are writes" behaviour that makes it illegal to guard
 * these maps with a read lock only. The concurrency tests are regression tests for the map
 * corruption (endless iteration) that occurred when multiple threads called <code>get()</code> in
 * parallel before the maps were internally synchronized.
 *
 * @author Philip Helger
 */
public final class SoftMapConcurrencyTest
{
  /**
   * Simulate garbage collection of the value of the provided key: clear the SoftReference and
   * optionally enqueue it in the internal ReferenceQueue - exactly what the GC would do.
   *
   * @param aMap
   *        The soft map to work on
   * @param aKey
   *        The key whose value should be "collected"
   * @param bEnqueue
   *        <code>true</code> to also enqueue the reference (as the GC does eventually)
   */
  private static <K, V> void _simulateGC (@NonNull final AbstractSoftMap <K, V> aMap,
                                          final K aKey,
                                          final boolean bEnqueue)
  {
    final AbstractSoftMap.SoftValue <K, V> aSoftValue = aMap.m_aSrcMap.get (aKey);
    aSoftValue.clear ();
    if (bEnqueue)
      aSoftValue.enqueue ();
  }

  /**
   * Documents that {@link AbstractSoftMap#get(Object)} is NOT a read-only operation: if the value
   * was garbage collected, get() structurally modifies the underlying map by removing the entry.
   * That's why get() internally acquires the write lock - an external read lock alone would not
   * protect concurrent get() calls from corrupting the underlying map.
   */
  @Test
  public void testGetIsAStructuralModification ()
  {
    final SoftHashMap <Integer, String> aMap = new SoftHashMap <> ();
    final String sValue1 = "value1";
    final String sValue2 = "value2";
    aMap.put (Integer.valueOf (1), sValue1);
    aMap.put (Integer.valueOf (2), sValue2);
    assertEquals (2, aMap.m_aSrcMap.size ());

    // Pretend the GC cleared the value of key 1
    _simulateGC (aMap, Integer.valueOf (1), false);

    // The plain read removes the entry from the underlying map!
    assertNull (aMap.get (Integer.valueOf (1)));
    assertEquals (1, aMap.m_aSrcMap.size ());
  }

  /**
   * Documents that {@link AbstractSoftMap#size()} is NOT a read-only operation either: it drains
   * the internal ReferenceQueue and removes all collected entries from the underlying map. The same
   * applies to isEmpty() (inherited via size()), put(), remove() and clear().
   */
  @Test
  public void testSizeIsAStructuralModification ()
  {
    final SoftHashMap <Integer, String> aMap = new SoftHashMap <> ();
    aMap.put (Integer.valueOf (1), "value1");
    assertEquals (1, aMap.m_aSrcMap.size ());

    // Pretend the GC cleared AND enqueued the value of key 1
    _simulateGC (aMap, Integer.valueOf (1), true);

    // size() drains the queue and removes the entry
    assertEquals (0, aMap.size ());
    assertEquals (0, aMap.m_aSrcMap.size ());
  }

  /**
   * Documents that {@link SoftLinkedHashMap} operates in ACCESS ORDER mode: every get() moves the
   * accessed entry to the end of the internal doubly linked list. So every get() is a structural
   * modification of the linked list - even if nothing was garbage collected at all. That's why
   * get() internally acquires the write lock: unsynchronized concurrent get() calls raced in
   * LinkedHashMap.afterNodeAccess() and corrupted the before/after pointers - creating a cycle in
   * the linked list, so that any subsequent iteration never terminated.
   */
  @Test
  public void testSoftLinkedHashMapGetReordersEntries ()
  {
    final SoftLinkedHashMap <Integer, String> aMap = new SoftLinkedHashMap <> (10);
    // Hold strong references so that GC cannot interfere
    final ICommonsList <String> aStrongRefs = new CommonsArrayList <> ("a", "b", "c");
    for (int i = 0; i < aStrongRefs.size (); ++i)
      aMap.put (Integer.valueOf (i), aStrongRefs.get (i));

    // A plain read...
    aMap.get (Integer.valueOf (0));

    // ...changed the iteration order: 0 moved to the end
    final ICommonsList <Integer> aKeys = new CommonsArrayList <> (aMap.keySet ());
    assertEquals (new CommonsArrayList <> (Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (0)), aKeys);
  }

  /**
   * Documents that the entrySet() iterator can return entries whose value is <code>null</code>
   * (garbage collected but not yet processed). Callers iterating the map and dereferencing the
   * value (like AbstractMapBasedCache.evictExpired() does with
   * <code>aMapEntry.getValue ().isExpiredAt (aNow)</code>) will throw a NullPointerException.
   */
  @Test
  public void testIteratorMayReturnNullValues ()
  {
    final SoftHashMap <Integer, String> aMap = new SoftHashMap <> ();
    aMap.put (Integer.valueOf (1), "value1");

    // Pretend the GC cleared the value but the queue was not yet processed
    _simulateGC (aMap, Integer.valueOf (1), false);

    int nCount = 0;
    for (final Map.Entry <Integer, String> aEntry : aMap.entrySet ())
    {
      assertEquals (Integer.valueOf (1), aEntry.getKey ());
      // The value is null - every consumer must be prepared for that
      assertNull (aEntry.getValue ());
      nCount++;
    }
    assertEquals (1, nCount);
  }

  /**
   * Tests that entrySet().contains() and entrySet().remove() use value equality (and not the
   * identity of the internal SoftValue wrappers), that garbage collected entries count as "not
   * contained", and that no temporary SoftValue objects are registered with the internal
   * ReferenceQueue anymore (which used to silently remove live entries).
   */
  @SuppressWarnings ("unlikely-arg-type")
  @Test
  public void testEntrySetContainsAndRemove ()
  {
    final SoftHashMap <Integer, String> aMap = new SoftHashMap <> ();
    aMap.put (Integer.valueOf (1), "value1");
    aMap.put (Integer.valueOf (2), "value2");

    // Equal but not identical probe value
    final String sEqualValue = new StringBuilder ("value1").toString ();
    assertTrue (aMap.entrySet ().contains (new MapEntry <> (Integer.valueOf (1), sEqualValue)));
    // Wrong value
    assertFalse (aMap.entrySet ().contains (new MapEntry <> (Integer.valueOf (1), "other")));
    // Wrong key
    assertFalse (aMap.entrySet ().contains (new MapEntry <> (Integer.valueOf (3), "value1")));
    // Not a Map.Entry
    assertFalse (aMap.entrySet ().contains ("value1"));

    // A garbage collected entry counts as "not contained" and cannot be removed
    _simulateGC (aMap, Integer.valueOf (2), false);
    assertFalse (aMap.entrySet ().contains (new MapEntry <> (Integer.valueOf (2), "value2")));
    assertFalse (aMap.entrySet ().remove (new MapEntry <> (Integer.valueOf (2), "value2")));

    // remove() with a non-matching value must not remove anything
    assertFalse (aMap.entrySet ().remove (new MapEntry <> (Integer.valueOf (1), "other")));
    assertNotNull (aMap.get (Integer.valueOf (1)));

    // remove() with a matching entry must remove it
    assertTrue (aMap.entrySet ().remove (new MapEntry <> (Integer.valueOf (1), sEqualValue)));
    assertNull (aMap.get (Integer.valueOf (1)));
    // The stale entry of the garbage collected key 2 is still physically present...
    assertEquals (1, aMap.size ());
    // ...until it is touched
    assertNull (aMap.get (Integer.valueOf (2)));
    assertEquals (0, aMap.size ());

    // add() is not supported - as with all JDK Map implementations
    try
    {
      aMap.entrySet ().add (new MapEntry <> (Integer.valueOf (9), "value9"));
      fail ("add() must not be supported");
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected
    }
  }

  /**
   * Regression test for the "endless iteration" bug: many threads performing ONLY get() calls on a
   * {@link SoftLinkedHashMap}. Because the map is in access-order mode, every get() rewires the
   * internal doubly linked list. Without synchronization the racing threads corrupted the
   * before/after pointers, typically creating a cycle - and the subsequent iteration never
   * terminated. Since AbstractSoftMap synchronizes internally, this must PASS.
   *
   * @throws Exception
   *         In case of error
   */
  @Test (timeout = 60_000)
  public void testConcurrentGetOnSoftLinkedHashMap () throws Exception
  {
    final int nKeys = 100;
    final int nThreads = 8;
    final int nIterations = 200_000;

    final SoftLinkedHashMap <Integer, String> aMap = new SoftLinkedHashMap <> (1_000);
    // Hold strong references so that GC cannot interfere
    final ICommonsList <String> aStrongRefs = new CommonsArrayList <> ();
    for (int i = 0; i < nKeys; ++i)
    {
      final String sValue = "value" + i;
      aStrongRefs.add (sValue);
      aMap.put (Integer.valueOf (i), sValue);
    }

    final CountDownLatch aStartLatch = new CountDownLatch (1);
    final AtomicReference <Throwable> aFirstError = new AtomicReference <> ();
    final Thread [] aThreads = new Thread [nThreads];
    for (int t = 0; t < nThreads; ++t)
    {
      final int nThreadIndex = t;
      aThreads[t] = new Thread (() -> {
        try
        {
          aStartLatch.await ();
          for (int j = 0; j < nIterations; ++j)
          {
            // Deterministic pseudo-random key per thread
            final int nKey = Math.abs (nThreadIndex * 7919 + j * 31) % nKeys;
            aMap.get (Integer.valueOf (nKey));
          }
        }
        catch (final Throwable ex)
        {
          // Store only first time
          aFirstError.compareAndSet (null, ex);
        }
      }, "softmap-get-" + t);
      aThreads[t].setDaemon (true);
      aThreads[t].start ();
    }

    aStartLatch.countDown ();
    for (final Thread aThread : aThreads)
    {
      aThread.join (30_000);
      if (aThread.isAlive ())
        fail ("Thread " + aThread.getName () + " is stuck - the internal linked list is corrupted");
    }

    if (aFirstError.get () != null)
      fail ("A reader thread died with an exception - the map is corrupted: " + aFirstError.get ());

    // Detect endless iteration deterministically: iterating may never yield more entries than were
    // ever put in. If it does, the linked list contains a cycle.
    int nCount = 0;
    for (@SuppressWarnings ("unused")
    final Map.Entry <Integer, String> aEntry : aMap.entrySet ())
    {
      nCount++;
      if (nCount > nKeys)
        fail ("Endless iteration detected - the internal linked list contains a cycle");
    }
    assertEquals ("Entries were lost - the internal linked list was truncated", nKeys, nCount);
  }

  /**
   * Regression test for the corresponding problem on the plain {@link SoftHashMap}: after values
   * have been garbage collected, concurrent get() calls all invoke HashMap.remove() on the shared
   * underlying map. Without synchronization the racing removals corrupted the HashMap (wrong size,
   * lost or dangling entries, in the worst case endless loops in the bin lists). Since
   * AbstractSoftMap synchronizes internally, this must PASS.
   *
   * @throws Exception
   *         In case of error
   */
  @Test (timeout = 60_000)
  public void testConcurrentGetAfterGCOnSoftHashMap () throws Exception
  {
    final int nKeys = 10_000;
    final int nThreads = 8;

    final SoftHashMap <Integer, String> aMap = new SoftHashMap <> ();
    for (int i = 0; i < nKeys; ++i)
      aMap.put (Integer.valueOf (i), "value" + i);

    // Pretend the GC cleared ALL values (but did not enqueue them yet)
    for (int i = 0; i < nKeys; ++i)
      _simulateGC (aMap, Integer.valueOf (i), false);

    final CountDownLatch aStartLatch = new CountDownLatch (1);
    final AtomicReference <Throwable> aFirstError = new AtomicReference <> ();
    final Thread [] aThreads = new Thread [nThreads];
    for (int t = 0; t < nThreads; ++t)
    {
      final int nOffset = t * (nKeys / nThreads);
      aThreads[t] = new Thread (() -> {
        try
        {
          aStartLatch.await ();
          // Every thread touches every key, starting at a different offset
          for (int j = 0; j < nKeys; ++j)
            aMap.get (Integer.valueOf ((nOffset + j) % nKeys));
        }
        catch (final Throwable ex)
        {
          // Store only first time
          aFirstError.compareAndSet (null, ex);
        }
      }, "softmap-gc-get-" + t);
      aThreads[t].setDaemon (true);
      aThreads[t].start ();
    }

    aStartLatch.countDown ();
    for (final Thread aThread : aThreads)
    {
      aThread.join (30_000);
      if (aThread.isAlive ())
        fail ("Thread " + aThread.getName () + " is stuck - the internal HashMap is corrupted");
    }

    if (aFirstError.get () != null)
      fail ("A reader thread died with an exception - the map is corrupted: " + aFirstError.get ());

    // All values were collected and all keys were touched - the map must be empty now
    assertEquals ("Concurrent removals corrupted the underlying HashMap", 0, aMap.m_aSrcMap.size ());
  }
}
