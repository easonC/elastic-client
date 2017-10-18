package easonc.elastic.client.util;



import java.math.BigDecimal;

/**
 * Created by zhuqi on 2016/11/16.
 */
public final class CommonFunc {


    /**
     * ip地址转byte[]
     *
     * @param ipAdd
     * @return
     */
    public static byte[] ipv42ByteArr(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            binIP[i] = (byte) Integer.parseInt(strs[i]);
        }
        return binIP;
    }

    /**
     * byte[]转ip地址
     *
     * @param addr
     * @return
     */
    public static String byteArr2Ipv4(byte[] addr) {
        String ip = "";
        for (int i = 0; i < addr.length; i++) {
            ip += (addr[i] & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }


    public static <T> String getClassShortName(Class<T> clazz) {
        String className = clazz.getName().toLowerCase();
        int index = className.lastIndexOf(".");
        if (index > 0)
            className = className.substring(index + 1);
        return className;
    }

    public static double getDoubleValue(BigDecimal decimal) {
        if (decimal == null)
            return BigDecimal.ZERO.doubleValue();
        return decimal.doubleValue();
    }
    public static double getDoubleValueOrDefault(Double doubleRef) {
        if (doubleRef == null)
            return 0;
        return doubleRef.doubleValue();
    }
    public static int getIntegerValueOrDefault(Integer integer) {
        if (integer == null)
            return 0;
        return integer.intValue();
    }
    public static int getIntegerValueOrDefault(Short integer) {
        if (integer == null)
            return 0;
        return integer.intValue();
    }
    interface ExpressionObject<T> {
        Object path(T t);
    }

}
