package utils;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class QueueWrapper {

    private final String serverId;
    private final String messageId;
    private final String authorId;
    private final String applicationId;
    private final long lastChanged;
    private final Document pagesDocument;
    private final int pages;

    public static class QueueBuilder {

        private String serverId;
        private String messageId;
        private String authorId;
        private String applicationId;
        private long lastChanged;
        private Document pagesDocument;
        private int pages;

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public void setLastChanged(long lastChanged) {
            this.lastChanged = lastChanged;
        }

        public void setPagesDocument(Document pages) {
            this.pagesDocument = pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public QueueWrapper build() {
            return new QueueWrapper(
                    serverId,
                    messageId,
                    authorId,
                    applicationId,
                    lastChanged,
                    pagesDocument,
                    pages
            );
        }
    }

    public QueueWrapper(
            String serverId,
            String messageId,
            String authorId,
            String applicationId,
            long lastChanged,
            Document pagesDocument,
            int pages
    ) {
        this.serverId = serverId;
        this.messageId = messageId;
        this.authorId = authorId;
        this.applicationId = applicationId;
        this.lastChanged = lastChanged;
        this.pagesDocument = pagesDocument;
        this.pages = pages;
    }

    @NotNull public String getServerId() {
        return serverId;
    }

    @NotNull public String getMessageId() {
        return messageId;
    }

    @NotNull public String getAuthorId() {
        return authorId;
    }

    @NotNull public String getApplicationId() {
        return applicationId;
    }

    public long getLastChanged() {
        return lastChanged;
    }

    @NotNull public Document getPagesDocument() {
        return pagesDocument;
    }

    public int getPages() {
        return pages;
    }

    public Document toBson() {
        return new Document()
                .append("serverId", serverId)
                .append("messageId", messageId)
                .append("authorId", authorId)
                .append("applicationId", applicationId)
                .append("lastChanged", lastChanged)
                .append("pagesDocument", pagesDocument)
                .append("pages", pages)
                .append("currentPage", 0);
    }
}
