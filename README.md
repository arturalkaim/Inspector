PA-Project
==========

Inspector for Java

https://fenix.tecnico.ulisboa.pt/downloadFile/3779580627842/inspector.pdf

In order to debug an application, it is useful to inspect its objects. An inspector describes the state of an object
and can also support making changes in that state.
The inspector must be started using the following form:
<br>
```java
new ist.meic.pa.Inspector().inspect(object);
```


<ul>
<li><b>q</b> Terminates inspection, allowing the calling program to proceed its execution. </li>
<li><b>i name</b> Inspects the value of the field named name of the object currently presented and makes that value
the current inspected object.</li>
<li><b>m name value</b> Modifies the value of the field named name of the object currently presented so that it
becomes value. This command must support, at minimum, fields of type int.</li>
<li><b>c name value 0 value 1 ...value n </b> Calls the method named name using the currently presented object
as receiver and the provided values as arguments and inspects the returned value, if there is one.</li>
  </ul>


<h3>Extra features</h3>

<ul>
<li><b>lm</b> - 	List the Methods of the inspected Objects.</li>
<li><b>b</b> - 	Reinspect the last inspected Object</li>
<li><b>bo</b> - 	List the last <value> objects inspected and asks the user one to inspect. <value> is 10 by default.</li>
<li><b>so</b> - 	Save the current inspected with the provided argument as name.</li>
<br>
<li><b>lc</b> - 	List the last <value> commands used and asks the user one to repeat.</li>
<li><b>r</b> - 	Repeat the last commands</li>
</ul>
<br>
Can call functions with saved objects as arguments, or set a field value. <br>
Just add “@” before the saved name object.<br>
	e.g.
```java
so theG 
m g @theG
```
The name “ret” is saved for the return of the last caled function.<br>
<br>
To start looking for a field to inspect in the superclass of the inspected object just add “+” before the name. This could be used to inspect shadowed superclass fields.<br>
	e.g. 
```java 
i +g
```
<br>
Documentation:<br>
https://docs.google.com/document/d/16zliy8b4IzN7HhyRWQ0UhQpO19Ad_I3oJKwCP7o2R8M/edit
