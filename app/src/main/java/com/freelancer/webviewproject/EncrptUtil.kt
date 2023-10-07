package com.freelancer.webviewproject

import android.os.Build
import android.util.Base64
import android.util.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec



object EncryptionUtils {
    private const val ENCRYPTION_ALGORITHM = "AES/CBC/PKCS7Padding"
    private const val SECRET_KEY_ALGORITHM = "AES"

    private const val SECRET_KEY = com.freelancer.webviewproject.BuildConfig.secretKey
    private const val IV = com.freelancer.webviewproject.BuildConfig.ivKey

    private const val D_SECRET_KEY = com.freelancer.webviewproject.BuildConfig.secretDKey
    private const val D_IV = com.freelancer.webviewproject.BuildConfig.ivDKey
    @Throws(Exception::class)
    fun encryptData(data: String): String {
        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        val secretKeySpec =
            SecretKeySpec(SECRET_KEY.toByteArray(charset("UTF-8")), SECRET_KEY_ALGORITHM)
        val ivParameterSpec = IvParameterSpec(IV.toByteArray(charset("UTF-8")))
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedBytes = cipher.doFinal(data.toByteArray(charset("UTF-8")))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted,Base64.NO_WRAP)
    }

    fun decrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
        val secretKeySpec = SecretKeySpec(D_SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(D_IV.toByteArray(Charsets.UTF_8))
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decrypted = cipher.doFinal(Base64.decode(data,Base64.NO_WRAP))
        return String(decrypted, Charsets.UTF_8)
    }

}