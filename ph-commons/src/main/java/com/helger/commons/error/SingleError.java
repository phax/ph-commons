/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.stream.Location;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.text.ConstantHasErrorText;
import com.helger.commons.error.text.DynamicHasErrorText;
import com.helger.commons.error.text.IHasErrorText;
import com.helger.commons.hashcode.HashCodeCalculator;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.location.ILocation;
import com.helger.commons.location.SimpleLocation;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Default implementation of {@link IError}.<br>
 * Note: cannot be called <code>Error</code> because this would conflict with
 * the default Java Exception class.
 *
 * @author Philip Helger
 * @since 8.5.0
 */
@Immutable
public class SingleError implements IError
{
  private final IErrorLevel m_aErrorLevel;
  private final String m_sErrorID;
  private final String m_sErrorFieldName;
  private final ILocation m_aErrorLocation;
  private final IHasErrorText m_aErrorText;
  private final Throwable m_aLinkedException;

  protected SingleError (@Nonnull final AbstractBuilder <?, ?> aBuilder)
  {
    this (aBuilder.m_aErrorLevel,
          aBuilder.m_sErrorID,
          aBuilder.m_sErrorFieldName,
          aBuilder.m_aErrorLocation,
          aBuilder.m_aErrorText,
          aBuilder.m_aLinkedException);
  }

  public SingleError (@Nonnull final IErrorLevel aErrorLevel,
                      @Nullable final String sErrorID,
                      @Nullable final String sErrorFieldName,
                      @Nullable final ILocation aErrorLocation,
                      @Nullable final IHasErrorText aErrorText,
                      @Nullable final Throwable aLinkedException)
  {
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_sErrorID = sErrorID;
    m_sErrorFieldName = sErrorFieldName;
    m_aErrorLocation = aErrorLocation != null ? aErrorLocation : SimpleLocation.NO_LOCATION;
    m_aErrorText = aErrorText;
    m_aLinkedException = aLinkedException;
  }

  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  @Override
  @Nullable
  public String getErrorID ()
  {
    return m_sErrorID;
  }

  @Override
  @Nullable
  public String getErrorFieldName ()
  {
    return m_sErrorFieldName;
  }

  @Override
  @Nonnull
  public ILocation getErrorLocation ()
  {
    return m_aErrorLocation;
  }

  @Override
  @Nullable
  public IHasErrorText getErrorTexts ()
  {
    return m_aErrorText;
  }

  @Override
  @Nullable
  public Throwable getLinkedException ()
  {
    return m_aLinkedException;
  }

  /**
   * Overridable implementation of Throwable for the Linked exception field.
   * This can be overridden to implement a different algorithm. This is
   * customizable because there is no default way of comparing Exceptions in
   * Java. If you override this method you must also override
   * {@link #hashCodeLinkedException(HashCodeGenerator, Throwable)}!
   *
   * @param t1
   *        First Throwable. May be <code>null</code>.
   * @param t2
   *        Second Throwable. May be <code>null</code>.
   * @return <code>true</code> if they are equals (or both null)
   */
  @OverrideOnDemand
  protected boolean equalsLinkedException (@Nullable final Throwable t1, @Nullable final Throwable t2)
  {
    if (EqualsHelper.identityEqual (t1, t2))
      return true;
    if (t1 == null || t2 == null)
      return false;
    return t1.getClass ().equals (t2.getClass ()) && EqualsHelper.equals (t1.getMessage (), t2.getMessage ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleError rhs = (SingleError) o;
    return m_aErrorLevel.equals (rhs.m_aErrorLevel) &&
           EqualsHelper.equals (m_sErrorID, rhs.m_sErrorID) &&
           EqualsHelper.equals (m_sErrorFieldName, rhs.m_sErrorFieldName) &&
           EqualsHelper.equals (m_aErrorLocation, rhs.m_aErrorLocation) &&
           EqualsHelper.equals (m_aErrorText, rhs.m_aErrorText) &&
           equalsLinkedException (m_aLinkedException, rhs.m_aLinkedException);
  }

  /**
   * Overridable implementation of Throwable for the Linked exception field.
   * This can be overridden to implement a different algorithm. This is
   * customizable because there is no default way of hashing Exceptions in Java.
   * If you override this method you must also override
   * {@link #equalsLinkedException(Throwable, Throwable)}!
   *
   * @param aHCG
   *        The hash code generator to append to. Never <code>null</code>.
   * @param t
   *        The Throwable to append. May be <code>null</code>.
   */
  @OverrideOnDemand
  protected void hashCodeLinkedException (@Nonnull final HashCodeGenerator aHCG, @Nullable final Throwable t)
  {
    if (t == null)
      aHCG.append (HashCodeCalculator.HASHCODE_NULL);
    else
      aHCG.append (t.getClass ()).append (t.getMessage ());
  }

  @Override
  public int hashCode ()
  {
    final HashCodeGenerator aHCG = new HashCodeGenerator (this).append (m_aErrorLevel)
                                                               .append (m_sErrorID)
                                                               .append (m_sErrorFieldName)
                                                               .append (m_aErrorLocation)
                                                               .append (m_aErrorText);
    hashCodeLinkedException (aHCG, m_aLinkedException);
    return aHCG.getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ErrorLevel", m_aErrorLevel)
                                       .appendIfNotNull ("ErrorID", m_sErrorID)
                                       .appendIfNotNull ("ErrorFieldName", m_sErrorFieldName)
                                       .appendIfNotNull ("ErrorLocation", m_aErrorLocation)
                                       .appendIfNotNull ("ErrorText", m_aErrorText)
                                       .appendIfNotNull ("LinkedException", m_aLinkedException)
                                       .getToString ();
  }

  /**
   * Create a new empty error builder with the default error level from
   * {@link Builder}.
   *
   * @return A new Error builder. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builder ()
  {
    return new Builder ();
  }

  /**
   * Create a new error builder containing all the data from the provided error.
   *
   * @param aError
   *        The error to copy the data from
   * @return A new Error builder containing all the data from the provided
   *         error. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builder (@Nonnull final IError aError)
  {
    return new Builder (aError);
  }

  /**
   * Create a new empty error builder with the SUCCESS error level.
   *
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#SUCCESS}. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builderSuccess ()
  {
    return builder ().errorLevel (EErrorLevel.SUCCESS);
  }

  /**
   * Create a new empty error builder with the INFO error level.
   *
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#INFO}. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builderInfo ()
  {
    return builder ().errorLevel (EErrorLevel.INFO);
  }

  /**
   * Create a new empty error builder with the WARN error level.
   *
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#WARN}. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builderWarn ()
  {
    return builder ().errorLevel (EErrorLevel.WARN);
  }

  /**
   * Create a new empty error builder with the ERROR error level.
   *
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#ERROR}. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builderError ()
  {
    return builder ().errorLevel (EErrorLevel.ERROR);
  }

  /**
   * Create a new empty error builder with the FATAL ERROR error level.
   *
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#FATAL_ERROR}. Never <code>null</code>.
   */
  @Nonnull
  public static Builder builderFatalError ()
  {
    return builder ().errorLevel (EErrorLevel.FATAL_ERROR);
  }

  /**
   * Abstract builder class for {@link SingleError} and derived classes.
   *
   * @author Philip Helger
   * @param <ERRTYPE>
   *        Result error type
   * @param <IMPLTYPE>
   *        Implementation type
   */
  public abstract static class AbstractBuilder <ERRTYPE extends SingleError, IMPLTYPE extends AbstractBuilder <ERRTYPE, IMPLTYPE>>
                                               implements
                                               IGenericImplTrait <IMPLTYPE>,
                                               IBuilder <ERRTYPE>
  {
    public static final IErrorLevel DEFAULT_ERROR_LEVEL = EErrorLevel.ERROR;

    protected IErrorLevel m_aErrorLevel = DEFAULT_ERROR_LEVEL;
    protected String m_sErrorID;
    protected String m_sErrorFieldName;
    protected ILocation m_aErrorLocation;
    protected IHasErrorText m_aErrorText;
    protected Throwable m_aLinkedException;

    public AbstractBuilder ()
    {}

    public AbstractBuilder (@Nonnull final IError aError)
    {
      ValueEnforcer.notNull (aError, "Error");
      errorLevel (aError.getErrorLevel ());
      errorID (aError.getErrorID ());
      errorFieldName (aError.getErrorFieldName ());
      errorLocation (aError.getErrorLocation ());
      errorText (aError.getErrorTexts ());
      linkedException (aError.getLinkedException ());
    }

    @Nonnull
    public final IMPLTYPE errorLevel (@Nonnull final IErrorLevel aErrorLevel)
    {
      ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
      m_aErrorLevel = aErrorLevel;
      return thisAsT ();
    }

    @Nonnull
    public final IMPLTYPE errorID (@Nullable final String sErrorID)
    {
      m_sErrorID = sErrorID;
      return thisAsT ();
    }

    @Nonnull
    public final IMPLTYPE errorFieldName (@Nullable final String sErrorFieldName)
    {
      m_sErrorFieldName = sErrorFieldName;
      return thisAsT ();
    }

    /**
     * Set a simple error location without line and column number
     *
     * @param sErrorLocation
     *        Error location string
     * @return this for chaining
     * @since 9.0.2
     */
    @Nonnull
    public final IMPLTYPE errorLocation (@Nullable final String sErrorLocation)
    {
      return errorLocation (new SimpleLocation (sErrorLocation));
    }

    @Nonnull
    public final IMPLTYPE errorLocation (@Nullable final Locator aLocator)
    {
      return errorLocation (SimpleLocation.create (aLocator));
    }

    @Nonnull
    public final IMPLTYPE errorLocation (@Nullable final SAXParseException aLocator)
    {
      return errorLocation (SimpleLocation.create (aLocator));
    }

    @Nonnull
    public final IMPLTYPE errorLocation (@Nullable final Location aLocator)
    {
      return errorLocation (SimpleLocation.create (aLocator));
    }

    @Nonnull
    public final IMPLTYPE errorLocation (@Nullable final ILocation aErrorLocation)
    {
      m_aErrorLocation = aErrorLocation;
      return thisAsT ();
    }

    @Nonnull
    public final IMPLTYPE errorText (@Nullable final String sErrorText)
    {
      return errorText (ConstantHasErrorText.createOnDemand (sErrorText));
    }

    @Nonnull
    public final IMPLTYPE errorText (@Nullable final IMultilingualText aMLT)
    {
      if (aMLT == null)
        m_aErrorText = null;
      else
        if (aMLT.texts ().size () == 1)
        {
          // If the multilingual text contains only a single locale, use it as a
          // constant value
          // If you don't like this behavior directly call #setErrorText with a
          // DynamicHasErrorText
          m_aErrorText = ConstantHasErrorText.createOnDemand (aMLT.texts ().getFirstValue ());
        }
        else
          m_aErrorText = new DynamicHasErrorText (aMLT);
      return thisAsT ();
    }

    @Nonnull
    public final IMPLTYPE errorText (@Nullable final IHasErrorText aErrorText)
    {
      m_aErrorText = aErrorText;
      return thisAsT ();
    }

    @Nonnull
    public final IMPLTYPE linkedException (@Nullable final Throwable aLinkedException)
    {
      m_aLinkedException = aLinkedException;
      return thisAsT ();
    }
  }

  /**
   * The default builder to build {@link SingleError} instances.
   *
   * @author Philip Helger
   */
  public static final class Builder extends AbstractBuilder <SingleError, Builder>
  {
    public Builder ()
    {}

    public Builder (@Nonnull final IError aError)
    {
      super (aError);
    }

    @Override
    @Nonnull
    public SingleError build ()
    {
      if (m_aErrorLevel == null)
        throw new IllegalStateException ("The error level must be provided");

      return new SingleError (this);
    }
  }
}
