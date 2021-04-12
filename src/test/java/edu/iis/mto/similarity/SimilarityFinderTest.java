package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimilarityFinderTest {

    SimilarityFinder finderTrue;
    SimilarityFinder finderFalse;
    SearchResult found;
    SearchResult notFound;

    @BeforeEach
    public void createFinders() {
        finderTrue = new SimilarityFinder(((elem, sequence) -> SearchResult.builder().withFound(true).build()));
        finderFalse = new SimilarityFinder(((elem, sequence) -> SearchResult.builder().withFound(false).build()));
    }

    @BeforeEach
    public void foundOrNotFound() {
        found = SearchResult.builder().withFound(true).build();
        notFound = SearchResult.builder().withFound(false).build();
    }

    @Test
    public void shouldReturnOneWhenLengthsSequenceAreZero() {
        int[] seq1 = {};
        int[] seq2 = {};

        double result = finderTrue.calculateJackardSimilarity(seq1, seq2);
        assertEquals(1.0d, result);
    }

    @Test
    public void shouldReturnOneWhenBothSequencesHaveEqualLengthsAndHaveSameElements() {
        int[] seq1 = {4, 5};
        int[] seq2 = {4, 5};

        double result = finderTrue.calculateJackardSimilarity(seq1, seq2);
        assertEquals(1.0d, result);
    }

    @Test
    public void shouldReturnZeroWhenSequencesHaveEqualLengthsAndHaveCompletelyDifferentElements() {
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {4, 5, 6};

        double result = finderFalse.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }

    @Test
    public void shouldReturnZeroWhenFirstSequenceIsEmpty() {
        int[] seq1 = {};
        int[] seq2 = {1, 2, 3};

        double result = finderFalse.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }

    @Test
    public void shouldReturnZeroWhenSecondSequenceIsEmpty() {
        int[] seq1 = {1, 2, 3};
        int[] seq2 = {};

        double result = finderFalse.calculateJackardSimilarity(seq1, seq2);

        assertEquals(0, result);
    }

    @Test
    public void shouldReturnQuarterWhenSequencesHaveTwoTheSameElementsAndTotalLengthIsTen() {
        int[] seq1 = {5, 6, 7, 8};
        int[] seq2 = {1, 2, 3, 4, 5, 6};

        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> {
            if (elem == 5) return found;
            else if (elem == 6) return found;
            else if (elem == 7) return notFound;
            else if (elem == 8) return notFound;
            else return null;
        });

        double result = finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(0.25d, result);
    }

    @Test
    public void shouldReturnHalfWhenSequencesHaveThreeTheSameElementsAndTotalLengthIsNine() {
        int[] seq1 = {3, 4, 5};
        int[] seq2 = {1, 2, 3, 4, 5, 6};

        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> {
            if (elem == 3) return found;
            else if (elem == 4) return found;
            else if (elem == 5) return found;
            else return null;
        });

        double result = finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(0.5d, result);
    }

    @Test
    public void shouldInvokeFourTimesSearchMethod() {
        int[] seq1 = {3, 4, 5, 6};
        int[] seq2 = {3, 4, 5, 6};
        final int[] invokeCounter = {0};

        SequenceSearcher searcherMock = (elem, sequence) -> {
            invokeCounter[0]++;
            return found;
        };

        SimilarityFinder finder = new SimilarityFinder(searcherMock);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(4, invokeCounter[0]);
    }

    @Test
    public void shouldNotInvokeSearchMethod() {
        int[] seq1 = {};
        int[] seq2 = {};
        final int[] invokeCounter = {0};

        SequenceSearcher searcherMock = (elem, sequence) -> {
            invokeCounter[0]++;
            return null;
        };

        SimilarityFinder finder = new SimilarityFinder(searcherMock);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(0, invokeCounter[0]);
    }

    @Test
    public void shouldFindTwoElementFromFirstSequenceInSecondSequence(){
        int[] seq1 = {1, 2};
        int[] seq2 = {1, 2, 3, 4, 5};
        final int[] foundCounter = {0};

        SequenceSearcher searcherMock = (elem, sequence) -> {
            if (elem == 1 || elem == 2) {
                foundCounter[0]++;
                return found;
            }
            return notFound;
        };

        SimilarityFinder finder = new SimilarityFinder(searcherMock);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(2, foundCounter[0]);
    }
}