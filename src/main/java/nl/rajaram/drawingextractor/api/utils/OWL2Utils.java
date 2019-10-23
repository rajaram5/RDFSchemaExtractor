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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import nl.rajaram.drawingextractor.model.drawio.Property;
import nl.rajaram.drawingextractor.model.drawio.RDFInstance;
import org.apache.logging.log4j.LogManager;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;

/**
 * Utils class to convert object to OWL RDF string
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2019-10-21
 * @version 0.1
 */
public class OWL2Utils {
    
      private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(OWL2Utils.class);
      
      private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();
    
    public static String getString(@Nonnull RDFInstance instance,
            @Nonnull RDFFormat format) {
        
        Preconditions.checkNotNull(instance, "RDFInstance object must not be null.");
        Preconditions.checkNotNull(format, "RDF format must not be null.");
        
        List<Statement> stmt = getInstanceStaements(instance);
        
        return statementsToString(stmt, format);        
    }
      
    public static String getString(@Nonnull List<RDFInstance> instances,
            @Nonnull RDFFormat format) {
        
        Preconditions.checkNotNull(instances, "RDFInstance object must not be null.");
        Preconditions.checkNotNull(format, "RDF format must not be null.");
        //Preconditions.checkState(!instances.isEmpty(), "RDFInstance object must not be empty.");
        
        List<Statement> stmt = new ArrayList();
        
        for (RDFInstance ins:instances) {
            stmt.addAll(getInstanceStaements(ins));
        }
        
        return statementsToString(stmt, format);        
    }
    
    
    private static List<Statement> getInstanceStaements(@Nonnull RDFInstance instance) {
        
        
        Model model = new LinkedHashModel();
        
        for(Property p:instance.getProperties()) {            
           model.add(p.getIri(), RDF.TYPE, p.getType());
           model.add(p.getIri(), RDFS.DOMAIN, instance.getType());
           model.add(p.getIri(), RDFS.RANGE, p.getRangeIri());   
           
           String bnode = Integer.toString(p.getIri().hashCode());
           
           int minCardinality = 1;
           
           if(p.isIsOptional()) {
               minCardinality = 0;
           }
           
           model.add(VALUEFACTORY.createBNode(bnode), RDF.TYPE, OWL.RESTRICTION); 
           model.add(VALUEFACTORY.createBNode(bnode), OWL.ONPROPERTY, p.getIri()); 
           model.add(VALUEFACTORY.createBNode(bnode), OWL.MINCARDINALITY,
                   VALUEFACTORY.createLiteral(minCardinality)); 
        }
        
        Iterator<Statement> it = model.iterator();
        List<Statement> statements = ImmutableList.copyOf(it);
        
        return statements;
        
    }
    
    
    private static String statementsToString(List<Statement> statements, RDFFormat format)
            throws RDFHandlerException, RepositoryException {

        StringWriter sw = new StringWriter();
        RDFWriter writer = Rio.createWriter(format, sw);
        writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);

        try {
            writer.startRDF();
            writer.handleNamespace(RDF.PREFIX, RDF.NAMESPACE);
            writer.handleNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
            writer.handleNamespace(DCAT.PREFIX, DCAT.NAMESPACE);
            writer.handleNamespace(XMLSchema.PREFIX, XMLSchema.NAMESPACE);
            writer.handleNamespace(OWL.PREFIX, OWL.NAMESPACE);
            writer.handleNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
            for (Statement st : statements) {
                writer.handleStatement(st);
            }
            writer.endRDF();
        } catch (RepositoryException | RDFHandlerException ex) {
            LOGGER.error("Error reading RDF statements");
            String errMsg = ex.getMessage();
        }
        return sw.toString();
    }
    
}
