package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;

public class VersionDetector{

    public static String detect(Document document) {
        DocumentType documentType = document.documentType();
        assert documentType != null : "null documentType";
        String publicId = documentType.publicId();

        if(documentType.toString().equalsIgnoreCase("<!doctype html>"))
            return "HTML 5";

        if (publicId.contains("HTML 4.01")) {
            if (publicId.contains("Strict")) {
                return "HTML 4.01 Strict";
            } else if (publicId.contains("Transitional")) {
                return "HTML 4.01 Transitional";
            } else if (publicId.contains("Frameset")) {
                return "HTML 4.01 Frameset";
            }
        } else if (publicId.contains("XHTML 1.0")) {
            if (publicId.contains("Strict")) {
                return "XHTML 1.0 Strict";
            } else if (publicId.contains("Transitional")) {
                return "XHTML 1.0 Transitional";
            } else if (publicId.contains("Frameset")) {
                return "XHTML 1.0 Frameset";
            }
        } else if (publicId.contains("XHTML 1.1")) {
            return "XHTML 1.1";
        } else if (publicId.contains("HTML 3.2")) {
            return "HTML 3.2";
        } else if (publicId.contains("HTML 2.0")) {
            return "HTML 2.0";
        }
        return "Unknown";
    }
}
