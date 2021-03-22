package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.*;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class SimilarityFinderTest {

    private SequenceSearcher sequenceSearcher;
    private SimilarityFinder similarityFinder;
    private int[] arr1;
    private int[] arr2;

    @BeforeEach
    void initialize() {
        final int MAX_RANGE = 100;
        final int MIN_RANGE = 1;
        arr1 = new int[ThreadLocalRandom.current().nextInt(MIN_RANGE, MAX_RANGE)];
        arr2 = new int[ThreadLocalRandom.current().nextInt(MIN_RANGE, MAX_RANGE)];
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = ThreadLocalRandom.current().nextInt(MIN_RANGE, MAX_RANGE);
        }
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = ThreadLocalRandom.current().nextInt(MIN_RANGE, MAX_RANGE);
        }
    }

    @Test
    void resultShouldBeEqualToOne_arraysAreEmpty() {
        similarityFinder = new SimilarityFinder(null);
        int[] arr1_1 = {};
        int[] arr2_1 = {};
        double result = similarityFinder.calculateJackardSimilarity(arr1_1, arr2_1);
        assertEquals(1.0d, result);
    }

    @Test
    void resultShouldBeGreaterOrEqualToZero_arraysAreRandom() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        double result = similarityFinder.calculateJackardSimilarity(arr1, arr2);
        assertTrue(result >= 0);
    }

    @Test
    void resultOfJaccardIndexShouldBeEqualToZero_simulatingDifferentArrays() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(false).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        double result = similarityFinder.calculateJackardSimilarity(arr1, arr2);
        assertEquals(0, result);
    }

    @Test
    void resultOfJaccardIndexShouldBeEqualToOne_arraysAreEqual() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        int[] arr1_1 = arr1.clone();
        int[] arr2_1 = arr1_1.clone();
        double result = similarityFinder.calculateJackardSimilarity(arr1_1, arr2_1);
        assertEquals(1, result);
    }

    @Test
    void nullPointerExceptionShouldBeThrown_firstArrayIsNull() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        assertThrows(NullPointerException.class, () -> similarityFinder.calculateJackardSimilarity(null, arr2));
    }

    @Test
    void nullPointerExceptionShouldBeThrown_secondArrayIsNull() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        assertThrows(NullPointerException.class, () -> similarityFinder.calculateJackardSimilarity(arr1, null));
    }

    @Test
    void searchMethodShouldBeCalledSameNumberOfTimesAsLengthOfFirstArray() throws NoSuchFieldException, IllegalAccessException {
        sequenceSearcher = new SequenceSearcher() {
            public int counter = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withFound(true).build();
            }
        };
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        similarityFinder.calculateJackardSimilarity(arr1, arr2);
        int counterValue = (int) sequenceSearcher.getClass().getDeclaredField("counter").get(sequenceSearcher);
        assertEquals(counterValue, arr1.length);
    }

    @Test
    void searchMethodShouldBeCalledWithConsecutiveElementsOfFirstArrayAndFullSecondArray() throws NoSuchFieldException, IllegalAccessException {
        sequenceSearcher = new SequenceSearcher() {
            public final List<Integer> elemList = new ArrayList<>();
            public final List<int[]> sequenceList = new ArrayList<>();
            @Override
            public SearchResult search(int elem, int[] sequence) {
                elemList.add(elem);
                sequenceList.add(sequence);
                return SearchResult.builder().withFound(true).build();
            }
        };
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        similarityFinder.calculateJackardSimilarity(arr1, arr2);

        @SuppressWarnings("unchecked")
        var elemList = (List<Integer>) sequenceSearcher.getClass().getDeclaredField("elemList").get(sequenceSearcher);
        @SuppressWarnings("unchecked")
        var sequenceList = (List<int[]>) sequenceSearcher.getClass().getDeclaredField("sequenceList").get(sequenceSearcher);

        assertArrayEquals(elemList.stream().mapToInt(Integer::intValue).toArray(), arr1);
        sequenceList.forEach(array -> assertArrayEquals(array, arr2));
    }
}
