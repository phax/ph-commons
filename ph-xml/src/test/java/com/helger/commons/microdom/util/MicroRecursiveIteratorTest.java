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
package com.helger.commons.microdom.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.serialize.MicroReader;

/**
 * Test class for class {@link MicroRecursiveIterator}.
 *
 * @author Philip Helger
 */
public final class MicroRecursiveIteratorTest
{
  @Test
  public void testConvertToMicroNode ()
  {
    final String sXML = "<?xml version='1.0'?><root attr='value'>text<child xmlns='http://myns'/><!-- comment --><?stylesheet x y z?></root>";
    final IMicroDocument aDoc = MicroReader.readMicroXML (sXML);
    assertNotNull (aDoc);
    final MicroRecursiveIterator it = new MicroRecursiveIterator (aDoc);
    for (final IMicroNode aNode : it)
      assertNotNull (aNode);

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    try
    {
      new MicroRecursiveIterator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
