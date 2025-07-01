package com.comerzzia.pos.util.base64;

import java.io.UnsupportedEncodingException; 

public class Base64Coder {	
	
	/** Constant to indicate UTF-8 encoding in the methods which accept it. */
	public final static String UTF8 = "UTF-8";

	private String encoding;

	/** Constructor with default encoding */
	public Base64Coder() {
	}
	
	/** Constructor indicating transformations encoding. */
	public Base64Coder(String encoding) {
		this.encoding = encoding;
	}
	
	/** Set transformations encoding.*/
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/** Encoding used in transformations.*/
	public String getEncoding() {
		return this.encoding;
	}
	
	/** 
	 * Returns a Base64 encoded string from a source string. Specified encoding will be used.
	 * @param data - Source string
	 * @return String - Base64 encoded string
	 * @throws UnsupportedEncodingException
	 */
	public String encodeBase64(String data) throws UnsupportedEncodingException {
		if (encoding != null) {
			byte[] result = encodeBase64(data.getBytes(encoding));
			return new String(result, encoding);
		}
		else {
			byte[] result = encodeBase64(data.getBytes());
			return new String(result);
		}
	}
	
	/** 
	 * Returns Base64 encoded byte array from a source byte array. Specified encoding will be used.
	 * @param data - Source byte array
	 * @return byte[] - Base64 encoded byte array
	 * @throws UnsupportedEncodingException
	 */	
	public byte[] encodeBase64(byte[] data) throws UnsupportedEncodingException {
		return encodeBase64(data, 0, data.length);
	}
	
	/** 
	 * Returns a string decoded from the given Base64 encoded source string.
	 * Specified encoding will be used.
	 * @param data - Base64 encoded string
	 * @return String - Decoded string
	 * @throws UnsupportedEncodingException
	 */
	public String decodeBase64(String data) throws UnsupportedEncodingException, Base64DecoderException {
		if (encoding != null) {
			byte[] result = decodeBase64(data.getBytes(encoding));
			return new String(result, encoding);
		}
		else {
			byte[] result = decodeBase64(data.getBytes());
			return new String(result);
		}
	}

	/** 
	 * Returns a byte array decoded from the given Base64 encoded source byte array.
	 * Specified encoding will be used.
	 * @param data - Base64 encoded byte array
	 * @return byte[] - Decoded byte array
	 * @throws UnsupportedEncodingException
	 */	
	public byte[] decodeBase64(byte[] data) throws Base64DecoderException {
		return decodeBase64(data, 0, data.length);
	}

	private byte[] encodeBase64(byte[] data, int offset, int len) throws UnsupportedEncodingException {
		byte result[] = null;

		if (data == null) {
			return null;
		}
		if (encoding != null) {
			result = encodeBytes(data, offset, len).getBytes(encoding);
		}
		else {
			result = encodeBytes(data, offset, len).getBytes();
		}
		return (result == null) ? data : result;
	}

	private byte[] decodeBase64(byte[] data, int offset, int len) throws Base64DecoderException {
		byte result[] = null;

		if (data == null) {
			return null;
		}
		result = decode(data, offset, len);
		return (result == null) ? data : result;
	}
	
	private byte[] encode3to4(byte abyte0[], int i, int j, byte abyte1[], int k) {
		int l = (j <= 0 ? 0 : (abyte0[i] << 24) >>> 8) | (j <= 1 ? 0 : (abyte0[i + 1] << 24) >>> 16) | (j <= 2 ? 0 : (abyte0[i + 2] << 24) >>> 24);
		switch (j) {
			case 3: // '\003'
				abyte1[k] = ALPHABET[l >>> 18];
				abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
				abyte1[k + 2] = ALPHABET[l >>> 6 & 0x3f];
				abyte1[k + 3] = ALPHABET[l & 0x3f];
				return abyte1;

			case 2: // '\002'
				abyte1[k] = ALPHABET[l >>> 18];
				abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
				abyte1[k + 2] = ALPHABET[l >>> 6 & 0x3f];
				abyte1[k + 3] = 61;
				return abyte1;

			case 1: // '\001'
				abyte1[k] = ALPHABET[l >>> 18];
				abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
				abyte1[k + 2] = 61;
				abyte1[k + 3] = 61;
				return abyte1;
		}
		return abyte1;
	}

	private String encodeBytes(byte abyte0[], int i, int j) throws UnsupportedEncodingException {
		int k = (j * 4) / 3;
		byte abyte1[] = new byte[k + (j % 3 <= 0 ? 0 : 4) + k / 76];
		int l = 0;
		int i1 = 0;
		int j1 = j - 2;
		int k1 = 0;
		while (l < j1) {
			encode3to4(abyte0, l, 3, abyte1, i1);
			if ((k1 += 4) == 76) {
				abyte1[i1 + 4] = 10;
				i1++;
				k1 = 0;
			}
			l += 3;
			i1 += 4;
		}
		if (l < j) {
			encode3to4(abyte0, l, j - l, abyte1, i1);
			i1 += 4;
		}
		if (encoding != null) {
			return new String(abyte1, 0, i1, encoding);
		}
		else {
			return new String(abyte1, 0, i1);
		}

	}

	private int decode4to3(byte abyte0[], int i, byte abyte1[], int j) {
		if (abyte0[i + 2] == 61) {
			int k = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12;
			abyte1[j] = (byte) (k >>> 16);
			return 1;
		}
		if (abyte0[i + 3] == 61) {
			int l = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12 | (DECODABET[abyte0[i + 2]] << 24) >>> 18;
			abyte1[j] = (byte) (l >>> 16);
			abyte1[j + 1] = (byte) (l >>> 8);
			return 2;
		}
		else {
			int i1 = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12 | (DECODABET[abyte0[i + 2]] << 24) >>> 18 | (DECODABET[abyte0[i + 3]] << 24) >>> 24;
			abyte1[j] = (byte) (i1 >> 16);
			abyte1[j + 1] = (byte) (i1 >> 8);
			abyte1[j + 2] = (byte) i1;
			return 3;
		}
	}

	private byte[] decode(byte abyte0[], int i, int j) throws Base64DecoderException {
		int k = (j * 3) / 4;
		byte abyte1[] = new byte[k];
		int l = 0;
		byte abyte2[] = new byte[4];
		int i1 = 0;

		for (int j1 = 0; j1 < j; j1++) {
			byte byte0 = (byte) (abyte0[j1] & 0x7f);
			byte byte1 = DECODABET[byte0];
			if (byte1 >= -5) {
				if (byte1 < -1)
					continue;
				abyte2[i1++] = byte0;
				if (i1 <= 3)
					continue;
				l += decode4to3(abyte2, 0, abyte1, l);
				i1 = 0;
				if (byte0 == 61)
					break;
			}
			else {
				throw new Base64DecoderException("Wrong Base 64 input character at position: " + j1 + ": " + abyte0[j1] + "(decimal)");
			}
		}

		byte abyte3[] = new byte[l];
		System.arraycopy(abyte1, 0, abyte3, 0, l);
		return abyte3;
	}

    private static final byte ALPHABET[] = {
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100,
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, 43, 47
    };
    
    private static final byte DECODABET[] = {
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5,
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9,
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9,
        -9, -9, -9, 62, -9, -9, -9, 63, 52, 53,
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9,
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
        25, -9, -9, -9, -9, -9, -9, 26, 27, 28,
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51, -9, -9, -9, -9
    };

}
