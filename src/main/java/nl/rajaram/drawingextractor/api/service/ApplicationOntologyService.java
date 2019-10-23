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
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import nl.rajaram.drawingextractor.api.storage.AGraphManager;
import nl.rajaram.drawingextractor.api.utils.CommandLineUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service layer to handle draw.io drawing related operations
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-21
 * @version 0.1
 */
@Service
public class ApplicationOntologyService {
    
    private static final Logger LOGGER = LogManager.getLogger(ApplicationOntologyService.class);
    
    
    private final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    
    @Autowired
    private TavernaWorkflowService tavernaWorkflowService;
    
    @Autowired
    private AGraphManager aGraphManager;
    
    @Value("${outputDir:/tmp/drawingExtractor}")
    private String outputDir;
    
    @Value("${rmlMappings.baseUrl:http://rdf.biosemantics.org/resource/rml/}")
    private String rmlBaseUrl;
    
    
    private static String APPLI_ONTO_FILENAME = "applicationOnto.ttl";
    private static final String RML_MAPPINGS_FILENAME = "rmlMappings.ttl";
    private static final String OPRF_PROV_FILENAME = "oprefProv.json";
    
    public String getRMLMappings(String subFolderName, MultipartFile inputfile, String rmlBaseUri)
            throws IOException {
        
        String tempFolder =  createRMLMappings(subFolderName, inputfile, rmlBaseUri);
        String content =  Files.toString(new File(tempFolder + "/" + RML_MAPPINGS_FILENAME),
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
        String tempFolder =  createRMLMappings(subFolderName, inputfile, null);
        
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
        
        Preconditions.checkNotNull(subFolderName, "subFolder name must not be null.");
        Preconditions.checkArgument(!subFolderName.isEmpty(),"subFolder name can't be EMPTY");
        Preconditions.checkNotNull(inputfile, "inputfile must not be null.");
        
        // Copy uploadFileToAgraphWorkFlow to the temp folder
        String tempFolder = outputDir + "/" + subFolderName; 
        APPLI_ONTO_FILENAME = inputfile.getOriginalFilename();
        String appicationOntology = tempFolder + "/" + APPLI_ONTO_FILENAME;     
        Files.createParentDirs(new File(appicationOntology));       
        inputfile.transferTo(Paths.get(appicationOntology).toFile());
        
        Resource resource = new ClassPathResource("application-ontology-2-rml.t2flow");
        File workflow = resource.getFile();
        
        aGraphManager.uploadFileToAgraph(tempFolder, appicationOntology);
        
        String baseUri = rmlBaseUrl;        
        if (rmlBaseUri != null){
            baseUri = rmlBaseUri;
        }
        
        Map<String, String> wfInput = new HashMap();
        wfInput.put("baseUrl", baseUri);
        wfInput.put("outputFile", RML_MAPPINGS_FILENAME);
        wfInput.put("repoName", aGraphManager.getRepositoryName());
        wfInput.put("endpoint", aGraphManager.getEndpointUrl());
        
        String wfCommand = tavernaWorkflowService.generateShellCommand(workflow.getAbsolutePath()
                ,wfInput);
        System.out.println(wfCommand);
        
        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, wfCommand);
        CommandLineUtils.runShellCommands(commands);
        aGraphManager.deleteAgraphRepository();
        
        return tempFolder;
        
    }
    
}
