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
package nl.rajaram.rdfschemaextractor.api.controller;

import nl.rajaram.rdfschemaextractor.api.controller.converter.RDFInstanceListMessageConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {"nl.rajaram.rdfschemaextractor.api.*"})
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Configuration
    public static class MvcConfig implements WebMvcConfigurer {
        @Bean
        public RDFInstanceListMessageConverter rdfxml() {
            return new RDFInstanceListMessageConverter((RDFFormat.RDFXML));
        }
        
        
        @Bean
        public RDFInstanceListMessageConverter rdfTurtle() {
            return new RDFInstanceListMessageConverter((RDFFormat.TURTLE));
        }
        
        @Bean
        public RDFInstanceListMessageConverter rdfN3() {
            return new RDFInstanceListMessageConverter((RDFFormat.NTRIPLES));
        }
        
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.mediaType(RDFFormat.RDFXML.getDefaultFileExtension(),
                    MediaType.parseMediaType(RDFFormat.RDFXML.getDefaultMIMEType()));
            
            configurer.mediaType(RDFFormat.TURTLE.getDefaultFileExtension(),
                    MediaType.parseMediaType(RDFFormat.TURTLE.getDefaultMIMEType()));
            
            configurer.mediaType(RDFFormat.NTRIPLES.getDefaultFileExtension(),
                    MediaType.parseMediaType(RDFFormat.NTRIPLES.getDefaultMIMEType()));
        }
    }
}
