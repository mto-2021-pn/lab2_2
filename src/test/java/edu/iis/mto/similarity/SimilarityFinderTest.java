package edu.iis.mto.similarity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class SimilarityFinderTest {

    @Test
    void testEmptySeq() {
        MockSequenceSearcher searcher = new MockSequenceSearcher();
        SimilarityFinder indexJaccard = new SimilarityFinder(searcher);
        int[] seq1 = {};
        int[] seq2 = {};
        assertThat(1.0d, Matchers.equalTo(indexJaccard.calculateJackardSimilarity(seq1, seq2)));

    }

    @Test
    void testResult() {
        class SequenceSearcherMock  implements SequenceSearcher {
            SearchResult searchResult;
            @Override
            public SearchResult search(int elem, int[] sequence) {

                if (elem % 2 == 0){
                   return searchResult = new SearchResult(SearchResult.builder().withFound(false));
                } else if (elem > 0 && elem < 20) {
                    return searchResult = new SearchResult(SearchResult.builder().withFound(true));
                }
                return null;
            }
        }
        int[] seq1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] seq2 = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        SequenceSearcherMock searcher = new SequenceSearcherMock();
        SimilarityFinder indexJaccard = new SimilarityFinder(searcher);
        assertEquals(5/15d, indexJaccard.calculateJackardSimilarity(seq1, seq2));


    }


    @Test
    void testSeparateSeq() {
        class SequenceSearcherMock  implements SequenceSearcher {
            SearchResult searchResult;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return searchResult = new SearchResult(SearchResult.builder().withFound(false));
            }
        }
        int[] seq1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] seq2 = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        SequenceSearcherMock searcher = new SequenceSearcherMock();
        SimilarityFinder indexJaccard = new SimilarityFinder(searcher);
        assertEquals(0, indexJaccard.calculateJackardSimilarity(seq1, seq2));


    }

    @Test
    void testIntersectValue() {
        class SequenceSearcherMock  implements SequenceSearcher {
            SearchResult searchResult;
            @Override
            public SearchResult search(int elem, int[] sequence) {

                if (elem % 2 == 0){
                    return searchResult = new SearchResult(SearchResult.builder().withFound(false));
                } else if (elem > 0 && elem < 20) {
                    return searchResult = new SearchResult(SearchResult.builder().withFound(true));
                }
                return null;
            }
        }
        int[] seq1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] seq2 = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        SequenceSearcherMock searcher = new SequenceSearcherMock();
        SimilarityFinder indexJaccard = new SimilarityFinder(searcher);
        indexJaccard.calculateJackardSimilarity(seq1, seq2);
        assertEquals(5, indexJaccard.getIntersectSize());

    }


    @Test
    void testUnionValue() {
        class SequenceSearcherMock  implements SequenceSearcher {
            SearchResult searchResult;
            @Override
            public SearchResult search(int elem, int[] sequence) {

                if (elem % 2 == 0){
                    return searchResult = new SearchResult(SearchResult.builder().withFound(false));
                } else if (elem > 0 && elem < 20) {
                    return searchResult = new SearchResult(SearchResult.builder().withFound(true));
                }
                return null;
            }
        }
        int[] seq1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] seq2 = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        SequenceSearcherMock searcher = new SequenceSearcherMock();
        SimilarityFinder indexJaccard = new SimilarityFinder(searcher);
        indexJaccard.calculateJackardSimilarity(seq1, seq2);
        assertEquals(15, indexJaccard.getUnionSize());

    }


}
