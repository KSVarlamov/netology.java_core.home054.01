package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> lostFromCSV = parseCSV(columnMapping, "data.csv");
        saveStringToFile(listToJson(lostFromCSV), "data-from-csv.json");

        List<Employee> listFromXML = parseXML("data.xml");
        saveStringToFile(listToJson(listFromXML), "data-from-xml.json");
    }

    private static void saveStringToFile(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String s) {
        List<Employee> result = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(s));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                   Element employee = (Element) node;
                   Employee empl = new Employee(
                           Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent()),
                           employee.getElementsByTagName("firstName").item(0).getTextContent(),
                           employee.getElementsByTagName("lastName").item(0).getTextContent(),
                           employee.getElementsByTagName("country").item(0).getTextContent(),
                           Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent()));
                   result.add(empl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(new File(fileName)))) {
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
