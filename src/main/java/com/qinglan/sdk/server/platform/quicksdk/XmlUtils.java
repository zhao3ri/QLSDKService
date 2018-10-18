package com.qinglan.sdk.server.platform.quicksdk;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by engine on 16/8/15.
 */
public class XmlUtils {
    public static QuickXmlBean parserXML(String strXML){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader sr = new StringReader(strXML);
            InputSource is = new InputSource(sr);
            Document doc = builder.parse(is);
            Element rootElement = doc.getDocumentElement();
            QuickXmlBean bean = null;
            NodeList message= rootElement.getElementsByTagName("message");
            if(message.getLength()>0){
               bean=new QuickXmlBean();
                message=message.item(0).getChildNodes();
            }else {
                System.out.println("Root is null");
            }
            for (int i = 0; i < message.getLength(); i++) {
                Node type = message.item(i);
                System.out.println(type.getNodeName()+":"+type.getTextContent());
                if(type.getNodeName().equals("is_test")){
                    bean.setIs_test(type.getTextContent().trim());
                }else if(type.getNodeName().equals("channel")){
                    bean.setChannel(type.getTextContent().trim());
                }else if(type.getNodeName().equals("channel_uid")){
                    bean.setChannel_uid(type.getTextContent().trim());
                }else if(type.getNodeName().equals("game_order")){
                    bean.setGame_order(type.getTextContent().trim());
                }else if(type.getNodeName().equals("order_no")){
                   bean.setOrder_no(type.getTextContent().trim());
                }else if(type.getNodeName().equals("pay_time")){
                   bean.setPay_time(type.getTextContent().trim());
                }else if(type.getNodeName().equals("amount")){
                    bean.setAmount(type.getTextContent().trim());
                }else if(type.getNodeName().equals("status")){
                    bean.setStatus(type.getTextContent().trim());
                }else if(type.getNodeName().equals("extras_params")){
                    bean.setExtras_params(type.getTextContent());
                }
            }
            return bean ;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
