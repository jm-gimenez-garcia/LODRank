# This script makes use of the igraph library. Use the following commands to use it:
# install.packages("igraph")
# library("igraph")

lod_graph <- graph.data.frame(read.table("outlinks.uniq.txt"), directed=TRUE, vertices = NULL)
lod_pagerank <- pagerank <- page.rank(lod_graph)
write.table(lod_pagerank[[1]],file="pagerank.csv",sep = ",",col.names = FALSE)