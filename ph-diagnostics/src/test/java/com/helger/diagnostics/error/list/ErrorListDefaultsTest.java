/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.level.EErrorLevel;

/**
 * Test class for the default methods of {@link IErrorList} and
 * {@link com.helger.diagnostics.error.level.IHasErrorLevels}.
 *
 * @author Philip Helger
 */
public final class ErrorListDefaultsTest
{
  private static final Locale L = Locale.US;

  @SuppressWarnings ("removal")
  @NonNull
  private static IErrorList _createList ()
  {
    return new ErrorList (SingleError.builderSuccess ().errorID ("id1").errorFieldName ("f1").errorText ("s").build (),
                          SingleError.builderInfo ().errorID ("id2").errorFieldName ("f2").errorText ("i").build (),
                          SingleError.builderWarn ().errorID ("id2").errorFieldName ("bar").errorText ("w").build (),
                          SingleError.builderError ().errorText ("e").build ());
  }

  @SuppressWarnings ("removal")
  @Test
  public void testHasEntryForField ()
  {
    final IErrorList aList = _createList ();

    assertTrue (aList.hasEntryForField ("f1"));
    assertFalse (aList.hasEntryForField ("nonexisting"));
    assertFalse (aList.hasNoEntryForField ("f1"));
    assertTrue (aList.hasNoEntryForField ("nonexisting"));

    assertTrue (aList.hasEntryForField ("f1", EErrorLevel.SUCCESS));
    assertFalse (aList.hasEntryForField ("f1", EErrorLevel.ERROR));
    assertFalse (aList.hasEntryForField ("f1", null));

    assertFalse (aList.hasErrorForField ("f1"));
    assertTrue (aList.hasErrorForField (null));

    assertTrue (aList.hasEntryForFields ("nonexisting", "f2"));
    assertFalse (aList.hasEntryForFields ("nonexisting"));
    assertFalse (aList.hasEntryForFields ((String []) null));

    assertFalse (aList.hasNoEntryForFields ("nonexisting", "f2"));
    assertTrue (aList.hasNoEntryForFields ("nonexisting"));
    assertTrue (aList.hasNoEntryForFields ((String []) null));
  }

  @Test
  public void testGetListOfFields ()
  {
    final IErrorList aList = _createList ();

    assertEquals (1, aList.getListOfField ("f1").size ());
    assertEquals (0, aList.getListOfField ("nonexisting").size ());

    assertEquals (2, aList.getListOfFields ("f1", "f2").size ());
    assertEquals (0, aList.getListOfFields (new String [0]).size ());
    assertEquals (0, aList.getListOfFields ((String []) null).size ());

    assertEquals (2, aList.getListOfFields (new CommonsArrayList <> ("f1", "f2")).size ());
    assertEquals (0, aList.getListOfFields (new CommonsArrayList <> ()).size ());
    assertEquals (0, aList.getListOfFields ((CommonsArrayList <String>) null).size ());

    assertEquals (2, aList.getListOfFieldsStartingWith ("f").size ());
    assertEquals (3, aList.getListOfFieldsStartingWith ("f", "b").size ());
    assertEquals (0, aList.getListOfFieldsStartingWith (new String [0]).size ());
    assertEquals (0, aList.getListOfFieldsStartingWith ((String []) null).size ());

    assertEquals (2, aList.getListOfFieldsRegExp ("f[0-9]").size ());
    assertEquals (0, aList.getListOfFieldsRegExp ("x[0-9]").size ());

    // The error without a field name
    assertEquals (1, aList.getListWithoutField ().size ());
  }

  @Test
  public void testGetSubLists ()
  {
    final IErrorList aList = _createList ();

    // Everything except the "success"
    assertEquals (3, aList.getAllFailures ().size ());
    assertEquals (1, aList.getAllErrors ().size ());
  }

  @Test
  public void testGetAllTexts ()
  {
    final IErrorList aList = _createList ();

    assertEquals (new CommonsArrayList <> ("s", "i", "w", "e"), aList.getAllTexts (L));
    assertEquals (new CommonsArrayList <> ("id1", "id2", "id2", null), aList.getAllDataItems (IError::getErrorID));

    assertEquals (3, aList.getGroupedByID ().size ());
    assertEquals (2, aList.getGroupedByID ().get ("id2").size ());
    assertEquals (4, aList.getGroupedByFieldName ().size ());
    assertEquals (1, aList.getGrouped (x -> x.getErrorLevel ().isError () ? "error" : "other").get ("error").size ());
  }

  @Test
  public void testErrorLevels ()
  {
    final IErrorList aList = _createList ();

    assertFalse (aList.containsOnlySuccess ());
    assertTrue (aList.containsAtLeastOneSuccess ());
    assertFalse (aList.containsNoSuccess ());
    assertEquals (1, aList.getSuccessCount ());

    assertFalse (aList.containsOnlyFailure ());
    assertTrue (aList.containsAtLeastOneFailure ());
    assertFalse (aList.containsNoFailure ());
    assertEquals (3, aList.getFailureCount ());

    assertFalse (aList.containsOnlyError ());
    assertTrue (aList.containsAtLeastOneError ());
    assertFalse (aList.containsNoError ());
    assertEquals (1, aList.getErrorCount ());

    assertTrue (aList.containsAtLeastOneWarningOrError ());
    assertSame (EErrorLevel.ERROR, aList.getMostSevereErrorLevel ());

    // Empty list - "containsOnly" is always false for an empty collection
    final IErrorList aEmpty = new ErrorList ();
    assertFalse (aEmpty.containsOnlySuccess ());
    assertFalse (aEmpty.containsAtLeastOneSuccess ());
    assertTrue (aEmpty.containsNoSuccess ());
    assertFalse (aEmpty.containsOnlyFailure ());
    assertTrue (aEmpty.containsNoFailure ());
    assertFalse (aEmpty.containsOnlyError ());
    assertTrue (aEmpty.containsNoError ());
    assertFalse (aEmpty.containsAtLeastOneWarningOrError ());
    assertSame (EErrorLevel.LOWEST, aEmpty.getMostSevereErrorLevel ());

    // Only successes
    @SuppressWarnings ("removal")
    final IErrorList aSuccess = new ErrorList (SingleError.builderSuccess ().errorText ("s").build ());
    assertTrue (aSuccess.containsOnlySuccess ());
    assertTrue (aSuccess.containsAtLeastOneSuccess ());
    assertFalse (aSuccess.containsNoSuccess ());
    assertFalse (aSuccess.containsOnlyFailure ());
    assertTrue (aSuccess.containsNoFailure ());

    // The highest level breaks the loop
    final IErrorList aFatal = new ErrorList (SingleError.builder ()
                                                        .errorLevel (EErrorLevel.FATAL_ERROR)
                                                        .errorText ("f")
                                                        .build (), SingleError.builderInfo ().errorText ("i").build ());
    assertSame (EErrorLevel.FATAL_ERROR, aFatal.getMostSevereErrorLevel ());
  }

  @Test
  public void testComparable ()
  {
    final IError aInfo = SingleError.builderInfo ().errorText ("i").build ();
    final IError aError = SingleError.builderError ().errorText ("e").build ();

    assertTrue (aInfo.isLT (aError));
    assertTrue (aInfo.isLE (aError));
    assertFalse (aInfo.isGT (aError));
    assertFalse (aInfo.isGE (aError));
    assertFalse (aInfo.isEQ (aError));
    assertTrue (aInfo.isNE (aError));
    assertTrue (aInfo.compareTo (aError) < 0);

    final IError aInfo2 = SingleError.builderInfo ().errorText ("i2").build ();
    assertTrue (aInfo.isEQ (aInfo2));
    assertTrue (aInfo.isGE (aInfo2));
    assertTrue (aInfo.isLE (aInfo2));
    assertEquals (0, aInfo.compareTo (aInfo2));
  }
}
