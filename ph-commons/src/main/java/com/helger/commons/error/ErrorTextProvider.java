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

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.location.ILocation;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link IErrorTextProvider} with a customizable
 * layout.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
@NotThreadSafe
public class ErrorTextProvider implements IErrorTextProvider, Serializable, ICloneable <ErrorTextProvider>
{
  public static final char PLACEHOLDER = '$';
  public static final String PLACEHOLDER_STR = Character.toString (PLACEHOLDER);

  public enum EField implements IHasID <String>
  {
    CONSTANT ("const"),
    ERROR_LEVEL ("level"),
    ERROR_ID ("id"),
    ERROR_FIELD_NAME ("fieldname"),
    ERROR_LOCATION ("location"),
    ERROR_TEXT ("text"),
    ERROR_LINKED_EXCEPTION_CLASS ("%lec"),
    ERROR_LINKED_EXCEPTION_MESSAGE ("%lem"),
    ERROR_LINKED_EXCEPTION_STACK_TRACE ("%lest"),
    ERROR_LINKED_EXCEPTION_CAUSE_CLASS ("%lecc"),
    ERROR_LINKED_EXCEPTION_CAUSE_MESSAGE ("%lecm"),
    ERROR_LINKED_EXCEPTION_CAUSE_STACK_TRACE ("%lecst");

    private final String m_sID;

    private EField (@Nonnull @Nonempty final String sID)
    {
      m_sID = sID;
    }

    @Nonnull
    @Nonempty
    public String getID ()
    {
      return m_sID;
    }

    public boolean isPlaceholderRequired ()
    {
      return this != CONSTANT;
    }

    @Nullable
    public static EField getFromIDOrNull (@Nullable final String sID)
    {
      return EnumHelper.getFromIDOrNull (EField.class, sID);
    }
  }

  /**
   * Base interface for objects that format a single field.
   *
   * @author Philip Helger
   */
  public static interface IFormattableItem extends Serializable
  {
    @Nonnull
    EField getField ();

    @Nonnull
    String getUnformattedText ();

    @Nonnull
    String getFormattedText (@Nonnull String sReplacement);
  }

  /**
   * Default placeholder based implementation of {@link IFormattableItem}.
   *
   * @author Philip Helger
   */
  public static final class FormattableItem implements IFormattableItem
  {
    private final EField m_eField;
    private final String m_sText;

    public FormattableItem (@Nonnull final EField eField, @Nonnull @Nonempty final String sText)
    {
      m_eField = ValueEnforcer.notNull (eField, "Field");
      m_sText = ValueEnforcer.notEmpty (sText, "Text");

      if (eField.isPlaceholderRequired ())
        ValueEnforcer.isTrue (m_sText.contains (PLACEHOLDER_STR),
                              () -> "Text '" + sText + "' is missing placeholder '" + PLACEHOLDER + "'");
    }

    @Nonnull
    public EField getField ()
    {
      return m_eField;
    }

    @Nonnull
    @Nonempty
    public String getUnformattedText ()
    {
      return m_sText;
    }

    @Nonnull
    public String getFormattedText (@Nonnull final String sReplacement)
    {
      return StringHelper.replaceAll (m_sText, PLACEHOLDER_STR, sReplacement);
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("Field", m_eField).append ("Text", m_sText).getToString ();
    }
  }

  public static final IErrorTextProvider DEFAULT = new ErrorTextProvider ().addItem (EField.ERROR_LEVEL, "[$]")
                                                                           .addItem (EField.ERROR_ID, "[$]")
                                                                           .addItem (EField.ERROR_FIELD_NAME, "in $")
                                                                           .addItem (EField.ERROR_LOCATION, "@ $")
                                                                           .addItem (EField.ERROR_TEXT, "$")
                                                                           .addItem (EField.ERROR_LINKED_EXCEPTION_CLASS,
                                                                                     "($:")
                                                                           .addItem (EField.ERROR_LINKED_EXCEPTION_MESSAGE,
                                                                                     "$)")
                                                                           .setFieldSeparator (" ");

  private final ICommonsList <IFormattableItem> m_aItems;
  private String m_sFieldSep = " ";

  public ErrorTextProvider ()
  {
    m_aItems = new CommonsArrayList <> ();
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        Object to copy from. May not be <code>null</code>.
   * @since 8.5.5
   */
  public ErrorTextProvider (@Nonnull final ErrorTextProvider aOther)
  {
    m_aItems = aOther.m_aItems.getClone ();
    m_sFieldSep = aOther.m_sFieldSep;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IFormattableItem> getAllItems ()
  {
    return m_aItems.getClone ();
  }

  /**
   * Add an error item to be disabled.
   *
   * @param eField
   *        The field to be used. May not be <code>null</code>.
   * @param sText
   *        The text that should contain the placeholder ({@value #PLACEHOLDER})
   *        that will be replaced
   * @return this for chaining
   */
  @Nonnull
  public ErrorTextProvider addItem (@Nonnull final EField eField, @Nonnull @Nonempty final String sText)
  {
    return addItem (new FormattableItem (eField, sText));
  }

  @Nonnull
  public ErrorTextProvider addItem (@Nonnull final IFormattableItem aItem)
  {
    ValueEnforcer.notNull (aItem, "Item");
    m_aItems.add (aItem);
    return this;
  }

  @Nonnull
  public String getFieldSeparator ()
  {
    return m_sFieldSep;
  }

  @Nonnull
  public ErrorTextProvider setFieldSeparator (@Nonnull final String sFieldSep)
  {
    m_sFieldSep = ValueEnforcer.notNull (sFieldSep, "FieldSep");
    return this;
  }

  @Nonnull
  public String getErrorText (@Nonnull final IError aError, @Nonnull final Locale aContentLocale)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final IFormattableItem aItem : m_aItems)
      switch (aItem.getField ())
      {
        case CONSTANT:
        {
          aSB.append (aItem.getUnformattedText ());
          break;
        }
        case ERROR_LEVEL:
        {
          if (aSB.length () > 0)
            aSB.append (m_sFieldSep);
          aSB.append (aItem.getFormattedText (aError.getErrorLevel ().getID ()));
          break;
        }
        case ERROR_ID:
        {
          if (aError.hasErrorID ())
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (aError.getErrorID ()));
          }
          break;
        }
        case ERROR_FIELD_NAME:
        {
          if (aError.hasErrorFieldName ())
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (aError.getErrorFieldName ()));
          }
          break;
        }
        case ERROR_LOCATION:
        {
          final ILocation aLocation = aError.getErrorLocation ();
          if (aLocation.isAnyInformationPresent ())
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (aLocation.getAsString ()));
          }
          break;
        }
        case ERROR_TEXT:
        {
          final String sErrorText = aError.getErrorText (aContentLocale);
          if (StringHelper.hasText (sErrorText))
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (sErrorText));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_CLASS:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          if (aLinkedEx != null)
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (aLinkedEx.getClass ().getName ()));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_MESSAGE:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          if (aLinkedEx != null)
          {
            final String sMsg = StringHelper.getNotNull (aLinkedEx.getMessage (), "");
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (sMsg));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_STACK_TRACE:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          if (aLinkedEx != null)
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (StackTraceHelper.getStackAsString (aLinkedEx)));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_CAUSE_CLASS:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          final Throwable aCause = aLinkedEx != null ? aLinkedEx.getCause () : null;
          if (aCause != null)
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (aCause.getClass ().getName ()));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_CAUSE_MESSAGE:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          final Throwable aCause = aLinkedEx != null ? aLinkedEx.getCause () : null;
          if (aCause != null)
          {
            final String sMsg = StringHelper.getNotNull (aCause.getMessage ());
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (sMsg));
          }
          break;
        }
        case ERROR_LINKED_EXCEPTION_CAUSE_STACK_TRACE:
        {
          final Throwable aLinkedEx = aError.getLinkedException ();
          final Throwable aCause = aLinkedEx != null ? aLinkedEx.getCause () : null;
          if (aCause != null)
          {
            if (aSB.length () > 0)
              aSB.append (m_sFieldSep);
            aSB.append (aItem.getFormattedText (StackTraceHelper.getStackAsString (aCause)));
          }
          break;
        }
        default:
          throw new IllegalStateException ("Unsupported error field " + aItem.getField ());
      }
    return aSB.toString ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorTextProvider getClone ()
  {
    return new ErrorTextProvider (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Items", m_aItems)
                                       .append ("FieldSeparator", m_sFieldSep)
                                       .getToString ();
  }
}
