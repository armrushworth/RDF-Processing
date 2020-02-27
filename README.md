# COMP318 Ontologies and Semantic Web Assignment

The task consists of writing a Java programme that uses Jena to edit an RDF file and answers queries expressed in SPARQL. The dataset used models data about Nobel Laureates and their prizes from the first edition of the Nobel prize in 1901 until recent years.

Your programme should consists of one class only, named RDFProcessing.java, that reads in the RDF dataset of the Nobel laureates, stores the graph in a triple store, and answers some SPARQL queries against the triple store.

Write a programme that uses the Jena API to carry out the following tasks, in order:
- Load the data in the Nobel prize data dump file, and create a triple store to host it locally in a directory called NobelDB that should be a sub-directory of your Assignment working directory. Write and execute a SPARQL query against the triple store that prints the first 20 elements in the triple store.
- Formulate the following SPARQL queries and execute them against the newly created database. Pretty print the results as in the Jena SPARQL tutorial:
  - **Query 1:** Find the name of most recent Nobel Chemistry award winner in the dataset.
  - **Query 2:** Find the categories awarded during the first edition of the Nobel prize (1901).
  - **Query 3:**  List, in ascending order of award number, the countries who have Physiology or Medicine prize winners affiliated to one of their universities together with the number of awards received by the country.
  - **Query 4:**  Find all the Nobel Laureates, with the year of the award, who were born either in Germany (present or at the time and denoted as "now-Germany") or in a country that is now known as Germany.
