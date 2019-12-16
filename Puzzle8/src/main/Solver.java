package main;
import main.lib.MinPQ;
import main.lib.Stack;
import main.lib.Stopwatch;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Solver {
    private boolean solvable;
    private Stack<Board> solution = null;
    private SearchNode searchNode;
    private int moves;
    
    private static class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int moves;
        private SearchNode previous;

        public SearchNode(Board board, SearchNode previous) {
            this.board = board;
            if (previous != null) {
                this.moves = previous.moves++;
                this.previous = previous;
            } else
                this.moves++;
        }

        @Override
        public int compareTo(SearchNode o) {
            return (this.board.manhattan() + this.moves)
                    - (o.board.manhattan() + o.moves);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<SearchNode> nodes = new MinPQ<>();
        
        MinPQ<SearchNode> twinNodes = new MinPQ<>();
        Set<Integer> hashes = new HashSet<>();
        Set<Integer> twinHashes = new HashSet<>();
        SearchNode searchTwinNode;
        
        boolean solved = false;
        Stopwatch watch = new Stopwatch();

        // insert the first initial node into the priority queue
        searchNode = new SearchNode(initial, null);
        nodes.insert(searchNode);

        // insert the first twin node into the twin priority queue
        searchTwinNode = new SearchNode(initial.twin(), null);
        twinNodes.insert(searchTwinNode);

        
        while (true) {

            // delete MIN node
            if (!nodes.isEmpty())
                searchNode = nodes.delMin();
            else
                break;

            // is goal?
            if (searchNode.board.isGoal()) {
                solvable = true;

                

                break;
            }

            // parallel search to determine whether this puzzle is solvable
            if (!twinNodes.isEmpty())
                searchTwinNode = twinNodes.delMin();
            else
                break;

            if (searchTwinNode.board.isGoal()) {
                break;
            }

            // insert onto the priority queue all neighboring search nodes
            for (Board neighbor : searchNode.board.neighbors()) {
                // critical optimization
                insertNeighboringSearchNodes(nodes, hashes, neighbor);
            }

            // insert onto twin priority queue all neighboring search nodes
            for (Board neighbor : searchTwinNode.board.neighbors()) {
                // critical optimization
                insertNeighboringSearchNodes(twinNodes, twinHashes, neighbor);
            }

            // time limit
            if (watch.elapsedTime() >30) {
                break;
            }

        }
    }

    private void insertNeighboringSearchNodes(MinPQ<SearchNode> nodes, Set<Integer> hashes, Board neighbor) {
        int hashCode;
        hashCode = neighbor.toString().hashCode();
        if (!hashes.contains(hashCode)) {
            nodes.insert(new SearchNode(neighbor, searchNode));
            hashes.add(hashCode);
           
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        if (solvable) {
            return createStackSolution().size() - 1;
            // return searchNode.moves;
        } else
            return -1;
    }

    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution() {
        if (isSolvable()) {
            return createStackSolution();
        }
        return null;
    }

    private Stack<Board> createStackSolution() {
        if (solution == null) {
            solution = new Stack<>();

            while (searchNode != null) {
                solution.push(searchNode.board);
                searchNode = searchNode.previous;
            }
        }
        return solution;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
       
    	 int[][] blocks = {
    			 
 	    		{1,2,3,4,5,6,7,8,9},
 	            {10,11,12,13,14,15,16,17,18},
 	            {19,20,21,22,23,24,25,26,27},
 	            {28,29,30,31,32,33,34,35,36},
	            {37,38,39,40,41,42,43,44,45},
	            {46,47,48,49,50,51,52,53,54},
	            {55,56,57,58,59,60,61,62,63},
 	            {64,0,65,67,68,78,69,70,72},
 	            {73,74,66,75,76,77,79,71,80}};
    	 /*int[][] blocks = {
  	    		{13,1,10,4},
  	            {8, 12,6,3},
  	            {15, 9, 5,14},
  	           {0, 7, 2,11}};*/
    	 /*int[][] blocks = {{2,0,5,4},
  	            	{10, 3,6,7},
  	            	{13, 1, 9,15},
  	            	{8, 11, 12,14}};*/
    	/*int[][] blocks = {
 	    		{1,6,4},
 	            {7,0,8},
 	            {2,3,5}};*/
    	/*int[][] blocks = {
    			{2,9,3,5},
	            {8,11,12,7},
	            {15,4,0,13},
	            {6,1, 10,14}};*/
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
        	System.out.println("No solution possible");
        else {
        	
           
        	System.out.println("Minimum number of moves = "+ solver.moves());
        	
            for (Board board : solver.solution()) {
            	//System.out.println("AQUI ITERACION: "+i);
            	System.out.println("Hamming "+board.hamming());
            	System.out.println("Manhatan "+board.manhattan());
                System.out.println(board);
            }
        }
    }
}
