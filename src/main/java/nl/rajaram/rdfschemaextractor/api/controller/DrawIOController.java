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
package nl.rajaram.rdfschemaextractor.api.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import nl.rajaram.rdfschemaextractor.api.service.DrawIoService;
import nl.rajaram.rdfschemaextractor.api.utils.OWL2Utils;
import nl.rajaram.rdfschemaextractor.api.utils.SHACLUtils;
import nl.rajaram.rdfschemaextractor.model.drawio.RDFInstance;
import org.eclipse.rdf4j.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

/**
 * Handle API calls related to DrawIO
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-21
 * @version 0.1
 */
@RestController
@RequestMapping("/drawIo")
public class DrawIOController {
    
    private static final Logger logger = LoggerFactory.getLogger(DrawIOController.class);
    
    @Autowired
    private DrawIoService drawIoService;   
    
    
    @RequestMapping(value = "/validateDrawIoDrawing", method = RequestMethod.POST,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<RDFInstance> validateDrawIoDrawing(final HttpServletRequest request, 
            @RequestParam("file") MultipartFile file) throws IOException, 
            ParserConfigurationException, SAXException, IllegalArgumentException {
        
        logger.info("Request to validate DrawIo drawing");
        List<RDFInstance> instances = drawIoService.validDrawIODrawing(file);        
        return instances;
    }
    
    @RequestMapping(value = "/convertToApplicationOntology", method = RequestMethod.POST,
            produces = {"application/n-triples", "text/turtle", "application/rdf+xml"})
    @ResponseStatus(HttpStatus.OK)
    public Model convertToApplicationOntology(final HttpServletRequest request, 
            @RequestParam("file") MultipartFile file) throws IOException, 
            ParserConfigurationException, SAXException, IllegalArgumentException {
        
        logger.info("Request to generate application ontology");
        List<RDFInstance> instances = drawIoService.getApplicationOntology(file);
        Model model = OWL2Utils.getRDFModel(instances);
        return model;
    }
    
    @RequestMapping(value = "/convertToSHACL", method = RequestMethod.POST,
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.OK)
    public Model convertToSHACL(final HttpServletRequest request, 
            @RequestParam("file") MultipartFile file) throws IOException, 
            ParserConfigurationException, SAXException, IllegalArgumentException {
        
        logger.info("Request to generate application ontology");
        List<RDFInstance> instances = drawIoService.getApplicationOntology(file);
        Model model = SHACLUtils.getRDFModel(instances);
        return model;
    }
    
}
