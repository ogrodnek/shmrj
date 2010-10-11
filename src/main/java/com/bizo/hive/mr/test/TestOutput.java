package com.bizo.hive.mr.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bizo.hive.mr.Output;

/**
 * In-Memory Output implementation useful for testing.
 * 
 * @author larry
 */
public final class TestOutput implements Output {
  private List<String[]> lines = new ArrayList<String[]>();

  @Override
  public void collect(final String[] arg0) throws Exception {
    lines.add(arg0);
  }
  
  public List<String[]> getLines() {
    return this.lines;
  }
  
  public boolean hasLine(final String... line) {
    for (final String[] row : this.lines) {
      if (Arrays.equals(row, line)) {
        return true;
      }
    }
    
    return false;
  }  
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    
    sb.append("TestOutput [").append("\n");
    for (final String[] row : lines) {
      sb.append("\t").append(Arrays.asList(row)).append("\n");
    }
    sb.append("]\n");
    
    return sb.toString();
  }
}
