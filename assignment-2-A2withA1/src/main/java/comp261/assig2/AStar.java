package comp261.assig2;

/*
Dijkstra search alogrithm

@author: Simon McCallum, Github Copilot
*/

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ResourceBundle.Control;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.lang.Math.*; //added

// dijkstra class for path finding in the graph
public class AStar {
 

    public static ArrayList<Edge> findShortestPathEdges(Graph graph, Stop start, Stop end) { //Completion adds/alters this 
        //System.out.println("suck my arse"); //reached by GUI not working 

        // check if the start and end stops are null
        if (start == null || end == null) {
            return null;
        }
        // used to check how many nodes you visited. Lower is better H.
        int totalExplored = 0;

        ArrayList<Stop> stops = graph.getStopList(); 
        
        // creating a comparison function for the priority queue based on Path
        Comparator<PathItem> pathStopCompare = new PathCostComparator();
        // create a new priority queue for the finge        
        PriorityQueue<PathItem> fringe = new PriorityQueue<PathItem>(pathStopCompare);
        
        // create a new array list for the path to be extracted
        ArrayList<PathItem> visitedStops = new ArrayList<PathItem>();
       
        int currentFingeCost = 0;

        // vital step to make sure you can find the new path
        graph.resetVisited(); //all cost of nodes is equal 

        start.setCost(0); //sets the cost to first node as 0 

        double f = g(start) + heuristic(start, end); // also could be f(start,start, 0, start,end);
        fringe.add(new PathItem(start, 0, f, null, null)); // the input is (node, cost, f, prev, edge)
        
        // while the queue is not empty
        while (!fringe.isEmpty()) { //infinite loop!!! (gets hit) (values exist)
            //System.out.println("-----------------------------------------------");
            //System.out.println("infinite"); //true 
            // TODO: get the stop with the lowest cost
            // get the top of the queue
            PathItem current = fringe.poll(); //peek and remove (not removing)
            //PathItem current = fringe.peek();
            //fringe.remove(current);
            // TODO: check if the stop from the queue has been visited
            if(!current.getStop().isVisited()){ //if current stop has not been visited 
                // TODO: if the stop has not been visited, mark as visited and add to visitedStops list
                current.getStop().setVisited(true); //make it visited 
                visitedStops.add(current); //adds to the fringe 
                // TODO: set the cost for the current node
                current.getStop().setCost(current.getCost()); //set the new cost of getting from the start node to the current node since another node has been visited
                totalExplored++;
            } //brackets end here?
            else continue; //goes to next iteration 
            // TODO: if the current stop is the end stop
            if(current.getStop().equals(end)){ //in the other case if after the if statement if we have reached out goal node
                //System.out.println("reached"); //crashed and never reached 
                // TODO: path to return
                 ArrayList<Edge> shortestEdgePath = makeEdgePath(graph, visitedStops, start, end); //create a path using our parameters
                 return shortestEdgePath;
            }
            // TODO: go through each of the current stop's neighbours and add to the fringe
            //System.out.println("before: "+fringe.size());
            for(Edge e : current.getStop().getNeighbours()){ //if we have not yet reached our goal visit the neighbours of our curent node
                //System.out.println("true");
                //fringe.add(current);
                // TODO: get the neighbour from the edge
                Stop neigh = e.getToStop();
                // TODO: if the neighbour has not been visited already
                if(!neigh.isVisited()){ //if we havent yet visited 
                    //TODO: set the neighbour's cost to the current stop's cost + the edge's cost (time or distance)
                    neigh.setCost(current.getStop().getCost()+e.getCost()); //set the cost of that newly visited node with the current cost plus the neighbour edge
                    //Error checking is useful
                    if (neigh.getCost() < 0) { //neighbour cost??? //current.getStop().getCost()
                        System.out.println("Error: negative cost");
                    }
                    // TODO: calculate the new f value using g edge cost and heuristic
                    // something like
                    f = g(current.getStop()) + e.getCost() + heuristic(neigh, end); //current is a pathItem type??? //cost V distance
                    //f is the hueristic cost reamining from the current node to our goal node 
                    // TODO: add the neighbour to the queue as a new PathItem
                    // using somthing like neighbour, cost, f, prev, edge 
                    //PathItem newPathItem = new PathItem(start, e.getCost(), neigh); 
                    //double cost = current.getCost() + e.getDistance(); //Minimum
                    double cost = current.getCost() + e.getTime(); //Completion 
                    PathItem newPathItem = new PathItem(neigh, cost, f, current.getStop(), e);
                    fringe.add(newPathItem); //we create a new pathItem with the updated parameters. 
                    //System.out.println(newPathItem);
                    //System.out.println("Suck my arse: "+fringe.size());
                    //System.out.println("after: "+fringe.size());
                }
            }
            //System.out.println("-----------------------------------------------");
        }
        // comment on failue
        System.out.println("Error: " + start + " to " + end + " not found");
        return new ArrayList<Edge>();
    }
    //take one pathItem from our fringe and check that if that stop has been visited we need to skip it and go to
    //the next iteration otherwise we will visit that stop and set the cost of getting to that stop and then check 
    //if that stop is the goal stop we want. If it is not then iterate through its neigbours and if the neighbour 
    //has not been visited then set the cost of getting to that neighbour and calculate the remaining heuristic to our goal 
    //then we add the new path item to the fringe. In the next iteration the priority queue will remove the stop
    //with the smallest cost since Dijstra and A* will always visit the closet stop to the start node. 

    /**
     * build a path from the end back to the start from the PathItem data
     * 
     * @param graph
     * @param visited the nodes visited while searching for this trip
     * @param start
     * @param goal
     * @return the list of stops in the path
     */
    private static ArrayList<Edge> makeEdgePath(Graph graph, ArrayList<PathItem> visited, Stop start, Stop goal) {
        ArrayList<Edge> path = new ArrayList<Edge>();
        Edge currentItem = visited.get(visited.size() - 1).getEdge(); // the last edge added is on the path

        // while the current item is not the start
        while (currentItem.getFromStop() != start) {
            path.add(currentItem);
            // find the stop from the visited PathItems
            for (PathItem visitedItems : visited) {
                if (visitedItems.getEdge() == null) {
                    continue;
                }
                if (visitedItems.getEdge().getToStop() == currentItem.getFromStop()) {
                    if (visitedItems.getEdge().getToStop() == null) {
                        return null;
                    }
                    currentItem = visitedItems.getEdge();
                }
            }
            if (currentItem == null) { // if not invalid path
                System.out.println("error: ");
                return null;
            }
        }
        // finally add the start and return
        path.add(currentItem);
        return path;
    }


    public static double f(Stop current, double edgeCost, Stop neighbour, Stop end) {
        return g(current) + edgeCost + heuristic(neighbour, end);
    }

    public static double g( Stop current) {
        return current.getCost();
    }

    public static double heuristic(Stop current, Stop goal) { //change for completion 
        // TODO: calculate a heuristic for the current stop to the goal stop
        //Distance 
        //double result = Math.sqrt((Math.pow((current.getLon()-goal.getLon()),2) + Math.pow((current.getLat()-goal.getLat()),2)));
        //System.out.println("before: "+result); 
        //result = result/Transport.WALKING_SPEED_KPH; //completion 
        double result = current.getPoint().distance(goal.getPoint())/Transport.TRAIN_SPEED_MPS;
        //System.out.println("after: "+result);
        return result; //Pythagoras theorem
        //((x1-x2)^2 +(y1-y2)^2)
        //return 0;
    }

}
