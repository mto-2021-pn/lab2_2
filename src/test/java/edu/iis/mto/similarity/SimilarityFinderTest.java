package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.fail;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SimilarityFinderTest {
    @Test
    void seq1Empty_seq2Empty_equalArrays(){
        int[] seq1 = {};
        int[] seq2 = {};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==1);
    }

    @Test
    void seq1Empty_seq2NotEmpty_notEqualArrays(){
        int[] seq1 = {};
        int[] seq2 = {1, 2};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==0);
    }

    @Test
    void seq1NotEmpty_seq2Empty_notEqualArrays(){
        int[] seq1 = {1, 2};
        int[] seq2 = {};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==0);
    }

    @Test
    void seq1NotEmpty_seq2NotEmpty_equalArrays(){
        int[] seq1 = {1, 2};
        int[] seq2 = {1, 2};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==1);
    }

    @Test
    void seq1NotEmpty_seq2NotEmpty_notEqualArrays_arraysHaveSameElem(){
        int[] seq1 = {1, 2};
        int[] seq2 = {1, 2, 3, 4};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==0.5);
    }

    @Test
    void seq1NotEmpty_seq2NotEmpty_notEqualArrays_arraysDontHaveSameElem(){
        int[] seq1 = {5, 6, 7, 8};
        int[] seq2 = {1, 2, 3, 4};

        SequenceSearcher seqserch = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);

        Assertions.assertTrue(sf.calculateJackardSimilarity(seq1, seq2)==0);
    }

    @Test
    public void countingSearchingInvokes_resEqualsLenOfFirstSeq_resShouldBeZero() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {};
        int[] seq2 = {};

        SequenceSearcher seqserch = new SequenceSearcher() {
            public int count = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                count++;
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);
        sf.calculateJackardSimilarity(seq1, seq2);

        Assertions.assertTrue(seqserch.getClass().getDeclaredField("count").getInt(seqserch) == 0);
    }

    @Test
    public void countingSearchingInvokes_resEqualsLenOfFirstSeq_resShouldBeOne() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {1};
        int[] seq2 = {1, 2, 3, 4};

        SequenceSearcher seqserch = new SequenceSearcher() {
            public int count = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                count++;
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);
        sf.calculateJackardSimilarity(seq1, seq2);

        Assertions.assertTrue(seqserch.getClass().getDeclaredField("count").getInt(seqserch) == 1);
    }

    @Test
    public void countingSearchingInvokes_resEqualsLenOfFirstSeq_resShouldBeTwo() throws NoSuchFieldException, IllegalAccessException {
        int[] seq1 = {1, 6};
        int[] seq2 = {1, 2, 3, 4};

        SequenceSearcher seqserch = new SequenceSearcher() {
            public int count = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                count++;
                for(int i = 0; i<sequence.length; i++){
                    if(sequence[i] == elem){
                        SearchResult sr = SearchResult.builder().withFound(true).withPosition(i).build();
                        return sr;
                    }
                }
                SearchResult sr = SearchResult.builder().withFound(false).withPosition(-1).build();
                return sr;
            }
        };

        SimilarityFinder sf = new SimilarityFinder(seqserch);
        sf.calculateJackardSimilarity(seq1, seq2);

        Assertions.assertTrue(seqserch.getClass().getDeclaredField("count").getInt(seqserch) == 2);
    }
}
