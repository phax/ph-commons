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
package com.helger.commons.state;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EMandatory}.
 *
 * @author Philip Helger
 */
public final class EMandatoryTest
{
  @Test
  public void testAll ()
  {
    for (final EMandatory e : EMandatory.values ())
      assertSame (e, EMandatory.valueOf (e.name ()));

    assertTrue (EMandatory.MANDATORY.isMandatory ());
    assertFalse (EMandatory.MANDATORY.isOptional ());
    assertFalse (EMandatory.OPTIONAL.isMandatory ());
    assertTrue (EMandatory.OPTIONAL.isOptional ());
    assertSame (EMandatory.MANDATORY, EMandatory.valueOf (true));
    assertSame (EMandatory.OPTIONAL, EMandatory.valueOf (false));
    assertSame (EMandatory.MANDATORY, EMandatory.MANDATORY.or (EMandatory.OPTIONAL));
    assertSame (EMandatory.MANDATORY, EMandatory.MANDATORY.or (EMandatory.MANDATORY));
    assertSame (EMandatory.OPTIONAL, EMandatory.MANDATORY.and (EMandatory.OPTIONAL));
    assertSame (EMandatory.MANDATORY, EMandatory.MANDATORY.and (EMandatory.MANDATORY));
    assertSame (EMandatory.OPTIONAL, EMandatory.OPTIONAL.or (EMandatory.OPTIONAL));
    assertSame (EMandatory.MANDATORY, EMandatory.OPTIONAL.or (EMandatory.MANDATORY));
    assertSame (EMandatory.OPTIONAL, EMandatory.OPTIONAL.and (EMandatory.OPTIONAL));
    assertSame (EMandatory.OPTIONAL, EMandatory.OPTIONAL.and (EMandatory.MANDATORY));
  }
}
