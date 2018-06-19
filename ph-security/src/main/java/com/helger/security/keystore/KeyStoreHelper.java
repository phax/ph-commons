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
package com.helger.security.keystore;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.io.resourceprovider.ClassPathResourceProvider;
import com.helger.commons.io.resourceprovider.FileSystemResourceProvider;
import com.helger.commons.io.resourceprovider.IReadableResourceProvider;
import com.helger.commons.io.resourceprovider.ReadableResourceProviderChain;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;

/**
 * Helper methods to access Java key stores of type JKS (Java KeyStore).
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@ThreadSafe
public final class KeyStoreHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (KeyStoreHelper.class);
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static IReadableResourceProvider s_aResourceProvider = new ReadableResourceProviderChain (new FileSystemResourceProvider ().setCanReadRelativePaths (true),
                                                                                                    new ClassPathResourceProvider ());

  @PresentForCodeCoverage
  private static final KeyStoreHelper s_aInstance = new KeyStoreHelper ();

  private KeyStoreHelper ()
  {}

  @Nonnull
  public static IReadableResourceProvider getResourceProvider ()
  {
    return s_aRWLock.readLocked ( () -> s_aResourceProvider);
  }

  public static void setResourceProvider (@Nonnull final IReadableResourceProvider aResourceProvider)
  {
    ValueEnforcer.notNull (aResourceProvider, "ResourceProvider");
    s_aRWLock.writeLocked ( () -> s_aResourceProvider = aResourceProvider);
  }

  @Nonnull
  public static KeyStore getSimiliarKeyStore (@Nonnull final KeyStore aOther) throws KeyStoreException
  {
    return KeyStore.getInstance (aOther.getType (), aOther.getProvider ());
  }

  /**
   * Load a key store from a resource.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        The path pointing to the key store. May not be <code>null</code>.
   * @param sKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
   * @return The Java key-store object.
   * @throws GeneralSecurityException
   *         In case of a key store error
   * @throws IOException
   *         In case key store loading fails
   * @throws IllegalArgumentException
   *         If the key store path is invalid
   */
  @Nonnull
  public static KeyStore loadKeyStoreDirect (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nonnull final String sKeyStorePath,
                                             @Nullable final String sKeyStorePassword) throws GeneralSecurityException,
                                                                                       IOException
  {
    return loadKeyStoreDirect (aKeyStoreType,
                               sKeyStorePath,
                               sKeyStorePassword == null ? null : sKeyStorePassword.toCharArray ());
  }

  /**
   * Load a key store from a resource.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        The path pointing to the key store. May not be <code>null</code>.
   * @param aKeyStorePassword
   *        The key store password. May be <code>null</code> to indicate that no
   *        password is required.
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
                                             @Nonnull final String sKeyStorePath,
                                             @Nullable final char [] aKeyStorePassword) throws GeneralSecurityException,
                                                                                        IOException
  {
    ValueEnforcer.notNull (aKeyStoreType, "KeyStoreType");
    ValueEnforcer.notNull (sKeyStorePath, "KeyStorePath");

    // Open the resource stream
    final InputStream aIS = getResourceProvider ().getInputStream (sKeyStorePath);
    if (aIS == null)
      throw new IllegalArgumentException ("Failed to open key store '" + sKeyStorePath + "'");

    try
    {
      final KeyStore aKeyStore = aKeyStoreType.getKeyStore ();
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
   *        The name of the alias in the source key store that should be put in
   *        the new key store
   * @param aAliasPassword
   *        The optional password to access the alias in the source key store.
   *        If it is not <code>null</code> the same password will be used in the
   *        created key store
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
    ValueEnforcer.notNull (aBaseKeyStore, "BaseKeyStore");
    ValueEnforcer.notNull (sAliasToCopy, "AliasToCopy");

    final KeyStore aKeyStore = getSimiliarKeyStore (aBaseKeyStore);
    // null stream means: create new key store
    aKeyStore.load (null, null);

    // Do we need a password?
    ProtectionParameter aPP = null;
    if (aAliasPassword != null)
      aPP = new PasswordProtection (aAliasPassword);

    aKeyStore.setEntry (sAliasToCopy, aBaseKeyStore.getEntry (sAliasToCopy, aPP), aPP);
    return aKeyStore;
  }

  /**
   * Load the provided key store in a safe manner.
   *
   * @param aKeyStoreType
   *        Type of key store. May not be <code>null</code>.
   * @param sKeyStorePath
   *        Path to the key store. May not be <code>null</code> to succeed.
   * @param sKeyStorePassword
   *        Password for the key store. May not be <code>null</code> to succeed.
   * @return The key store loading result. Never <code>null</code>.
   */
  @Nonnull
  public static LoadedKeyStore loadKeyStore (@Nonnull final IKeyStoreType aKeyStoreType,
                                             @Nullable final String sKeyStorePath,
                                             @Nullable final String sKeyStorePassword)
  {
    ValueEnforcer.notNull (aKeyStoreType, "KeyStoreType");

    // Get the parameters for the key store
    if (StringHelper.hasNoText (sKeyStorePath))
      return new LoadedKeyStore (null, EKeyStoreLoadError.KEYSTORE_NO_PATH);

    KeyStore aKeyStore = null;
    // Try to load key store
    try
    {
      aKeyStore = loadKeyStoreDirect (aKeyStoreType, sKeyStorePath, sKeyStorePassword);
    }
    catch (final IllegalArgumentException ex)
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("No such key store '" + sKeyStorePath + "': " + ex.getMessage (), ex.getCause ());

      return new LoadedKeyStore (null,
                                 EKeyStoreLoadError.KEYSTORE_LOAD_ERROR_NON_EXISTING,
                                 sKeyStorePath,
                                 ex.getMessage ());
    }
    catch (final Exception ex)
    {
      final boolean bInvalidPW = ex instanceof IOException && ex.getCause () instanceof UnrecoverableKeyException;

      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("Failed to load key store '" + sKeyStorePath + "': " + ex.getMessage (),
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

    if (StringHelper.hasNoText (sKeyStoreKeyAlias))
      return new LoadedKey <> (null, EKeyStoreLoadError.KEY_NO_ALIAS, sKeyStorePath);

    if (aKeyStoreKeyPassword == null)
      return new LoadedKey <> (null, EKeyStoreLoadError.KEY_NO_PASSWORD, sKeyStoreKeyAlias, sKeyStorePath);

    // Try to load the key.
    T aKeyEntry = null;
    try
    {
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
   *        Key store path. For nice error messages only. May be
   *        <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non-
   *        <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to
   *        succeed.
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
   *        Key store path. For nice error messages only. May be
   *        <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non-
   *        <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to
   *        succeed.
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
   *        Key store path. For nice error messages only. May be
   *        <code>null</code>.
   * @param sKeyStoreKeyAlias
   *        The alias to be resolved in the key store. Must be non-
   *        <code>null</code> to succeed.
   * @param aKeyStoreKeyPassword
   *        The key password for the key store. Must be non-<code>null</code> to
   *        succeed.
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
}
