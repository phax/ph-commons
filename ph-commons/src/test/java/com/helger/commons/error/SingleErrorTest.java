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
package com.helger.commons.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.exception.mock.MockRuntimeException;
import com.helger.commons.location.SimpleLocation;

/**
 * Test class for class {@link SingleError}.
 *
 * @author Philip Helger
 */
public final class SingleErrorTest
{
  @Test
  public void testEmpty ()
  {
    final Locale aDisplayLocale = Locale.US;
    final SingleError aErr = SingleError.builder ().build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertNull (aErr.getErrorID ());
    assertNull (aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertFalse (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNull (aErr.getErrorTexts ());
    assertNull (aErr.getErrorText (aDisplayLocale));
    assertNull (aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertNull (aErr.getLinkedExceptionMessage ());
    assertNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error]", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testID ()
  {
    final Locale aDisplayLocale = Locale.US;
    final SingleError aErr = SingleError.builder ().setErrorID ("abc").build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertEquals ("abc", aErr.getErrorID ());
    assertNull (aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertFalse (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNull (aErr.getErrorTexts ());
    assertNull (aErr.getErrorText (aDisplayLocale));
    assertNull (aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertNull (aErr.getLinkedExceptionMessage ());
    assertNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] [abc]", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testFieldName ()
  {
    final Locale aDisplayLocale = Locale.US;
    final SingleError aErr = SingleError.builder ().setErrorFieldName ("abc").build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertNull (aErr.getErrorID ());
    assertEquals ("abc", aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertFalse (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNull (aErr.getErrorTexts ());
    assertNull (aErr.getErrorText (aDisplayLocale));
    assertNull (aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertNull (aErr.getLinkedExceptionMessage ());
    assertNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] in abc", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testLocation ()
  {
    final Locale aDisplayLocale = Locale.US;
    final SingleError aErr = SingleError.builder ().setErrorLocation (new SimpleLocation ("abc", 4, 17)).build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertNull (aErr.getErrorID ());
    assertNull (aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertTrue (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNull (aErr.getErrorTexts ());
    assertNull (aErr.getErrorText (aDisplayLocale));
    assertNull (aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertNull (aErr.getLinkedExceptionMessage ());
    assertNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] @ abc(4:17)", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testText ()
  {
    final Locale aDisplayLocale = Locale.US;
    final SingleError aErr = SingleError.builder ().setErrorText ("abc").build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertNull (aErr.getErrorID ());
    assertNull (aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertFalse (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNotNull (aErr.getErrorTexts ());
    assertEquals ("abc", aErr.getErrorText (aDisplayLocale));
    assertNull (aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertNull (aErr.getLinkedExceptionMessage ());
    assertNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] abc", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testLinkedException ()
  {
    final Locale aDisplayLocale = Locale.US;
    final MockRuntimeException ex = new MockRuntimeException ("msg");
    final SingleError aErr = SingleError.builder ().setLinkedException (ex).build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertNull (aErr.getErrorID ());
    assertNull (aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertFalse (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNull (aErr.getErrorTexts ());
    assertNull (aErr.getErrorText (aDisplayLocale));
    assertSame (ex, aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertEquals ("msg", aErr.getLinkedExceptionMessage ());
    assertNotNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] (" + ex.getClass ().getName () + ": msg)", aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }

  @Test
  public void testAll ()
  {
    final Locale aDisplayLocale = Locale.US;
    final MockRuntimeException ex = new MockRuntimeException ("exception msg");
    final SingleError aErr = SingleError.builder ()
                                        .setErrorID ("id1")
                                        .setErrorFieldName ("field")
                                        .setErrorLocation (new SimpleLocation ("file.xml", 4, 13))
                                        .setErrorText ("abc")
                                        .setLinkedException (ex)
                                        .build ();
    assertEquals (EErrorLevel.ERROR, aErr.getErrorLevel ());
    assertEquals ("id1", aErr.getErrorID ());
    assertEquals ("field", aErr.getErrorFieldName ());
    assertNotNull (aErr.getErrorLocation ());
    assertTrue (aErr.getErrorLocation ().isAnyInformationPresent ());
    assertNotNull (aErr.getErrorTexts ());
    assertEquals ("abc", aErr.getErrorText (aDisplayLocale));
    assertSame (ex, aErr.getLinkedException ());
    assertNull (aErr.getLinkedExceptionCause ());
    assertEquals ("exception msg", aErr.getLinkedExceptionMessage ());
    assertNotNull (aErr.getLinkedExceptionStackTrace ());
    assertEquals ("[error] [id1] in field @ file.xml(4:13) abc (" +
                  ex.getClass ().getName () +
                  ": exception msg)",
                  aErr.getAsString (aDisplayLocale));
    assertEquals (aErr, SingleError.builder (aErr).build ());
  }
}
