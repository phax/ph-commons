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
package com.helger.jaxb.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.xml.XMLFactory;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;
import jakarta.xml.bind.helpers.ValidationEventImpl;
import jakarta.xml.bind.helpers.ValidationEventLocatorImpl;

/**
 * Test class for the {@link AbstractValidationEventHandler} implementations.
 *
 * @author Philip Helger
 */
public final class ValidationEventHandlerFuncTest
{
  private static ValidationEvent _event (final int nSeverity, final String sMsg, final Throwable t)
  {
    return new ValidationEventImpl (nSeverity, sMsg, null, t);
  }

  @Test
  public void testErrorLevels ()
  {
    final CollectingValidationEventHandler aHdl = new CollectingValidationEventHandler ();

    assertTrue (aHdl.handleEvent (_event (ValidationEvent.WARNING, "warning", null)));
    assertTrue (aHdl.handleEvent (_event (ValidationEvent.ERROR, "error", null)));
    // A fatal error stops the processing
    assertFalse (aHdl.handleEvent (_event (ValidationEvent.FATAL_ERROR, "fatal", null)));
    // An unknown severity is handled as an error
    assertTrue (aHdl.handleEvent (new ValidationEvent ()
    {
      public int getSeverity ()
      {
        return 4711;
      }

      public String getMessage ()
      {
        return "unknown";
      }

      public Throwable getLinkedException ()
      {
        return null;
      }

      public ValidationEventLocator getLocator ()
      {
        return null;
      }
    }));

    final ErrorList aErrors = new ErrorList (aHdl.getErrorList ());
    assertEquals (4, aErrors.size ());
    assertSame (EErrorLevel.WARN, aErrors.get (0).getErrorLevel ());
    assertSame (EErrorLevel.ERROR, aErrors.get (1).getErrorLevel ());
    assertSame (EErrorLevel.FATAL_ERROR, aErrors.get (2).getErrorLevel ());
    assertSame (EErrorLevel.ERROR, aErrors.get (3).getErrorLevel ());
  }

  @Test
  public void testMessages ()
  {
    final CollectingValidationEventHandler aHdl = new CollectingValidationEventHandler ();

    // No message and no exception
    aHdl.handleEvent (_event (ValidationEvent.WARNING, null, null));
    // No message but an exception with a message
    aHdl.handleEvent (_event (ValidationEvent.WARNING, null, new IllegalArgumentException ("exception message")));
    // No message and an exception without a message
    aHdl.handleEvent (_event (ValidationEvent.WARNING, null, new IllegalArgumentException ()));

    final ErrorList aErrors = new ErrorList (aHdl.getErrorList ());
    assertEquals (3, aErrors.size ());
    assertEquals ("Validation event", aErrors.get (0).getErrorText (java.util.Locale.US));
    assertEquals ("exception message", aErrors.get (1).getErrorText (java.util.Locale.US));
    assertEquals ("Exception", aErrors.get (2).getErrorText (java.util.Locale.US));
  }

  @Test
  public void testLocator () throws Exception
  {
    final CollectingValidationEventHandler aHdl = new CollectingValidationEventHandler ();

    // Locator with a URL
    final ValidationEventLocatorImpl aLocURL = new ValidationEventLocatorImpl ();
    aLocURL.setURL (new URL ("http://www.helger.com/any.xml"));
    aLocURL.setLineNumber (4);
    aLocURL.setColumnNumber (17);
    aHdl.handleEvent (new ValidationEventImpl (ValidationEvent.WARNING, "msg", aLocURL));

    // Locator with an object
    final ValidationEventLocatorImpl aLocObj = new ValidationEventLocatorImpl ();
    aLocObj.setObject ("any object");
    aHdl.handleEvent (new ValidationEventImpl (ValidationEvent.WARNING, "msg", aLocObj));

    // Locator with a node
    final Document aDoc = XMLFactory.newDocument ();
    aDoc.appendChild (aDoc.createElement ("root"));
    final ValidationEventLocatorImpl aLocNode = new ValidationEventLocatorImpl ();
    aLocNode.setNode (aDoc.getDocumentElement ());
    aHdl.handleEvent (new ValidationEventImpl (ValidationEvent.WARNING, "msg", aLocNode));

    // Empty locator
    aHdl.handleEvent (new ValidationEventImpl (ValidationEvent.WARNING, "msg", new ValidationEventLocatorImpl ()));

    final ErrorList aErrors = new ErrorList (aHdl.getErrorList ());
    assertEquals (4, aErrors.size ());
    assertEquals ("http://www.helger.com/any.xml", aErrors.get (0).getErrorLocation ().getResourceID ());
    assertEquals (4, aErrors.get (0).getErrorLocation ().getLineNumber ());
    assertEquals (17, aErrors.get (0).getErrorLocation ().getColumnNumber ());
    assertEquals ("obj: any object", aErrors.get (1).getErrorFieldName ());
    assertNotNull (aErrors.get (2).getErrorFieldName ());
    assertNull (aErrors.get (3).getErrorFieldName ());
  }

  @Test
  public void testCollecting ()
  {
    final CollectingValidationEventHandler aHdl = new CollectingValidationEventHandler ();
    assertTrue (aHdl.getErrorList ().isEmpty ());
    assertFalse (aHdl.clearResourceErrors ().isChanged ());

    aHdl.handleEvent (_event (ValidationEvent.WARNING, "warning", null));
    assertEquals (1, aHdl.getErrorList ().size ());
    assertNotNull (aHdl.toString ());

    final MutableInt aCount = new MutableInt (0);
    aHdl.forEachResourceError (x -> aCount.inc ());
    assertEquals (1, aCount.intValue ());

    assertTrue (aHdl.clearResourceErrors ().isChanged ());
    assertTrue (aHdl.getErrorList ().isEmpty ());
  }

  @Test
  public void testWrappedCollecting ()
  {
    final ErrorList aErrorList = new ErrorList ();
    final WrappedCollectingValidationEventHandler aHdl = new WrappedCollectingValidationEventHandler (aErrorList);
    assertSame (aErrorList, aHdl.wrappedErrorList ());
    assertNotNull (aHdl.toString ());

    aHdl.handleEvent (_event (ValidationEvent.ERROR, "error", null));
    assertEquals (1, aErrorList.size ());
  }

  @Test
  public void testDoNothing ()
  {
    final DoNothingValidationEventHandler aHdl = new DoNothingValidationEventHandler ();
    assertTrue (aHdl.handleEvent (_event (ValidationEvent.ERROR, "error", null)));
    assertNotNull (aHdl.toString ());
  }

  @Test
  public void testLogging ()
  {
    final LoggingValidationEventHandler aHdl = LoggingValidationEventHandler.DEFAULT_INSTANCE;
    assertTrue (aHdl.handleEvent (_event (ValidationEvent.WARNING, "warning", null)));
    assertTrue (aHdl.handleEvent (_event (ValidationEvent.ERROR, "error", new IllegalArgumentException ())));
  }

  @Test
  public void testAndThen ()
  {
    final AtomicInteger aCount1 = new AtomicInteger (0);
    final AtomicInteger aCount2 = new AtomicInteger (0);
    final IValidationEventHandler aHdl1 = x -> {
      aCount1.incrementAndGet ();
      return true;
    };
    final ValidationEventHandler aHdl2 = x -> {
      aCount2.incrementAndGet ();
      return true;
    };

    // Both are invoked
    assertTrue (aHdl1.andThen (aHdl2).handleEvent (_event (ValidationEvent.WARNING, "w", null)));
    assertEquals (1, aCount1.get ());
    assertEquals (1, aCount2.get ());

    // Only the first one
    assertTrue (aHdl1.andThen (null).handleEvent (_event (ValidationEvent.WARNING, "w", null)));
    assertEquals (2, aCount1.get ());
    assertEquals (1, aCount2.get ());

    // Only the second one
    assertTrue (IValidationEventHandler.and (null, aHdl2).handleEvent (_event (ValidationEvent.WARNING, "w", null)));
    assertEquals (2, aCount1.get ());
    assertEquals (2, aCount2.get ());

    // None at all
    assertTrue (IValidationEventHandler.and (null, null).handleEvent (_event (ValidationEvent.WARNING, "w", null)));

    // The first one stops the processing
    final IValidationEventHandler aStop = x -> false;
    assertFalse (aStop.andThen (aHdl2).handleEvent (_event (ValidationEvent.WARNING, "w", null)));
    assertEquals (2, aCount2.get ());
  }

  @Test
  public void testConstantFactory ()
  {
    final DoNothingValidationEventHandler aHdl = new DoNothingValidationEventHandler ();
    final ConstantValidationEventHandlerFactory aFactory = new ConstantValidationEventHandlerFactory (aHdl);
    assertSame (aHdl, aFactory.getEventHandler ());
    assertSame (aHdl, aFactory.apply (null));
    assertSame (aHdl, aFactory.apply (LoggingValidationEventHandler.DEFAULT_INSTANCE));

    assertNull (new ConstantValidationEventHandlerFactory (null).apply (aHdl));
  }

  @Test
  public void testErrorDetails ()
  {
    final CollectingValidationEventHandler aHdl = new CollectingValidationEventHandler ();
    final IllegalArgumentException aEx = new IllegalArgumentException ("linked");
    aHdl.handleEvent (_event (ValidationEvent.ERROR, "msg", aEx));

    final IError aError = new ErrorList (aHdl.getErrorList ()).getFirstOrNull ();
    assertSame (aEx, aError.getLinkedException ());
    assertEquals ("msg", aError.getErrorText (java.util.Locale.US));
  }
}
