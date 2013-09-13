package com.compomics.natter_remake.controllers;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author Davy
 */
public class InvalidXMLCharacterFilterReader extends FilterReader {

    /**
     * 
     * @param in 
     */
    public InvalidXMLCharacterFilterReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        char[] buf = new char[1];
        int result = in.read(buf, 0, 1);
        if (result == -1) {
            return -1;
        } else if (isBadXMLChar(buf[0])) {
            return (int) '-';
        } else {
            return (int) buf[0];
        }
    }

    @Override
    public int read(char[] buf, int from, int len) throws IOException {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        count = in.read(buf, from, len);
        if(count != -1) {

            for (int i = 0; i < buf.length; i++) {
                if (!isBadXMLChar(buf[i])) {
                    builder.append(buf[i]);
                }
            }
            buf = builder.toString().toCharArray();
        }
        return count;
    }

    private boolean isBadXMLChar(char c) {
        boolean badXML = true;
        if ((c == 0x9)
                || (c == 0xA)
                || (c == 0xD)
                || ((c >= 0x20) && (c <= 0xD7FF))
                || ((c >= 0xE000) && (c <= 0xFFFD))
                || ((c >= 0x10000) && (c <= 0x10FFFF))) {
            badXML = false;
        }
        return badXML;
    }
}
