package com.comerzzia.iskaypet.pos.devices.drivers;

import com.comerzzia.pos.drivers.direct.CodesDirect;

public class IskaypetCodesDirect extends CodesDirect {

    public static String STRIKE_SET = "STRIKE_SET";
    public static String STRIKE_RESET = "STRIKE_RESET";
    public static String IMAGE_ALIGN_CENTER = "IMAGE_ALIGN_CENTER";
    public static String IMAGE_BIT_MODE = "IMAGE_BIT_MODE";
    public static String LINE_SPACE_24 = "LINE_SPACE_24";
    public static String LINE_SPACE_30 = "LINE_SPACE_30";


    public String getStrikeSet() {
        return this.getCodigos().get(STRIKE_SET);
    }

    public String getStrikeReset() {
        return this.getCodigos().get(STRIKE_RESET);
    }

    public String getImageAlignCenter() {
        return this.getCodigos().get(IMAGE_ALIGN_CENTER);
    }

    public String getImageBitMode() {
        return this.getCodigos().get(IMAGE_BIT_MODE);
    }

    public String getLineSpace24() {
        return this.getCodigos().get(LINE_SPACE_24);
    }

    public String getLineSpace30() {
        return this.getCodigos().get(LINE_SPACE_30);
    }
}
