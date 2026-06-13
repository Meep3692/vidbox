package ca.awoo.vidbox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StreamSet {
    public static record QualityStream(int quality, Stream stream) implements Comparable<QualityStream>{
        @Override
        public int compareTo(QualityStream other) {
            return quality-other.quality;
        }
    }

    private final List<QualityStream> streams = new ArrayList<>();

    public StreamSet(QualityStream... streams){
        for(QualityStream stream : streams){
            this.streams.add(stream);
        }
        this.streams.sort(Comparator.naturalOrder());
    }

    public Stream getStream(int quality){
        int i = 0;
        while(i < streams.size() && streams.get(i).quality <= quality){
            i++;
        }
        return streams.get(i-1).stream;
    }

}
