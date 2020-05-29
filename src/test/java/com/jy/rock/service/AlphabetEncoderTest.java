package com.jy.rock.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hzhou
 */
public class AlphabetEncoderTest {

    @Test
    public void encode() {
        for (int i = 0; i <= AlphabetEncoder.MAX_INT; i++) {
            String encode = AlphabetEncoder.encode(i);
            int decode = AlphabetEncoder.decode(encode);
            assertThat(decode).isEqualTo(i);
            System.out.println(i + "---->" + encode);
        }
    }

    @Test
    public void max() {
        System.out.println(AlphabetEncoder.decode("ZZZZ"));
    }

}
