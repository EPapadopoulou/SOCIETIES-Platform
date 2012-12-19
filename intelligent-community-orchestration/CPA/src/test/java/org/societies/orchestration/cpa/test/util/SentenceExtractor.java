/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.orchestration.cpa.test.util;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 12/7/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class SentenceExtractor {
    private File f = null;

    private String[] allsentences = null;
    public SentenceExtractor(String file){
        String data = SentenceExtractor.readFile(file);
        //System.out.println("data start: "+data.substring(0,3000));
        //allsentences = data.split("[\\p{P}]");
        allsentences = data.split("(?<=[a-z])\\.\\s+");
        //System.out.println("allsentences lenght: "+allsentences.length);
        //for(int i=0;i<10;i++)
        //    System.out.println("i: "+i+" sentence: \""+allsentences[i]+"\"");
        data=null;

    }
    private static String readFile(String path) {
        FileInputStream stream = null;

        try {
            stream = new FileInputStream(new File(path));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert stream != null;
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public String[] getSentences(int start, int n){
        String[] ret = new String[n];
        for(int i=start;(i-start)<n && i<allsentences.length;i++)
            ret[(i-start)] = allsentences[i].trim();
        return ret;
    }
    public static void main(String args[]){
        SentenceExtractor extractor = new SentenceExtractor("./src/test/resources/reuters21578content.txt");
        String sentence = extractor.getSentences(20,1)[0];
        System.out.println("sentence 1 : \""+sentence+"\"");

        //small test of lengths..

        for(int i =0; i <100;i++)     {
            sentence = extractor.getSentences(i,1)[0];
            if(sentence.length()>500){
                System.out.println("very long sentence ("+sentence.length()+"): \""+sentence+"\"");
            }
        }

    }
}
