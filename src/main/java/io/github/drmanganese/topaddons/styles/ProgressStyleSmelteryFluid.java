package io.github.drmanganese.topaddons.styles;

import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;

public class ProgressStyleSmelteryFluid extends ProgressStyle {

    @Override
    public int getBorderColorTop() {
        return 0x00ffffff;
    }

    @Override
    public int getBorderColorBottom() {
        return 0x00ffffff;
    }

    @Override
    public boolean isShowText() {
        return false;
    }

    @Override
    public int getBackgroundColor() {
        return 0x00ffffff;
    }
}
