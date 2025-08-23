/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.http.csp;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.codec.base64.Base64;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.trait.IGenericImplTrait;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.mime.IMimeType;
import com.helger.security.messagedigest.EMessageDigestAlgorithm;
import com.helger.url.ISimpleURL;

import jakarta.annotation.Nonnull;

/**
 * A source list to be used in a CSP directive ({@link CSPDirective}). It's just a convenient way to
 * build a CSP directive value.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
@NotThreadSafe
public abstract class AbstractCSPSourceList <IMPLTYPE extends AbstractCSPSourceList <IMPLTYPE>> implements
                                            IGenericImplTrait <IMPLTYPE>
{
  public static final String KEYWORD_NONE = "'none'";
  public static final String KEYWORD_REPORT_SAMPLE = "'report-sample'";
  public static final String KEYWORD_SELF = "'self'";
  public static final String KEYWORD_STRICT_DYNAMIC = "'strict-dynamic'";
  public static final String KEYWORD_UNSAFE_INLINE = "'unsafe-inline'";
  public static final String KEYWORD_UNSAFE_EVAL = "'unsafe-eval'";
  public static final String NONCE_PREFIX = "'nonce-";
  public static final String NONCE_SUFFIX = "'";
  public static final String HASH_PREFIX = "'";
  public static final String HASH_SUFFIX = "'";

  private final ICommonsOrderedSet <String> m_aList = new CommonsLinkedHashSet <> ();

  public AbstractCSPSourceList ()
  {}

  @Nonnegative
  public int getExpressionCount ()
  {
    return m_aList.size ();
  }

  /**
   * Add a scheme
   *
   * @param sScheme
   *        Scheme in the format <code>scheme ":"</code>
   * @return this
   */
  @Nonnull
  public IMPLTYPE addScheme (@Nonnull @Nonempty final String sScheme)
  {
    ValueEnforcer.notEmpty (sScheme, "Scheme");
    ValueEnforcer.isTrue (sScheme.length () > 1 && sScheme.endsWith (":"),
                          () -> "Passed scheme '" + sScheme + "' is invalid!");
    m_aList.add (sScheme);
    return thisAsT ();
  }

  /**
   * Add a host
   *
   * @param aHost
   *        Host to add. Must be a valid URL.
   * @return this
   */
  @Nonnull
  public IMPLTYPE addHost (@Nonnull final ISimpleURL aHost)
  {
    ValueEnforcer.notNull (aHost, "Host");
    return addHost (aHost.getAsString ());
  }

  /**
   * Add a host
   *
   * @param sHost
   *        Host to add. Must be a valid URL or a star prefixed version.
   * @return this
   */
  @Nonnull
  public IMPLTYPE addHost (@Nonnull @Nonempty final String sHost)
  {
    ValueEnforcer.notEmpty (sHost, "Host");
    m_aList.add (sHost);
    return thisAsT ();
  }

  /**
   * Add a MIME type (for plugin-types)
   *
   * @param aMimeType
   *        MIME type to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public IMPLTYPE addMimeType (@Nonnull final IMimeType aMimeType)
  {
    ValueEnforcer.notNull (aMimeType, "aMimeType");
    m_aList.add (aMimeType.getAsString ());
    return thisAsT ();
  }

  /**
   * source expression 'none' represents an empty set of URIs
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordNone ()
  {
    m_aList.add (KEYWORD_NONE);
    return thisAsT ();
  }

  /**
   * If this expression is included in a directive controlling scripts or styles, and the directive
   * causes the browser to block any inline scripts, inline styles, or event handler attributes,
   * then the violation report that the browser generates will contain a sample property containing
   * the first 40 characters of the blocked resource.
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordReportSample ()
  {
    m_aList.add (KEYWORD_REPORT_SAMPLE);
    return thisAsT ();
  }

  /**
   * source expression 'self' represents the set of URIs which are in the same origin as the
   * protected resource
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordSelf ()
  {
    m_aList.add (KEYWORD_SELF);
    return thisAsT ();
  }

  /**
   * The 'strict-dynamic' keyword makes the trust conferred on a script by a nonce or a hash extend
   * to scripts that this script dynamically loads, for example by creating new &lt;script&gt; tags
   * using Document.createElement() and then inserting them into the document using
   * Node.appendChild().
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordStrictDynamic ()
  {
    m_aList.add (KEYWORD_STRICT_DYNAMIC);
    return thisAsT ();
  }

  /**
   * By default, if a CSP contains a default-src or a script-src directive, then JavaScript
   * functions which evaluate their arguments as JavaScript are disabled. This includes "eval()",
   * the code argument to "setTimeout()", or the "Function()" constructor.
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordUnsafeEval ()
  {
    m_aList.add (KEYWORD_UNSAFE_EVAL);
    return thisAsT ();
  }

  /**
   * source expression 'unsafe-inline' represents content supplied inline in the resource itself
   *
   * @return this
   */
  @Nonnull
  public IMPLTYPE addKeywordUnsafeInline ()
  {
    m_aList.add (KEYWORD_UNSAFE_INLINE);
    return thisAsT ();
  }

  /**
   * Add the provided nonce value. The {@value #NONCE_PREFIX} and {@link #NONCE_SUFFIX} are added
   * automatically. The byte array is automatically Bas64 encoded.
   *
   * @param aNonceValue
   *        The plain nonce bytes. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public IMPLTYPE addNonce (@Nonnull @Nonempty final byte [] aNonceValue)
  {
    ValueEnforcer.notEmpty (aNonceValue, "NonceValue");
    return addNonce (Base64.safeEncodeBytes (aNonceValue));
  }

  /**
   * Add the provided Base64 encoded nonce value. The {@value #NONCE_PREFIX} and
   * {@link #NONCE_SUFFIX} are added automatically.
   *
   * @param sNonceBase64Value
   *        The Base64 encoded nonce value
   * @return this for chaining
   */
  @Nonnull
  public IMPLTYPE addNonce (@Nonnull @Nonempty final String sNonceBase64Value)
  {
    ValueEnforcer.notEmpty (sNonceBase64Value, "NonceBase64Value");

    m_aList.add (NONCE_PREFIX + sNonceBase64Value + NONCE_SUFFIX);
    return thisAsT ();
  }

  /**
   * Add the provided nonce value. The {@value #HASH_PREFIX} and {@link #HASH_SUFFIX} are added
   * automatically. The byte array is automatically Bas64 encoded!
   *
   * @param eMDAlgo
   *        The message digest algorithm used. May only {@link EMessageDigestAlgorithm#SHA_256},
   *        {@link EMessageDigestAlgorithm#SHA_384} or {@link EMessageDigestAlgorithm#SHA_512}. May
   *        not be <code>null</code>.
   * @param aHashValue
   *        The plain hash digest value. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public IMPLTYPE addHash (@Nonnull final EMessageDigestAlgorithm eMDAlgo, @Nonnull @Nonempty final byte [] aHashValue)
  {
    ValueEnforcer.notEmpty (aHashValue, "HashValue");
    return addHash (eMDAlgo, Base64.safeEncodeBytes (aHashValue));
  }

  /**
   * Add the provided Base64 encoded hash value. The {@value #HASH_PREFIX} and {@link #HASH_SUFFIX}
   * are added automatically.
   *
   * @param eMDAlgo
   *        The message digest algorithm used. May only {@link EMessageDigestAlgorithm#SHA_256},
   *        {@link EMessageDigestAlgorithm#SHA_384} or {@link EMessageDigestAlgorithm#SHA_512}. May
   *        not be <code>null</code>.
   * @param sHashBase64Value
   *        The Base64 encoded hash value
   * @return this for chaining
   */
  @Nonnull
  public IMPLTYPE addHash (@Nonnull final EMessageDigestAlgorithm eMDAlgo, @Nonnull final String sHashBase64Value)
  {
    ValueEnforcer.notNull (eMDAlgo, "MDAlgo");
    ValueEnforcer.notEmpty (sHashBase64Value, "HashBase64Value");

    String sAlgorithmName;
    switch (eMDAlgo)
    {
      case SHA_256:
        sAlgorithmName = "sha256";
        break;
      case SHA_384:
        sAlgorithmName = "sha384";
        break;
      case SHA_512:
        sAlgorithmName = "sha512";
        break;
      default:
        throw new IllegalArgumentException ("Only SHA256, SHA384 and SHA512 are supported algorithms");
    }

    m_aList.add (HASH_PREFIX + sAlgorithmName + "-" + sHashBase64Value + HASH_SUFFIX);
    return thisAsT ();
  }

  /**
   * @return The whole source list as a single string, separated by a blank char.
   */
  @Nonnull
  public String getAsString ()
  {
    return StringImplode.getImploded (' ', m_aList);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractCSPSourceList <?> rhs = (AbstractCSPSourceList <?>) o;
    return m_aList.equals (rhs.m_aList);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aList).getHashCode ();
  }

  @Override
  @Nonnull
  public String toString ()
  {
    return new ToStringGenerator (this).append ("List", m_aList).getToString ();
  }
}
