package com.helger.commons.error;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.error.text.IHasErrorText;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link IError}.
 *
 * @author Philip Helger
 */
public class SingleError implements IError
{
  private final IErrorLevel m_aErrorLevel;
  private String m_sErrorID;
  private String m_sErrorFieldName;
  private IErrorLocation m_aErrorLocation;
  private IHasErrorText m_aErrorText;
  private Throwable m_aLinkedException;

  public SingleError (@Nonnull final IErrorLevel aErrorLevel)
  {
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
  }

  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  protected void setErrorID (@Nullable final String sErrorID)
  {
    m_sErrorID = sErrorID;
  }

  @Nullable
  public String getErrorID ()
  {
    return m_sErrorID;
  }

  protected void setErrorFieldName (@Nullable final String sErrorFieldName)
  {
    m_sErrorFieldName = sErrorFieldName;
  }

  @Nullable
  public String getErrorFieldName ()
  {
    return m_sErrorFieldName;
  }

  protected void setErrorLocation (@Nullable final IErrorLocation aErrorLocation)
  {
    m_aErrorLocation = aErrorLocation;
  }

  @Nonnull
  public IErrorLocation getErrorLocation ()
  {
    final IErrorLocation ret = m_aErrorLocation;
    return ret != null ? ret : ErrorLocation.NO_LOCATION;
  }

  protected void setErrorText (@Nullable final IHasErrorText aErrorText)
  {
    m_aErrorText = aErrorText;
  }

  @Nullable
  public String getErrorText (@Nonnull final Locale aContentLocale)
  {
    final IHasErrorText ret = m_aErrorText;
    return ret == null ? null : ret.getDisplayText (aContentLocale);
  }

  protected void setLinkedException (@Nullable final Throwable aLinkedException)
  {
    m_aLinkedException = aLinkedException;
  }

  @Nullable
  public Throwable getLinkedException ()
  {
    return m_aLinkedException;
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
   * Builder class for {@link SingleError} instances
   *
   * @author Philip Helger
   */
  public static class Builder
  {
    private IErrorLevel m_aErrorLevel = EErrorLevel.ERROR;
    private String m_sErrorID;
    private String m_sErrorFieldName;
    private IErrorLocation m_aErrorLocation;
    private IHasErrorText m_aErrorText;
    private Throwable m_aLinkedException;

    public Builder ()
    {}

    @Nonnull
    public Builder setErrorLevel (@Nullable final IErrorLevel aErrorLevel)
    {
      m_aErrorLevel = aErrorLevel;
      return this;
    }

    @Nonnull
    public Builder setErrorID (@Nullable final String sErrorID)
    {
      m_sErrorID = sErrorID;
      return this;
    }

    @Nonnull
    public Builder setErrorFieldName (@Nullable final String sErrorFieldName)
    {
      m_sErrorFieldName = sErrorFieldName;
      return this;
    }

    @Nonnull
    public Builder setErrorLocation (@Nullable final IErrorLocation aErrorLocation)
    {
      m_aErrorLocation = aErrorLocation;
      return this;
    }

    @Nonnull
    public Builder setErrorText (@Nullable final String sErrorText)
    {
      return setErrorText (sErrorText == null ? null : x -> sErrorText);
    }

    @Nonnull
    public Builder setErrorText (@Nullable final IHasErrorText aErrorText)
    {
      m_aErrorText = aErrorText;
      return this;
    }

    @Nonnull
    public Builder setLinkedException (@Nullable final Throwable aLinkedException)
    {
      m_aLinkedException = aLinkedException;
      return this;
    }

    @Nonnull
    public SingleError build ()
    {
      final SingleError ret = new SingleError (m_aErrorLevel);
      ret.setErrorID (m_sErrorID);
      ret.setErrorFieldName (m_sErrorFieldName);
      ret.setErrorLocation (m_aErrorLocation);
      ret.setErrorText (m_aErrorText);
      ret.setLinkedException (m_aLinkedException);
      return ret;
    }
  }

  @Nonnull
  public static Builder builder ()
  {
    return new Builder ();
  }
}
