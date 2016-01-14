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
package com.helger.commons.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ResourceErrorGroup}.
 *
 * @author Philip Helger
 */
public final class ResourceErrorGroupTest
{
  @Test
  public void testCtor ()
  {
    final IResourceLocation loc = new ResourceLocation ("res", null);

    ResourceErrorGroup aREG = new ResourceErrorGroup ();
    assertTrue (aREG.isEmpty ());
    assertEquals (0, aREG.getSize ());

    aREG = new ResourceErrorGroup (new ResourceError (loc, EErrorLevel.ERROR, "mock error"));
    assertFalse (aREG.isEmpty ());
    assertEquals (1, aREG.getSize ());

    aREG = new ResourceErrorGroup (new ResourceError (loc, EErrorLevel.ERROR, "mock error"),
                                   new ResourceError (loc, EErrorLevel.WARN, "mock msg"));
    assertFalse (aREG.isEmpty ());
    assertEquals (2, aREG.getSize ());

    aREG = new ResourceErrorGroup (CollectionHelper.newList (new ResourceError (loc, EErrorLevel.ERROR, "mock error"),
                                                             new ResourceError (loc, EErrorLevel.WARN, "mock msg")));
    assertFalse (aREG.isEmpty ());
    assertEquals (2, aREG.getSize ());

    try
    {
      new ResourceErrorGroup ((IResourceError []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new ResourceErrorGroup ((Iterable <IResourceError>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      aREG.addResourceError (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      aREG.addResourceErrorGroup (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testBasic ()
  {
    final IResourceLocation loc = new ResourceLocation ("res", null);

    ResourceErrorGroup aREG = new ResourceErrorGroup ();
    assertTrue (aREG.isEmpty ());
    assertEquals (0, aREG.getSize ());
    assertFalse (aREG.containsOnlySuccess ());
    assertFalse (aREG.containsOnlyFailure ());
    assertFalse (aREG.containsOnlyError ());
    assertFalse (aREG.containsAtLeastOneSuccess ());
    assertFalse (aREG.containsAtLeastOneFailure ());
    assertFalse (aREG.containsAtLeastOneError ());
    assertTrue (aREG.containsNoSuccess ());
    assertTrue (aREG.containsNoFailure ());
    assertTrue (aREG.containsNoError ());
    assertEquals (EErrorLevel.SUCCESS, aREG.getMostSevereErrorLevel ());
    assertNotNull (aREG.getAllFailures ());
    assertEquals (0, aREG.getAllFailures ().getSize ());

    aREG.addResourceError (new ResourceError (loc, EErrorLevel.WARN, "mock msg"));
    assertFalse (aREG.isEmpty ());
    assertEquals (1, aREG.getSize ());
    assertFalse (aREG.containsOnlySuccess ());
    assertTrue (aREG.containsOnlyFailure ());
    assertFalse (aREG.containsOnlyError ());
    assertFalse (aREG.containsAtLeastOneSuccess ());
    assertTrue (aREG.containsAtLeastOneFailure ());
    assertFalse (aREG.containsAtLeastOneError ());
    assertTrue (aREG.containsNoSuccess ());
    assertFalse (aREG.containsNoFailure ());
    assertTrue (aREG.containsNoError ());
    assertEquals (EErrorLevel.WARN, aREG.getMostSevereErrorLevel ());
    assertNotNull (aREG.getAllFailures ());
    assertEquals (1, aREG.getAllFailures ().getSize ());

    aREG.addResourceError (new ResourceError (loc, EErrorLevel.SUCCESS, "successfully"));
    assertFalse (aREG.isEmpty ());
    assertEquals (2, aREG.getSize ());
    assertFalse (aREG.containsOnlySuccess ());
    assertFalse (aREG.containsOnlyFailure ());
    assertFalse (aREG.containsOnlyError ());
    assertTrue (aREG.containsAtLeastOneSuccess ());
    assertTrue (aREG.containsAtLeastOneFailure ());
    assertFalse (aREG.containsAtLeastOneError ());
    assertFalse (aREG.containsNoSuccess ());
    assertFalse (aREG.containsNoFailure ());
    assertTrue (aREG.containsNoError ());
    assertEquals (EErrorLevel.WARN, aREG.getMostSevereErrorLevel ());
    assertNotNull (aREG.getAllFailures ());
    assertEquals (1, aREG.getAllFailures ().getSize ());
    assertEquals (0, aREG.getAllErrors ().getSize ());

    aREG.addResourceError (new ResourceError (loc, EErrorLevel.ERROR, "mock error"));
    assertFalse (aREG.isEmpty ());
    assertEquals (3, aREG.getSize ());
    assertFalse (aREG.containsOnlySuccess ());
    assertFalse (aREG.containsOnlyFailure ());
    assertFalse (aREG.containsOnlyError ());
    assertTrue (aREG.containsAtLeastOneSuccess ());
    assertTrue (aREG.containsAtLeastOneFailure ());
    assertTrue (aREG.containsAtLeastOneError ());
    assertFalse (aREG.containsNoSuccess ());
    assertFalse (aREG.containsNoFailure ());
    assertFalse (aREG.containsNoError ());
    assertEquals (EErrorLevel.ERROR, aREG.getMostSevereErrorLevel ());
    assertNotNull (aREG.getAllFailures ());
    assertEquals (2, aREG.getAllFailures ().getSize ());
    assertEquals (1, aREG.getAllErrors ().getSize ());

    // Success only
    aREG = new ResourceErrorGroup (new ResourceError (loc, EErrorLevel.SUCCESS, "mock success"));
    assertTrue (aREG.containsOnlySuccess ());
    assertFalse (aREG.containsOnlyFailure ());
    assertFalse (aREG.containsOnlyError ());
    assertTrue (aREG.containsAtLeastOneSuccess ());
    assertFalse (aREG.containsAtLeastOneFailure ());
    assertFalse (aREG.containsAtLeastOneError ());
    assertFalse (aREG.containsNoSuccess ());
    assertTrue (aREG.containsNoFailure ());
    assertTrue (aREG.containsNoError ());

    // Error only
    aREG = new ResourceErrorGroup (new ResourceError (loc, EErrorLevel.ERROR, "mock error"));
    assertFalse (aREG.containsOnlySuccess ());
    assertTrue (aREG.containsOnlyFailure ());
    assertTrue (aREG.containsOnlyError ());
    assertFalse (aREG.containsAtLeastOneSuccess ());
    assertTrue (aREG.containsAtLeastOneFailure ());
    assertTrue (aREG.containsAtLeastOneError ());
    assertTrue (aREG.containsNoSuccess ());
    assertFalse (aREG.containsNoFailure ());
    assertFalse (aREG.containsNoError ());

    final ResourceErrorGroup aREG2 = new ResourceErrorGroup ();
    aREG2.addResourceErrorGroup (aREG);
    assertFalse (aREG2.containsOnlySuccess ());
    assertTrue (aREG2.containsOnlyFailure ());
    assertTrue (aREG2.containsOnlyError ());
    assertFalse (aREG2.containsAtLeastOneSuccess ());
    assertTrue (aREG2.containsAtLeastOneFailure ());
    assertTrue (aREG2.containsAtLeastOneError ());
    assertTrue (aREG.containsNoSuccess ());
    assertFalse (aREG.containsNoFailure ());
    assertFalse (aREG.containsNoError ());

    assertEquals (1, CollectionHelper.newList (aREG2).size ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ResourceErrorGroup (),
                                                                       new ResourceErrorGroup ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceErrorGroup (),
                                                                           new ResourceErrorGroup (new ResourceError (loc,
                                                                                                                      EErrorLevel.ERROR,
                                                                                                                      "mock error"),
                                                                                                   new ResourceError (loc,
                                                                                                                      EErrorLevel.WARN,
                                                                                                                      "mock msg")));
    CommonsTestHelper.testGetClone (new ResourceErrorGroup (new ResourceError (loc, EErrorLevel.ERROR, "mock error"),
                                                            new ResourceError (loc, EErrorLevel.WARN, "mock msg")));
  }
}
