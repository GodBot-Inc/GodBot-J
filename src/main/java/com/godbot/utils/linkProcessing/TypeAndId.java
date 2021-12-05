package com.godbot.utils.linkProcessing;

import org.jetbrains.annotations.NotNull;

public class TypeAndId {

     @NotNull public final String type;
     @NotNull public final String Id;

    public TypeAndId(@NotNull String type, @NotNull String id) {
        this.type = type;
        Id = id;
    }
}
