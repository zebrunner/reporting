package com.qaprosoft.zafira.services.util;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Created by irina on 16.8.17.
 */

public class SaltManager
{
	public String generateSalt()
	{
		final Random random = new SecureRandom();
		byte[] salt = new byte[32];
		random.nextBytes(salt);
		return saltToString(salt);
	}

	public void saveSalt(String salt, File file) throws IOException
	{
		FileUtils.writeByteArrayToFile(file, Base64.encodeBase64(salt.getBytes()));
	}

	public String loadSalt(File file) throws IOException
	{
		return saltToString(FileUtils.readFileToByteArray(file));
	}

	public String saltToString(byte[] input)
	{
		return Base64.encodeBase64String(input);
	}

	// public String getSalt() {
	// String salt = null;
	// File file = new File("./src/main/resources/salt.properties");
	// try {
	// salt = loadSalt(file);
	// if (salt == null){
	// salt = generateSalt();
	// saveSalt(salt,file);
	// }
	// } catch (IOException e) {
	// LOGGER.error("Unable to load or save salt.properties, check your salt.properties file " + e.getMessage(), e);
	// }
	// return salt;
	// }
}
