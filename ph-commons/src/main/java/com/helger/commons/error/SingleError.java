package com.helger.commons.error;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.error.text.ConstantHasErrorText;
import com.helger.commons.error.text.DynamicHasErrorText;
import com.helger.commons.error.text.IHasErrorText;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Default implementation of {@link IError}.
 *
 * @author Philip Helger
 */
@Immutable
public class SingleError implements IError
{
  private final IErrorLevel m_aErrorLevel;
  private final String m_sErrorID;
  private final String m_sErrorFieldName;
  private final IErrorLocation m_aErrorLocation;
  private final IHasErrorText m_aErrorText;
  private final Throwable m_aLinkedException;

  public SingleError (@Nonnull final IErrorLevel aErrorLevel,
                      @Nullable final String sErrorID,
                      @Nullable final String sErrorFieldName,
                      @Nullable final IErrorLocation aErrorLocation,
                      @Nullable final IHasErrorText aErrorText,
                      @Nullable final Throwable aLinkedException)
  {
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_sErrorID = sErrorID;
    m_sErrorFieldName = sErrorFieldName;
    m_aErrorLocation = aErrorLocation != null ? aErrorLocation : ErrorLocation.NO_LOCATION;
    m_aErrorText = aErrorText;
    m_aLinkedException = aLinkedException;
  }

  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  @Nullable
  public String getErrorID ()
  {
    return m_sErrorID;
  }

  @Nullable
  public String getErrorFieldName ()
  {
    return m_sErrorFieldName;
  }

  @Nonnull
  public IErrorLocation getErrorLocation ()
  {
    return m_aErrorLocation;
  }

  @Nullable
  public IHasErrorText getErrorTexts ()
  {
    return m_aErrorText;
  }

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
      aHCG.append (t);
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
                                       .toString ();
  }

  /**
   * Abstract builder class for {@link SingleError} and derived classes.
   *
   * @author Philip Helger
   * @param <T>
   *        Result type
   * @param <IMPLTYPE>
   *        Implementation type
   */
  public abstract static class AbstractBuilder <T extends SingleError, IMPLTYPE extends AbstractBuilder <T, IMPLTYPE>>
                                               implements IGenericImplTrait <IMPLTYPE>
  {
    protected IErrorLevel m_aErrorLevel = EErrorLevel.ERROR;
    protected String m_sErrorID;
    protected String m_sErrorFieldName;
    protected IErrorLocation m_aErrorLocation;
    protected IHasErrorText m_aErrorText;
    protected Throwable m_aLinkedException;

    public AbstractBuilder ()
    {}

    @Nonnull
    public IMPLTYPE setErrorLevel (@Nonnull final IErrorLevel aErrorLevel)
    {
      ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
      m_aErrorLevel = aErrorLevel;
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setErrorID (@Nullable final String sErrorID)
    {
      m_sErrorID = sErrorID;
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setErrorFieldName (@Nullable final String sErrorFieldName)
    {
      m_sErrorFieldName = sErrorFieldName;
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setErrorLocation (@Nullable final IErrorLocation aErrorLocation)
    {
      m_aErrorLocation = aErrorLocation;
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setErrorText (@Nullable final String sErrorText)
    {
      return setErrorText (ConstantHasErrorText.createOnDemand (sErrorText));
    }

    @Nonnull
    public IMPLTYPE setErrorText (@Nullable final IMultilingualText aMLT)
    {
      if (aMLT == null)
        m_aErrorText = null;
      else
        if (aMLT.getSize () == 1)
        {
          // If the multilingual text contains only a single locale, use it as a
          // constant value
          // If you don't like this behavior directly call #setErrorText with a
          // DynamicHasErrorText
          m_aErrorText = ConstantHasErrorText.createOnDemand (aMLT.getAllTexts ().getFirstValue ());
        }
        else
          m_aErrorText = new DynamicHasErrorText (aMLT);
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setErrorText (@Nullable final IHasErrorText aErrorText)
    {
      m_aErrorText = aErrorText;
      return thisAsT ();
    }

    @Nonnull
    public IMPLTYPE setLinkedException (@Nullable final Throwable aLinkedException)
    {
      m_aLinkedException = aLinkedException;
      return thisAsT ();
    }

    @Nonnull
    public abstract T build ();
  }

  public static final class SingleErrorBuilder extends AbstractBuilder <SingleError, SingleErrorBuilder>
  {
    @Override
    @Nonnull
    public SingleError build ()
    {
      return new SingleError (m_aErrorLevel,
                              m_sErrorID,
                              m_sErrorFieldName,
                              m_aErrorLocation,
                              m_aErrorText,
                              m_aLinkedException);
    }
  }

  /**
   * @return A new Error builder. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builder ()
  {
    return new SingleErrorBuilder ();
  }

  /**
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#SUCCESS}. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builderSuccess ()
  {
    return builder ().setErrorLevel (EErrorLevel.SUCCESS);
  }

  /**
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#INFO}. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builderInfo ()
  {
    return builder ().setErrorLevel (EErrorLevel.INFO);
  }

  /**
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#WARN}. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builderWarn ()
  {
    return builder ().setErrorLevel (EErrorLevel.WARN);
  }

  /**
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#ERROR}. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builderError ()
  {
    return builder ().setErrorLevel (EErrorLevel.ERROR);
  }

  /**
   * @return A new Error builder with default error level
   *         {@link EErrorLevel#FATAL_ERROR}. Never <code>null</code>.
   */
  @Nonnull
  public static SingleErrorBuilder builderFatalError ()
  {
    return builder ().setErrorLevel (EErrorLevel.FATAL_ERROR);
  }
}
