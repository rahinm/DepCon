/*
Copyright 2020 Mohammad A. Rahin                                                                                                          

Licensed under the Apache License, Version 2.0 (the "License");                                                                           
you may not use this file except in compliance with the License.                                                                          
You may obtain a copy of the License at                                                                                                   
    http://www.apache.org/licenses/LICENSE-2.0                                                                                            
Unless required by applicable law or agreed to in writing, software                                                                       
distributed under the License is distributed on an "AS IS" BASIS,                                                                         
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.                                                                  
See the License for the specific language governing permissions and                                                                       
limitations under the License.       
 */

package net.dollmar.svc.depcon.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dollmar.svc.depcon.dao.ApplicationDao;
import net.dollmar.svc.depcon.dao.ArtifactDao;
import net.dollmar.svc.depcon.entity.Application;
import net.dollmar.svc.depcon.entity.Artifact;
import net.dollmar.svc.depcon.utils.DepConException;
import net.dollmar.svc.depcon.utils.EntityBuilder;
import net.dollmar.svc.depcon.utils.Utils;


public class Dependencies {

  private final static String LINE_PREFIX = "[INFO]";
  private final static String END_MARKER_1 = "[INFO] BUILD SUCCESS";
  private final static String END_MARKER_2 = "Finished at";  

  private String prodName;
  private String prodVersion;
  private boolean endMarkerReached = false;

  //List<String> ignoreArtifactsFrom = Arrays.asList("biz.thelogicgroup", "com.barclaycard", "org.eclips1");
  List<String> ignoreArtifactsFrom = new ArrayList<>();

  public Dependencies(final String appName, final String appVersion) {
    this.prodName = appName;
    this.prodVersion = appVersion;

    // create the ignore list
    String il = System.getProperty("depcon.ignore.artifacts.from");
    if (!Utils.isEmptyString(il)) {
      String[] groupIds = il.split(",");
      for (String g : groupIds) {
        ignoreArtifactsFrom.add(g.trim());
      }
    }
  }


  private boolean shouldIgnore(String groupId) {
    return ignoreArtifactsFrom.stream().anyMatch(i -> groupId.startsWith(i));
  }

  
  private boolean reachedEnd(String line) {
    if (endMarkerReached)
      return true;
    if (END_MARKER_1.equals(line)) {
      endMarkerReached = true;
      return true;
    }
    if (line.contains(END_MARKER_2)) {
      endMarkerReached = true;
      return true;
    }
    return false;
  }


  /*
   * Ideally we expect the dependency list file to have been created using 
   * 'mvn dependency:list' command.However, if someone submits a file that
   * was created using 'mvn dependency:tree' we'd need to filter out ascii 
   * art    * characters (!+-\ etc) at the beginning of each line that help 
   * depicting a tree like structure.
   */
  private String extraTrim(String line) {
    int i = 0;
    for(;i < line.length(); i++) {
      char c = line.charAt(i);
      if (Character.isAlphabetic(c) || Character.isDigit(c)) {
        break;
      }
    }
    return line.substring(i).trim();
  }


  public Collection<String[]> importFile(final String depFile) {
    Objects.requireNonNull(depFile);
    endMarkerReached = false;

    Collection<String[]> li = new ArrayList<>();
    try (Stream<String> stream = Files.lines(Paths.get(depFile))) {

      li = stream
          .map(String::trim)
          .filter(line -> line.startsWith(LINE_PREFIX))
          .filter(line -> !reachedEnd(line))
          .map(line -> extraTrim(line.substring(line.indexOf(LINE_PREFIX) + LINE_PREFIX.length())).split(":"))
          .filter(a -> (a.length == 5))
          .filter(a -> !shouldIgnore(a[0]))
          .sorted(Comparator.comparing(a -> a[1]))
          .collect(Collectors.toList());

      return li;
    }
    catch (IOException e) {
      throw new DepConException("Error: Failed to import dependencies", e);
    }
  }


  private String formatDependency(String[] dep) {
    StringBuilder sb = new StringBuilder();
    sb.append(dep[1]).append(" ==> ");
    for(int i = 0; i < dep.length; i++) {
      sb.append(dep[i]);
      if (i != (dep.length -1)) {
        sb.append(" | ");
      }
    }
    return sb.toString();
  }


  @SuppressWarnings("unused")
  private void printDependencies(Collection<String[]> deps) {

    for(String[] dep : deps) {
      System.out.println(formatDependency(dep));
    }
  }

  @SuppressWarnings("unused")
  public void persist(Collection<String[]> deps) {
    ApplicationDao appDao = new ApplicationDao();

    if (appDao.getApplication(prodName, prodVersion) != null) {
      throw new DepConException(String.format("Dependencies for application '%s:%s' has already been imported", prodName, prodVersion));
    }
    Collection<Artifact> arts = deps
        .stream()
        .map(dep -> { 
          Artifact art = EntityBuilder.buildArtifact(dep);
          Artifact existing = new ArtifactDao().getArtifact(art);
          
          return (existing != null) ? existing : art;
          }).collect(Collectors.toList());
    
    Application app = appDao.saveApplication(EntityBuilder.buildApplication(prodName, prodVersion), arts);
  }	


//  public static void main(String[] args) {
//    if (args.length != 3) {
//      System.out.println("Usage: Dependencies app-name app-version dep-file-name");
//    }
//    else {
//      Dependencies d = new Dependencies(args[0], args[1]);
//
//      Collection<String[]> deps = d.importFile(args[2]);
//      d.printDependencies(deps);
//      //d.persist(deps);
//    }
//    System.exit(0);
//  }

}
