package br.com.jbst.components;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

@Component
public class MD5Component {
	public String encrypt(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(value.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String encryptedValue = no.toString(16);
			while (encryptedValue.length() < 32) {
				encryptedValue = "0" + encryptedValue;
			}
			return encryptedValue;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}