package com.isl.outland_horizon.level.capa.data;

import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public interface OhInterFace {
    CompoundTag saveNbtData(CompoundTag tag);
    void loadNbtData(CompoundTag tag);
    void copy(OhInterFace cap);
    Map<String, OhAttribute.ScapeApi> getProfession();
}
