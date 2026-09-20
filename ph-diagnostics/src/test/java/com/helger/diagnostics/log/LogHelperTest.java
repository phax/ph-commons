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
package com.helger.diagnostics.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IHasErrorLevel;

/**
 * Test class for class {@link LogHelper}.
 *
 * @author Philip Helger
 */
public final class LogHelperTest
{
  @Test
  public void testAll ()
  {
    final Logger aLogger = LoggerFactory.getLogger (LogHelperTest.class);
    for (final EErrorLevel eLevel : EErrorLevel.values ())
    {
      LogHelper.log (aLogger, eLevel, "my message");
      LogHelper.log (aLogger, eLevel, "my message with exception", new Exception ());
      LogHelper.log (aLogger, eLevel, "my message with exception", new RuntimeException ());
    }
  }

  @Test
  public void testAllWithClass ()
  {
    for (final EErrorLevel eLevel : EErrorLevel.values ())
    {
      LogHelper.log (LogHelperTest.class, eLevel, "my message");
      LogHelper.log (LogHelperTest.class, eLevel, "my message with exception", new Exception ());
    }
  }

  @Test
  public void testAllWithErrorLevelProvider ()
  {
    final Logger aLogger = LoggerFactory.getLogger (LogHelperTest.class);
    for (final EErrorLevel eLevel : EErrorLevel.values ())
    {
      final IHasErrorLevel aProvider = () -> eLevel;
      LogHelper.log (aLogger, aProvider, "my message");
      LogHelper.log (aLogger, aProvider, "my message with exception", new Exception ());
      LogHelper.log (LogHelperTest.class, aProvider, "my message");
      LogHelper.log (LogHelperTest.class, aProvider, "my message with exception", new Exception ());
    }
  }

  @Test
  public void testAllWithSupplier ()
  {
    final Logger aLogger = LoggerFactory.getLogger (LogHelperTest.class);
    for (final EErrorLevel eLevel : EErrorLevel.values ())
    {
      final IHasErrorLevel aProvider = () -> eLevel;
      LogHelper.log (aLogger, eLevel, () -> "my message");
      LogHelper.log (aLogger, eLevel, () -> "my message with exception", new Exception ());
      LogHelper.log (LogHelperTest.class, eLevel, () -> "my message");
      LogHelper.log (LogHelperTest.class, eLevel, () -> "my message with exception", new Exception ());
      LogHelper.log (aLogger, aProvider, () -> "my message");
      LogHelper.log (aLogger, aProvider, () -> "my message with exception", new Exception ());
      LogHelper.log (LogHelperTest.class, aProvider, () -> "my message");
      LogHelper.log (LogHelperTest.class, aProvider, () -> "my message with exception", new Exception ());
    }
  }

  @Test
  public void testIsEnabled ()
  {
    final Logger aLogger = LoggerFactory.getLogger (LogHelperTest.class);
    for (final EErrorLevel eLevel : EErrorLevel.values ())
    {
      final IHasErrorLevel aProvider = () -> eLevel;
      final boolean bEnabled = LogHelper.isEnabled (aLogger, eLevel);
      assertEquals (Boolean.valueOf (bEnabled), Boolean.valueOf (LogHelper.isEnabled (LogHelperTest.class, eLevel)));
      assertEquals (Boolean.valueOf (bEnabled), Boolean.valueOf (LogHelper.isEnabled (aLogger, aProvider)));
      assertEquals (Boolean.valueOf (bEnabled), Boolean.valueOf (LogHelper.isEnabled (LogHelperTest.class, aProvider)));
      assertEquals (Boolean.valueOf (bEnabled),
                    Boolean.valueOf (LogHelper.getFuncIsEnabled (aLogger, eLevel).isEnabled ()));

      assertNotNull (LogHelper.getFuncLogger (aLogger, eLevel));
    }
  }
}
