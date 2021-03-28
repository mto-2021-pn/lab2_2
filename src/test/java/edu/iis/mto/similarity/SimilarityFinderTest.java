package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimilarityFinderTest {

    @Test
    public void shouldReturnOneWhenBothSequencesAreEmpty() {
        // given
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> null);
        int[] firstSeq = {};
        int[] secondSeq = {};

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(1.0d, similarity);
    }

    @Test
    public void shouldReturnZeroWhenSequencesContainCompletelyDifferentNumbers() {
        // given
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                       .withFound(false)
                                                                                       .build());
        int[] firstSeq = {1, 2, 3, 4, 5};
        int[] secondSeq = {6, 7, 8, 9, 10};

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(0d, similarity);
    }

    @Test
    public void shouldReturnOneWhenSequencesContainTheSameNumbersAndAreOfTheSameLength() {
        // given
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                       .withFound(true)
                                                                                       .build());
        int[] firstSeq = {1, 2, 3};
        int[] secondSeq = {1, 2, 3};

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(1.0d, similarity);
    }

    @Test
    public void shouldReturnZeroWhenFirstSequenceIsEmpty() {
        // given
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> null);
        int[] firstSeq = {};
        int[] secondSeq = {1, 2, 3};

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(0d, similarity);
    }

    @Test
    public void shouldReturnZeroWhenSecondSequenceIsEmpty() {
        // given
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                                                                                       .withFound(false)
                                                                                       .build());
        int[] firstSeq = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] secondSeq = {};

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(0d, similarity);
    }

    @Test
    public void shouldReturnQuarterWhenSequencesContainTwoTheSameElementsAndTotalLengthIsTen() {
        // given
        SearchResult foundResult = SearchResult.builder()
                                               .withFound(true)
                                               .build();
        SearchResult notFoundResult = SearchResult.builder()
                                                  .withFound(false)
                                                  .build();
        int[] firstSeq = {1, 2, 3, 4};
        int[] secondSeq = {4, 1, 11, 19, 23, 35};

        SimilarityFinder finder = new SimilarityFinder(((elem, sequence) -> {
            if (elem == 1)
                return foundResult;
            else if (elem == 2)
                return notFoundResult;
            else if (elem == 3)
                return notFoundResult;
            else if (elem == 4)
                return foundResult;
            return null;
        }));

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(0.25d, similarity);
    }

    @Test
    public void shouldReturnHalfWhenSequencesAreOfDifferentLengthAndHaveOneCommonElement() {
        // given
        int[] firstSeq = {31, 199};
        int[] secondSeq = {31};
        SimilarityFinder finder = new SimilarityFinder(((elem, sequence) -> {
            if (elem == 31)
                return SearchResult.builder()
                                   .withFound(true)
                                   .build();
            else if (elem == 199)
                return SearchResult.builder()
                                   .withFound(false)
                                   .build();
            return null;
        }));

        // when
        double similarity = finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        assertEquals(0.5d, similarity);
    }

    @Test
    public void shouldInvokeSearchMethodTwice() throws NoSuchFieldException, IllegalAccessException {
        // given
        int[] firstSeq = {12, 13};
        int[] secondSeq = {15, 12};
        SearchResult anySearchResult = SearchResult.builder()
                                                   .withFound(false)
                                                   .build();
        SequenceSearcher mockSearcher = new SequenceSearcher() {

            public int invocationCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                ++invocationCount;
                return anySearchResult;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(mockSearcher);

        // when
        finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        int actualInvocationCount = mockSearcher.getClass()
                                                .getDeclaredField("invocationCount")
                                                .getInt(mockSearcher);
        assertEquals(2, actualInvocationCount);
    }

    @Test
    public void shouldNotInvokeSearchMethodWhenSequencesAreEmpty() throws NoSuchFieldException, IllegalAccessException {
        // given
        int[] firstSeq = {};
        int[] secondSeq = {};
        SequenceSearcher mockSearcher = new SequenceSearcher() {

            public int invocationCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                ++invocationCount;
                return null;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(mockSearcher);

        // when
        finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        int actualInvocationCount = mockSearcher.getClass()
                                                .getDeclaredField("invocationCount")
                                                .getInt(mockSearcher);
        assertEquals(0, actualInvocationCount);
    }

    @Test
    public void shouldFindThreeElementsFromFirstSequenceInSecondSequence() throws NoSuchFieldException, IllegalAccessException {
        // given
        int[] firstSeq = {10, 20, 30};
        int[] secondSeq = {50, 40, 30, 20, 10};
        SequenceSearcher mockSearcher = new SequenceSearcher() {

            public int found = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                if (elem == 10 || elem == 20 || elem == 30) {
                    ++found;
                    return SearchResult.builder()
                                       .withFound(true)
                                       .build();
                }
                return SearchResult.builder()
                                   .withFound(false)
                                   .build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(mockSearcher);

        // when
        finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        int actualCountOfFoundElements = mockSearcher.getClass()
                                                     .getDeclaredField("found")
                                                     .getInt(mockSearcher);
        assertEquals(3, actualCountOfFoundElements);
    }

    @Test
    public void shouldInvokeSearchMethodWithElementsFromSequencePassedAsFirstParameterInCalculateJackardSimilarity() {
        // given
        int[] firstSeq = {10, 20, 30};
        int[] secondSeq = {50, 40, 30, 20, 10};
        List<Integer> elements = new ArrayList<>();
        SequenceSearcher mockSearcher = (elem, sequence) -> {
            elements.add(elem);
            return SearchResult.builder()
                               .withFound(false)
                               .build();
        };
        SimilarityFinder finder = new SimilarityFinder(mockSearcher);
        // when
        finder.calculateJackardSimilarity(firstSeq, secondSeq);

        // then
        List<Integer> firstSequenceAsList = Arrays.stream(firstSeq)
                                                  .boxed()
                                                  .collect(Collectors.toList());
        assertTrue(firstSequenceAsList.containsAll(elements) && elements.containsAll(firstSequenceAsList));
    }
}
