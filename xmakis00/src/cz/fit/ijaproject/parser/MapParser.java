package cz.fit.ijaproject.parser;

import cz.fit.ijaproject.warehouse.Category;
import cz.fit.ijaproject.warehouse.Shelf;
import cz.fit.ijaproject.warehouse.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.font.ShapeGraphicAttribute;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Module that works with files that contain map and items data.
 *
 * @author Jozef Makiš
 */
public class MapParser {
    /**
     * Variable contains path to map data.
     */
    private final String filenameMap;
    /**
     * Variable contains path to item data.
     */
    private final String filenameData;
    /**
     * Map parameters.
     */
    private int warehouseWidth;
    private int warehouseHeight;

    private int startX;
    private int startY;

    private int getdeliverX;
    private int getDeliverY;
    /**
     * List contains all loaded shells from file.
     */
    private final ArrayList<Shelf> loadedShelfs = new ArrayList<>();
    /**
     * List contains all loaded item types.
     */
    private final ArrayList<Type> loadedTypes = new ArrayList<>();
    /**
     * List contains all loaded categories.
     */
    private final ArrayList<Category> loadedCategory = new ArrayList<>();

    public MapParser(String filename, String filename1) {
        this.filenameMap = filename;
        this.filenameData = filename1;
    }
    /**
     * Method parses data from map file. First of all
     * it starts with map size, then it goes for shelf IDs and
     * theirs location in the map.
     */
    private void parseMap () {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        if (filenameMap == null) {
            System.err.println("Wrong map filename !");
            System.exit(1);
        }

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(this.filenameMap));
            doc.getDocumentElement().normalize();
            // Setting up our warehouse map width and height.
            if (doc.getDocumentElement().getNodeName().equals("warehouse")) {
                try {
                    this.warehouseWidth = Integer.parseInt(doc.getDocumentElement().getAttribute("width"));
                    this.warehouseHeight = Integer.parseInt(doc.getDocumentElement().getAttribute("height"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Wrong document element warehouse !");
                System.exit(1);
            }
            // Parsing shelf IDs and its location in the map.
            NodeList shelfsList = doc.getElementsByTagName("shelfs");
            for (int i = 0; i < shelfsList.getLength(); i++) {
                Node shelf = shelfsList.item(i);

                if (shelf.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) shelf;

                    int shelfcount = element.getElementsByTagName("shelf").getLength();
                    for (int j = 0; j < shelfcount; j++) {
                        int id = Integer.parseInt(element.getElementsByTagName("shelf").item(j).getAttributes().getNamedItem("id").getNodeValue());
                        int x = Integer.parseInt(element.getElementsByTagName("shelf").item(j).getAttributes().getNamedItem("x").getNodeValue());
                        int y = Integer.parseInt(element.getElementsByTagName("shelf").item(j).getAttributes().getNamedItem("y").getNodeValue());
                        this.loadedShelfs.add( new Shelf(id, x, y, null, 0));
                    }
                }
            }
            // Parsing start location of the delivery cart.
            NodeList start = doc.getElementsByTagName("start");
            if (start.getLength() != 0){
                Element elstart = (Element) start.item(0);
                this.startX = Integer.parseInt(elstart.getAttribute("x"));
                this.startY = Integer.parseInt(elstart.getAttribute("y"));
            } else {
                System.err.println("Wrong start element !");
                System.exit(1);
            }
            // Delivery stoppage.
            NodeList deliver = doc.getElementsByTagName("deliver");
            if (deliver.getLength() != 0){
                Element eldeliver = (Element) deliver.item(0);
                this.getdeliverX = Integer.parseInt(eldeliver.getAttribute("x"));
                this.getDeliverY = Integer.parseInt(eldeliver.getAttribute("y"));
            } else {
                System.err.println("Wrong deliver element !");
                System.exit(1);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Map parsing failed !");
            System.exit(1);
        }
    }
    /**
     * Method that parses input data from item file list. First of all it
     * starts with categories, then goes for types and at the end we
     * assign certain values to certain shelf that are in the file.
     */
    private void parseData () {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        if (this.filenameData == null) {
            System.err.println("Wrong data filename !");
            System.exit(1);
        }

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(this.filenameData));
            doc.getDocumentElement().normalize();

            if (!(doc.getDocumentElement().getNodeName().equals("data"))) {
                System.err.println("Wrong document element data !");
                System.exit(1);
            }

            // Parsing categories and storing them in to list
            NodeList definesList = doc.getElementsByTagName("defines");
            Element elDefList = (Element) definesList.item(0);
            NodeList defineList = elDefList.getElementsByTagName("categories");

            for (int i = 0; i < defineList.getLength(); i++) {
                Node define = defineList.item(i);

                if (define.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) define;
                    NodeList catList = element.getElementsByTagName("category");

                    for (int j = 0; j < catList.getLength(); j++) {
                        Node category = catList.item(j);

                        if (category.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElement = (Element) category;
                            int id = Integer.parseInt(subElement.getAttribute("id"));
                            String name = subElement.getTextContent();
                            this.loadedCategory.add(new Category(id, name));
                        }
                    }
                }
            }
            // Parsing types and theirs certain values that are in the file
            Element eltypesList = (Element) definesList.item(0);
            NodeList typeList = eltypesList.getElementsByTagName("types");

            for (int i = 0; i < typeList.getLength(); i++) {
                Node type_node = typeList.item(i);

                if (type_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) type_node;
                    NodeList typList = element.getElementsByTagName("type");

                    for (int j = 0; j < typList.getLength(); j++) {
                        Node type_in_category = typList.item(j);
                        if (type_in_category.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElement = (Element) type_in_category;
                            int id = Integer.parseInt(subElement.getAttribute("id"));
                            int cat_id = Integer.parseInt(subElement.getAttribute("category"));
                            double weight = Double.parseDouble(subElement.getAttribute("weight"));
                            String typeName = subElement.getTextContent();
                            this.loadedTypes.add(new Type(id, weight, typeName, this.loadedCategory.get(cat_id)));
                        }
                    }

                }
            }
            // Seting up list of shelfs with objects from previous parsing.
            NodeList itemsList = doc.getElementsByTagName("items");
            Element elitemList = (Element) itemsList.item(0);
            NodeList itemList = elitemList.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Node item_node = itemList.item(i);

                if (item_node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item_node;
                    int shelfid = Integer.parseInt(element.getAttribute("shelfid"));
                    int typeid = Integer.parseInt(element.getAttribute("typeid"));
                    int count = Integer.parseInt(element.getAttribute("count"));
                    this.loadedShelfs.set(shelfid, this.loadedShelfs.get(shelfid)).setType(this.loadedTypes.get(typeid));
                    this.loadedShelfs.set(shelfid, this.loadedShelfs.get(shelfid)).setCount(count);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Data parsing failed !");
            System.exit(1);
        }

    }
    /**
     * Public methon that starts whole parsing process. This needs to
     * be reachable from controller files.
     * @return list of all shelfs located in the file
     */
    public ArrayList<Shelf> retShelf () {
        parseMap();
        parseData();
        return this.loadedShelfs;
    }

    public ArrayList<Type> retTypes () {
        return loadedTypes;
    }

    public int retWareHouseHeight () {
        return warehouseHeight;
    }

    public int retWareHouseWidth () {
        return warehouseWidth;
    }

    public int getStartX () {
        return startX;
    }

    public int getStartY () {
        return startY;
    }

    public int getFinishX () {
        return getdeliverX;
    }

    public int getFinishY () {
        return getDeliverY;
    }

}
