/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goowee.commons.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.security.SecureRandom

/**
 * Utility class for symmetric AES encryption and key management.
 * <p>
 * Provides methods for generating AES keys, saving/loading keys from files,
 * and encrypting/decrypting strings using AES in CBC mode with PKCS5 padding.
 * <p>
 * The class ensures random IV generation for each encryption
 * and combines the IV with the ciphertext for proper decryption.
 * <p>
 * Example usage:
 * <pre>
 * byte[] key = CryptoUtils.generateAESKey()
 * CryptoUtils.saveAESKey(key, 'secret.key')
 * byte[] loadedKey = CryptoUtils.loadAESKey('secret.key')
 *
 * String encrypted = CryptoUtils.encrypt('Hello World', loadedKey)
 * String decrypted = CryptoUtils.decrypt(encrypted, loadedKey)
 * </pre>
 */
@Slf4j
@CompileStatic
class CryptoUtils {

    private static final String CIPHER_ALGORITHM = 'AES/CBC/PKCS5Padding'

    /**
     * Generates a random AES key of the specified length.
     *
     * @param bytes the length of the key in bytes (default is 32 bytes = 256 bits)
     * @return a byte array containing the generated AES key
     */
    static byte[] generateAESKey(int bytes = 32) {
        byte[] key = new byte[bytes]
        new SecureRandom().nextBytes(key)
        return key
    }

    /**
     * Saves an AES key to a file at the specified path.
     * <p>
     * If the filesystem supports POSIX file permissions, the file is
     * restricted to read/write access for the owner only.
     *
     * @param password the AES key to save as a byte array
     * @param pathname the path to the file where the key will be saved
     */
    static void saveAESKey(byte[] password, String pathname) {
        Path path = Paths.get(pathname)
        Files.write(path, password)

        // Set file permissions if the filesystem supports POSIX
        if (!Files.getFileStore(path).supportsFileAttributeView('posix')) {
            log.warn "Non-POSIX filesystem detected. '${pathname}' file permissions were not set. Ensure the file is protected."
            return
        }

        try {
            Set<PosixFilePermission> perms = [
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            ] as Set
            Files.setPosixFilePermissions(path, perms)

        } catch (Exception e) {
            log.warn "Unable to set POSIX permissions: ${e.message}"
        }
    }

    /**
     * Loads an AES key from the specified file.
     *
     * @param pathname the path to the file containing the AES key
     * @return a byte array with the loaded key, or {@code null} if an error occurs
     */
    static byte[] loadAESKey(String pathname) {
        try {
            Path path = Paths.get(pathname)
            byte[] keyStorePassword = Files.readAllBytes(path)
            return keyStorePassword

        } catch (Exception e) {
            log.error "Error loading '${pathname}': ${e.message}"
            log.info LogUtils.logStackTrace(e)
            return null
        }
    }

    /**
     * Encrypts a string using AES with the provided symmetric key.
     * <p>
     * A random 16-byte IV is generated for each encryption. The resulting
     * ciphertext is combined with the IV and encoded in Base64.
     *
     * <p>Example:
     * <pre>
     * byte[] key = CryptoUtils.generateAESKey()
     * String encrypted = CryptoUtils.encrypt("Hello World", key)
     * </pre>
     *
     * @param value the string to encrypt
     * @param symmetricKey the AES key as a byte array
     * @return the Base64-encoded ciphertext (including IV), or an empty string if input is null or empty
     */
    static String encrypt(String value, byte[] symmetricKey) {
        if (!value) {
            return ''
        }

        if (!symmetricKey) {
            throw new Exception("Encryption error: no key provided.")
        }

        // Generate random IV
        byte[] iv = new byte[16]
        new SecureRandom().nextBytes(iv)

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey, 'AES')
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv))

        byte[] encrypted = cipher.doFinal(value.bytes)

        // Combine IV + encrypted data
        byte[] combined = new byte[iv.length + encrypted.length]
        System.arraycopy(iv, 0, combined, 0, iv.length)
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length)

        return Base64.encoder.encodeToString(combined)
    }

    /**
     * Decrypts a Base64-encoded AES-encrypted string.
     * <p>
     * Expects the input to contain the IV as the first 16 bytes, followed by
     * the ciphertext. Returns an empty string if input is null or empty.
     *
     * <p>Example:
     * <pre>
     * byte[] key = CryptoUtils.generateAESKey()
     * String encrypted = CryptoUtils.encrypt("Hello World", key)
     * String decrypted = CryptoUtils.decrypt(encrypted, key)
     * </pre>
     *
     * @param encryptedValue the Base64-encoded ciphertext including the IV
     * @param symmetricKey the AES key as a byte array
     * @return the decrypted string
     */
    static String decrypt(String encryptedValue, byte[] symmetricKey) {
        if (!encryptedValue) {
            return ''
        }

        byte[] data = Base64.decoder.decode(encryptedValue)
        byte[] iv = data[0..<16] as byte[]
        byte[] encrypted = data[16..-1] as byte[]

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey, 'AES')
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv))

        byte[] decrypted = cipher.doFinal(encrypted)
        return new String(decrypted)
    }
}
