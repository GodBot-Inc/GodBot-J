package com.godbot.discord.snippets.Embeds.trackInfo;

public class TrackLines {
    public static String build(long currentMs, long maxMs) {
        // ▬
        // :radio_button:
        long position = currentMs / (maxMs / 20);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 21; i++) {
            if (i == position - 1) {
                sb.append(":radio_button:");
                continue;
            }
            sb.append("▬");
        }
        System.out.println(sb);
        return sb.toString();
    }

    public static String buildDefault() {
        return ":radio_button:▬▬▬▬▬▬▬";
    }
}
