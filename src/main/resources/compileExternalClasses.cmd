::	This file is part of Burningwave Core.                                                                      
::	                                                                                                            
::	Author: Roberto Gentili                                                                                     
::	                                                                                                            
::	Hosted at: https://github.com/burningwave/jvm-driver                                                              
::	                                                                                                            
::	- -                                                                                                          
::	                                                                                                            
::	The MIT License (MIT)                                                                                       
::	                                                                                                            
::	Copyright (c) 2021 Roberto Gentili                                                                          
::	                                                                                                            
::	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
::	documentation files (the "Software"), to deal in the Software without restriction, including without        
::	limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of   
::	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following    
::	conditions:                                                                                                 
::	                                                                                                            
::	The above copyright notice and this permission notice shall be included in all copies or substantial        
::	portions of the Software.                                                                                   
::	                                                                                                            
::	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT       
::	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO   
::	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
::	AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
::	OR OTHER DEALINGS IN THE SOFTWARE.
::
@echo off
set JAVA_HOME=%1

rmdir /s /q %~dp0org
mkdir %~dp0org\burningwave\jvm\driver\java

call %JAVA_HOME%\bin\javac.exe -cp "%~dp0..\..\..\target\classes;%~dp0;" --release 9 %~dp0jdk\internal\loader\ClassLoaderDelegateForJDK9.java
call %JAVA_HOME%\bin\javac.exe -cp "%~dp0..\..\..\target\classes;%~dp0;" --release 8 %~dp0java\lang\reflect\AccessibleSetterInvokerForJDK9.java
call %JAVA_HOME%\bin\javac.exe -cp "%~dp0..\..\..\target\classes;%~dp0;" --release 8 %~dp0java\lang\ConsulterRetrieverForJDK9.java

move %~dp0jdk\internal\loader\ClassLoaderDelegateForJDK9.class %~dp0org\burningwave\jvm\driver\java\ClassLoaderDelegateForJDK9.bwc
move %~dp0java\lang\reflect\AccessibleSetterInvokerForJDK9.class %~dp0org\burningwave\jvm\driver\java\AccessibleSetterInvokerForJDK9.bwc
move %~dp0java\lang\ConsulterRetrieverForJDK9.class %~dp0org\burningwave\jvm\driver\java\ConsulterRetrieverForJDK9.bwc