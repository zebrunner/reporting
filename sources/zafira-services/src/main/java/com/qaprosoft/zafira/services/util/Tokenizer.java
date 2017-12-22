/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.util;

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
