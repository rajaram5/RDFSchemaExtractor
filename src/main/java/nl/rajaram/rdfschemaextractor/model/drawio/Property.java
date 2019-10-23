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
package nl.rajaram.rdfschemaextractor.model.drawio;

import org.eclipse.rdf4j.model.IRI;

/**
 * Object to represent properties of RDF instance
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2019-10-17
 * @version 0.1
 */
public class Property {
    
    private IRI type;
    private IRI iri;
    private IRI rangeIri;
    private boolean isOptional = false;

    /**
     * @return the type
     */
    public IRI getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(IRI type) {
        this.type = type;
    }

    /**
     * @return the iri
     */
    public IRI getIri() {
        return iri;
    }

    /**
     * @param iri the iri to set
     */
    public void setIri(IRI iri) {
        this.iri = iri;
    }

    /**
     * @return the rangeIri
     */
    public IRI getRangeIri() {
        return rangeIri;
    }

    /**
     * @param rangeIri the rangeIri to set
     */
    public void setRangeIri(IRI rangeIri) {
        this.rangeIri = rangeIri;
    }

    /**
     * @return the isOptional
     */
    public boolean isIsOptional() {
        return isOptional;
    }

    /**
     * @param isOptional the isOptional to set
     */
    public void setIsOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }    
         
}
