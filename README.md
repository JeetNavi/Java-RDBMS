
Implementation of the minimization procedure for conjunctive queries and a lightweight database engine for evaluating queries called Minibase.

## README file

### Extracting join conditions from the body of a query 
This is done in my QueryPlan class, specifically in the groupComparisonAtoms method.  
In order to explain my way of extracting join conditions i will go through an example:  

We must construct a left deep plan.  
So for the CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z)  
We must first join A and B. Then we join this result with C, then we join this result with D.  
So the first join concerns A and B (lets call it AB). The second join Concerns AB and C (lets call it ABC). The third and final join concerns ABC and D.  

As mentioned in my JavaDocs, we keep track of which relational atom (RA) number each variable belongs to.  
In the CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z).  
The variable w belongs to relational atom 0, x belongs to 1, y belongs to 2 and z belongs to 3.  

Now imagine we have the following added comparison atoms:  
CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z), w=x, x=z.  
Each of the comparison atoms above are join conditions.  
For example, w=x is a join condition because w belongs to RA number 0 and x belongs to RA number 1, and 0!=1.  
If the lhs and rhs belonged to the same RA number, then it would be a selection condition.  
If the lhs or rhs was a constant, this would also be a selection condition.  

I will now explain how i assign join conditions.  
When i say that a join condition belongs to a relational atom, i mean that the join is between that relational atom and the   
previous relational atom (which may already be joined).  
For instance, in CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z), w=x, x=y.  
We assign the join condition x=y to the relational atom C because it is the join condition for AB and C.  
The way i work this out is by **choosing the maximum of the 2 RA numbers**.  
For instance, in x=y, with RA number of x being 1 and RA number of y being 2, since y's RA number is greater than x's RA number (2>1),  
we assign the join condition to y's relational atom.  

So in the CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z), w=x, x=z.  
The join condition w=x is assigned to RA number 1; the join condition is between A and B.  
The join condition x=z is assigned to the RA number 3; the join condition is between ABC and D.  

Note that in CQ: Q(w,x,y,z) :- A(w), B(x), C(y), D(z), w=x, x=z. There is no join condition between AB and C, so this ends up being a cartesian product.  

### Explanation of building query plan  

This is explained with code line comments in my buildQueryPlan() method in my QueryPlan class.  

### Explanation of optimisation

* I apply any and all selection conditions on individual relational atoms before applying any joins, so we filter as early as possible.  
This is correct because the output tuples will not change if we perform individual selection conditions at different times in the plan.
This is better because selections are essentially free whereas joins are expensive.  
So the intermediate inputs to joins are smaller when we apply selection conditions at the beginning.  

* If there are multiple selection conditions on an individual relation, this is one-by-one rather than a large conjunction (sigma cascade).  
The reason why this is correct is because selection is cascadable and commutative.  
This is better as we are applying each outer selection on a fewer set of input tuples as we apply more inner selections.  

* Because we are instructed to create a left deep plan, i was not able to apply optimisations on the joins.  

We also make some optimisations by rewriting. The following are all correct because they create equivalent CQ's, it is just simplified.  
The query rewriter applies heuristics & RA rules, without looking into the actual database state (no info about cardinalities, indices, etc.)  

* If we see a comparison atom consisting of only constants, We can remove it from the body if it is always true. If it is always false, we can stop early because there will be no output tuples.  

Finally, my sumOperator is optimised such that it doesn't need to have all of the child tuples in a buffer.  
Instead, it only needs 1 child tuple in a buffer (essentially there is no buffer).  
What we do is we maintain a mapping from groups to current sums.  
We then look at the child tuple and see if the group this child tuple belongs to is currently a key in this hashmap.  
If it is a key in the hashmap, we update the corresponding sum value accordingly (by adding to it).  
If it is not a key in the hashmap, we insert the key and insert the initial sum (which depends on the sum aggregate).  
We then get the next child tuple and repeat.  
This way we do not need to maintain any buffer, so there is no chance of us running out of space in main memory here!  
This is correct because of the properties of sum. We are able to keep temporary values and update it until there is no more to add.  
