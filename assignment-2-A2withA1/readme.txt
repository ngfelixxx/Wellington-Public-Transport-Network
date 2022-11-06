# COMP 261 Assignment 2 - Felix Ng (ngfeli)

## What the code does
* [x] Minimum
* [x] Core
* [x] Complete
* Challenge
   * [x] Allow time or distance in A* search
   * [ ] Limit travel by transportation type
   * [ ] Flexible walking distance calculated within A*
   * [ ] Implement pin dropping for start and goal points
   * [ ] Implement an on route stop (ie start to mid then mid to goal)
   * [ ] Download the Metlink data and implement the Trips and Stoptime to actually use the real data to make a time of Day A*

`write about your code`
I made a tick box so you can toggle between using the time heuristic or the distance heuristic in my A* search by default it uses time as the cost. 
When I set the maximum walking distance to a certain number it will count how many strongly connected components there are in the graph. 
When I key in a start stop and press enter then key in a destination stop and press enter it will find a path between those 2 stops using my A* algorithm.
Same with when I click on a stop and click on another one after. 

## Important Structures
`write here about your structures` 
I have used a priority queue as my fringe for my A* search alorithm because that when we poll from the queue we always get the closest stop from the start node
This will always result in the shortest path being found. We have a list of stops that have already been visited so we don't visit them again. We also have a list 
of Edges that get returned when we have found the shortest path. 
I have used an ArrayList to store my visitOrder for All the connections so that I can iterate backwards on that collection in order to assign the root
stop to the stop in the graph that is from that subgraph, all the connections in that subgraph will point towards that root node. This means once we 
found a stop to assign the root we have found a connected component and therefore we increment the count of connected components. 

## Good example of data structure use
`write something you did that is good`
I have implemented the slider and walking edges toggle button so that when I set the maximum walking distance eg. to 120 there will be 8 connected components.
This is because when I iterated through all of the stops twice in a nested loop to determine potential neighbours and I checked the distance between each of
the two stops Gispoints and if the distance is less than the distance that was set by the user, the edge will then be formed since it is walkable and it counts
the amount of these edges that got formed. Then as a result I was able to find the connected components on my graph given the walking edges.