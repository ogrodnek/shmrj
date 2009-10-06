package com.bizo.hive.mr;
/*
 * Copyright 2009 bizo.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Test;


public class GenericMRTest {

  @Test(expected=NoSuchElementException.class)
  public void testReduceTooFar() throws Exception {
    new GenericMR().reduce(new StringReader("a\tb\tc"), new StringWriter(), new Reducer() {
      @Override
      public void reduce(String key, Iterator<String[]> records, Output output) throws Exception {
        while (true) {
          records.next();
        }
      }
    });
  }
  
  @Test
  public void testEmptyMap() throws Exception {
    final StringWriter out = new StringWriter();
    
    new GenericMR().map(new StringReader(""), out, identityMapper());
    
    Assert.assertEquals(0, out.toString().length());
  }
  
  @Test
  public void testIdentityMap() throws Exception {
    final String in = "a\tb\nc\td";
    final StringWriter out = new StringWriter();
    
    new GenericMR().map(new StringReader(in), out, identityMapper());
    
    Assert.assertEquals(in + "\n", out.toString());
  }
  
  @Test
  public void testIdentityReduce() throws Exception {
    final String in = "a\tb\nc\td";
    final StringWriter out = new StringWriter();
    
    new GenericMR().reduce(new StringReader(in), out, identityReducer());
    
    Assert.assertEquals(in + "\n", out.toString());
  }
  
  @Test
  public void testWordCountReduce() throws Exception {
    final String in = "hello\t1\nhello\t2\nokay\t4\nokay\t6\nokay\t2";
    final StringWriter out = new StringWriter();
    
    new GenericMR().reduce(new StringReader(in), out, new Reducer() {
      @Override
      public void reduce(String key, Iterator<String[]> records, Output output) throws Exception {
        int count = 0;
        
        while (records.hasNext()) {
          count += Integer.parseInt(records.next()[1]);
        }
        
        output.collect(new String[] { key, String.valueOf(count) });
      }
    });
    
    final String expected = "hello\t3\nokay\t12\n";
    
    Assert.assertEquals(expected, out.toString());
  }
  
  private Mapper identityMapper() {
    return new Mapper() {
      @Override
      public void map(String[] record, Output output) throws Exception {
        output.collect(record);
      }
    };
  }
  
  private Reducer identityReducer() {
   return new Reducer() {
    @Override
    public void reduce(String key, Iterator<String[]> records, Output output) throws Exception {
      while (records.hasNext()) {
        output.collect(records.next());
      }
    }
  }; 
  }
}
