/**
 * The MIT License
 * Copyright © 2018 Rajaram Kaliyaperumal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rajaram.drawingextractor.api.utils;

/**
 *
 * @author rajaram
 */
import no.acando.xmltordf.Builder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class TestCode {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        
        File file = new File("/home/rajaram/Downloads/Test.xml");        
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(file));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Builder.getAdvancedBuilderStream().
                setBaseNamespace("http://rdf.biosemantics.org/xml2rdf/", 
                        Builder.AppliesTo.bothElementsAndAttributes)
                .uuidBasedIdInsteadOfBlankNodes("http://rdf.biosemantics.org/resource/")
                .build().convertToStream(in, out);
        
        String str = out.toString();
        System.out.println(str);

    }

}