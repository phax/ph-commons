/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.diagnostics.error.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.level.EErrorLevel;

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

    aList.add (SingleError.builderInfo ().errorText ("TestInfo").build ());
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

    aList.add (SingleError.builderError ().errorText ("TestError").build ());
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

    aList.add (SingleError.builderWarn ().errorText ("TestWarning").build ());
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

    aList.add (SingleError.builderInfo ().errorFieldName ("field1").errorText ("TestInfo").build ());
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

    aList.add (SingleError.builderError ().errorFieldName ("field2").errorText ("TestError").build ());
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

    aList.add (SingleError.builderWarn ().errorFieldName ("field1").errorText ("TestWarning").build ());
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
  public void testField ()
  {
    final ErrorList aFEL = new ErrorList ();
    assertTrue (aFEL.isEmpty ());
    assertFalse (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.SUCCESS, aFEL.getMostSevereErrorLevel ());

    aFEL.add (SingleError.builderInfo ().errorFieldName ("f1").errorText ("info").build ());
    assertFalse (aFEL.isEmpty ());
    assertFalse (aFEL.containsAtLeastOneWarningOrError ());
    assertEquals (EErrorLevel.INFO, aFEL.getMostSevereErrorLevel ());

    aFEL.add (SingleError.builderError ().errorFieldName ("f2").errorText ("error").build ());
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
