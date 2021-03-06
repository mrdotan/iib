package com.key;

/**
 * 密钥查找器
 * POS终端的工作密钥与终端标识相关
 * @author  Liang Yabao
 * 2012-3-22
 */
public interface KeyFinder {

	public byte[] findKey(String terminalID);
	
	public byte[] findMasterKey();
	
}