package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimilarityFinderTest {

    SequenceSearcher searcherThatAlwaysReturnsTrue = new SequenceSearcher() {
        @Override
        public SearchResult search(int elem, int[] sequence) {
            SearchResult.Builder builder = SearchResult.builder();
            builder.withFound(true);
            return builder.build();
        }
    };

    SequenceSearcher searcherThatAlwaysReturnsFalse = new SequenceSearcher() {
        @Override
        public SearchResult search(int elem, int[] sequence) {
            SearchResult.Builder builder = SearchResult.builder();
            builder.withFound(false);
            return builder.build();
        }
    };

    SequenceSearcher searcherThatReturnsTrueForNumberSmallerThanFive = new SequenceSearcher() {
        @Override
        public SearchResult search(int elem, int[] sequence) {
            SearchResult.Builder builder = SearchResult.builder();

            if (elem < 5)
                builder.withFound(true);
            else
                builder.withFound(false);

            return builder.build();
        }
    };

    private int invocationCount = 0;
    SequenceSearcher searcherWithInvocationCount = new SequenceSearcher() {
        @Override
        public SearchResult search(int elem, int[] sequence) {
            SearchResult.Builder builder = SearchResult.builder();
            builder.withFound(true);
            invocationCount++;
            return builder.build();
        }
    };

    private int findCount = 0;
    SequenceSearcher searcherThatReturnsTrueForNumberSmallerThanFiveWithFindCount = new SequenceSearcher() {
        @Override
        public SearchResult search(int elem, int[] sequence) {
            SearchResult.Builder builder = SearchResult.builder();

            if (elem < 5) {
                findCount++;
                builder.withFound(true);
            } else
                builder.withFound(false);

            return builder.build();
        }
    };

    private SimilarityFinder similarityFinder;
    private int[] emptyArray = new int[]{};
    private int[] firstArray = new int[]{1};
    private int[] copyOfFirstArray = new int[]{1};
    private int[] secondArray = new int[]{2};
    private int[] thirdArray = new int[]{1, 2, 8, 9, 10};
    private int[] fourthArray = new int[]{11, 7, 8, 9, 10};

    @Test
    void passTheSameArraysShouldReturnOne() {
        double result, expectedValue = 1;

        similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsTrue);
        result = similarityFinder.calculateJackardSimilarity(firstArray, copyOfFirstArray);

        assertEquals(expectedValue, result);
    }

    @Test
    void passDifferentArraysShouldReturnZero() {
        double result, expectedValue = 0;

        similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsFalse);
        result = similarityFinder.calculateJackardSimilarity(firstArray, secondArray);

        assertEquals(expectedValue, result);
    }

    @Test
    void passPartlyTheSameArraysShouldReturnQuarter() {
        double result, expectedValue = 0.25;

        similarityFinder = new SimilarityFinder(searcherThatReturnsTrueForNumberSmallerThanFive);
        result = similarityFinder.calculateJackardSimilarity(thirdArray, fourthArray);

        assertEquals(expectedValue, result);
    }

    @Test
    void passEmptySequenceShouldReturnOne() {
        double result, expectedValue = 1;

        similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsTrue);
        result = similarityFinder.calculateJackardSimilarity(emptyArray, emptyArray);

        assertEquals(expectedValue, result);
    }

    @Test
    void passOneArrayEmptyShouldReturnZero() {
        double result, expectedValue = 0;

        similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsFalse);
        result = similarityFinder.calculateJackardSimilarity(emptyArray, firstArray);

        assertEquals(expectedValue, result);
    }

    @Test
    void passTheSameArraysShouldReturnOneExecuteTenTimes() {
        double result, expectedValue = 1;

        for (int i = 0; i < 10; ++i) {
            similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsTrue);
            result = similarityFinder.calculateJackardSimilarity(firstArray, copyOfFirstArray);
            assertEquals(expectedValue, result);
        }
    }

    @Test
    void passEmptySequenceShouldReturnOneExecuteTenTimes() {
        double result, expectedValue = 1;

        for (int i = 0; i < 10; ++i) {
            similarityFinder = new SimilarityFinder(searcherThatAlwaysReturnsTrue);
            result = similarityFinder.calculateJackardSimilarity(emptyArray, emptyArray);
            assertEquals(expectedValue, result);
        }
    }

    @Test
    void passArrayOfFiveElementsShouldInvokeMethodFiveTimes() {
        int expectedValue = 5;

        similarityFinder = new SimilarityFinder(searcherWithInvocationCount);
        similarityFinder.calculateJackardSimilarity(thirdArray, fourthArray);

        assertEquals(expectedValue, invocationCount);
    }

    @Test
    void passEmptyArrayElementsShouldNotInvokeMethod() {
        int expectedValue = 0;

        similarityFinder = new SimilarityFinder(searcherWithInvocationCount);
        similarityFinder.calculateJackardSimilarity(emptyArray, firstArray);

        assertEquals(expectedValue, invocationCount);
    }

    @Test
    void passArrayOfFiveElementsShouldFindTwoElements() {
        int expectedValue = 2;

        similarityFinder = new SimilarityFinder(searcherThatReturnsTrueForNumberSmallerThanFiveWithFindCount);
        similarityFinder.calculateJackardSimilarity(thirdArray, fourthArray);

        assertEquals(expectedValue, findCount);
    }
}
