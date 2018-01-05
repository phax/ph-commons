/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
/**
d * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.error.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.error.SingleError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.exception.mock.MockIOException;
import com.helger.commons.location.SimpleLocation;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ErrorList}.
 *
 * @author Philip Helger
 */
public final class ErrorListTest
{
  @Test
  public void testBasicGlobal ()
  {
    final ErrorList aList = new ErrorList ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    assertFalse (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (0, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (0, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertFalse (aList.hasEntryForField (null));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (0, aList.getGroupedByID ().size ());
    assertEquals (0, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderInfo ().setErrorText ("TestInfo").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertFalse (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.INFO, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (1, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (1, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertTrue (aList.hasEntryForField (null));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (1, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderError ().setErrorText ("TestError").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (2, aList.size ());
    assertTrue (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.ERROR, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (2, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (2, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertTrue (aList.hasEntryForField (null));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (1, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderWarn ().setErrorText ("TestWarning").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (3, aList.size ());
    assertTrue (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.ERROR, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (3, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (3, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (3, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertTrue (aList.hasEntryForField (null));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (1, aList.getGroupedByFieldName ().size ());

    CommonsTestHelper.testDefaultSerialization (aList);
  }

  @Test
  public void testBasicField ()
  {
    final ErrorList aList = new ErrorList ();
    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    assertFalse (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (0, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (0, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertFalse (aList.hasEntryForField ("field2"));
    assertFalse (aList.hasEntryForField (null));
    assertFalse (aList.hasEntryForField ("field1", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField ("field2", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField (null, EErrorLevel.ERROR));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (0, aList.getGroupedByID ().size ());
    assertEquals (0, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderInfo ().setErrorFieldName ("field1").setErrorText ("TestInfo").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (1, aList.size ());
    assertFalse (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.INFO, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (1, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertTrue (aList.hasEntryForField ("field1"));
    assertFalse (aList.hasEntryForField ("field2"));
    assertFalse (aList.hasEntryForField (null));
    assertFalse (aList.hasEntryForField ("field1", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField ("field2", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField (null, EErrorLevel.ERROR));
    assertEquals (1, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (1, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (1, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (1, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (1, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderError ().setErrorFieldName ("field2").setErrorText ("TestError").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (2, aList.size ());
    assertTrue (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.ERROR, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (2, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertTrue (aList.hasEntryForField ("field1"));
    assertTrue (aList.hasEntryForField ("field2"));
    assertFalse (aList.hasEntryForField (null));
    assertFalse (aList.hasEntryForField ("field1", EErrorLevel.ERROR));
    assertTrue (aList.hasEntryForField ("field2", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField (null, EErrorLevel.ERROR));
    assertEquals (1, aList.getListOfField ("field1").size ());
    assertEquals (1, aList.getListOfField ("field2").size ());
    assertEquals (2, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (2, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (2, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (2, aList.getGroupedByFieldName ().size ());

    aList.add (SingleError.builderWarn ().setErrorFieldName ("field1").setErrorText ("TestWarning").build ());
    assertFalse (aList.isEmpty ());
    assertEquals (3, aList.size ());
    assertTrue (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.ERROR, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (3, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (3, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertTrue (aList.hasEntryForField ("field1"));
    assertTrue (aList.hasEntryForField ("field2"));
    assertFalse (aList.hasEntryForField (null));
    assertFalse (aList.hasEntryForField ("field1", EErrorLevel.ERROR));
    assertTrue (aList.hasEntryForField ("field2", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField (null, EErrorLevel.ERROR));
    assertEquals (2, aList.getListOfField ("field1").size ());
    assertEquals (1, aList.getListOfField ("field2").size ());
    assertEquals (3, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (3, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (3, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (1, aList.getGroupedByID ().size ());
    assertEquals (2, aList.getGroupedByFieldName ().size ());

    CommonsTestHelper.testDefaultSerialization (aList);

    // Clear list
    assertTrue (aList.removeAll ().isChanged ());
    assertFalse (aList.removeAll ().isChanged ());

    assertTrue (aList.isEmpty ());
    assertEquals (0, aList.size ());
    assertFalse (aList.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aList.getMostSevereErrorLevel ());
    assertNotNull (aList);
    assertEquals (0, aList.size ());
    assertNotNull (aList.getAllTexts (Locale.US));
    assertEquals (0, aList.getAllTexts (Locale.US).size ());
    assertNotNull (aList.getListWithoutField ());
    assertEquals (0, aList.getListWithoutField ().size ());
    assertFalse (aList.hasEntryForField ("field1"));
    assertFalse (aList.hasEntryForField ("field2"));
    assertFalse (aList.hasEntryForField (null));
    assertFalse (aList.hasEntryForField ("field1", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField ("field2", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField (null, EErrorLevel.ERROR));
    assertEquals (0, aList.getListOfField ("field1").size ());
    assertEquals (0, aList.getListOfField ("field2").size ());
    assertEquals (0, aList.getListOfFields ("field1", "field2").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ("field").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("field\\d").size ());
    assertEquals (0, aList.getGroupedByID ().size ());
    assertEquals (0, aList.getGroupedByFieldName ().size ());
  }

  @Test
  public void testSerialize ()
  {
    final ErrorList aList = new ErrorList ();
    CommonsTestHelper.testDefaultSerialization (aList);

    aList.add (SingleError.builderInfo ()
                          .setErrorID ("test-1")
                          .setErrorFieldName ("field1")
                          .setErrorText ("TestInfo")
                          .setErrorLocation (new SimpleLocation ("here.xml", 17, 3))
                          .build ());
    CommonsTestHelper.testDefaultSerialization (aList);

    aList.add (SingleError.builderInfo ()
                          .setErrorID ("test-2")
                          .setErrorFieldName ("field1")
                          .setErrorText ("TestInfo")
                          .setErrorLocation (new SimpleLocation ("here.xml", 17, 3))
                          .setLinkedException (new MockIOException ("Mock"))
                          .build ());
    CommonsTestHelper.testDefaultSerialization (aList);
  }

  @Test
  public void testField ()
  {
    final ErrorList aFEL = new ErrorList ();
    assertTrue (aFEL.isEmpty ());
    assertFalse (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aFEL.getMostSevereErrorLevel ());

    aFEL.add (SingleError.builderInfo ().setErrorFieldName ("f1").setErrorText ("info").build ());
    assertFalse (aFEL.isEmpty ());
    assertFalse (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.INFO, aFEL.getMostSevereErrorLevel ());

    aFEL.add (SingleError.builderError ().setErrorFieldName ("f2").setErrorText ("error").build ());
    assertFalse (aFEL.isEmpty ());
    assertTrue (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.ERROR, aFEL.getMostSevereErrorLevel ());

    assertNotNull (aFEL.getListOfField ("f1"));
    assertFalse (aFEL.getListOfField ("f1").isEmpty ());
    assertNotNull (aFEL.getListOfField ("f1-gibtsned"));
    assertTrue (aFEL.getListOfField ("f1-gibtsned").isEmpty ());

    assertTrue (aFEL.removeAll ().isChanged ());
    assertFalse (aFEL.removeAll ().isChanged ());

    assertTrue (aFEL.isEmpty ());
    assertFalse (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aFEL.getMostSevereErrorLevel ());
  }
}
