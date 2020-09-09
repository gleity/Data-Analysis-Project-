# Data-Analysis-Project
In this project we developed a learning algorithm that learns to identify hand-written digits (specifically MNIST and Kannada-MNIST datasets) pictures.
The algorithm builds am optimal decision tree by these arguments:
version, percent of training set, max power of 2 for max tree size, input file name and output file name.
In the project, we have to versions of decision trees, which differs in their condition nodes. In version 1 we have a simple condition type.
The condition is "is the value of pixel (X,Y) > 128 ?". In version 2 we generate graphs randomly (linear lines and paraboles) and choose the one with the highest hit rate
with pixels that their value is bigger then 128.

The algorithm builds all the trees in sizes of powers of 2 until we reach the max size of 2^size, and then we choose the most optimal tree between all those trees.
The calculation of the most optimal tree is done by calculating the tree entropy through the trees construction, and improving leafs entropy each time we expand the tree.
