package com.camper.SmartDesktop;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.camper.SmartDesktop.Main.DIRPATH;

public class SavingElements
{
    private static <T> void addToXml(String keyForSaving) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException
    {
        var factory = DocumentBuilderFactory.newInstance();

        if (keyForSaving.equals("Note"))
        {
            //var factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
            final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);


            var builder = factory.newDocumentBuilder();
            /*Document doc = builder.parse("src/XML/Validation/config-schema.xml");

            var xpfactory = XPathFactory.newInstance();
            var path = xpfactory.newXPath();
            //SCHEMA
            String mail = path.evaluate("/config/contact/mail/text()",doc);
            System.out.println(mail);*/



            //var builder = factory.newDocumentBuilder();
            var doc = builder.newDocument();
            String namespace = "http://www.w3.org/2000/svg";

            var rootElement = doc.createElement("config");
            doc.appendChild(rootElement);
            rootElement.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("xsi:noNamespaceSchemaLocation","config.xsd");
            var fontElement = doc.createElement("font");
            fontElement.setAttribute("style","bold");

            var nameElement = doc.createElement("name");
            fontElement.appendChild(nameElement);
            var textNodeNameElement = doc.createTextNode("Times New Roman");
            nameElement.appendChild(textNodeNameElement);

            var sizeElement = doc.createElement("size");
            fontElement.appendChild(sizeElement);
            var textNodeSizeElement = doc.createTextNode("31");
            sizeElement.appendChild(textNodeSizeElement);
            sizeElement.setAttribute("unit","pt");

            rootElement.appendChild(fontElement);

            var t = TransformerFactory.newInstance().newTransformer();
            //t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"url:/gavnim/gavno");
            //Делать ли отступ(принимает значения "yes"/"no")
            t.setOutputProperty(OutputKeys.INDENT,"yes");
            //Принимает значение "xml", "html", "txt" или специальное строковое значение
            t.setOutputProperty(OutputKeys.METHOD,"xml");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //первый параметр откуда, второй - куда
            t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\save.xml"))));

            /*//DTD
            factory.setNamespaceAware(false);
            factory.setValidating(true);
            builder = factory.newDocumentBuilder();
            var handler = new MyHandler();
            builder.setErrorHandler(handler);
            doc = builder.parse("src/XML/Validation/config.xml");
            String fontName = path.evaluate("config/font/name/text()",doc);
            //Выводит количество дочерних элементов для элемента разметки config
            int count = Integer.parseInt (path.evaluate("count(config/font)",doc));
            System.out.println(fontName);
            System.out.println(count);
            //Если мы получаем несколько узлов, то для их обработки можно сделать следующий вызов
            var result = path.evaluateExpression("config/font", doc , XPathNodes.class);
            for (Node node : result)
            {
                var nodeChildList = node.getChildNodes();
                for (int i = 0; i<nodeChildList.getLength();i++)
                {
                    //обработать ноды
                }

            }*/
        }
    }

    public static void saveAll() throws ParserConfigurationException, TransformerException, SAXException, XPathExpressionException, IOException
    {
        addToXml("Note");
    }
}
