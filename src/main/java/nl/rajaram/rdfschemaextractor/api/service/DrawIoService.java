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
 * To change this template uploadFileToAgraphWorkFlow, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rajaram.rdfschemaextractor.api.service;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import nl.rajaram.rdfschemaextractor.api.utils.OWL2Utils;
import nl.rajaram.rdfschemaextractor.model.drawio.RDFInstance;
import nl.rajaram.rdfschemaextractor.model.drawio.Property;
import nl.rajaram.rdfschemaextractor.model.io.DrawioParser;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

/**
 * Service layer to handle draw.io drawing related operations
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-21
 * @version 0.1
 */
@Service
public class DrawIoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrawIoService.class);

    private final String GET_INSTANCES_QUERY = "get_instances.rq";

    private final String GET_INSTANCE_TYPE_QUERY = "get_instance_type.rq";

    private final String GET_ALL_PROPERTIES_OF_INSTANCE_QUERY = "get_all_properties_of_instance.rq";

    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    public List<RDFInstance> getApplicationOntology(MultipartFile inputfile) throws SAXException,
            IllegalArgumentException, ParserConfigurationException, IOException {
        
        if (inputfile.isEmpty()) {
            throw new IllegalArgumentException("Input XML file is empty. Please check input file.");
        }

        DrawioParser parser = new DrawioParser();
        List<Statement> stmt = parser.parseToRDF(inputfile.getInputStream());

        Repository db = new SailRepository(new MemoryStore());
        db.init();
        List<RDFInstance> instances = new ArrayList();

        try (RepositoryConnection conn = db.getConnection()) {

            conn.add(stmt);
            List<String> instanceIds = getInstanceIds(db);
            LOGGER.info("Number of instance  = " + instanceIds.size());         
            
            if (instanceIds.isEmpty()) {            
                throw new IllegalArgumentException("Invalid input XML file. "
                        + "No instances found in the input xml file. Did you use rombus shape to "
                        + "denote instances in the input file?");        
            }

            for (String id : instanceIds) {
                RDFInstance ins = getRDFInstance(id, db);

                if (!ins.getProperties().isEmpty()) {
                    instances.add(ins);                
                }
                
            }
            LOGGER.info("Number of instance with properties = " + instances.size());
            
            if (instances.isEmpty()) {            
                throw new IllegalArgumentException("Invalid input XML file. " +  instanceIds.size() 
                        + " instances found but no predicates associated with these instances "
                                + "are found. Did you follow rdf schema drawing guidelines?");        
            }
        } finally {
            db.shutDown();
        }

        return instances;
    }
    
    
    public List<RDFInstance> validDrawIODrawing(MultipartFile inputfile) throws SAXException,
            IllegalArgumentException, ParserConfigurationException, IOException {
        
        if (inputfile.isEmpty()) {
            throw new IllegalArgumentException("Input XML file is empty. Please check input file.");
        }

        DrawioParser parser = new DrawioParser();
        List<Statement> stmt = parser.parseToRDF(inputfile.getInputStream());

        Repository db = new SailRepository(new MemoryStore());
        db.init();
        List<RDFInstance> instances = new ArrayList();

        try (RepositoryConnection conn = db.getConnection()) {

            conn.add(stmt);
            List<String> instanceIds = getInstanceIds(db);
            LOGGER.info("Number of instance  = " + instanceIds.size());         
            
            if (instanceIds.isEmpty()) {            
                return instances;      
            }

            for (String id : instanceIds) {
                RDFInstance ins = getRDFInstance(id, db);
                    instances.add(ins);                
            }
            LOGGER.info("Number of instance with properties = " + instances.size());
        } finally {
            db.shutDown();
        }

        return instances;
    }

    private RDFInstance getRDFInstance(String instanceId, Repository db) throws IOException {

        RDFInstance ins = new RDFInstance();

        try (RepositoryConnection conn = db.getConnection()) {

            URL fileURL = DrawIoService.class.getResource(GET_ALL_PROPERTIES_OF_INSTANCE_QUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);
            queryString = queryString.replace("INPUT_ID", instanceId);

            TupleQuery query = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = query.evaluate()) {
                
                List<Property> properties = new ArrayList();

                while (result.hasNext()) {
                    BindingSet solution = result.next();
                    String iriStr = solution.getValue("predicateIRI").stringValue();
                    LOGGER.info(" predicateI RI = " + iriStr);
                    // Create new property object
                    Property p = new Property();
                    p.setIri(VALUEFACTORY.createIRI(iriStr));
                    
                    // Set predicate range IRI
                    String rangeIri = solution.getValue("rangeIRI").stringValue();
                    if (rangeIri != null) {
                        p.setRangeIri(VALUEFACTORY.createIRI(rangeIri));
                    }
                    // Check if predicate is a data property
                    if (solution.getValue("rangeStyleAttribute").stringValue().contains("rounded")){
                        p.setType(OWL.DATATYPEPROPERTY);
                    } else {  // else set predicate to object property
                        p.setType(OWL.OBJECTPROPERTY);
                    }
                    
                    // If predicate range is a rdf instance then, set instance type as range 
                    if (solution.getValue("rangeStyleAttribute").stringValue().contains("rhombus")){
                        
                        String rangeid = solution.getValue("rangeId").stringValue();
                        IRI rangeType = getInstanceType(rangeid, db);

                        if (rangeType != null) {
                            p.setRangeIri(rangeType);
                        } else { // if no instance type found then set rdfs:Resource as a range
                            p.setRangeIri(RDFS.RESOURCE);
                        }
                    }
                    // If arrow is a dashed line then the property is optional
                    if (solution.getValue("arrowStyleAttribute").stringValue().contains("dashed")){
                        p.setIsOptional(true);
                    }

                    properties.add(p);
                }
                ins.setProperties(properties);
            }
        }

        IRI instanceType = getInstanceType(instanceId, db);
        if (instanceType != null) {
            ins.setType(instanceType);
        }

        return ins;
    }

    private IRI getInstanceType(String instanceId, Repository db) throws IOException {

        IRI type = null;

        try (RepositoryConnection conn = db.getConnection()) {

            URL fileURL = DrawIoService.class.getResource(GET_INSTANCE_TYPE_QUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);

            queryString = queryString.replace("INPUT_ID", instanceId);

            TupleQuery query = conn.prepareTupleQuery(queryString);

            try (TupleQueryResult result = query.evaluate()) {
                while (result.hasNext()) {
                    BindingSet solution = result.next();
                    String typeStr = solution.getValue("type").stringValue();
                    System.out.println("?type = " + typeStr);
                    type = VALUEFACTORY.createIRI(typeStr);
                }
            }
        }
        return type;
    }

    private List<String> getInstanceIds(Repository db) throws IOException {

        List<String> instances = new ArrayList();

        try (RepositoryConnection conn = db.getConnection()) {

            URL fileURL = DrawIoService.class.getResource(GET_INSTANCES_QUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);
            TupleQuery query = conn.prepareTupleQuery(queryString);

            try (TupleQueryResult result = query.evaluate()) {
                while (result.hasNext()) {
                    BindingSet solution = result.next();
                    String instance = solution.getValue("instanceId").stringValue();
                    LOGGER.info("Var (?instanceId) value = " + instance);
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    public String getRMLMappings(String subFolderName, MultipartFile inputfile, String rmlBaseUri)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getOpenRefineJSON(String subFolderName, MultipartFile inputfile)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
