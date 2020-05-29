package com.jy.rock.service;

import com.google.common.base.Strings;
import com.google.common.primitives.Chars;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 62进制换算 (0-9A-Za-z)
 *
 * @author hzhou
 */
public class AlphabetEncoder {

    /**
     * 最大的值
     */
    public static final int MAX_INT = 238327;

    /**
     * encode的时候要补全的长度
     */
    private static final int PAD_LENGTH = 3;

    private static final char[] ALPHABET = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final int ENCODE_LENGTH = ALPHABET.length;

    /**
     * 将给定的数字转换为编码
     *
     * @param victim 给定的数字
     * @return 转换后的编码
     * @throws IllegalArgumentException 给定的数字格式错误
     */
    public static String encode(int victim) throws IllegalArgumentException {
        if (victim < 0 || victim > MAX_INT) {
            throw new IllegalArgumentException(MessageFormat.format("给定的值非法，必须介于[{0}, {1}]", 0, MAX_INT + ""));
        }

        final List<Character> list = new ArrayList<>();

        do {
            list.add(ALPHABET[victim % ENCODE_LENGTH]);
            victim /= ENCODE_LENGTH;
        } while (victim > 0);

        Collections.reverse(list);
        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Character c : list) {
            stringBuilder.append(c);
        }
        return Strings.padStart(stringBuilder.toString(), PAD_LENGTH, '0');
    }

    /**
     * 将给定的编码转换为数字
     *
     * @param encoded 给定的编码
     * @return 转换后的数字
     * @throws IllegalArgumentException 给定的编码格式错误
     */
    public static int decode(@NotNull String encoded) throws IllegalArgumentException {
        if (encoded.length() != PAD_LENGTH) {
            throw new IllegalArgumentException(MessageFormat.format("编码长度错误，必须是：{0}位字符", PAD_LENGTH));
        }
        String e = StringUtils.stripStart(encoded, "0");
        int ret = 0;
        char c;
        for (int index = 0; index < e.length(); index++) {
            c = e.charAt(index);
            ret *= ENCODE_LENGTH;
            ret += Chars.indexOf(ALPHABET, c);
        }
        return ret;
    }
}
