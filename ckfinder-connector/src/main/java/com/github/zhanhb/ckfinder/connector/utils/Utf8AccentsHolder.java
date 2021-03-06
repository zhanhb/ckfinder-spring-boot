package com.github.zhanhb.ckfinder.connector.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Ascii value of unicode, when force ascii is enabled.
 *
 * @author zhanhb
 */
@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
class Utf8AccentsHolder {

  private static final Map<Character, String> MAP;

  static {
    Map<Character, String> map = new HashMap<>(275);
    map.put((char) 181, "u");
    map.put((char) 192, "A");
    map.put((char) 193, "A");
    map.put((char) 194, "A");
    map.put((char) 195, "A");
    map.put((char) 196, "Ae");
    map.put((char) 197, "A");
    map.put((char) 198, "Ae");
    map.put((char) 199, "C");
    map.put((char) 200, "E");
    map.put((char) 201, "E");
    map.put((char) 202, "E");
    map.put((char) 203, "E");
    map.put((char) 204, "I");
    map.put((char) 205, "I");
    map.put((char) 206, "I");
    map.put((char) 207, "I");
    map.put((char) 208, "Dh");
    map.put((char) 209, "N");
    map.put((char) 210, "O");
    map.put((char) 211, "O");
    map.put((char) 212, "O");
    map.put((char) 213, "O");
    map.put((char) 214, "Oe");
    map.put((char) 216, "O");
    map.put((char) 217, "U");
    map.put((char) 218, "U");
    map.put((char) 219, "U");
    map.put((char) 220, "Ue");
    map.put((char) 221, "Y");
    map.put((char) 222, "Th");
    map.put((char) 223, "ss");
    map.put((char) 224, "a");
    map.put((char) 225, "a");
    map.put((char) 226, "a");
    map.put((char) 227, "a");
    map.put((char) 228, "ae");
    map.put((char) 229, "a");
    map.put((char) 230, "ae");
    map.put((char) 231, "c");
    map.put((char) 232, "e");
    map.put((char) 233, "e");
    map.put((char) 234, "e");
    map.put((char) 235, "e");
    map.put((char) 236, "i");
    map.put((char) 237, "i");
    map.put((char) 238, "i");
    map.put((char) 239, "i");
    map.put((char) 240, "dh");
    map.put((char) 241, "n");
    map.put((char) 242, "o");
    map.put((char) 243, "o");
    map.put((char) 244, "o");
    map.put((char) 245, "o");
    map.put((char) 246, "oe");
    map.put((char) 248, "o");
    map.put((char) 249, "u");
    map.put((char) 250, "u");
    map.put((char) 251, "u");
    map.put((char) 252, "ue");
    map.put((char) 253, "y");
    map.put((char) 254, "th");
    map.put((char) 255, "y");
    map.put((char) 256, "A");
    map.put((char) 257, "a");
    map.put((char) 258, "A");
    map.put((char) 259, "a");
    map.put((char) 260, "A");
    map.put((char) 261, "a");
    map.put((char) 262, "C");
    map.put((char) 263, "c");
    map.put((char) 264, "C");
    map.put((char) 265, "c");
    map.put((char) 266, "C");
    map.put((char) 267, "c");
    map.put((char) 268, "C");
    map.put((char) 269, "c");
    map.put((char) 270, "D");
    map.put((char) 271, "d");
    map.put((char) 272, "D");
    map.put((char) 273, "d");
    map.put((char) 274, "E");
    map.put((char) 275, "e");
    map.put((char) 276, "E");
    map.put((char) 277, "e");
    map.put((char) 278, "E");
    map.put((char) 279, "e");
    map.put((char) 280, "E");
    map.put((char) 281, "e");
    map.put((char) 282, "E");
    map.put((char) 283, "e");
    map.put((char) 284, "G");
    map.put((char) 285, "g");
    map.put((char) 286, "G");
    map.put((char) 287, "g");
    map.put((char) 288, "G");
    map.put((char) 289, "g");
    map.put((char) 290, "G");
    map.put((char) 291, "g");
    map.put((char) 292, "H");
    map.put((char) 293, "h");
    map.put((char) 294, "H");
    map.put((char) 295, "h");
    map.put((char) 296, "I");
    map.put((char) 297, "i");
    map.put((char) 298, "I");
    map.put((char) 299, "i");
    map.put((char) 302, "I");
    map.put((char) 303, "i");
    map.put((char) 308, "J");
    map.put((char) 309, "j");
    map.put((char) 310, "K");
    map.put((char) 311, "k");
    map.put((char) 313, "L");
    map.put((char) 314, "l");
    map.put((char) 315, "L");
    map.put((char) 316, "l");
    map.put((char) 317, "L");
    map.put((char) 318, "l");
    map.put((char) 321, "L");
    map.put((char) 322, "l");
    map.put((char) 323, "N");
    map.put((char) 324, "n");
    map.put((char) 325, "N");
    map.put((char) 326, "n");
    map.put((char) 327, "N");
    map.put((char) 328, "n");
    map.put((char) 332, "O");
    map.put((char) 333, "o");
    map.put((char) 336, "O");
    map.put((char) 337, "o");
    map.put((char) 340, "R");
    map.put((char) 341, "r");
    map.put((char) 342, "R");
    map.put((char) 343, "r");
    map.put((char) 344, "R");
    map.put((char) 345, "r");
    map.put((char) 346, "S");
    map.put((char) 347, "s");
    map.put((char) 348, "S");
    map.put((char) 349, "s");
    map.put((char) 350, "S");
    map.put((char) 351, "s");
    map.put((char) 352, "S");
    map.put((char) 353, "s");
    map.put((char) 354, "T");
    map.put((char) 355, "t");
    map.put((char) 356, "T");
    map.put((char) 357, "t");
    map.put((char) 358, "T");
    map.put((char) 359, "t");
    map.put((char) 360, "U");
    map.put((char) 361, "u");
    map.put((char) 362, "U");
    map.put((char) 363, "u");
    map.put((char) 364, "U");
    map.put((char) 365, "u");
    map.put((char) 366, "U");
    map.put((char) 367, "u");
    map.put((char) 368, "U");
    map.put((char) 369, "u");
    map.put((char) 370, "U");
    map.put((char) 371, "u");
    map.put((char) 372, "W");
    map.put((char) 373, "w");
    map.put((char) 374, "Y");
    map.put((char) 375, "y");
    map.put((char) 376, "Y");
    map.put((char) 377, "Z");
    map.put((char) 378, "z");
    map.put((char) 379, "Z");
    map.put((char) 380, "z");
    map.put((char) 381, "Z");
    map.put((char) 382, "z");
    map.put((char) 401, "F");
    map.put((char) 402, "f");
    map.put((char) 416, "O");
    map.put((char) 417, "o");
    map.put((char) 431, "U");
    map.put((char) 432, "u");
    map.put((char) 536, "S");
    map.put((char) 537, "s");
    map.put((char) 538, "T");
    map.put((char) 539, "t");
    map.put((char) 7682, "B");
    map.put((char) 7683, "b");
    map.put((char) 7690, "D");
    map.put((char) 7691, "d");
    map.put((char) 7710, "F");
    map.put((char) 7711, "f");
    map.put((char) 7744, "M");
    map.put((char) 7745, "m");
    map.put((char) 7766, "P");
    map.put((char) 7767, "p");
    map.put((char) 7776, "S");
    map.put((char) 7777, "s");
    map.put((char) 7786, "T");
    map.put((char) 7787, "t");
    map.put((char) 7808, "W");
    map.put((char) 7809, "w");
    map.put((char) 7810, "W");
    map.put((char) 7811, "w");
    map.put((char) 7812, "W");
    map.put((char) 7813, "w");
    map.put((char) 7922, "Y");
    map.put((char) 7923, "y");
    MAP = map;
  }

  @SuppressWarnings({"NestedAssignment", "ValueOfIncrementOrDecrementUsed"})
  public static String convert(String raw) {
    Map<Character, String> map = MAP;

    for (int i = 0, len = raw.length(); i != len; ++i) {
      String str;
      if ((str = map.get(raw.charAt(i))) != null) {
        StringBuilder sb = new StringBuilder(i + (len - i) * 2);
        for (int pos = 0;;) {
          sb.append(raw, pos, i).append(str);
          pos = i + 1;
          do {
            if (++i == len) {
              return sb.append(raw, pos, len).toString();
            }
          } while ((str = map.get(raw.charAt(i))) == null);
        }
      }
    }
    return raw;
  }

}
