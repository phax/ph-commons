/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.changelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.text.IMultiLingualText;
import com.helger.commons.text.impl.MultiLingualText;
import com.helger.commons.text.impl.TextProvider;

/**
 * Test class for class {@link ChangeLogEntry}.
 * 
 * @author Philip Helger
 */
public final class ChangeLogEntryTest extends AbstractPHTestCase
{
  @Test
  public void testAll ()
  {
    final ChangeLog aChangeLog = new ChangeLog ("1.0", "mock");
    final Date aDate = new Date ();

    final ChangeLogEntry aEntry = new ChangeLogEntry (aChangeLog,
                                                      aDate,
                                                      EChangeLogAction.ADD,
                                                      EChangeLogCategory.API,
                                                      true);
    assertSame (aChangeLog, aEntry.getChangeLog ());
    assertEquals (aDate, aEntry.getDate ());
    assertTrue (aDate != aEntry.getDate ());
    assertSame (EChangeLogAction.ADD, aEntry.getAction ());
    assertSame (EChangeLogCategory.API, aEntry.getCategory ());
    assertNotNull (aEntry.getAllTexts ());
    assertTrue (aEntry.getAllTexts ().isEmpty ());
    assertNotNull (aEntry.getAllIssues ());
    assertTrue (aEntry.getAllIssues ().isEmpty ());
    assertTrue (aEntry.isIncompatible ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (aEntry, new ChangeLogEntry (aChangeLog,
                                                                                                aDate,
                                                                                                EChangeLogAction.ADD,
                                                                                                EChangeLogCategory.API,
                                                                                                true));

    ChangeLogEntry aEntry2 = new ChangeLogEntry (aChangeLog, aDate, EChangeLogAction.ADD, EChangeLogCategory.API, true);
    aEntry2.setText (L_DE, "Ist doch auc egal :)");
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry, aEntry2);

    aEntry2 = new ChangeLogEntry (aChangeLog, aDate, EChangeLogAction.ADD, EChangeLogCategory.API, true);
    aEntry2.addIssue ("phloc-1234");
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry, aEntry2);

    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry,
                                                                        new ChangeLogEntry (aChangeLog,
                                                                                            aDate,
                                                                                            EChangeLogAction.ADD,
                                                                                            EChangeLogCategory.API,
                                                                                            false));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry,
                                                                        new ChangeLogEntry (aChangeLog,
                                                                                            new GregorianCalendar (2010,
                                                                                                                   Calendar.JULY,
                                                                                                                   6).getTime (),
                                                                                            EChangeLogAction.ADD,
                                                                                            EChangeLogCategory.API,
                                                                                            true));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry,
                                                                        new ChangeLogEntry (aChangeLog,
                                                                                            aDate,
                                                                                            EChangeLogAction.CHANGE,
                                                                                            EChangeLogCategory.API,
                                                                                            true));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aEntry,
                                                                        new ChangeLogEntry (aChangeLog,
                                                                                            aDate,
                                                                                            EChangeLogAction.ADD,
                                                                                            EChangeLogCategory.BUSINESS_LOGIC,
                                                                                            true));

    // Check the text afterwards
    assertFalse (aEntry.setText (L_DE, null).isChanged ());
    assertFalse (aEntry.setText (L_DE, "").isChanged ());
    assertFalse (aEntry.setText (L_DE, "   \t    ").isChanged ());
    assertTrue (aEntry.setText (L_DE, "Unit test ").isChanged ());
    assertEquals ("Unit test", aEntry.getText (L_DE));

    final IMultiLingualText aMLT = new MultiLingualText (TextProvider.create_DE_EN ("de", "en"));
    assertTrue (aEntry.setText (aMLT).isChanged ());
    assertFalse (aEntry.setText (aMLT).isChanged ());

    // check the issues
    assertTrue (aEntry.getAllIssues ().isEmpty ());
    assertFalse (aEntry.addIssue (null).isChanged ());
    assertFalse (aEntry.addIssue ("").isChanged ());
    assertFalse (aEntry.addIssue ("    \t      ").isChanged ());
    assertTrue (aEntry.addIssue ("pdaf-566").isChanged ());

    try
    {
      // null text not allowed
      aEntry.setText (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null changelog
      new ChangeLogEntry (null, aDate, EChangeLogAction.ADD, EChangeLogCategory.API, true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null date
      new ChangeLogEntry (aChangeLog, null, EChangeLogAction.ADD, EChangeLogCategory.API, true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null action
      new ChangeLogEntry (aChangeLog, aDate, null, EChangeLogCategory.API, true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null category
      new ChangeLogEntry (aChangeLog, aDate, EChangeLogAction.ADD, null, true);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
