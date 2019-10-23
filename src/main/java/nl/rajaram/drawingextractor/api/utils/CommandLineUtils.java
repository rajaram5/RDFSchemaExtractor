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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utils class to handle Shell commandline related operation
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2018-03-23
 * @version 0.1
 */
public class CommandLineUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(CommandLineUtils.class);
    
    public static String runShellCommands(Map<String, String> commands){
        
        String output = null;
        try { 
            int count = 0;
            for(String dir:commands.keySet()){
                String command = commands.get(dir);                
                LOGGER.info("Shell command : " + command);
                Process process = Runtime.getRuntime().exec(command, null, new File(dir));
                process.waitFor();
                output = IOUtils.toString(process.getInputStream(), "UTF-8");
            }
            
        } catch (IOException | InterruptedException ex) {
            String msg = "Error executing shell command. " + ex.getMessage();
            LOGGER.error(msg);
        }
        return output;
        
    }
    
}
