package ca.awoo;

import java.util.List;

public class CompositeSourceProvider implements SourceProvider {
    private final List<SourceProvider> providers;

    public CompositeSourceProvider(List<SourceProvider> providers) {
        this.providers = providers;
    }

    @Override
    public boolean supports(String source) {
        for (SourceProvider provider : providers) {
            if (provider.supports(source)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Video> expandSources(String source) {
        for (SourceProvider provider : providers) {
            if (provider.supports(source)) {
                return provider.expandSources(source);
            }
        }
        // Fallback or empty if no provider supports it
        return java.util.Collections.singletonList(new SimpleVideo(source));
    }
}
