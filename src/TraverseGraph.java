import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.*;
import org.json.*;

import java.util.LinkedList;

public class TraverseGraph {

    //Input Arguments
    static String startFlowNodeID;
    static String endFlowNodeID;

    public static void main(String[] args) throws IOException {
        System.out.println();
        //Command Line arguments
        startFlowNodeID = args[0].toString();
        endFlowNodeID = args[1].toString();

        //Get request to fetch the XML representation of the BPMN file
        URL url = new URL("https://n35ro2ic4d.execute-api.eu-central-1.amazonaws.com/prod/engine-rest/process-definition/key/invoice/xml");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET"); //GET request
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) { //If response value is not 200 then print error
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            Scanner sc = new Scanner(url.openStream());
            String response = "";
            while (sc.hasNext()) {
                response = response + sc.nextLine();
            }
            JSONObject obj = new JSONObject(response); //New JSON Object from API response
            String parseXML = obj.get("bpmn20Xml").toString(); //Parse XML by using Camunda Model API
            InputStream inputStream = new ByteArrayInputStream(parseXML.getBytes(Charset.forName("UTF-8")));

            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream); //For reading data from the Model API

            // find element instance by ID & store in variable type String for creating Graph Data Structure
            StartEvent start = (StartEvent) modelInstance.getModelElementById("StartEvent_1");
            String startEvent_1ID = start.getId();
            BusinessRuleTask assign = (BusinessRuleTask) modelInstance.getModelElementById("assignApprover");
            String assignApproverID = assign.getId();
            CallActivity review = (CallActivity) modelInstance.getModelElementById("reviewInvoice");
            String reviewInvoiceID = review.getId();
            ExclusiveGateway reviewSuccessful = (ExclusiveGateway) modelInstance.getModelElementById("reviewSuccessful_gw");
            String reviewSuccessful_gwID = reviewSuccessful.getId();
            EndEvent endInvoiceNotProcessed = (EndEvent) modelInstance.getModelElementById("invoiceNotProcessed");
            String invoiceNotProcessedID = endInvoiceNotProcessed.getId();
            UserTask userTaskApproveInvoice = (UserTask) modelInstance.getModelElementById("approveInvoice");
            String approveInvoiceID = userTaskApproveInvoice.getId();
            SequenceFlow sequenceFlow = (SequenceFlow) modelInstance.getModelElementById("invoiceApproved");
            String invoiceApprovedID = sequenceFlow.getId();
            UserTask userTaskPrepareBankTransfer = (UserTask) modelInstance.getModelElementById("prepareBankTransfer");
            String prepareBankTransferID = userTaskPrepareBankTransfer.getId();
            ServiceTask serviceTask = (ServiceTask) modelInstance.getModelElementById("ServiceTask_1");
            String ServiceTask_1ID = serviceTask.getId();
            EndEvent endInvoiceProcessed = (EndEvent) modelInstance.getModelElementById("invoiceProcessed");
            String invoiceProcessedID = endInvoiceProcessed.getId();

            //Create Graph Data Structure (Vertices & Edges) and pass arguments as String
            GraphDataStructure graphDataStructure = new GraphDataStructure();
            graphDataStructure.addEdge(startEvent_1ID, assignApproverID);
            graphDataStructure.addEdge(assignApproverID, approveInvoiceID);
            graphDataStructure.addEdge(approveInvoiceID, invoiceApprovedID);
            graphDataStructure.addEdge(invoiceApprovedID, reviewInvoiceID);
            graphDataStructure.addEdge(invoiceApprovedID, prepareBankTransferID);
            graphDataStructure.addEdge(prepareBankTransferID, ServiceTask_1ID);
            graphDataStructure.addEdge(ServiceTask_1ID, invoiceProcessedID);
            graphDataStructure.addEdge(reviewInvoiceID, reviewSuccessful_gwID);
            graphDataStructure.addEdge(reviewSuccessful_gwID, approveInvoiceID);
            graphDataStructure.addEdge(reviewSuccessful_gwID, invoiceNotProcessedID);
            sc.close();

            //Generate a collection of nodes
            LinkedList<String> visited = new LinkedList();
            visited.add(startFlowNodeID);
            new TraverseGraph().depthFirst(graphDataStructure, visited);
        }
    }

    //Algorithm for Depth-first-search (DFS) for traversing/searching the given exemplary invoice approval diagram

    private void depthFirst(GraphDataStructure graphDataStructure, LinkedList<String> visited) {

        LinkedList<String> nodes = graphDataStructure.adjacentNodes(visited.getLast());
        // inspect adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }

            if (node.equals(endFlowNodeID)) {
                visited.add(node);
                printPath(visited);
                visited.removeLast();
                break;

            }

        }
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(endFlowNodeID)) {
                continue;
            }

            visited.addLast(node);
            depthFirst(graphDataStructure, visited);
            visited.removeLast();
        }
    }


    //Print path along with Input Nodes
    private void printPath(LinkedList<String> visited) {

        System.out.print("The path from " + startFlowNodeID + " to " + endFlowNodeID + " is: ");
        System.out.print(visited);
        System.out.println();
    }
}




