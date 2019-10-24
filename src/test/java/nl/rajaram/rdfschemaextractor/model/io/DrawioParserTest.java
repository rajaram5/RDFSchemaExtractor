/**
 * The MIT License
 * Copyright © 2019 Rajaram Kaliyaperumal
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

import java.io.InputStream;
import java.util.List;
import nl.rajaram.rdfschemaextractor.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.Statement;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rajaram
 */
public class DrawioParserTest {
    
    
    DrawioParser instance = new DrawioParser();
    
    /**
     * Test of parseToRDF method, for null inputstream.
     */
    @Test(expected = NullPointerException.class)
    public void testNullInputStream() throws Exception {
         instance.parseToRDF(null);
    }
    
    /**
     * Test of parseToRDF method, for proper empty input file.
     */
    @Test(expected = IllegalStateException.class)
    public void testEmptyInputFile() throws Exception {
        InputStream drawioXML = ExampleFilesUtils.getFileContentAsInputStream(
                ExampleFilesUtils.EMPTY_FILE);        
        instance.parseToRDF(drawioXML);
    }
    
    /**
     * Test of parseToRDF method, for proper input file.
     */
    @Test
    public void testParseToRDF() throws Exception {
        InputStream drawioXML = ExampleFilesUtils.getFileContentAsInputStream(
                ExampleFilesUtils.PROPER_DIAGRAM);        
        List<Statement> result = instance.parseToRDF(drawioXML);
        assertTrue(result.size() > 0);
    }
    
}
