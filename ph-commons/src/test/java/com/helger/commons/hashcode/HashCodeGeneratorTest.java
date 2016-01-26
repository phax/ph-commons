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
package com.helger.commons.hashcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.exception.LoggedRuntimeException;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.state.EChange;
import com.helger.commons.system.SystemHelper;

/**
 * Test class for class {@link HashCodeGenerator}.
 *
 * @author Philip Helger
 */
public final class HashCodeGeneratorTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (HashCodeGeneratorTest.class);

  private static void _appendFields (final HashCodeGenerator aHC)
  {
    // Primitive values
    aHC.append (true);
    aHC.append (false);
    aHC.append ((byte) 0x44);
    aHC.append ('ä');
    aHC.append (3.1415924);
    aHC.append (0.00);
    aHC.append (3141.59f);
    aHC.append (0.00f);
    aHC.append (-4711);
    aHC.append (0x2f99887766554433L);
    aHC.append ((short) 4701);

    // Object with value
    aHC.append (EChange.CHANGED);
    aHC.append (BigDecimal.ZERO);
    aHC.append (new StringBuffer ("Hallo"));
    aHC.append (new StringBuilder ("Hallo Welt"));
    aHC.append (CollectionHelper.newSet ("Hallo", "Welt", "from", "unit", "test"));
    aHC.append (IteratorHelper.getIterator ("Hallo", "Welt", "from", "unit", "test"));
    aHC.append (IteratorHelper.getEnumeration ("Hallo", "Welt", "from", "unit", "test"));

    // Multi values containing null
    aHC.append (CollectionHelper.newSet ("Hallo", null, null, "unit", "test"));

    // Objects null
    aHC.append ((Enum <?>) null);
    aHC.append ((Object) null);
    aHC.append ((StringBuffer) null);
    aHC.append ((StringBuilder) null);
    aHC.append ((Iterable <?>) null);

    // Array objects filled
    aHC.append (new boolean [] { false, true });
    aHC.append (new byte [] { 23, 0x44 });
    aHC.append ("Hallo".toCharArray ());
    aHC.append (new double [] { 3.1415924, 55.0001 });
    aHC.append (new float [] { 3141.59f, 99f });
    aHC.append (new int [] { -4711, 0, 65535, 65536 });
    aHC.append (new long [] { 0x2f99887766554433L, -567895678900L });
    aHC.append (new short [] { 4701, -32767 });
    aHC.append (EChange.values ());
    aHC.append (new Object [] { EChange.CHANGED, BigDecimal.ONE, System.out });
    aHC.append (CollectionHelper.newList (EChange.CHANGED, BigDecimal.ONE, System.out));

    // Arrays as objects
    aHC.append ((Object) new boolean [] { false, true });
    aHC.append ((Object) new byte [] { 23, 0x44 });
    aHC.append ((Object) "Hallo".toCharArray ());
    aHC.append ((Object) new double [] { 3.1415924, 55.0001 });
    aHC.append ((Object) new float [] { 3141.59f, 99f });
    aHC.append ((Object) new int [] { -4711, 0, 65535, 65536 });
    aHC.append ((Object) new long [] { 0x2f99887766554433L, -567895678900L });
    aHC.append ((Object) new short [] { 4701, -32767 });
    aHC.append ((Object) EChange.values ());
    aHC.append ((Object) new Object [] { EChange.CHANGED, BigDecimal.ONE, System.out });
    aHC.append ((Object) CollectionHelper.newList (EChange.CHANGED, BigDecimal.ONE, System.out));

    // Array objects filled and containing nulls
    aHC.append (new Enum [] { EChange.CHANGED, null, EChange.UNCHANGED });
    aHC.append (new Object [] { EChange.CHANGED, BigDecimal.TEN, null, System.out, null });

    // Array objects empty
    aHC.append (new boolean [0]);
    aHC.append (new byte [0]);
    aHC.append (new char [0]);
    aHC.append (new double [0]);
    aHC.append (new float [0]);
    aHC.append (new int [0]);
    aHC.append (new long [0]);
    aHC.append (new short [0]);
    aHC.append (new Enum [0]);
    aHC.append (new Object [0]);

    // Array objects null
    aHC.append ((boolean []) null);
    aHC.append ((byte []) null);
    aHC.append ((char []) null);
    aHC.append ((double []) null);
    aHC.append ((float []) null);
    aHC.append ((int []) null);
    aHC.append ((long []) null);
    aHC.append ((short []) null);
    aHC.append ((Enum []) null);
    aHC.append ((Object []) null);
  }

  @Test
  public void testIllegalCtor ()
  {
    try
    {
      new HashCodeGenerator ((Object) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new HashCodeGenerator ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Convert class manually to Object parameter
    new HashCodeGenerator ((Object) getClass ());
  }

  @Test
  public void testAppend ()
  {
    final HashCodeGenerator aHC = new HashCodeGenerator (this);
    _appendFields (aHC);
    final HashCodeGenerator aHC2 = new HashCodeGenerator (getClass ());
    _appendFields (aHC2);
    assertEquals (aHC.getHashCode (), aHC2.getHashCode ());
    assertEquals (aHC.getHashCodeObj (), aHC2.getHashCodeObj ());

    try
    {
      // Already closed!
      aHC.append (5);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    // Must still be the same
    assertEquals (aHC.getHashCode (), aHC2.getHashCode ());
    assertEquals (aHC.getHashCodeObj (), aHC2.getHashCodeObj ());
  }

  @Test
  public void testSpecialCases ()
  {
    // BigDecimal.ZERO ends up in 0 hashCode
    assertEquals (0, BigDecimal.ZERO.hashCode ());
    // -> That's why there is a special "null" handling in the HashCodeGenerator
    assertFalse (new HashCodeGenerator (this).append (BigDecimal.ZERO)
                                             .getHashCode () == new HashCodeGenerator (this).append ((BigDecimal) null)
                                                                                            .getHashCode ());
    // Check that array class and native class don't have the same hash code
    assertTrue (new HashCodeGenerator (new Byte [1]).getHashCode () != new HashCodeGenerator (Byte.valueOf ((byte) 0)).getHashCode ());

    // Check that the derived hash code is not modified
    final int nHashCode = new HashCodeGenerator (this).append (123).getHashCode ();
    assertEquals (nHashCode, HashCodeGenerator.getDerived (nHashCode).getHashCode ());

    try
    {
      HashCodeGenerator.getDerived (IHashCodeGenerator.ILLEGAL_HASHCODE);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @Ignore ("runs endlessly")
  public void findIllegalValue () throws InterruptedException
  {
    final AtomicBoolean b = new AtomicBoolean (false);
    final Runnable r = () -> {
      final byte [] aBytes = new byte [10000];
      VerySecureRandom.getInstance ().nextBytes (aBytes);
      int i = 0;
      try
      {
        final HashCodeGenerator hc = new HashCodeGenerator (byte.class);
        for (; i < aBytes.length; ++i)
          hc.append (i);
      }
      catch (final LoggedRuntimeException ex)
      {
        final StringBuilder aSB = new StringBuilder ("new byte[]{");
        for (int j = 0; j < i; ++j)
          aSB.append ("(byte)").append (aBytes[i]).append (',');
        aSB.append ("};");
        SimpleFileIO.writeFile (new File ("HashCode0" + Instant.now ().toEpochMilli () + ".txt"),
                                aSB.toString (),
                                CCharset.CHARSET_ISO_8859_1_OBJ);
        b.set (true);
        s_aLogger.error ("Found match!");
      }
    };

    final int nThreads = SystemHelper.getNumberOfProcessors () * 4;
    final Thread [] aThreads = new Thread [nThreads];

    int nTries = 0;
    while (!b.get ())
    {
      for (int i = 0; i < nThreads; ++i)
      {
        aThreads[i] = new Thread (r, "ph-Thread" + i);
        aThreads[i].start ();
      }
      for (int i = 0; i < nThreads; ++i)
        aThreads[i].join ();

      nTries += nThreads;
      if ((nTries % 1000) == 0)
        s_aLogger.info (nTries + " tries");
    }
  }

  @Test
  public void testUniqueness ()
  {
    final int nHash1 = new HashCodeGenerator (this).append (2).append (3).getHashCode ();
    final int nHash2 = new HashCodeGenerator (this).append (3).append (2).getHashCode ();
    assertTrue (nHash1 != nHash2);
  }
}
