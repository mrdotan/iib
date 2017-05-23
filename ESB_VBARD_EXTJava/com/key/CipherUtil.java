package com.key;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
public class CipherUtil {
	private KeyFinder finder;
	private static final String algorithm ="DESede";
	//private static final String algorithm ="DESede/CBC/Zeros";
	//private static final String algorithm ="DESede/CBC/PKCS5Padding";
	//private static final String algorithm ="DESede/ECB/NoPadding";
	//private static final String algorithm ="DESede/CBC/PKCS5";
	//private static final String algorithm ="DESede/ECB/PKCS7";
	
	
	private SecretKey masterKey = null;
	Cipher workingKeyDecryptor;
	Cipher workingKeyEncryptor;

	/**
	 * 设置主密钥 algorithm ="DESede"
	 * @param masterKey 
	 * @throws Exception 
	 */
	public void setMasterKey(SecretKey masterKey) throws Exception {
		this.masterKey = masterKey;
		try {
			workingKeyDecryptor = Cipher.getInstance(this.algorithm);
			workingKeyDecryptor.init(Cipher.DECRYPT_MODE, masterKey);
			workingKeyEncryptor = Cipher.getInstance(this.algorithm);
			workingKeyEncryptor.init(Cipher.ENCRYPT_MODE, masterKey);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e);
		} catch (NoSuchPaddingException e) {
			throw new Exception(e);
		} catch (InvalidKeyException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 设置主密钥
	 * @param bytes	24 Byte for triple DES algorithan
	 * @throws Exception 
	 */
	public void setMasterKey(byte[] bytes) throws Exception{
		try {
			this.masterKey = this.generateKey(bytes);
			this.setMasterKey(masterKey);
		} catch (InvalidKeyException e) {
			throw new Exception(e);
		} catch (InvalidKeySpecException e) {
			throw new Exception(e);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e);
		}
	}
	
	public SecretKey getMasterKey() {
		return masterKey;
	}

	/**
	 * 加密工作依赖于工作密钥查找器，每一个巴枪对应于一个工作密钥
	 * @param finder	工作密钥查找器
	 */
	public CipherUtil(KeyFinder finder){
		this.finder = finder;
	}
	
	/**
	 * 根据字节数组生成密钥对象
	 * @param bytes	密钥
	 * @return	密钥对象
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException	没有对应的加密算法
	 */
	public SecretKey generateKey(byte[] bytes) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException{
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(this.algorithm);
		SecretKey securekey = keyFactory.generateSecret(new DESedeKeySpec(bytes));
		return securekey;
	}
	
	protected Cipher decryptor(String  terminalID) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException{
		byte[] keyBytes = finder.findKey(terminalID);
		Key securekey = this.generateKey(keyBytes);
		Cipher decryptor = Cipher.getInstance(this.algorithm);
		decryptor.init(Cipher.DECRYPT_MODE, securekey);
		System.out.println("算法："+decryptor.getAlgorithm());
		return decryptor;
	}
	protected Cipher encryptor(String  terminalID) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException{
		byte[] keyBytes = finder.findKey(terminalID);
		Key securekey = this.generateKey(keyBytes);
		Cipher encryptor = Cipher.getInstance(this.algorithm);
		encryptor.init(Cipher.ENCRYPT_MODE, securekey);
		return encryptor;
	}
	
	/**
	 * 解密mac
	 * @param terminalID
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(String terminalID,byte[] data) throws Exception {
		try {
			Cipher decryptor = this.decryptor(terminalID);
			return decryptor.doFinal(data);
	
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e);
		} catch (InvalidKeySpecException e) {
			throw new Exception(e);
		} catch (NoSuchPaddingException e) {
			throw new Exception(e);
		} catch (InvalidKeyException e) {
			throw new Exception(e);
		} catch (IllegalBlockSizeException e) {
			throw new Exception(e);
		} catch (BadPaddingException e) {
			throw new Exception(e);
		}
	}

	/**
	 * 加密mac
	 * @param terminalID
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(String terminalID,byte[] data) throws Exception {
		try {
			Cipher encryptor = this.encryptor(terminalID);
			return encryptor.doFinal(data);
	
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e);
		} catch (InvalidKeySpecException e) {
			throw new Exception(e);
		} catch (NoSuchPaddingException e) {
			throw new Exception(e);
		} catch (InvalidKeyException e) {
			throw new Exception(e);
		} catch (IllegalBlockSizeException e) {
			throw new Exception(e);
		} catch (BadPaddingException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 解密个人密码
	 * @param terminalID	终端标识
	 * @param pinData	个人密码密文
	 * @return	个人密码明文
	 * @throws Exception
	 */
	public String decryptPassword(String terminalID, byte[] pinData) throws Exception{
		byte[] pin = this.decrypt(terminalID, pinData);
		try {
			return new String(pin);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加密个人密码
	 * @param terminalID	终端标识
	 * @param password	个人密码明文
	 * @return	个人密码密文
	 * @throws Exception
	 */
	public byte[] encryptPassword(String terminalID,String password) throws Exception{
		byte[] pin = null;
		try {
			pin = this.encrypt(terminalID, password.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pin;
	}
	
	/**
	 * 解密工作密钥
	 * @param key	工作密钥密文
	 * @return	工作密钥明文
	 * @throws Exception
	 */
	public byte[] decryptWorkingKey(byte[] key) throws Exception{
		
		try {
			return workingKeyDecryptor.doFinal(key);
		} catch (IllegalBlockSizeException e) {
			throw new Exception(e);
		} catch (BadPaddingException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 加密工作密钥
	 * @param key	工作密钥明文
	 * @return	工作密钥密文
	 * @throws Exception
	 */
	public byte[] encryptWorkingKey(byte[] key) throws Exception{
		
		try {
			return workingKeyEncryptor.doFinal(key);
		} catch (IllegalBlockSizeException e) {
			throw new Exception(e);
		} catch (BadPaddingException e) {
			throw new Exception(e);
		}
	}
}
