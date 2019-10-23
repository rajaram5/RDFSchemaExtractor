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
package nl.rajaram.drawingextractor.api.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import nl.rajaram.drawingextractor.api.service.ApplicationOntologyService;
import nl.rajaram.drawingextractor.api.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handle API calls related to Application ontology
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-21
 * @version 0.1
 */
@RestController
@RequestMapping("/applicationOntology")
public class ApplicationOntologyController {
    
    private static final Logger LOGGER = LogManager.getLogger(ApplicationOntologyController.class);
    
    @Autowired
    private ApplicationOntologyService appOntoService;    
    
    @RequestMapping(value = "/generateRML", method = RequestMethod.POST,
            produces = {"application/n-triples", "text/turtle",
                "application/rdf+xml"})
    @ResponseStatus(HttpStatus.OK)
    public String generateRMLMappings(final HttpServletRequest request, 
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "rmlBaseUri", required = false) String rmlBaseUri)
            throws IOException, Exception {
        String sessionDir = request.getSession().getId();
        LOGGER.info("Request to generate RML mappings");
        String content = appOntoService.getRMLMappings(sessionDir, file, rmlBaseUri);
        
        content = RDFUtils.getString(content, RDFUtils.getRDFFormat(request.getHeader("Accept")));
        return content;
    }
    
    
    @RequestMapping(value = "/generateOpenRefineJSON", method = RequestMethod.POST,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public String generateOpenRefineJSON(final HttpServletRequest request, 
            @RequestParam("file") MultipartFile file) throws IOException {
        String sessionDir = request.getSession().getId();
        LOGGER.info("Request to generate OpenRefine JSON prov blob");
        return appOntoService.getOpenRefineJSON(sessionDir, file);
    }
    
}
