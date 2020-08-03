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
import java.util.logging.Handler;

public class Search {

    static String START = "approveInvoice";
    static String END = "invoiceProcessed";

    public static void main(String[] args) throws IOException {

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
            JSONObject obj = new JSONObject(response);
            String parseXML = obj.get("bpmn20Xml").toString();
            InputStream inputStream = new ByteArrayInputStream(parseXML.getBytes(Charset.forName("UTF-8")));
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);

            // find element instance by ID & store in variable type String for creating Grapg
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

            Graph graph = new Graph();
            graph.addEdge(startEvent_1ID, assignApproverID);
            graph.addEdge(assignApproverID, approveInvoiceID);
            graph.addEdge(approveInvoiceID, invoiceApprovedID);
            graph.addEdge(invoiceApprovedID, reviewInvoiceID);
            graph.addEdge(invoiceApprovedID, prepareBankTransferID);
            graph.addEdge(prepareBankTransferID, ServiceTask_1ID);
            graph.addEdge(ServiceTask_1ID, invoiceProcessedID);
            graph.addEdge(reviewInvoiceID, reviewSuccessful_gwID);
            graph.addEdge(reviewSuccessful_gwID, approveInvoiceID);
            graph.addEdge(reviewSuccessful_gwID, invoiceNotProcessedID);
            sc.close();


            LinkedList<String> visited = new LinkedList();

            visited.add(START);
            new Search().depthFirst(graph, visited);
        }

    }

    private void depthFirst(Graph graph, LinkedList<String> visited) {
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                printPath(visited);
                visited.removeLast();
                break;
            }
        }
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            depthFirst(graph, visited);
            visited.removeLast();
        }
    }

    private void printPath(LinkedList<String> visited) {

        System.out.print("The path from " + START + " to " + END + " is: ");
        System.out.print(visited);
        System.out.println();


    }
}




