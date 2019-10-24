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
package nl.rajaram.rdfschemaextractor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import nl.rajaram.rdfschemaextractor.api.service.DrawIoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains references to the example files which are used in the Junit tests.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2019-10-24
 * @version 0.1
 */
public class ExampleFilesUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilesUtils.class);
    
    public static final String PROPER_DIAGRAM = "ProperDrawIoDiagram.xml";
    public static final String WRONG_DIAGRAM = "DrawIoDiagramWithWrongSymbolSet.xml";
    public static final String EMPTY_FILE = "empty.xml";
    
    /**
     * Method to read the content of a file
     *
     * @param fileName File name
     * @return File content as a InputStream
     */
    public static InputStream getFileContentAsInputStream(String fileName) {
        InputStream inputStream = null;
        try {           
            URL filePath = ExampleFilesUtils.class.getResource(fileName);
            File file = new File(filePath.getPath());
            inputStream = new FileInputStream(file);
        } catch (IOException ex) {
            LOGGER.error("Error getting turle file", ex);
        }
        return inputStream;
    }
    
}
