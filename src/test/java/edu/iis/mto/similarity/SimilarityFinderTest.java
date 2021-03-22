package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimilarityFinderTest {

    @Test
    public void shouldReturnOneWhenSequenceLengthsAreZero() {
        SimilarityFinder finder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return null;
            }
        });
        int[] seq1 = {};
        int[] seq2 = {};

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(1.0d, result);
    }

    @Test
    public void shouldReturnOneWhenBothSequencesHaveEqualLengthsAndContainSameElements() {
        SimilarityFinder finder = new SimilarityFinder(((elem, sequence) -> SearchResult.builder().withFound(true).build()));
        int[] seq1 = {1, 2};
        int[] seq2 = {1, 2};

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(1.0d, result);
    }

    @Test
    public void shouldReturnZeroWhenSequencesHaveEqualLengthsAndContainCompletelyDifferentElements() {
        SimilarityFinder finder = new SimilarityFinder(((elem, sequence) -> SearchResult.builder().withFound(false).build()));
        int[] seq1 = {1, 2};
        int[] seq2 = {3, 4};

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }
}
