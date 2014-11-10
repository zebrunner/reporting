package com.zafira.services.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class Tokenizer
{
	private SecureRandom prng;
	private MessageDigest sha;

	public Tokenizer(String srAlg, String mdAlg) throws NoSuchAlgorithmException
	{
		this.prng = SecureRandom.getInstance("SHA1PRNG");
		this.sha = MessageDigest.getInstance("SHA-1");
	}

	public String randomToken()
	{
		String randomNum = new Integer(prng.nextInt()).toString();
		return new String(Hex.encodeHex(sha.digest(randomNum.getBytes())));
	}
}
