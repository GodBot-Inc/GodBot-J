package utils.linkProcessing.interpretations;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Playable {

    @NotNull public final String type;
    //                      Id is Sc link-Link of searcher
    @NotNull public final HashMap<Boolean, String> identifiers;

    public Playable(@NotNull String type, @NotNull HashMap<Boolean, String> identifiers) {
        this.type = type;
        this.identifiers = identifiers;
    }

    public @NotNull String getType() {
        return type;
    }

    public @NotNull HashMap<Boolean, String> getIdentifiers() {
        return identifiers;
    }
}
