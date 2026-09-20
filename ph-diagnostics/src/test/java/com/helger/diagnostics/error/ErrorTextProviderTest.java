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
package com.helger.diagnostics.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.Test;

import com.helger.base.location.SimpleLocation;
import com.helger.diagnostics.error.ErrorTextProvider.EField;
import com.helger.diagnostics.error.ErrorTextProvider.FormattableItem;
import com.helger.diagnostics.error.level.EErrorLevel;

/**
 * Test class for class {@link ErrorTextProvider}.
 *
 * @author Philip Helger
 */
public final class ErrorTextProviderTest
{
  private static final Locale L = Locale.US;

  private static ErrorTextProvider _createWithAllFields ()
  {
    return new ErrorTextProvider ().addItem (EField.CONSTANT, "C")
                                   .addItem (EField.ERROR_DATETIME, "[$]")
                                   .addItem (EField.ERROR_LEVEL, "[$]")
                                   .addItem (EField.ERROR_ID, "[$]")
                                   .addItem (EField.ERROR_FIELD_NAME, "in [$]")
                                   .addItem (EField.ERROR_LOCATION, "@ $")
                                   .addItem (EField.ERROR_TEXT, "$")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_CLASS, "($:")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_MESSAGE, "$)")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_STACK_TRACE, "st[$]")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_CAUSE_CLASS, "cc[$]")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_CAUSE_MESSAGE, "cm[$]")
                                   .addItem (EField.ERROR_LINKED_EXCEPTION_CAUSE_STACK_TRACE, "cst[$]");
  }

  @Test
  public void testEField ()
  {
    for (final EField eField : EField.values ())
    {
      assertNotNull (eField.getID ());
      assertSame (eField, EField.getFromIDOrNull (eField.getID ()));
      assertEquals (Boolean.valueOf (eField != EField.CONSTANT), Boolean.valueOf (eField.isPlaceholderRequired ()));
    }
    assertNull (EField.getFromIDOrNull (null));
    assertNull (EField.getFromIDOrNull ("bla-foo-fasel"));
  }

  @Test
  public void testFormattableItem ()
  {
    final FormattableItem aItem = new FormattableItem (EField.ERROR_TEXT, "[$]");
    assertSame (EField.ERROR_TEXT, aItem.getField ());
    assertEquals ("[$]", aItem.getUnformattedText ());
    assertEquals ("[abc]", aItem.getFormattedText ("abc"));
    assertNotNull (aItem.toString ());

    // Constants need no placeholder
    assertEquals ("abc", new FormattableItem (EField.CONSTANT, "abc").getUnformattedText ());

    try
    {
      // Placeholder is missing
      new FormattableItem (EField.ERROR_TEXT, "abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new FormattableItem (null, "[$]");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new FormattableItem (EField.ERROR_TEXT, "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testBasic ()
  {
    final ErrorTextProvider aETP = new ErrorTextProvider ();
    assertEquals (" ", aETP.getFieldSeparator ());
    assertTrue (aETP.getAllItems ().isEmpty ());

    assertSame (aETP, aETP.setFieldSeparator ("-"));
    assertEquals ("-", aETP.getFieldSeparator ());

    assertSame (aETP, aETP.addItem (EField.ERROR_TEXT, "$"));
    assertEquals (1, aETP.getAllItems ().size ());
    assertSame (aETP, aETP.addItem (new FormattableItem (EField.ERROR_ID, "[$]")));
    assertEquals (2, aETP.getAllItems ().size ());
    assertNotNull (aETP.toString ());

    final ErrorTextProvider aClone = aETP.getClone ();
    assertNotSame (aETP, aClone);
    assertEquals (aETP.getFieldSeparator (), aClone.getFieldSeparator ());
    assertEquals (aETP.getAllItems (), aClone.getAllItems ());

    // Modifying the clone does not modify the original
    aClone.addItem (EField.CONSTANT, "x");
    assertEquals (2, aETP.getAllItems ().size ());
    assertEquals (3, aClone.getAllItems ().size ());
  }

  @Test
  public void testDefaultProvider ()
  {
    final IError aError = SingleError.builderWarn ().errorID ("id1").errorText ("Error text").build ();
    assertEquals ("[warn] [id1] Error text", ErrorTextProvider.DEFAULT.getErrorText (aError, L));
  }

  @Test
  public void testAllFieldsPresent ()
  {
    final Exception aCause = new IllegalStateException ("Cause message");
    final Exception aEx = new IllegalArgumentException ("Outer message", aCause);
    final LocalDateTime aDT = LocalDateTime.of (2020, 1, 2, 3, 4, 5);
    final IError aError = SingleError.builder ()
                                     .dateTime (aDT)
                                     .errorLevel (EErrorLevel.WARN)
                                     .errorID ("id1")
                                     .errorFieldName ("field1")
                                     .errorLocation (new SimpleLocation ("res", 4, 17))
                                     .errorText ("Error text")
                                     .linkedException (aEx)
                                     .build ();

    final String sText = _createWithAllFields ().getErrorText (aError, L);
    assertTrue (sText,
                sText.startsWith ("C [" + aDT.toString () + "] [warn] [id1] in [field1] @ res(4:17) Error text"));
    assertTrue (sText, sText.contains ("(" + IllegalArgumentException.class.getName () + ":"));
    assertTrue (sText, sText.contains ("Outer message)"));
    assertTrue (sText, sText.contains ("cc[" + IllegalStateException.class.getName () + "]"));
    assertTrue (sText, sText.contains ("cm[Cause message]"));
    assertTrue (sText, sText.contains ("st["));
    assertTrue (sText, sText.contains ("cst["));
  }

  @Test
  public void testAllFieldsAbsent ()
  {
    // Neither date time, ID, field name, location, text nor exception
    final IError aError = SingleError.builder ().build ();
    assertEquals ("C [error]", _createWithAllFields ().getErrorText (aError, L));
  }

  @Test
  public void testExceptionWithoutCause ()
  {
    final Exception aEx = new IllegalArgumentException ();
    final IError aError = SingleError.builder ().linkedException (aEx).build ();
    final String sText = _createWithAllFields ().getErrorText (aError, L);
    // Message is null - an empty String is used instead
    assertTrue (sText, sText.contains ("(" + IllegalArgumentException.class.getName () + ": )"));
    // No cause present
    assertFalse (sText, sText.contains ("cc["));
    assertFalse (sText, sText.contains ("cm["));
    assertFalse (sText, sText.contains ("cst["));
  }

  @Test
  public void testFieldSeparatorIsUsed ()
  {
    final IError aError = SingleError.builderInfo ().errorID ("id1").build ();
    final ErrorTextProvider aETP = new ErrorTextProvider ().addItem (EField.ERROR_LEVEL, "[$]")
                                                           .addItem (EField.ERROR_ID, "[$]")
                                                           .setFieldSeparator ("::");
    assertEquals ("[info]::[id1]", aETP.getErrorText (aError, L));
  }
}
