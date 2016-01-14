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
package com.helger.commons.text.resolve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.annotation.NoTranslationRequired;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.text.AbstractReadOnlyMapBasedMultilingualText;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.commons.text.display.IHasDisplayTextWithArgs;
import com.helger.commons.text.util.TextHelper;

/**
 * Test class for class {@link DefaultTextResolver}.
 *
 * @author Philip Helger
 */
public final class DefaultTextResolverTest
{
  @NoTranslationRequired
  public static enum EText implements IHasDisplayText,IHasDisplayTextWithArgs
  {
   TEXT1 ("Text1de", "Text1en"),
   TEXT2 ("Text2de", "Text2en"),
   TEXT3 ("Text3{0}de", "Text3{0}en");

    private final AbstractReadOnlyMapBasedMultilingualText m_aTP;

    private EText (final String sDE, final String sEN)
    {
      m_aTP = TextHelper.create_DE_EN (sDE, sEN);
    }

    @Nullable
    public String getDisplayText (@Nonnull final Locale aContentLocale)
    {
      return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
    }

    @Nullable
    public String getDisplayTextWithArgs (@Nonnull final Locale aContentLocale, @Nullable final Object... aArgs)
    {
      return DefaultTextResolver.getTextWithArgsStatic (this, m_aTP, aContentLocale, aArgs);
    }
  }

  @Test
  public void testGetText ()
  {
    final Locale aDE = TextHelper.DE;
    final Locale aDE_AT = LocaleCache.getInstance ().getLocale ("de", "AT");
    final Locale aEN = TextHelper.EN;
    final Locale aEN_US = LocaleCache.getInstance ().getLocale ("en", "US");
    final Locale aSR = LocaleCache.getInstance ().getLocale ("sr", "RS");

    // Regular
    assertEquals ("Text1de", EText.TEXT1.getDisplayText (aDE));
    assertEquals ("Text1de", EText.TEXT1.getDisplayText (aDE_AT));
    assertEquals ("Text1en", EText.TEXT1.getDisplayText (aEN));
    assertEquals ("Text1en", EText.TEXT1.getDisplayText (aEN_US));

    // German has override!
    assertEquals ("Text2de-override", EText.TEXT2.getDisplayText (aDE));
    assertEquals ("Text2de-override", EText.TEXT2.getDisplayText (aDE_AT));
    assertEquals ("Text2en", EText.TEXT2.getDisplayText (aEN));
    assertEquals ("Text2en", EText.TEXT2.getDisplayText (aEN_US));

    // No fallback properties file present
    assertNull (EText.TEXT1.getDisplayText (aSR));
    assertNull (EText.TEXT2.getDisplayText (aSR));

    // Check bundle names
    assertTrue (DefaultTextResolver.getInstance ()
                                   .getAllUsedOverrideBundleNames ()
                                   .contains ("properties/override-de"));
    assertFalse (DefaultTextResolver.getInstance ()
                                    .getAllUsedOverrideBundleNames ()
                                    .contains ("properties/override-en"));
    assertFalse (DefaultTextResolver.getInstance ().getAllUsedFallbackBundleNames ().contains ("properties/sr_RS"));
  }

  @Test
  public void testGetTextWithArgs ()
  {
    final Locale aDE = TextHelper.DE;
    final Locale aEN = TextHelper.EN;

    // Regular
    assertEquals ("Text3abcde", EText.TEXT3.getDisplayTextWithArgs (aDE, "abc"));
    assertEquals ("Text3abcen", EText.TEXT3.getDisplayTextWithArgs (aEN, "abc"));

    assertEquals (EText.TEXT3.getDisplayText (aEN), EText.TEXT3.getDisplayTextWithArgs (aEN, new Object [] {}));
    assertEquals (EText.TEXT3.getDisplayText (aEN), EText.TEXT3.getDisplayTextWithArgs (aEN, (Object []) null));

    // Clear cache and try again (should not make any difference)
    DefaultTextResolver.getInstance ().clearCache ();

    // Regular
    assertEquals ("Text3abcde", EText.TEXT3.getDisplayTextWithArgs (aDE, "abc"));
    assertEquals ("Text3abcen", EText.TEXT3.getDisplayTextWithArgs (aEN, "abc"));
  }
}
