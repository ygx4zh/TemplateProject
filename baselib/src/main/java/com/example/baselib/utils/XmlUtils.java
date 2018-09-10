package com.example.baselib.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;

/**
 * Xml工具类
 *
 * @author YGX
 */

public class XmlUtils {

    /**
     * return special tag value,
     * <example>
     *     <?xml encoding="utf-8" version="1.0"?>
     *     <tag1>
     *         <tag2>value2</tag2>
     *     </tag1>
     *
     *     tag: tag2; return value: value2;
     * </example>
     * @param xmlSrc xml string
     * @param tag name of tag
     * @return value if exist, otherwise return null;
     */
    public static String getSpecialTagValue(String xmlSrc, String tag){
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            ByteArrayInputStream is = new ByteArrayInputStream(xmlSrc.getBytes("UTF-8"));
            parser.setInput(is,"UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equals(tag)) {
                            return parser.nextText();
                        }
                        break;
                }

                parser.next();
                eventType = parser.getEventType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
