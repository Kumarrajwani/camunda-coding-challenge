_Camunda - > Java Coding Challenge - Traverse the diagram:_

- The application fetches the XML representation of the exemplary ‘invoice approval’ BPMN diagram
  depicted above from a remote server.
- It parses the XML into a traversable data structure.
- It finds the possible paths on the graph between a given start node, and a given end
  node.
- It prints out the IDs of all nodes on the found path to System.out.

How to run in your local machine?

    1. Download traverse-graph.jar in your local machine
    2. Open CMD (Windows)/Terminal (Mac)

Write Command: java -jar Path/traverse-graph.jar startFlowNodeID endFlowNodeID

For example:

    Input at CMD: java -jar Mahesh/traverse-graph.jar approveInvoice invoiceProcessed

    Output: The path from approveInvoice to invoiceProcessed is: [approveInvoice, invoiceApproved, prepareBankTransfer, ServiceTask_1, invoiceProcessed]

Answers to the additional questions:

1.  It took 4 hours to solve this challenge, which includes fetching JSON file (GET Request), creating a graph data structure, research about Camunda Model API(as I never used it), writing business logic/DFS algorithm, creating README.md file and deployment(.jar) with CMD arguments parsing.

2.  I considered all edge cases while generating the graph data structure.

3.  In terms of implementation, I faced the following problems/limitations:

        1. First time worked with Camunda Model API, that's why it took some extra time.
        2. Faced some challenges while implementing a graph data structure
        3. As suggested, I tried to find a method of traversing the graph in Camunda out of box features/documentations but couldn't find that then went for custom development.
