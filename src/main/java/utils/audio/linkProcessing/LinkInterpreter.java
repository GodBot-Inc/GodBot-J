package utils.audio.linkProcessing;

public class LinkInterpreter {
    // Here we interpret links and convert them into the following format: <Title - Artist> if providable
    public static LinkInterpretation interpret() {
        return new LinkInterpretation() {
            @Override
            public String getSearchTerm() {
                return null;
            }

            @Override
            public boolean isProvidable() {
                return false;
            }

            @Override
            public int getDuration() {
                return 0;
            }

            @Override
            public String getTrackData() {
                return null;
            }
        };
    }
}
