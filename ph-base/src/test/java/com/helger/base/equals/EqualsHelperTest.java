package com.helger.base.equals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;

/**
 * Test class for class {@link EqualsHelper}.
 *
 * @author Philip Helger
 */
public final class EqualsHelperTest
{
  @Test
  public void testEquals_Float ()
  {
    CommonsAssert.assertEquals (0f, -0f);
    CommonsAssert.assertEquals (1.1f, 1.1f);
    CommonsAssert.assertEquals (Float.NaN, Float.NaN);
    CommonsAssert.assertEquals (1f / 0f, Float.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (-1f / 0f, Float.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Float.MIN_VALUE, Float.MIN_VALUE);
    CommonsAssert.assertEquals (Float.MAX_VALUE, Float.MAX_VALUE);
  }

  @Test
  public void testEquals_Double ()
  {
    CommonsAssert.assertEquals (0d, -0d);
    CommonsAssert.assertEquals (1.1d, 1.1d);
    CommonsAssert.assertEquals (Double.NaN, Double.NaN);
    CommonsAssert.assertEquals (1d / 0d, Double.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (-1d / 0d, Double.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    CommonsAssert.assertEquals (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    CommonsAssert.assertEquals (Double.MIN_VALUE, Double.MIN_VALUE);
    CommonsAssert.assertEquals (Double.MAX_VALUE, Double.MAX_VALUE);
  }

  @Test
  public void testEqualsIgnoreCase ()
  {
    final String s1 = "s1";
    final String s2 = "S1";
    assertTrue (EqualsHelper.equalsIgnoreCase (s1, s1));
    assertTrue (EqualsHelper.equalsIgnoreCase (s1, s2));
    assertTrue (EqualsHelper.equalsIgnoreCase (s2, s1));
    assertFalse (EqualsHelper.equalsIgnoreCase (s1, null));
    assertFalse (EqualsHelper.equalsIgnoreCase (null, s2));
    assertTrue (EqualsHelper.equalsIgnoreCase (null, null));
  }

  @Test
  public void testEqualsCustom ()
  {
    final AtomicBoolean aPredCalled = new AtomicBoolean (false);
    final BiPredicate <String, String> aPredicate = (x, y) -> {
      aPredCalled.set (true);
      return true;
    };

    assertTrue (EqualsHelper.equalsCustom (null, null, aPredicate));
    assertFalse (aPredCalled.get ());

    assertFalse (EqualsHelper.equalsCustom ("a", null, aPredicate));
    assertFalse (aPredCalled.get ());

    assertFalse (EqualsHelper.equalsCustom (null, "a", aPredicate));
    assertFalse (aPredCalled.get ());

    // Predicate is only called here
    assertTrue (EqualsHelper.equalsCustom ("b", "a", aPredicate));
    assertTrue (aPredCalled.get ());
  }
}
