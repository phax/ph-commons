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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collection;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.id.IHasID;
import com.helger.commons.type.IHasObjectType;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.TypedObject;

/**
 * Test class for class {@link ClassHierarchyCache}.
 *
 * @author Philip Helger
 */
public final class ClassHierarchyCacheTest
{
  @Test
  public void testGetClassHierarchy ()
  {
    // Very basic class
    Collection <Class <?>> aHierarchy = ClassHierarchyCache.getClassHierarchy (Object.class);
    assertEquals (1, aHierarchy.size ());
    assertTrue (aHierarchy.contains (Object.class));

    // More sophisticated static class (no interfaces)
    aHierarchy = ClassHierarchyCache.getClassHierarchy (CGlobal.class);
    // Is usually 2, but with Cobertura enabled, it is 3!
    assertTrue (aHierarchy.size () >= 2);
    assertTrue (aHierarchy.contains (CGlobal.class));
    assertTrue (aHierarchy.contains (Object.class));

    // More sophisticated static class (with interfaces)
    aHierarchy = ClassHierarchyCache.getClassHierarchy (TypedObject.class);
    assertTrue (aHierarchy.size () >= 6);
    assertTrue (aHierarchy.contains (TypedObject.class));
    assertTrue (aHierarchy.contains (IHasObjectType.class));
    assertTrue (aHierarchy.contains (ITypedObject.class));
    assertTrue (aHierarchy.contains (IHasID.class));
    assertTrue (aHierarchy.contains (Object.class));
    assertTrue (aHierarchy.contains (Serializable.class));

    try
    {
      ClassHierarchyCache.getClassHierarchy (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
