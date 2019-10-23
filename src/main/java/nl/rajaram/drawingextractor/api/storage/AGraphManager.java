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
package nl.rajaram.drawingextractor.api.storage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import nl.rajaram.drawingextractor.api.utils.CommandLineUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Handle agraph related operation
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-23
 * @version 0.1
 */
@Service
public class AGraphManager {
    
    private static final Logger LOGGER = LogManager.getLogger(AGraphManager.class);
    
    @Value("${agraph.agloadDir:/tmp/drawingExtractor}")
    private String agloadDir;
    
    @Value("${agraph.agloadName:agload}")
    private String agloadName;
    
    @Value("${agraph.port:10035}")
    private String agPort;
    
    @Value("${agraph.repositoryName:test-wf}")
    private String agRepositoryName;
    
    @Value("${agraph.endpoint:http://localhost:10035}")
    private String agEndpoint;
    
    @Value("${agraph.username:raja}")
    private String agUserName;
    
    @Value("${agraph.password:raja}")
    private String agPassword;
    
    
    
    public void uploadFileToAgraph(String tempFolder, String filePath) throws IOException {        
        
        LOGGER.info("Creating repository by name {}", agRepositoryName);
        LOGGER.info("Uploading file to the repository {}", agRepositoryName);
        
        StringBuilder sb = new StringBuilder(agloadDir);  
        sb.append("/agload --port ");
        sb.append(agPort);
        sb.append(" ");
        sb.append(agRepositoryName);
        sb.append(" ");
        sb.append(filePath);
        
        Map<String, String> commands = new HashMap();
        commands.put(tempFolder, sb.toString());
        CommandLineUtils.runShellCommands(commands);
    }
    
    
    public int deleteAgraphRepository() throws ProtocolException, IOException {
        URL url = new URL(agEndpoint + "/repositories/" + agRepositoryName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        
        String userpassword = agUserName + ":" + agPassword;
        String encodedAuthorization = Base64.getEncoder()
                .encodeToString(userpassword.getBytes("utf-8"));
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
        
        LOGGER.info("Deleting repository by name {}", agRepositoryName);
        int responseCode = connection.getResponseCode();
        return responseCode;
    }
    
    public String getRepositoryName() {
        return agRepositoryName;
    }
    
    public String getEndpointUrl() {
        return agEndpoint;
    }
    
}
