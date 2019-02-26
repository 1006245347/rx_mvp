package jason.com.rxremvplib.utils;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {
	public static String encrypt(String data, String key) throws Exception {
		try {
			/*String data = "Test String";
			String key = "1234567812345678";*/
			String iv = key;
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			return Base64Utils.encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String desEncrypt(String data, String key) throws Exception {
		try {
			/*String data = "2fbwW9+8vPId2/foafZq6Q==";
			String key = "1234567812345678";*/
			String iv = key;
			byte[] encrypted1 = Base64Utils.decode(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		 	try {
				System.out.println(encrypt("Test String","1234567812345678"));
				System.out.println(desEncrypt("2fbwW9+8vPId2/foafZq6Q==","1234567812345678"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
