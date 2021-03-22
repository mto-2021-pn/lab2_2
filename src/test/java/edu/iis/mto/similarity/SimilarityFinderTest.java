package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.*;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void resultShouldBeEqualTo1() {
        similarityFinder = new SimilarityFinder(null);
        int[] arr1_1 = {};
        int[] arr2_1 = {};
        double result = similarityFinder.calculateJackardSimilarity(arr1_1, arr2_1);
        assertEquals(1.0d, result);
    }

    @Test
    void resultShouldBeGreaterThan0() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        double result = similarityFinder.calculateJackardSimilarity(arr1, arr2);
        assertTrue(result >= 0);
    }

    @Test
    void resultOfJaccardIndexShouldBeEqualToZero() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(false).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        double result = similarityFinder.calculateJackardSimilarity(arr1, arr2);
        assertEquals(0, result);
    }

    @Test
    void resultOfJaccardIndexShouldBeEqualToOne() {
        sequenceSearcher = (elem, sequence) -> SearchResult.builder().withFound(true).build();
        similarityFinder = new SimilarityFinder(sequenceSearcher);
        int[] arr1_1 = arr1.clone();
        int[] arr2_1 = arr1_1.clone();
        double result = similarityFinder.calculateJackardSimilarity(arr1_1, arr2_1);
        assertEquals(1, result);
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
}
