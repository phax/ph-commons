package com.helger.commons.collection.ext;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

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

    final ICommonsSet <String> aSortedKeys = aTest.getSortedByKey (Comparator.naturalOrder ()).copyOfKeySet ();
    assertEquals ("aaa", aSortedKeys.getAtIndex (0));
    assertEquals ("bbb", aSortedKeys.getAtIndex (1));
    assertEquals ("ccc", aSortedKeys.getAtIndex (2));

    CommonsTestHelper.testDefaultSerialization (aTest);
  }
}
