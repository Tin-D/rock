package com.jy.rock.core;

import com.google.common.base.Charsets;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class ParamEncryptUtilTest {

    private static final String key = "OlZGXZmXMbZVBWyu";

    private static final String input = "YR1fbJOLr2AXaaEwcOeofHXnSZmKKU9GIajyOT6hjZNt8mEJj4gk5YRTyVfOVScoEebGD6RJzIU3176VNbwWRdYMU58LSWA7WXJpNCGrrkLcJp6YhmbCDRVivqZjO0Tg";

    @Test
    public void testA() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(new SecureRandom(key.getBytes()));
//        kgen.init(256, new SecureRandom(key.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] b = Base64.getUrlDecoder().decode(input);
        byte[] result = cipher.doFinal(b);
        String s = new String(result, Charsets.UTF_8);
        System.out.println(s);
    }
}
