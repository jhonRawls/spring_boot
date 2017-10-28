package cn.ibadi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.ibaid.exception.SystemException;



/**
 * xml读取
 */
public class XmlReaders {
    private static Logger logger = LoggerFactory.getLogger(XmlReaders.class);
    private static DocumentBuilder builder;
    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch(Exception e) {
            throw new SystemException("init xml failed");
        }
    }

    private Document document;

    private String root;

    private XPath xpath;

    private XmlReaders() {}

    public static XmlReaders create(String xml) {
        if (StringUtils.isEmpty(xml)) {
            throw new IllegalArgumentException("xml can't be empty.");
        }
        //xml = xml.replaceAll("(\\r|\\n)", "");
        return create(new ByteArrayInputStream(xml.getBytes()));
    }

    public static synchronized XmlReaders create(InputStream inputStream) {
        XmlReaders readers = new XmlReaders();
        try {
            readers.document = builder.parse(inputStream);
            readers.root = readers.document.getFirstChild().getNodeName();
            readers.xpath = XPathFactory.newInstance().newXPath();
        } catch(Exception e) {
            throw new SystemException("Xmls create fail", e);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch(IOException e) {
                logger.error(null, e);
            }
        }
        return readers;
    }

    public String getRoot() {
        return this.root;
    }

    public String evaluate(String xpath) {
        try {
            return this.xpath.evaluate(xpath, document);
        } catch(XPathExpressionException e) {
            logger.error(null, e);
            return null;
        }
    }

    public Node getNode(String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() <= 0) {
            return null;
        }
        return nodes.item(0);
    }

    public NodeList getNodes(String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() <= 0) {
            return null;
        }
        return nodes;
    }

    /**
     * 获取某个节点的文本内容，若有多个该节点，只会返回第一个
     * @param tagName 标签名
     * @return 文本内容，或NULL
     */
    public String getNodeText(String tagName) {
        Node node = getNode(tagName);
        return node == null ? null : node.getTextContent();
    }

    /**
     * 获取某个节点的Integer，若有多个该节点，只会返回第一个
     * @param tagName 标签名
     * @return Integer值，或NULL
     */
    public Integer getNodeInt(String tagName) {
        String nodeContent = getNodeText(tagName);
        return nodeContent == null ? null : Integer.valueOf(nodeContent);
    }

    /**
     * 获取某个节点的Long值，若有多个该节点，只会返回第一个
     * @param tagName 标签名
     * @return Long值，或NULL
     */
    public Long getNodeLong(String tagName) {
        String nodeContent = getNodeText(tagName);
        return nodeContent == null ? null : Long.valueOf(nodeContent);
    }

    /**
     * 获取某个节点的Float，若有多个该节点，只会返回第一个
     * @param tagName 标签名
     * @return Float值，或NULL
     */
    public Float getNodeFloat(String tagName) {
        String nodeContent = getNodeText(tagName);
        return nodeContent == null ? null : Float.valueOf(nodeContent);
    }

    public static void main(String[] args) {
        String xml =
            "<alipay><is_success>T</is_success><request><param name=\"trade_no\">2015031800001000970051218861</param><param name=\"_input_charset\">UTF-8</param><param name=\"service\">single_trade_query</param><param name=\"partner\">2088411293539364</param><param name=\"out_trade_no\">0515123083831256</param></request><response><trade><body>12308</body><buyer_email>1034792318@qq.com</buyer_email><buyer_id>2088212560609971</buyer_id><discount>0.00</discount><flag_trade_locked>0</flag_trade_locked><gmt_create>2015-03-18 15:22:26</gmt_create><gmt_last_modified_time>2015-03-18 15:43:11</gmt_last_modified_time><gmt_payment>2015-03-18 15:22:36</gmt_payment><gmt_refund>2015-03-18 15:43:11</gmt_refund><is_total_fee_adjust>F</is_total_fee_adjust><operator_role>B</operator_role><out_trade_no>20150318151412944</out_trade_no><payment_type>8</payment_type><price>0.10</price><quantity>1</quantity><refund_fee>0.03</refund_fee><refund_flow_type>1</refund_flow_type><refund_id>97622780097</refund_id><refund_status>REFUND_SUCCESS</refund_status><seller_email>piaokuan03@12308.com</seller_email><seller_id>2088411293539364</seller_id><subject>12308全国汽车票</subject><to_buyer_fee>0.03</to_buyer_fee><to_seller_fee>0.10</to_seller_fee><total_fee>0.10</total_fee><trade_no>2015031800001000970051218861</trade_no><trade_status>TRADE_SUCCESS</trade_status><use_coupon>F</use_coupon></trade></response><sign>39350dc1dd1a85815bfc2f153ae436e1</sign><sign_type>MD5</sign_type></alipay>";
        XmlReaders reader = XmlReaders.create(xml);
        System.out.println(reader.evaluate("//alipay/request/param/@name"));
        System.out.println(reader.evaluate("//alipay/request/param[@name='trade_no']"));
        System.out.println(reader.evaluate("//alipay/response/trade/seller_email"));
    }
}
