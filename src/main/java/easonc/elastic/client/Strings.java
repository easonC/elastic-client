package easonc.elastic.client;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuqi on 2016/11/16.
 */
public class Strings {

    public static boolean isWhitespaceOrNull(String str) {
        return StringUtils.isEmpty(str) || StringUtils.isWhitespace(str);
    }

    public static String[] spilt(String str, char separatorChar) {
        return StringUtils.split(str, separatorChar);
    }

    public static List<Integer> string2Integers(String str, char separatorChar) {
        if (!Strings.isWhitespaceOrNull(str)) {
            String[] arr = Strings.spilt(str, separatorChar);
            if (arr != null && arr.length > 0) {
                List<Integer> integers = new ArrayList<>(arr.length);
                for (int i = 0; i < arr.length; i++) {
                    integers.add(Integer.parseInt(arr[i]));
                }

                return integers;
            }
        }
        return null;
    }
}
