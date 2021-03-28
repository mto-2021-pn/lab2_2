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

    @Test
    public void shouldReturnZeroWhenFirstSequenceLengthIsZeroAndSecondSequenceHasLengthGreaterThanZero() {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(false).build());
        int[] seq1 = {};
        int[] seq2 = {1, 2};

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }

    @Test
    public void shouldReturnZeroWhenFirstSequenceLengthIsGreaterThanZeroAndSecondSequenceLengthIsZero() {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(false).build());
        int[] seq1 = {1, 2};
        int[] seq2 = {};

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }

    @Test
    public void shouldReturnHalfWhenFirstSequenceContainsTwoElementsFromSecondSequenceAndBothSequencesHaveEqualSize() {
        SearchResult found = SearchResult.builder().withFound(true).build();
        SearchResult notFound = SearchResult.builder().withFound(false).build();
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {2, 3, 4};
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> {
            if (elem == 1) return notFound;
            else if (elem == 2) return found;
            else if (elem == 3) return found;
            else return null;
        });

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0.5d, result);
    }

    @Test
    public void shouldReturnQuarterWhenFirstSequenceContainsTwoElementsFromSecondSequenceAndUnionSizeIsEight() {
        SearchResult found = SearchResult.builder().withFound(true).build();
        SearchResult notFound = SearchResult.builder().withFound(false).build();
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {2, 3, 4, 5, 6, 7, 8};
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> {
            if (elem == 1) return notFound;
            else if (elem == 2) return found;
            else if (elem == 3) return found;
            else return null;
        });

        double result = finder.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0.25d, result);
    }

    @Test
    public void shouldSearchMethodInvokeThreeTimes() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {1, 2, 3};
        SearchResult searchResult = SearchResult.builder().withFound(true).build();
        SequenceSearcher searcherMock = new SequenceSearcher() {
            public int invokeCounter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invokeCounter++;
                return searchResult;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(searcherMock);

        finder.calculateJackardSimilarity(seq1, seq2);

        int invokeCount = searcherMock.getClass().getDeclaredField("invokeCounter").getInt(searcherMock);
        assertEquals(3, invokeCount);
    }

    @Test
    public void shouldSearchMethodNotInvoke() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {};
        int[] seq2 = {};
        SequenceSearcher searcherMock = new SequenceSearcher() {
            public int invokeCounter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invokeCounter++;
                return null;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(searcherMock);

        finder.calculateJackardSimilarity(seq1, seq2);

        int invokeCount = searcherMock.getClass().getDeclaredField("invokeCounter").getInt(searcherMock);
        assertEquals(0, invokeCount);
    }

    @Test
    public void shouldFindOneElementFromFirstSequenceInSecondSequence() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {1, 4, 5};
        SequenceSearcher searcherMock = new SequenceSearcher() {
            public int foundCounter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                if (elem == 1) {
                    foundCounter++;
                    return SearchResult.builder().withFound(true).build();
                }
                return SearchResult.builder().withFound(false).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(searcherMock);

        finder.calculateJackardSimilarity(seq1, seq2);

        int foundCount = searcherMock.getClass().getDeclaredField("foundCounter").getInt(searcherMock);
        assertEquals(1, foundCount);
    }
}
