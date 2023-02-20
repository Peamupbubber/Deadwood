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

import java.util.ArrayList;

public class XMLCardParser {
    private Card card;
    private String name;
    private String img;
    private int budget;
    private int sceneNumber;
    private String desc;
    private ArrayList<Card> cards;

    private String fileName;

    public XMLCardParser(String fileName)
    {
        this.fileName = fileName;
        cards = new ArrayList<Card>();
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
            System.out.print("Exception");
        }

        Element root = doc.getDocumentElement();
        NodeList children = root.getElementsByTagName("card");
        
        for(int i = 0; i < children.getLength(); i++)
        {
            Node empNode = children.item(i);

            String name = empNode.getAttributes().getNamedItem("name").getNodeValue();
            String img = empNode.getAttributes().getNamedItem("img").getNodeValue();
            String bud = empNode.getAttributes().getNamedItem("budget").getNodeValue();
            int budget = Integer.parseInt(bud);

            NodeList childs = empNode.getChildNodes();
            Node scene = childs.item(1);
            String num = scene.getAttributes().getNamedItem("number").getNodeValue();
            int sceneNumber = Integer.parseInt(num);
            String desc = scene.getTextContent();
            Card empCard = new Card(budget, desc, name, img, sceneNumber);

            for(int j = 3; j < childs.getLength(); j+=2)
            {
                Node child = childs.item(j);
                String partName = "";
                String lvl = "";
                
                partName = child.getAttributes().getNamedItem("name").getNodeValue();
                lvl = child.getAttributes().getNamedItem("level").getNodeValue();
                
                NodeList chil = child.getChildNodes();
                
                String Sx = chil.item(1).getAttributes().getNamedItem("x").getNodeValue();
                String Sy = chil.item(1).getAttributes().getNamedItem("y").getNodeValue();
                String Sh = chil.item(1).getAttributes().getNamedItem("h").getNodeValue();
                String Sw = chil.item(1).getAttributes().getNamedItem("w").getNodeValue();
                String line = chil.item(2).getTextContent();

                int level = Integer.parseInt(lvl);
                int x = Integer.parseInt(Sx);
                int y = Integer.parseInt(Sy);
                int h = Integer.parseInt(Sh);
                int w = Integer.parseInt(Sw);
                int[] area = {x,y,h,w};
                
                Role empRole = new Role(level, true, partName, line, area);
                empRole.setCardOnSet(empCard);
                empCard.addRoleToCard(empRole);
            }
            cards.add(empCard);
        }
    }

    public ArrayList<Card> getCardList()
    {
        return cards;
    }
}
