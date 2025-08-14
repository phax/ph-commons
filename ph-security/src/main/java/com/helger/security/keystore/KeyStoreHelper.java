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
package com.helger.security.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.functional.IThrowingConsumer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.lang.ClassHelper;
import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.resourceprovider.ClassPathResourceProvider;
import com.helger.commons.io.resourceprovider.FileSystemResourceProvider;
import com.helger.commons.io.resourceprovider.IReadableResourceProvider;
import com.helger.commons.io.resourceprovider.ReadableResourceProviderChain;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Helper methods to access Java key stores of type JKS (Java KeyStore) or PKCS12.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class KeyStoreHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (KeyStoreHelper.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static IReadableResourceProvider s_aResourceProvider = new ReadableResourceProviderChain (new FileSystemResourceProvider ().setCanReadRelativePaths (true),
                                                                                                    new ClassPathResourceProvider ());

  @PresentForCodeCoverage
  private static final KeyStoreHelper INSTANCE = new KeyStoreHelper ();

  private KeyStoreHelper ()
  {}

  @Nonnull
  public static IReadableResourceProvider getResourceProvider ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aResourceProvider);
  }

  public static void setResourceProvider (@Nonnull final IReadableResourceProvider aResourceProvider)
  {
    ValueEnforcer.notNull (aResourceProvider, "ResourceProvider");
    RW_LOCK.writeLocked ( () -> s_aResourceProvider = aResourceProvider);
  }

  @Nonnull
  public static KeyStore getSimiliarKeyStore (@Nonnull final KeyStore aOther) throws KeyStoreException
  {
    ValueEnforcer.notNull (aOther, "Other");

    return getSimiliarKeyStore (aOther, null);
  }

  @Nonnull
  public static KeyStore getSimiliarKeyStore (@Nonnull final KeyStore aOther,
                                              @Nullable final Provider aSecurityProvider) throws KeyStoreException
  {
    ValueEnforcer.notNull (aOther, "Other");

    return KeyStore.getInstance (aOther.getType (),
                                 aSecurityProvider != null ? aSecurityProvider : aOther.getProvider ());
  }

  /**
   * Load a key store from a resource.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        The path pointing to the key store. May only be <code>null</code> for
   *        {@link EKeyStoreType#PKCS11} or other key store types that don't require a path.
   * @param aKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no password is
   *        required.
   * @return The Java key-store object.
   * @see KeyStore#load(InputStream, char[])
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   * @throws IllegalArgumentException
   *         If the key store path is invalid
   */
  @Nonnull
  public static KeyStore loadKeyStoreDirect (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nullable final String sKeyStorePath,
                                             @Nullable final char [] aKeyStorePassword) throws GeneralSecurityException,
                                                                                        IOException
  {
    return loadKeyStoreDirect (aKeyStoreType, sKeyStorePath, aKeyStorePassword, null);
  }

  /**
   * Load a key store from a resource.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        The path pointing to the key store. May only be <code>null</code> for
   *        {@link EKeyStoreType#PKCS11} or other key store types that don't require a path.
   * @param aKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no password is
   *        required.
   * @param aSecurityProvider
   *        The Security Provider to use. May be <code>null</code>.
   * @return The Java key-store object.
   * @see KeyStore#load(InputStream, char[])
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   * @throws IllegalArgumentException
   *         If the key store path is invalid
   * @since 11.1.1
   */
  @Nonnull
  public static KeyStore loadKeyStoreDirect (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nullable final String sKeyStorePath,
                                             @Nullable final char [] aKeyStorePassword,
                                             @Nullable final Provider aSecurityProvider) throws GeneralSecurityException,
                                                                                         IOException
  {
    ValueEnforcer.notNull (aKeyStoreType, "KeyStoreType");

    final InputStream aIS;
    if (aKeyStoreType.isKeyStorePathRequired ())
    {
      ValueEnforcer.notNull (sKeyStorePath, "KeyStorePath");

      // Open the resource stream
      aIS = getResourceProvider ().getInputStream (sKeyStorePath);
      if (aIS == null)
        throw new IllegalArgumentException ("Failed to open key store '" + sKeyStorePath + "'");
    }
    else
    {
      aIS = null;
    }
    try
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Trying to load key store from path '" +
                      sKeyStorePath +
                      "' using type " +
                      aKeyStoreType.getID ());

      final KeyStore aKeyStore = aSecurityProvider != null ? aKeyStoreType.getKeyStore (aSecurityProvider)
                                                           : aKeyStoreType.getKeyStore ();
      aKeyStore.load (aIS, aKeyStorePassword);
      return aKeyStore;
    }
    catch (final KeyStoreException ex)
    {
      throw new IllegalStateException ("No provider can handle key stores of type " + aKeyStoreType, ex);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  /**
   * Create a new key store based on an existing key store
   *
   * @param aBaseKeyStore
   *        The source key store. May not be <code>null</code>
   * @param sAliasToCopy
   *        The name of the alias in the source key store that should be put in the new key store
   * @param aAliasPassword
   *        The optional password to access the alias in the source key store. If it is not
   *        <code>null</code> the same password will be used in the created key store
   * @return The created in-memory key store
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   */
  @Nonnull
  public static KeyStore createKeyStoreWithOnlyOneItem (@Nonnull final KeyStore aBaseKeyStore,
                                                        @Nonnull final String sAliasToCopy,
                                                        @Nullable final char [] aAliasPassword) throws GeneralSecurityException,
                                                                                                IOException
  {
    return createKeyStoreWithOnlyOneItem (aBaseKeyStore, sAliasToCopy, aAliasPassword, null);
  }

  /**
   * Create a new key store based on an existing key store
   *
   * @param aBaseKeyStore
   *        The source key store. May not be <code>null</code>
   * @param sAliasToCopy
   *        The name of the alias in the source key store that should be put in the new key store
   * @param aAliasPassword
   *        The optional password to access the alias in the source key store. If it is not
   *        <code>null</code> the same password will be used in the created key store
   * @param aSecurityProvider
   *        The Security Provider to use. May be <code>null</code>.
   * @return The created in-memory key store
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   * @since 11.1.1
   */
  @Nonnull
  public static KeyStore createKeyStoreWithOnlyOneItem (@Nonnull final KeyStore aBaseKeyStore,
                                                        @Nonnull final String sAliasToCopy,
                                                        @Nullable final char [] aAliasPassword,
                                                        @Nullable final Provider aSecurityProvider) throws GeneralSecurityException,
                                                                                                    IOException
  {
    ValueEnforcer.notNull (aBaseKeyStore, "BaseKeyStore");
    ValueEnforcer.notNull (sAliasToCopy, "AliasToCopy");

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Create a new key store using type " + aBaseKeyStore.getType ());

    final KeyStore aKeyStore = getSimiliarKeyStore (aBaseKeyStore, aSecurityProvider);
    // null stream means: create new key store
    aKeyStore.load (null, null);

    // Do we need a password?
    ProtectionParameter aPP = null;
    if (aAliasPassword != null)
      aPP = new PasswordProtection (aAliasPassword);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Copying alias '" + sAliasToCopy + "' from old key store to new key store");

    aKeyStore.setEntry (sAliasToCopy, aBaseKeyStore.getEntry (sAliasToCopy, aPP), aPP);
    return aKeyStore;
  }

  private static boolean _isInvalidPasswordException (@Nonnull final Exception ex)
  {
    return ex instanceof IOException && ex.getCause () instanceof UnrecoverableKeyException;
  }

  /**
   * Load the provided key store in a safe manner.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Path to the key store. May not be <code>null</code> for all key store types that require
   *        a path.
   * @param aKeyStorePassword
   *        Password for the key store. May not be <code>null</code> to succeed.
   * @return The key store loading result. Never <code>null</code>.
   * @since 11.1.9
   */
  @Nonnull
  public static LoadedKeyStore loadKeyStore (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nullable final String sKeyStorePath,
                                             @Nullable final char [] aKeyStorePassword)
  {
    return loadKeyStore (aKeyStoreType, sKeyStorePath, aKeyStorePassword, null);
  }

  /**
   * Load the provided key store in a safe manner.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Path to the key store. May not be <code>null</code> for all key store types that require
   *        a path.
   * @param aKeyStorePassword
   *        Password for the key store. May not be <code>null</code> to succeed.
   * @param aSecurityProvider
   *        The Security Provider to use. May be <code>null</code>.
   * @return The key store loading result. Never <code>null</code>.
   * @since 11.1.9
   */
  @Nonnull
  public static LoadedKeyStore loadKeyStore (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nullable final String sKeyStorePath,
                                             @Nullable final char [] aKeyStorePassword,
                                             @Nullable final Provider aSecurityProvider)
  {
    ValueEnforcer.notNull (aKeyStoreType, "KeyStoreType");

    // Get the parameters for the key store
    if (aKeyStoreType.isKeyStorePathRequired () && Strings.isEmpty (sKeyStorePath))
      return new LoadedKeyStore (null, EKeyStoreLoadError.KEYSTORE_NO_PATH);

    final KeyStore aKeyStore;
    // Try to load key store
    try
    {
      aKeyStore = loadKeyStoreDirect (aKeyStoreType, sKeyStorePath, aKeyStorePassword, aSecurityProvider);
    }
    catch (final IllegalArgumentException ex)
    {
      LOGGER.warn ("No such key store '" + sKeyStorePath + "': " + ex.getMessage (), ex.getCause ());

      return new LoadedKeyStore (null,
                                 EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_NON_EXISTING,
                                 sKeyStorePath,
                                 ex.getMessage ());
    }
    catch (final Exception ex)
    {
      final boolean bInvalidPW = _isInvalidPasswordException (ex);

      LOGGER.warn ("Failed to load key store '" +
                   sKeyStorePath +
                   "' of type " +
                   aKeyStoreType.getID () +
                   ": " +
                   ex.getMessage (),
                   bInvalidPW ? null : ex.getCause ());

      return new LoadedKeyStore (null,
                                 bInvalidPW ? EKeyStoreLoadError.KEYSTORE_INVALID_PASSWORD
                                            : EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_FORMAT_ERROR,
                                 sKeyStorePath,
                                 ex.getMessage ());
    }

    // Finally success
    return new LoadedKeyStore (aKeyStore, null);
  }

  @Nonnull
  private static <T extends KeyStore.Entry> LoadedKey <T> _loadKey (@Nonnull final KeyStore aKeyStore,
                                                                    @Nonnull final String sKeyStorePath,
                                                                    @Nullable final String sKeyStoreKeyAlias,
                                                                    @Nullable final char [] aKeyStoreKeyPassword,
                                                                    @Nonnull final Class <T> aTargetClass)
  {
    ValueEnforcer.notNull (aKeyStore, "KeyStore");
    ValueEnforcer.notNull (sKeyStorePath, "KeyStorePath");
    ValueEnforcer.notNull (aTargetClass, "TargetClass");

    if (Strings.isEmpty (sKeyStoreKeyAlias))
      return new LoadedKey <> (null, EKeyStoreLoadError.KEY_NO_ALIAS, sKeyStorePath);

    if (aKeyStoreKeyPassword == null)
      return new LoadedKey <> (null, EKeyStoreLoadError.KEY_NO_PASSWORD, sKeyStoreKeyAlias, sKeyStorePath);

    // Try to load the key.
    final T aKeyEntry;
    try
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Trying to access key store entry with alias '" +
                      sKeyStoreKeyAlias +
                      "' as a " +
                      aTargetClass.getName ());

      final KeyStore.ProtectionParameter aProtection = new KeyStore.PasswordProtection (aKeyStoreKeyPassword);
      final KeyStore.Entry aEntry = aKeyStore.getEntry (sKeyStoreKeyAlias, aProtection);
      if (aEntry == null)
      {
        // No such entry
        return new LoadedKey <> (null, EKeyStoreLoadError.KEY_INVALID_ALIAS, sKeyStoreKeyAlias, sKeyStorePath);
      }
      if (!aTargetClass.isAssignableFrom (aEntry.getClass ()))
      {
        // Not a matching
        return new LoadedKey <> (null,
                                 EKeyStoreLoadError.KEY_INVALID_TYPE,
                                 sKeyStoreKeyAlias,
                                 sKeyStorePath,
                                 ClassHelper.getClassName (aEntry));
      }
      aKeyEntry = aTargetClass.cast (aEntry);
    }
    catch (final UnrecoverableKeyException ex)
    {
      return new LoadedKey <> (null,
                               EKeyStoreLoadError.KEY_INVALID_PASSWORD,
                               sKeyStoreKeyAlias,
                               sKeyStorePath,
                               ex.getMessage ());
    }
    catch (final GeneralSecurityException ex)
    {
      return new LoadedKey <> (null,
                               EKeyStoreLoadError.KEY_LOAD_ERROR,
                               sKeyStoreKeyAlias,
                               sKeyStorePath,
                               ex.getMessage ());
    }

    // Finally success
    return new LoadedKey <> (aKeyEntry, null);
  }

  /**
   * Load the specified private key entry from the provided key store.
   *
   * @param aKeyStore
   *        The key store to load the key from. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Key store path. For nice error messages only. May not be <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non- <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to succeed.
   * @return The key loading result. Never <code>null</code>.
   */
  @Nonnull
  public static LoadedKey <KeyStore.PrivateKeyEntry> loadPrivateKey (@Nonnull final KeyStore aKeyStore,
                                                                     @Nonnull final String sKeyStorePath,
                                                                     @Nullable final String sKeyStoreKeyAlias,
                                                                     @Nullable final char [] aKeyStoreKeyPassword)
  {
    return _loadKey (aKeyStore, sKeyStorePath, sKeyStoreKeyAlias, aKeyStoreKeyPassword, KeyStore.PrivateKeyEntry.class);
  }

  /**
   * Load the specified secret key entry from the provided key store.
   *
   * @param aKeyStore
   *        The key store to load the key from. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Key store path. For nice error messages only. May not be <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non- <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to succeed.
   * @return The key loading result. Never <code>null</code>.
   */
  @Nonnull
  public static LoadedKey <KeyStore.SecretKeyEntry> loadSecretKey (@Nonnull final KeyStore aKeyStore,
                                                                   @Nonnull final String sKeyStorePath,
                                                                   @Nullable final String sKeyStoreKeyAlias,
                                                                   @Nullable final char [] aKeyStoreKeyPassword)
  {
    return _loadKey (aKeyStore, sKeyStorePath, sKeyStoreKeyAlias, aKeyStoreKeyPassword, KeyStore.SecretKeyEntry.class);
  }

  /**
   * Load the specified private key entry from the provided key store.
   *
   * @param aKeyStore
   *        The key store to load the key from. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Key store path. For nice error messages only. May not be <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non- <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to succeed.
   * @return The key loading result. Never <code>null</code>.
   */
  @Nonnull
  public static LoadedKey <KeyStore.TrustedCertificateEntry> loadTrustedCertificateKey (@Nonnull final KeyStore aKeyStore,
                                                                                        @Nonnull final String sKeyStorePath,
                                                                                        @Nullable final String sKeyStoreKeyAlias,
                                                                                        @Nullable final char [] aKeyStoreKeyPassword)
  {
    return _loadKey (aKeyStore,
                     sKeyStorePath,
                     sKeyStoreKeyAlias,
                     aKeyStoreKeyPassword,
                     KeyStore.TrustedCertificateEntry.class);
  }

  /**
   * Helper method to iterate all aliases of a key store
   *
   * @param aKeyStore
   *        The key store to be iterated. May not be <code>null</code>.
   * @param aAliasConsumer
   *        The consumer for each alias. May not be <code>null</code>.
   * @since 11.1.10
   */
  public static void iterateKeyStore (@Nonnull final KeyStore aKeyStore,
                                      @Nonnull final IThrowingConsumer <String, KeyStoreException> aAliasConsumer)
  {
    ValueEnforcer.notNull (aKeyStore, "KeyStore");
    ValueEnforcer.notNull (aAliasConsumer, "AliasConsumer");

    try
    {
      final Enumeration <String> aAliases = aKeyStore.aliases ();
      while (aAliases.hasMoreElements ())
      {
        final String sAlias = aAliases.nextElement ();
        aAliasConsumer.accept (sAlias);
      }
    }
    catch (final KeyStoreException ex)
    {
      LOGGER.warn ("Failed to iterate key store", ex);
    }
  }

  /**
   * Get all trusted certificates
   *
   * @param aTrustStore
   *        Trust store to iterate
   * @return A non-<code>null</code> set of all trusted certificates. Never <code>null</code>.
   * @since 11.2.0
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <X509Certificate> getAllTrustedCertificates (@Nullable final KeyStore aTrustStore)
  {
    final ICommonsSet <X509Certificate> aCerts = new CommonsHashSet <> ();
    if (aTrustStore != null)
      iterateKeyStore (aTrustStore, alias -> {
        if (aTrustStore.isCertificateEntry (alias))
        {
          final Certificate aCert = aTrustStore.getCertificate (alias);
          if (aCert instanceof final X509Certificate aX509Cert)
            aCerts.add (aX509Cert);
        }
      });
    return aCerts;
  }
}
