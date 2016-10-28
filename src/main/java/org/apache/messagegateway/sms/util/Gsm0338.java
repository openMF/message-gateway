package org.apache.messagegateway.sms.util;

/**
 * Based on http://www.smsitaly.com/Download/ETSI_GSM_03.38.pdf
 */
public class Gsm0338 {
    private static final short[] isoGsm0338Array = { 64, 163, 36, 165, 232, 233, 249, 236, 242, 199, 10, 216, 248, 13,
            197, 229, 0, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 230, 223, 201, 32, 33, 34, 35, 164, 37, 38, 39, 40, 41,
            42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 161, 65, 66, 67,
            68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 196, 214, 209,
            220, 167, 191, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115,
            116, 117, 118, 119, 120, 121, 122, 228, 246, 241, 252, 224 };

    private static final short[][] extendedIsoGsm0338Array = { { 10, 12 }, { 20, 94 }, { 40, 123 }, { 41, 125 },
            { 47, 92 }, { 60, 91 }, { 61, 126 }, { 62, 93 }, { 64, 124 }, { 101, 164 } };

    public static boolean isEncodeableInGsm0338(String isoString) {
        byte[] isoBytes = isoString.getBytes();
        
        outer: for (int i = 0; i < isoBytes.length; i++) {
            for (int j = 0; j < isoGsm0338Array.length; j++) {
                if (isoGsm0338Array[j] == isoBytes[i]) {
                    continue outer;
                }
            }
            
            for (int j = 0; j < extendedIsoGsm0338Array.length; j++) {
                if (extendedIsoGsm0338Array[j][1] == isoBytes[i]) {
                    continue outer;
                }
            }
            
            return false;
        }
        
        return true;
    }
}
