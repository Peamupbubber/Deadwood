import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import java.util.*;


public class XMLBoardParser {
    
    private String fileName;
    private ArrayList<FilmSet> filmSets;
    // private ArrayList<FilmSet> neighbors;

    public XMLBoardParser(String fileName)
    {
        this.fileName = fileName;
        filmSets = new ArrayList<FilmSet>();
        parse(fileName);
    }

    private Document read(String fileName) throws ParserConfigurationException
    {
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = docFac.newDocumentBuilder();

        Document doc = null;

        try{
            doc = build.parse(fileName);
        }
        catch(Exception e)
        {
            System.out.println("Exception");
        }
        return doc;
    }

    private void parse(String fileName)
    {
        Document doc = null;
        try{
            doc = read(fileName);
        }catch(Exception e)
        {
            System.out.println("Exception");
        }

        Element root = doc.getDocumentElement();
        NodeList children = root.getElementsByTagName("set");

        for(int i = 0; i < children.getLength(); i++)
        {
            Node childNode = children.item(i);

            String name = childNode.getAttributes().getNamedItem("name").getNodeValue();

            FilmSet set = new FilmSet(name);

            NodeList childrens = childNode.getChildNodes();
            
            for(int j = 1; j < childrens.getLength(); j+=2)
            {
                Node childrensNode = childrens.item(j);
                String nodeName = childrensNode.getNodeName();
                if(nodeName.equals("neighbors"))
                {
                    NodeList neighbors = childrensNode.getChildNodes();
                    for(int l = 1; l < neighbors.getLength(); l+=2)
                    {
                        Node neighbor = neighbors.item(l);
                        if(!neighbor.getAttributes().getNamedItem("name").getNodeValue().equals("trailer") || !neighbor.getAttributes().getNamedItem("name").getNodeValue().equals("office")) {
                            set.addNeighbor(neighbor.getAttributes().getNamedItem("name").getNodeValue());
                        }
                        
                    }
                }else if(nodeName.equals("takes"))
                {
                    NodeList takes = childrensNode.getChildNodes();
                    
                    Node maxShotCounter = takes.item(1);

                    int ShotCounter = Integer.parseInt(maxShotCounter.getAttributes().getNamedItem("number").getNodeValue());
                    set.setMaxShotCounters(ShotCounter);
                    for(int l = 1; l < takes.getLength(); l+=2)
                    {
                        Node take = takes.item(l);
                        NodeList areas = take.getChildNodes();
                        Node areaVals = areas.item(0);

                        int[] area = new int[4];
                        area[0] = Integer.parseInt(areaVals.getAttributes().getNamedItem("x").getNodeValue());
                        area[1] = Integer.parseInt(areaVals.getAttributes().getNamedItem("y").getNodeValue());
                        area[2] = Integer.parseInt(areaVals.getAttributes().getNamedItem("h").getNodeValue());
                        area[3] = Integer.parseInt(areaVals.getAttributes().getNamedItem("w").getNodeValue());
                        
                        set.addShotCounterArea(ShotCounter, area);
                        ShotCounter--;
                    }
                }else if(nodeName.equals("parts"))
                {
                    NodeList parts = childrensNode.getChildNodes();

                    for(int l = 1; l < parts.getLength(); l+=2)
                    {
                        Node part = parts.item(l);

                        String partName = part.getAttributes().getNamedItem("name").getNodeValue();
                        int level = Integer.parseInt(part.getAttributes().getNamedItem("level").getNodeValue());
                        
                        NodeList partChild = part.getChildNodes();

                        int x = Integer.parseInt(partChild.item(1).getAttributes().getNamedItem("x").getNodeValue());
                        int y = Integer.parseInt(partChild.item(1).getAttributes().getNamedItem("y").getNodeValue());
                        int h = Integer.parseInt(partChild.item(1).getAttributes().getNamedItem("h").getNodeValue());
                        int w = Integer.parseInt(partChild.item(1).getAttributes().getNamedItem("w").getNodeValue());
                        String line = partChild.item(3).getTextContent();
                        int[] area = {x,y,h,w};
                        Role role = new Role(level, false, partName, line, area);
                        set.addRoleToSet(role);
                        
                    }
                }else if(nodeName.equals("area"))
                {
                    int x = Integer.parseInt(childrensNode.getAttributes().getNamedItem("x").getNodeValue());
                    int y = Integer.parseInt(childrensNode.getAttributes().getNamedItem("y").getNodeValue());
                    int h = Integer.parseInt(childrensNode.getAttributes().getNamedItem("h").getNodeValue());
                    int w = Integer.parseInt(childrensNode.getAttributes().getNamedItem("w").getNodeValue());
                    int[] area = {x, y, h, w};
                    set.setArea(area);
                    
                }
            }
            filmSets.add(set);
        }
    }

    public ArrayList<FilmSet> getSetList()
    {
        return filmSets;
    }
}
