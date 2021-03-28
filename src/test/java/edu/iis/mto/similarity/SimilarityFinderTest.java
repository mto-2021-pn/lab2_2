package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import static org.junit.jupiter.api.Assertions.fail;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

class SimilarityFinderTest {

    @Test
    public void shouldReturnOneWhenBothSequenceAreEmpty() {
        // given

        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> null);

        int[] seq1 = {};
        int[] seq2 = {};
        double expectedValue = 1;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedValue, result);
    }

    @Test
    public void shouldReturnZeroWhenFirstArgumentSequenceIsEmpty() {
        // give

        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> null);

        int[] seq1 = {};
        int[] seq2 = {1, 4};
        double expectedResult = 0;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldReturnZeroWhenSecondArgumentSequenceIsEmpty() {
        // give
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                                 .withFound(false)
                                                                                                 .build());

        int[] seq1 = {120, 99};
        int[] seq2 = {};
        double expectedResult = 0;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldReturnOneWhenBothSequencesAreEqual() {
        // give
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                                 .withFound(true)
                                                                                                 .build());

        int[] seq1 = {44, 88};
        int[] seq2 = {44, 88};
        double expectedResult = 1;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldReturnZeroWhenSequencesHaveNoCommonElements() {
        // give
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                                 .withFound(false)
                                                                                                 .build());

        int[] seq1 = {1, 2, 3};
        int[] seq2 = {4, 5, 6};
        double expectedResult = 0;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldReturnHalfWhenGivenIntersectingSequencesAndEqualSequencesLengths() {
        // give
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> {
            if (Arrays.equals(sequence, new int[] {5, 8, 7}) == false) {
                return null;
            }

            if (elem == 9) {
                return SearchResult.builder()
                                   .withFound(false)
                                   .build();
            } else if (elem == 5) {
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            } else if (elem == 7) {
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            }

            return null;
        });

        int[] seq1 = {9, 5, 7};
        int[] seq2 = {5, 8, 7};
        double expectedResult = 0.5;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldReturnZeroPointTwentyFiveWhenGivenIntersectingSequencesAndDifferentSequenceLengths() {
        // give
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> {
            if (Arrays.equals(sequence, new int[] {10, 4, 7, 22, 123, 0}) == false) {
                return null;
            }

            if (elem == 4) {
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            } else if (elem == 22) {
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            } else if (elem == 88) {
                return SearchResult.builder()
                                   .withFound(false)
                                   .build();
            } else if (elem == 99) {
                return SearchResult.builder()
                                   .withFound(false)
                                   .build();
            }

            return null;
        });

        int[] seq1 = {4, 22, 88, 99};
        int[] seq2 = {10, 4, 7, 22, 123, 0};
        double expectedResult = 0.25;

        // when
        double result = similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldNotInvokeSequenceSearcherSearchWhenBothSequencesAreEmpty() throws NoSuchFieldException, IllegalAccessException {
        // given
        SequenceSearcher sequenceSearcherMock = new SequenceSearcher() {

            private int searchInvocationCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                searchInvocationCount++;
                return null;
            }
        };

        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcherMock);

        int[] seq1 = {};
        int[] seq2 = {};
        int expectedSearchInvocationCount = 0;

        // when
        similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        Field searchInvocationCountField = sequenceSearcherMock.getClass()
                                                               .getDeclaredField("searchInvocationCount");
        searchInvocationCountField.setAccessible(true);
        int numberOfSearchInvocations = searchInvocationCountField.getInt(sequenceSearcherMock);
        assertEquals(expectedSearchInvocationCount, numberOfSearchInvocations);
    }

    @Test
    public void shouldInvokeSequenceSearcherSearchFourTimes() throws NoSuchFieldException, IllegalAccessException {
        // given
        SequenceSearcher sequenceSearcherMock = new SequenceSearcher() {

            private int searchInvocationCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                searchInvocationCount++;
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            }
        };

        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcherMock);

        int[] seq1 = {1, 3, 9, 4};
        int[] seq2 = {10, 5, 12, 11, 54, 9};
        int expectedSearchInvocationCount = 4;

        // when
        similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        Field searchInvocationCountField = sequenceSearcherMock.getClass()
                                                               .getDeclaredField("searchInvocationCount");
        searchInvocationCountField.setAccessible(true);
        int numberOfSearchInvocations = searchInvocationCountField.getInt(sequenceSearcherMock);

        assertEquals(expectedSearchInvocationCount, numberOfSearchInvocations);
    }

    @Test
    public void shouldInvokeSequenceSearcherSearchWithThreeAsElementArgument() throws NoSuchFieldException, IllegalAccessException {
        // given
        SequenceSearcher sequenceSearcherMock = new SequenceSearcher() {

            private int passedElem = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                passedElem = elem;
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            }
        };

        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcherMock);

        int[] seq1 = {3};
        int[] seq2 = {-4, 88, 100};
        int expectedPassedElement = 3;

        // when
        similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        Field passedElemField = sequenceSearcherMock.getClass()
                                                    .getDeclaredField("passedElem");
        passedElemField.setAccessible(true);
        int passedElem = passedElemField.getInt(sequenceSearcherMock);

        assertEquals(expectedPassedElement, passedElem);
    }

    @Test
    public void shouldInvokeSequenceSearcherSearchWithGivenSequenceArgument() throws NoSuchFieldException, IllegalAccessException {
        // given
        SequenceSearcher sequenceSearcherMock = new SequenceSearcher() {

            private int[] passedSequence = {};

            @Override
            public SearchResult search(int elem, int[] sequence) {
                passedSequence = sequence.clone();
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            }
        };

        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcherMock);

        int[] seq1 = {6};
        int[] seq2 = {90, -88, 111, 33};

        // when
        similarityFinder.calculateJackardSimilarity(seq1, seq2);

        // then
        Field passedSequenceField = sequenceSearcherMock.getClass()
                                                        .getDeclaredField("passedSequence");
        passedSequenceField.setAccessible(true);
        int[] passedSequence = (int[]) passedSequenceField.get(sequenceSearcherMock);

        assertArrayEquals(seq2, passedSequence);
    }
}
