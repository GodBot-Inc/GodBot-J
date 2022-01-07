package snippets;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Arrays;
import java.util.List;

public class Buttons {

    public static class QueueBuilder {

        private boolean firstDisabled = false;
        private boolean leftDisabled = false;
        private boolean refreshDisabled = true;
        private boolean rightDisabled = false;
        private boolean lastDisabled = false;

        public static QueueBuilder fromActionRow(ActionRow actionRow) {
            QueueBuilder queueBuilder = new QueueBuilder();

            for (Button button : actionRow.getButtons()) {
                if (button.getId() == null) {
                    continue;
                }

                if (button.getId().equals("queue_first_disabled")) {
                    queueBuilder.firstDisabled = true;
                } else if (button.getId().equals("queue_left_disabled")) {
                    queueBuilder.leftDisabled = true;
                } else if (button.getId().equals("queue_right_disabled")) {
                    queueBuilder.rightDisabled = true;
                } else if (button.getId().equals("queue_last_disabled")) {
                    queueBuilder.lastDisabled = true;
                } else if (button.getId().equals("queue_refresh")) {
                    queueBuilder.refreshDisabled = false;
                }
            }

            return queueBuilder;
        }

        public QueueBuilder setFirstDisabled(boolean firstDisabled) {
            this.firstDisabled = firstDisabled;

            return this;
        }

        public QueueBuilder setLeftDisabled(boolean leftDisabled) {
            this.leftDisabled = leftDisabled;
            return this;
        }

        public QueueBuilder setRefreshDisabled(boolean refreshDisabled) {
            this.refreshDisabled = refreshDisabled;
            return this;
        }

        public QueueBuilder setRightDisabled(boolean rightDisabled) {
            this.rightDisabled = rightDisabled;
            return this;
        }

        public QueueBuilder setLastDisabled(boolean lastDisabled) {
            this.lastDisabled = lastDisabled;
            return this;
            //:arrow_backward::arrow_backward: U+25B6U+25B6
        }

        public Button[] buildDefault() {
            return new Button[] {
                    Button.secondary(
                            "queue_first_disabled",
                            ":arrow_backward::arrow_backward:"
                    ),
                    Button.secondary(
                            "queue_left_disabled",
                            ":arrow_backward:"
                    ),
                    Button.secondary(
                            "queue_refresh",
                            "↻"
                    ).asDisabled(),
                    Button.primary(
                            "queue_right",
                            Emoji.fromUnicode("U+25B6")
                    ),
                    Button.primary(
                            "queue_last",
                            Emoji.fromUnicode("U+25B6")
                    )
            };
        }

        public Button buildFirst() {
            if (firstDisabled) {
                return Button.primary(
                        "queue_first_disabled",
                        Emoji.fromUnicode("U+23EA")
                ).asDisabled();
            }
            return Button.primary(
                    "queue_first",
                    Emoji.fromUnicode("U+23EA")
            );
        }

        public Button buildLeft() {
            if (leftDisabled) {
                return Button.primary(
                        "queue_left_disabled",
                        Emoji.fromUnicode("U+25C0")
                ).asDisabled();
            }
            return Button.primary(
                    "queue_left",
                    Emoji.fromUnicode("U+25C0")
            );
        }

        public Button buildRefresh() {
            if (refreshDisabled) {
                return Button.primary(
                        "queue_refresh_disabled",
                        "↻"
                ).asDisabled();
            }
            return Button.primary(
                    "queue_refresh",
                    "↻"
            );
        }

        public Button buildRight() {
            if (rightDisabled) {
                return Button.primary(
                        "queue_right_disabled",
                        Emoji.fromUnicode("U+25B6")
                ).asDisabled();
            }
            return Button.primary(
                    "queue_right",
                    Emoji.fromUnicode("U+25B6")
            );
        }

        public Button buildLast() {
            if (lastDisabled) {
                return Button.primary(
                        "queue_last_disabled",
                        Emoji.fromUnicode("U+23E9")
                ).asDisabled();
            }
            return Button.primary(
                    "queue_last",
                    Emoji.fromUnicode("U+23E9")
            );
        }

        public Button[] build() {
            Button first;
            Button left;
            Button refresh;
            Button right;
            Button last;

            if (firstDisabled) {
                first = Button.secondary(
                        "queue_first_disabled",
                        Emoji.fromUnicode("U+23EA")
                ).asDisabled();
            } else {
                first = Button.secondary(
                        "queue_first",
                        Emoji.fromUnicode("U+23EA")
                );
            }

            if (leftDisabled) {
                left = Button.secondary(
                        "queue_left_disabled",
                        Emoji.fromUnicode("U+25C0")
                ).asDisabled();
            } else {
                left = Button.secondary(
                        "queue_left",
                        Emoji.fromUnicode("U+25C0")
                );
            }

            if (refreshDisabled) {
                refresh = Button.secondary(
                        "queue_refresh_disabled",
                        "↻"
                ).asDisabled();
            } else {
                refresh = Button.secondary(
                        "queue_refresh",
                        "↻"
                );
            }

            if (rightDisabled) {
                right = Button.secondary(
                        "queue_right_disabled",
                        Emoji.fromUnicode("U+25B6")
                ).asDisabled();
            } else {
                right = Button.secondary(
                        "queue_right",
                        Emoji.fromUnicode("U+25B6")
                );
            }

            if (lastDisabled) {
                last = Button.secondary(
                        "queue_last_disabled",
                        Emoji.fromUnicode("U+23E9")
                ).asDisabled();
            } else {
                last = Button.secondary(
                        "queue_last",
                        Emoji.fromUnicode("U+23E9")
                );
            }

            return new Button[] {first, left, right, last};
        }

        public List<Button> buildAsList() {
            return Arrays.asList(build());
        }
    }
}
