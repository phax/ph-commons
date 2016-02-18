package com.helger.commons.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.collection.impl.NonBlockingStack;

/**
 * Test class for class {@link StackHelper}
 *
 * @author Philip Helger
 */
public final class StackHelperTest
{
  @Test
  public void testNewStackArray ()
  {
    final NonBlockingStack <String> aStack = StackHelper.newStack ("Hallo", "Welt");
    assertEquals (StackHelper.getStackCopyWithoutTop (aStack), StackHelper.newStack ("Hallo"));
    assertNotNull (aStack);
    assertEquals (aStack.size (), 2);
    assertTrue (aStack.contains ("Welt"));
    assertTrue (aStack.contains ("Hallo"));
    assertEquals ("Welt", aStack.peek ());
    assertEquals ("Welt", aStack.pop ());
    assertEquals ("Hallo", aStack.peek ());
    assertEquals ("Hallo", aStack.pop ());
    assertTrue (aStack.isEmpty ());

    assertNull (StackHelper.getStackCopyWithoutTop (new NonBlockingStack <String> ()));
  }

}
