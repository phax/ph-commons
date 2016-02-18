package com.helger.commons.collection.ext;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

/**
 * Test class for class {@link CommonsHashMap}.
 *
 * @author Philip Helger
 */
public final class CommonsHashMapTest
{
  @Test
  public void testSorted ()
  {
    final ICommonsMap <String, String> aTest = new CommonsHashMap <> ();
    aTest.put ("aaa", "bla");
    aTest.put ("bbb", "blb");
    aTest.put ("ccc", "blc");

    assertEquals ("aaa", aTest.getSortedByKey (Comparator.naturalOrder ()).keySet ().getAtIndex (0));
    assertEquals ("bbb", aTest.getSortedByKey (Comparator.naturalOrder ()).keySet ().getAtIndex (1));
    assertEquals ("ccc", aTest.getSortedByKey (Comparator.naturalOrder ()).keySet ().getAtIndex (2));
  }
}
