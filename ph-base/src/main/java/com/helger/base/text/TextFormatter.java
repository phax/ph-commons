package com.helger.base.text;

import java.text.MessageFormat;
import java.util.Locale;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class TextFormatter
{
  private TextFormatter ()
  {}

  @Nullable
  public static String getFormattedText (@Nullable final String sText, @Nullable final Object... aArgs)
  {
    if (sText == null)
    {
      // Avoid NPE in MessageFormat
      return null;
    }

    if (ArrayHelper.isEmpty (aArgs))
    {
      // Return text unchanged
      return sText;
    }

    final MessageFormat aMF = new MessageFormat (sText, Locale.getDefault (Locale.Category.FORMAT));
    return aMF.format (aArgs);
  }

  @Nullable
  public static String getFormattedText (@Nonnull final Locale aDisplayLocale,
                                         @Nullable final String sText,
                                         @Nullable final Object... aArgs)
  {
    ValueEnforcer.notNull (aDisplayLocale, "DisplayLocale");

    if (sText == null)
    {
      // Avoid NPE in MessageFormat
      return null;
    }

    if (ArrayHelper.isEmpty (aArgs))
    {
      // Return text unchanged
      return sText;
    }

    final MessageFormat aMF = new MessageFormat (sText, aDisplayLocale);
    return aMF.format (aArgs);
  }

}
