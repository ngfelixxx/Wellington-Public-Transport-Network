package comp261.assig2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.scene.paint.Color;

public class Graph {

    private HashMap<String, Stop> stopsMap;
    private HashMap<String, Trip> tripsMap;

    private ArrayList<Stop> stopList;
    public Trie trie;

    public Zoning geoJson;

    private int subGraphs = 0;

    public Graph(HashMap<String, Stop> stops, HashMap<String, Trip> trips) {
        this.stopsMap = stops;
        this.tripsMap = trips;
        buildStopList();
        createNeighbours();
    }

    // constructor with parsing
    public Graph(File stopFile, File tripFile, File geoJsonFile) {
        stopsMap = new HashMap<String, Stop>();
        tripsMap = new HashMap<String, Trip>();
        stopsMap = Parser.parseStops(stopFile);
        tripsMap = Parser.parseTrips(tripFile);
        geoJson = Parser.parseGeoJson(geoJsonFile);
        // caclculate exact memory usage of the graph

        buildStopList();
        attachTripsToStops();
        createNeighbours();

    }

    // buildStoplist from hashmap
    private void buildStopList() {
        stopList = new ArrayList<Stop>();
        for (Stop s : stopsMap.values()) {
            stopList.add(s);
        }
    }



    // attach trip data to each stop
    private void attachTripsToStops() {
        for (Trip trip : tripsMap.values()) {
            for (String stopId : trip.getStopIds()) {
                Stop stop = stopsMap.get(stopId);
                if (stop != null) {
                    // add the trip to the stop
                    stop.addTrip(trip);
                } else {
                    System.out.println("Missing stop pattern id: " + stopId);
                }
            }
        }
    }

    /**
     * For every stop tell it to construct the edges associated with the trips that have been stored in it.
     */
    private void createNeighbours() {
        for (Stop stop : stopsMap.values()) {
            stop.makeNeighbours(this.stopsMap);
        }
    }

    // get first stop that starts with a search string
    public Stop getFirstStop(String search) {
        // Search for the first stop matching the search string
        // This is slow and would be faster with a Trie 
        Stop firstStop = null;
        for (Stop stop : stopList) {
            if (stop.getName().startsWith(search)) {
                firstStop = stop;
                break;
            }
        }
        return firstStop;
        //This would be the call to the Trie from Assignment 1
        //return trie.getAll(search).get(0);
    }

    // get all stops that start with a search string
    public List<Stop> getAllStops(String search) {
        //search for all stops matching the search string
        List<Stop> allStops = new ArrayList<Stop>();
        for (Stop stop : stopList) {
            if (stop.getName().startsWith(search)) {
                allStops.add(stop);
            }
        }
        return allStops;
        // This would be the call to the Trie from Assignment 1
        // return trie.getAll(search);
    }

    // getter for stopList
    public ArrayList<Stop> getStopList() {
        return stopList;
    }

    public HashMap<String, Stop> getStops() {
        return stopsMap;
    }

    public void setStops(HashMap<String, Stop> stops) {
        this.stopsMap = stops;
    }

    public HashMap<String, Trip> getTrips() {
        return tripsMap;
    }

    public void setTrips(HashMap<String, Trip> trips) {
        this.tripsMap = trips;
    }



    public void resetVisited() {
        for (Stop stop : stopList) {
            stop.setVisited(false);
            stop.setCost(Double.MAX_VALUE);
        }
    }

    // I have used Kosaraju's_algorithm from https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
    // You can use this or use Tarjan's algorithm for strongly connected components https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
    // find graph components and lable the nodes in each component
    public int findComponents() { //SCC //sussy baka 
        // TODO: implement component analysis
        //Stack<Stop> stack = new Stack<Stop>(); //imported stack 
        int component = 0;
        // TODO: reset visited and cost 
        resetVisited(); //should also reset cost along with it 
        for(Stop s : stopList){
            s.setRoot(null);
        }
        // TODO: go through all stops use recursive to visitAllConnections and build visit order
        ArrayList<Stop> All = new ArrayList<Stop>(); //gets populated 
        for(Stop s : stopList){
            if(!s.isVisited()){
                visitAllConnections(s, All);
            }
        }
        // TODO: search in reverse visit order setting the root node recurssively on assignRoot
        for (int c = All.size()-1; c >= 0; c--) {
            if(All.get(c).getRoot() == null){
                assignRoot(All.get(c), All.get(c), component);
                // TODO: keep a count of the components
                component = component + 1;
            }
        }
        // TODO: set the subgraphs to number of components and return it
        // something like subGraphs = components;
        subGraphs = component;
        return subGraphs;
    }

    /**
     * Recursively visit all connections of a stop and build a list of stops visited from this stop
     */
    private void visitAllConnections(Stop stop, ArrayList<Stop> visitOrder) { //fill order //done???
        // TODO: set vistited to true
        stop.setVisited(true);
        // TODO: add stop to visitOrder
        visitOrder.add(stop);
        // TODO: for each edge of the stop if the ToStop is not visited, recurse this function with the toStop of the neighbour Edge
        for(Edge e : stop.getNeighbours()){
            if(!e.getToStop().isVisited()){
                visitAllConnections(e.getToStop(), visitOrder);
            }
        }
        //push stack?
    }

    /**
     * Recursively assign the root node of a subgraph
     */
    public void assignRoot(Stop stop, Stop root, int component) { //DFSUtil //done?
        // TODO: set the root of the subgraph to the stop, and the subgraph ID
        stop.setRoot(root);
        stop.setSubGraphId(component);
        // TODO: for each of the edges in neighbours if the toStop root is empty recurse assigning root and component
        for(Edge e : stop.getNeighbours()){
            if(e.getToStop().getRoot() == null){ //assuming null is empty? should be checking for if neighbour is unvisited?
                //System.out.println("suck my arse");
                assignRoot(e.getToStop(), root, component); //have to change stop to neighbour or we would end up in an infinite loop?
            }
        }
    }

    /**
     * reset the root and the subgraph ID of all stops
     */
    public void resetRoot() {
        for (Stop stop : stopList) {
            stop.setRoot(null);
            stop.setSubGraphId(-1);
        }
    }


    public int getSubGraphCount() {
        return subGraphs;
    }

    // add walking edges
    public void addWalkingEdges(double walkingDistance) { //transpose //done???
        // TODO: add walking edges to all stops
        int count = 0;
        // TODO: step through all stops and all potential neighbours 
         for(Stop s : stopList){
             for(Stop pn : stopList){
                //TODO: check the distannce between to stops and if it is less then walkingDistance add an edge to the graph
                // something like:   
                if(s.getPoint().distance(pn.getPoint()) < walkingDistance){
                    Edge newEdge = new Edge(s, pn, Transport.WALKING_TRIP_ID, s.distance(pn)/Transport.WALKING_SPEED_MPS);
                    //System.out.println(newEdge);
                    s.addNeighbour(newEdge);
                }
                
                // count the number of edges added
                count++;

            }
        }
        System.out.println("Walking edges added: " + count);
        //returns graph?
    }

    // remove walking edges  - could just make them invalid or check the walking_checkbox
    public void removeWalkingEdges() {
        for (Stop stop : stopList) {
            stop.deleteAllEdges(Transport.WALKING_TRIP_ID);// remove all edges with the walking trip id
        }
    }

    // A nicer way to build a string of a list of stops
    public String DisplayStops(List<Stop> listOfStops) {
        String returnString = "";
        for (Stop stop : listOfStops) {
            returnString += stop.getName() + "\n";
        }
        return returnString;
    }

}
