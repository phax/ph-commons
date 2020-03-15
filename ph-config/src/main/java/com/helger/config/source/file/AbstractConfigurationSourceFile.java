package com.helger.config.source.file;

import java.io.File;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;

/**
 * Abstract implementation of {@link IConfigurationSource} for file based
 * configuration sources.
 *
 * @author Philip Helger
 */
public abstract class AbstractConfigurationSourceFile extends AbstractConfigurationSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractConfigurationSourceFile.class);
  private static final EConfigSourceType TYPE = EConfigSourceType.FILE;

  private final File m_aFile;

  public AbstractConfigurationSourceFile (@Nonnull final File aFile)
  {
    this (aFile, TYPE.getDefaultPriority ());
  }

  public AbstractConfigurationSourceFile (@Nonnull final File aFile, final int nPriority)
  {
    super (TYPE, nPriority);
    ValueEnforcer.notNull (aFile, "File");
    m_aFile = aFile;
    if (aFile.isFile () && !aFile.canRead ())
      LOGGER.warn ("The configuration file '" + aFile.getAbsolutePath () + "' exists, but is not readable");
  }

  /**
   * @return The file as passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final File getFile ()
  {
    return m_aFile;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final AbstractConfigurationSourceFile rhs = (AbstractConfigurationSourceFile) o;
    return EqualsHelper.equals (m_aFile, rhs.m_aFile);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFile).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("File", m_aFile).getToString ();
  }
}
