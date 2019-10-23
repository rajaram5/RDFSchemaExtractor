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
 * To change this template uploadFileToAgraphWorkFlow, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rajaram.drawingextractor.api.service;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import nl.rajaram.drawingextractor.api.storage.AGraphManager;
import nl.rajaram.drawingextractor.api.utils.CommandLineUtils;
import nl.rajaram.drawingextractor.api.utils.OWL2Utils;
import nl.rajaram.drawingextractor.model.drawio.RDFInstance;
import nl.rajaram.drawingextractor.model.drawio.Property;
import nl.rajaram.drawingextractor.model.io.DrawioParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    private static final Logger LOGGER = LogManager.getLogger(DrawIoService.class);

    @Autowired
    private TavernaWorkflowService tavernaWorkflowService;

    @Autowired
    private AGraphManager aGraphManager;

    @Value("${outputDir:/tmp/drawingExtractor}")
    private String outputDir;

    @Value("${rmlMappings.baseUrl:http://rdf.biosemantics.org/resource/rml/}")
    private String rmlBaseUrl;

    private static final String INPUT_FILENAME = "inputFile.xml";
    private static final String SEMI_RDF_FILENAME = "semiInput.rdf";
    private static final String APPLI_ONTO_FILENAME = "applicationOnto.ttl";
    private static final String RML_MAPPINGS_FILENAME = "rmlMappings.ttl";
    private static final String OPRF_PROV_FILENAME = "oprefProv.json";

    private Repository repository = new SailRepository(new MemoryStore());

    private final String GET_INSTANCES_QUERY = "get_instances.rq";
    
    private final String GET_INSTANCE_TYPE_QUERY = "get_instance_type.rq";
    
    private final String GET_ALL_PROPERTIES_OF_INSTANCE_QUERY = "get_all_properties_of_instance.rq";
    
    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    public String getApplicationOntology(String subFolderName, MultipartFile inputfile) throws IOException, FileNotFoundException, ParserConfigurationException, SAXException {

        DrawioParser parser = new DrawioParser();
        String rdfStr = parser.parse(inputfile.getInputStream());

        Repository db = new SailRepository(new MemoryStore());
        db.initialize();
        List<RDFInstance> instances = new ArrayList();

        // Open a connection to the database
        try (RepositoryConnection conn = db.getConnection()) {
            try (InputStream input
                    = new ByteArrayInputStream(rdfStr.getBytes());) {
                // add the RDF data from the inputstream directly to our database
                conn.add(input, "", RDFFormat.TURTLE);
            }
            

            for (String s : getInstances(db)) {

                System.out.println("Instance id : " + s);
                
                instances.add(getInstance(s, db));
            }
            
            System.out.println(instances.size());

        } finally {
            // Before our program exits, make sure the database is properly shut down.
            db.shutDown();
        }

        return OWL2Utils.getString(instances, RDFFormat.TURTLE);
    }
    
    private RDFInstance getInstance(String instanceId, Repository db) throws IOException {
    	
    	RDFInstance ins = null;
    	
        try (RepositoryConnection conn = db.getConnection()) {
            
            URL fileURL = DrawIoService.class.getResource(GET_ALL_PROPERTIES_OF_INSTANCE_QUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);            
            queryString = queryString.replace("INPUT_ID", instanceId); 
            
            TupleQuery query = conn.prepareTupleQuery(queryString);

            // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
            try (TupleQueryResult result = query.evaluate()) {
                // we just iterate over all solutions in the result...
            	ins = new RDFInstance();
            	List<Property> properties = new ArrayList();
                while (result.hasNext()) {
                    BindingSet solution = result.next();
                    String iriStr = solution.getValue("predicateIRI").stringValue();
                    System.out.println("?predicateIRI = " + iriStr);
                    Property p = new Property();
                    p.setIri(VALUEFACTORY.createIRI(iriStr));
                    
                    String rangeIri = solution.getValue("rangeIRI").stringValue();
                	
                	if(rangeIri != null) {
                    	p.setRangeIri(VALUEFACTORY.createIRI(rangeIri));
                	}
                    
                    if(solution.getValue("rangeStyleAttribute").stringValue().contains("rounded")) {                    	
                    	p.setType(VALUEFACTORY.createIRI("http://www.w3.org/2002/07/owl#DatatypeProperty"));
                    	
                    }
                    else {
                    	p.setType(VALUEFACTORY.createIRI("http://www.w3.org/2002/07/owl#ObjectProperty"));              	
                    	
                    }
                    
                    if(solution.getValue("rangeStyleAttribute").stringValue().contains("rhombus")) {                    	
                    	String rangeid = solution.getValue("rangeId").stringValue();
                    	
                    	IRI rangeType = getInstanceType(rangeid, db);
                    	
                    	if (rangeType != null) {
                    		p.setRangeIri(rangeType);
                    	}
                    	else {
                    		p.setRangeIri(VALUEFACTORY.createIRI("http://www.w3.org/2000/01/rdf-schema#Resource"));
                    	}
                    }
                    
                    if(solution.getValue("arrowStyleAttribute").stringValue().contains("dashed")) {    
                    	
                    	p.setIsOptional(true);
                    
                    }
                    
                    
                    properties.add(p);
                }
                
                ins.setProperties(properties);
            }
        }
        
        if (ins != null) {
        	ins.setType(getInstanceType(instanceId, db));
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

            // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
            try (TupleQueryResult result = query.evaluate()) {
                // we just iterate over all solutions in the result...
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

    private List<String> getInstances(Repository db) throws IOException {

        List<String> instances = new ArrayList();

        try (RepositoryConnection conn = db.getConnection()) {
            
            URL fileURL = DrawIoService.class.getResource(GET_INSTANCES_QUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);
            TupleQuery query = conn.prepareTupleQuery(queryString);

            // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
            try (TupleQueryResult result = query.evaluate()) {
                // we just iterate over all solutions in the result...
                while (result.hasNext()) {
                    BindingSet solution = result.next();
                    String instance = solution.getValue("instanceId").stringValue();
                    System.out.println("?instanceId = " + instance);
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    public String getRMLMappings(String subFolderName, MultipartFile inputfile, String rmlBaseUri)
            throws IOException {

        String tempFolder = createRMLMappings(subFolderName, inputfile, rmlBaseUri);
        String content = Files.toString(new File(tempFolder + "/" + RML_MAPPINGS_FILENAME),
                Charsets.UTF_8);

        MoreFiles.deleteRecursively(Paths.get(tempFolder));
        return content;
    }

    public String getOpenRefineJSON(String subFolderName, MultipartFile inputfile)
            throws IOException {

        String tempFolder = createOpenRefineProv(subFolderName, inputfile);
        String content = Files.toString(new File(tempFolder + "/" + OPRF_PROV_FILENAME),
                Charsets.UTF_8);

        MoreFiles.deleteRecursively(Paths.get(tempFolder));
        return content;

    }

    private String createOpenRefineProv(String subFolderName, MultipartFile inputfile)
            throws IOException {
        String tempFolder = createRMLMappings(subFolderName, inputfile, null);

        Resource resource = new ClassPathResource("rml-2-op-json-prov.t2flow");
        File workflow = resource.getFile();

        String rmlFile = tempFolder + "/" + RML_MAPPINGS_FILENAME;
        aGraphManager.uploadFileToAgraph(tempFolder, rmlFile);

        Map<String, String> wfInput = new HashMap();
        wfInput.put("outputFile", OPRF_PROV_FILENAME);
        wfInput.put("repoName", aGraphManager.getRepositoryName());
        wfInput.put("endpoint", aGraphManager.getEndpointUrl());

        String wfCommand = tavernaWorkflowService.generateShellCommand(workflow.getAbsolutePath(),
                wfInput);
        System.out.println(wfCommand);

        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, wfCommand);
        CommandLineUtils.runShellCommands(commands);
        aGraphManager.deleteAgraphRepository();

        return tempFolder;
    }

    private String createRMLMappings(String subFolderName, MultipartFile inputfile,
            String rmlBaseUri)
            throws IOException {

        String tempFolder = createApplicationOntology(subFolderName, inputfile);

        Resource resource = new ClassPathResource("application-ontology-2-rml.t2flow");
        File workflow = resource.getFile();

        String appicationOntology = tempFolder + "/" + APPLI_ONTO_FILENAME;
        aGraphManager.uploadFileToAgraph(tempFolder, appicationOntology);

        String baseUri = rmlBaseUrl;
        if (rmlBaseUri != null) {
            baseUri = rmlBaseUri;
        }

        Map<String, String> wfInput = new HashMap();
        wfInput.put("baseUrl", baseUri);
        wfInput.put("outputFile", RML_MAPPINGS_FILENAME);
        wfInput.put("repoName", aGraphManager.getRepositoryName());
        wfInput.put("endpoint", aGraphManager.getEndpointUrl());

        String wfCommand = tavernaWorkflowService.generateShellCommand(workflow.getAbsolutePath(),
                 wfInput);
        System.out.println(wfCommand);

        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, wfCommand);
        CommandLineUtils.runShellCommands(commands);
        aGraphManager.deleteAgraphRepository();

        return tempFolder;

    }

    private String createApplicationOntology(String subFolderName, MultipartFile inputfile) throws IOException {

        Preconditions.checkNotNull(subFolderName, "subFolder name must not be null.");
        Preconditions.checkArgument(!subFolderName.isEmpty(), "subFolder name can't be EMPTY");
        Preconditions.checkNotNull(inputfile, "inputfile must not be null.");

        // Copy uploadFileToAgraphWorkFlow to the temp folder
        String tempFolder = outputDir + "/" + subFolderName;
        String inFilePath = tempFolder + "/" + INPUT_FILENAME;
        Files.createParentDirs(new File(inFilePath));
        inputfile.transferTo(Paths.get(inFilePath).toFile());

        convertxmToSemRdf(tempFolder);

        String semiRdf = tempFolder + "/" + SEMI_RDF_FILENAME;
        aGraphManager.uploadFileToAgraph(tempFolder, semiRdf);

        runApplicationOntoProcess(tempFolder);
        return tempFolder;
    }

    private void convertxmToSemRdf(String tempFolder) throws IOException {

        Resource resource = new ClassPathResource("draw-io-2-semirdf.t2flow");
        File semiRdfWorkflowFile = resource.getFile();

        Map<String, String> wfInput = new HashMap();
        wfInput.put("drawIoFile", INPUT_FILENAME);
        wfInput.put("outputFile", SEMI_RDF_FILENAME);

        String wfCommand = tavernaWorkflowService.generateShellCommand(
                semiRdfWorkflowFile.getAbsolutePath(), wfInput);
        System.out.println(wfCommand);

        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, wfCommand);
        CommandLineUtils.runShellCommands(commands);
    }

    private void runApplicationOntoProcess(String tempFolder) throws IOException {

        Resource resource = new ClassPathResource("draw-io-2-application-ontology.t2flow");
        File semiRdfWorkflowFile = resource.getFile();

        Map<String, String> wfInput = new HashMap();
        wfInput.put("outputFile", APPLI_ONTO_FILENAME);
        wfInput.put("repoName", aGraphManager.getRepositoryName());
        wfInput.put("endpoint", aGraphManager.getEndpointUrl());

        String wfCommand = tavernaWorkflowService.generateShellCommand(
                semiRdfWorkflowFile.getAbsolutePath(), wfInput);
        System.out.println(wfCommand);

        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, wfCommand);
        CommandLineUtils.runShellCommands(commands);
        aGraphManager.deleteAgraphRepository();

    }

}
