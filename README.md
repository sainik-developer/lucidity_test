Assumption:
------------------------------
1. Rider has unlimited ability to carry food. 
2. Rider can visit any node any number of time if that reduces the traveling distance. 
3. If users can visit as many restaurant as early as possible that eventually will reduce the travelling distance. 
Eg. if there is customer (whose packet is already picked) and a restaurant is equally far from current location. it's much smarter to go for the restaurant rather customer as there may be different way to reach the customer later which as well can drop other nearby packets. 
Visiting restaurant has higher precedence than of same distance customer(whose packet can be delivered)  
4. Name of the Restaurant, customer and rider is unique

Approach 
----------------------------------
1. Try to use Dijkastra to find shortest path to next customer or restaurant. repetitively considering current is source but stop when we find a match. 
so in average case we don't completely search the graph. 
2. A Hurestic Selection Strategy is used to determine next node in dijkastra shortest path. 
