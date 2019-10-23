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
package nl.rajaram.rdfschemaextractor.api.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;

/**
 * Utils class to handle rdf related related operation
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-23
 * @version 0.1
 */
public class RDFUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(RDFUtils.class);
    public static final RDFFormat FILE_FORMAT = RDFFormat.TURTLE;   
    
    public static String getString(@Nonnull String content, @Nonnull RDFFormat format)
            throws Exception {
        Preconditions.checkNotNull(content, "content object must not be null.");
        Preconditions.checkNotNull(format, "RDF format must not be null.");
        StringWriter sw = new StringWriter();
        RDFWriter writer = Rio.createWriter(format, sw);
        writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        List<Statement> statement = getContentAsStatements(content, "");
        try {
            propagateToHandler(statement, writer);
        } catch (RepositoryException | RDFHandlerException ex) {
            LOGGER.error("Error reading RDF statements");
            String errMsg = ex.getMessage();
            throw new Exception(errMsg);
        }
        return sw.toString();
    }
    
    
    private static void propagateToHandler(List<Statement> statements, RDFHandler handler)
            throws RDFHandlerException, RepositoryException {
        handler.startRDF();
        handler.handleNamespace(RDF.PREFIX, RDF.NAMESPACE);
        handler.handleNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
        handler.handleNamespace(DCAT.PREFIX, DCAT.NAMESPACE);
        handler.handleNamespace(XMLSchema.PREFIX, XMLSchema.NAMESPACE);
        handler.handleNamespace(OWL.PREFIX, OWL.NAMESPACE);
        handler.handleNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
        handler.handleNamespace("r2rml", "http://www.w3.org/ns/r2rml#");
        handler.handleNamespace("rml", "http://semweb.mmlab.be/ns/rml#");
        
        for (Statement st : statements) {
            handler.handleStatement(st);
        }
        handler.endRDF();
    }
    
    
    /**
     * Method to read the content of a turtle file
     * 
     * @param content Turtle string
     * @param baseURI
     * @return File content as a string
     */
    public static List<Statement> getContentAsStatements(String content, String baseURI)  {        
        List<Statement> statements = null;  
        try {
            StringReader reader = new StringReader(content);
            Model model;
            model = Rio.parse(reader, baseURI, FILE_FORMAT);
            Iterator<Statement> it = model.iterator();
            statements =  Lists.newArrayList(it);
        } catch (IOException | RDFParseException | 
                UnsupportedRDFormatException ex) {
            LOGGER.error("Error getting turle file {}", ex);          
        }         
        return statements;
    }
    
    public static RDFFormat getRDFFormat(String format) {
        
        if(format == null || format.isEmpty()) {
            return RDFFormat.TURTLE;
        } else if(format.equalsIgnoreCase("application/ld+json")) {
            return RDFFormat.RDFJSON;
        } else if(format.equalsIgnoreCase("application/rdf+xml")) {
            return RDFFormat.RDFXML;
        } else if(format.equalsIgnoreCase("application/n-triples")) {
            return RDFFormat.NTRIPLES;
        } else {
            return RDFFormat.TURTLE;
        }
    }

    
}
