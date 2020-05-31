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

public class UserContext {
  
  private ThreadLocal<Subject> threadLocal;
  
  private static UserContext INSTANCE;
  
  static {
    INSTANCE = new UserContext();
  }
  
  private UserContext() {
    threadLocal = new ThreadLocal<>();
  }
  
  public static UserContext getConext() {
    return INSTANCE;
  }
  
  
  public void set(Subject sub) {
    threadLocal.set(sub);
  }
  
  
  public Subject getSubject() {
    return threadLocal.get();
  }

  public void removeSubject() {
    threadLocal.remove();
  }
  
}
