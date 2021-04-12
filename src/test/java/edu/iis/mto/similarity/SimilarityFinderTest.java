package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimilarityFinderTest {


    @Test
    void emptySeqTest() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = SearchResult.builder().build();
                return searchResult;
            }
        });

        int seq [] = {};
        int seq2 [] = {};

        assertEquals(1.0d, similarityFinder.calculateJackardSimilarity(seq, seq2));

    }


    @Test
    void oneCommonTest(){
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = SearchResult.builder().withFound(true).build();
                return searchResult;
            }
        });

        int seq [] ={10};
        int seq2 [] = {1, 2, 3, 10};

        assertEquals(0.25d,similarityFinder.calculateJackardSimilarity(seq, seq2));
    }


    @Test
    void twoCommonTest(){
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = SearchResult.builder().withFound(true).build();
                return searchResult;
            }
        });

        int seq [] = {10, 20};
        int seq2 [] = {1, 2, 20, 30, 10};
        assertEquals(0.4, similarityFinder.calculateJackardSimilarity(seq, seq2));
    }

    @Test
    void noCommonTest(){
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = SearchResult.builder().withFound(false).build();
                return searchResult;
            }
        });
        int seq [] = {10, 20};
        int seq2 [] = {1, 2, 3, 4};

        assertEquals(0, similarityFinder.calculateJackardSimilarity(seq, seq2));
    }


    


}
