package com.helger.commons.url;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;

/**
 * A list of URL parameters with a sanity API. It allows for multiple URL
 * parameters with the same name and thereby maintaining the order of the URL
 * parameters.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class URLParameterList extends CommonsArrayList <URLParameter>
{
  public URLParameterList ()
  {}

  public URLParameterList (@Nullable final List <? extends URLParameter> aOther)
  {
    super (aOther);
  }

  /**
   * Add a parameter without a value
   *
   * @param sName
   *        The name of the parameter. May neither be <code>null</code> nor
   *        empty.
   * @return this
   */
  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName)
  {
    return add (sName, "");
  }

  @Nonnull
  public URLParameterList add (@Nonnull final Map.Entry <String, String> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final boolean bValue)
  {
    return add (sName, Boolean.toString (bValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final int nValue)
  {
    return add (sName, Integer.toString (nValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, final long nValue)
  {
    return add (sName, Long.toString (nValue));
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, @Nonnull final BigInteger aValue)
  {
    return add (sName, aValue.toString ());
  }

  @Nonnull
  public URLParameterList add (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    add (new URLParameter (sName, sValue));
    return this;
  }

  @Nonnull
  public final URLParameterList addAll (@Nullable final Map <String, String> aParams)
  {
    if (aParams != null)
      for (final Map.Entry <String, String> aEntry : aParams.entrySet ())
        add (aEntry);
    return this;
  }

  @Nonnull
  public final URLParameterList addAll (@Nullable final List <? extends URLParameter> aParams)
  {
    if (aParams != null)
      for (final URLParameter aParam : aParams)
        add (aParam);
    return this;
  }

  /**
   * Remove all parameter with the given name.
   *
   * @param sName
   *        The key to remove
   * @return this
   */
  @Nonnull
  public URLParameterList remove (@Nullable final String sName)
  {
    removeIf (aParam -> aParam.hasName (sName));
    return this;
  }

  /**
   * Remove all parameter with the given name and value.
   *
   * @param sName
   *        The key to remove. May be <code>null</code>.
   * @param sValue
   *        The value to be removed. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public URLParameterList remove (@Nullable final String sName, @Nullable final String sValue)
  {
    removeIf (aParam -> aParam.hasName (sName) && aParam.hasValue (sValue));
    return this;
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public URLParameterList getClone ()
  {
    return new URLParameterList (this);
  }
}
