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
package nl.rajaram.rdfschemaextractor.model.io;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import no.acando.xmltordf.Builder;
import org.xml.sax.SAXException;

/**
 * Parser for interacting with draw.io xml file
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2019-10-17
 * @version 0.1
 */
public class DrawioParser {
    
    /**
     * Parse XML string of drawio content to create rdf string
     *
     * @param drawioXMLFile Drawio xml content
     * @return rdf string
     * @throws java.io.FileNotFoundException Throws this exception if input file is not found
     */
    public String parse(@Nonnull InputStream drawioXML) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        Preconditions.checkNotNull(drawioXML, "Draw.io XML content must not be null.");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Builder.getAdvancedBuilderStream().
                setBaseNamespace("http://rdf.biosemantics.org/xml2rdf/", 
                        Builder.AppliesTo.bothElementsAndAttributes)
                .uuidBasedIdInsteadOfBlankNodes("http://rdf.biosemantics.org/resource/")
                .build().convertToStream(drawioXML, out);
        
        String rdfString = out.toString();
        
        return rdfString;
    }
    
}
