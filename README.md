PA-Project
==========

Inspector for Java

https://fenix.tecnico.ulisboa.pt/downloadFile/3779580627842/inspector.pdf

In order to debug an application, it is useful to inspect its objects. An inspector describes the state of an object
and can also support making changes in that state.


<ul>
<li><b>q</b> Terminates inspection, allowing the calling program to proceed its execution. </li>
<li><b>i name</b> Inspects the value of the eld named name of the object currently presented and makes that value
the current inspected object.</li>
<li><b>m name value</b> Modies the value of the eld named name of the object currently presented so that it
becomes value. This command must support, at minimum, elds of type int.</li>
<li><b>c name value 0 value 1 ...value n </b> Calls the method named name using the currently presented object
as receiver and the provided values as arguments and inspects the returned value, if there is one.</li>
  </ul>

Document
https://docs.google.com/document/d/16zliy8b4IzN7HhyRWQ0UhQpO19Ad_I3oJKwCP7o2R8M/edit
