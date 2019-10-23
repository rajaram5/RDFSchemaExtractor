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
package nl.rajaram.drawingextractor.api.service;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service layer to handle taverna related operations
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-21
 * @version 0.1
 */
@Service
public class TavernaWorkflowService {
    
    private static final Logger LOGGER = LogManager.getLogger(TavernaWorkflowService.class);
    
    @Value("${taverna.baseDir:/tmp/drawingExtractor}")
    private String tavernaBaseDir;
    
    @Value("${taverna.runScriptName:executeworkflow.sh }")
    private String tavernaRunScriptName;
    
    public String generateShellCommand(String workflowName, Map<String, String> input){
        
        StringBuilder command = new StringBuilder();
        command.append("sh ");
        command.append(tavernaBaseDir + "/" + tavernaRunScriptName);
        
        for(String inputName:input.keySet()){
            command.append(" -inputvalue ");
            command.append(inputName);
            command.append(" ");
            command.append(input.get(inputName));
        }
        command.append(" ");
        command.append(workflowName);
        return command.toString();
        
    }
    
}
