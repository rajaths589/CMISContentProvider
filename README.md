CMISContentProvider
===================

GSoC'13 project for Apache OpenOffice

CMIS UCP for Apache OpenOffice.

 

Before I describe my project plan I would like to write a bit about CMIS and UCP:

CMIS is an acronym for Content Management Interoperability Services. CMIS establishes a standard for all the Enterprise Content Management systems just like what SQL did for the relational databases. CMIS is an abstract layer used for controlling various web repositories and document management systems. Advantages of CMIS is that it is platform independent and available for a wide-range of programming languages. (For more information, links have been provided in the references section).  OpenCMIS/Apache CheMISitry is a collection of libraries/tools/frameworks for CMIS client and server development. It is available as Java, Python , PHP , .NET , Objective-C libraries/modules.  

 

UCP is Universal Content Provider. Apache OpenOffice connects to various data sources using Universal Content Broker(UCB) which has a core module and a set of UCP’s for each source.  UCB provides generalized access to data sources  and a set of functions for querying, modifying, creating, access to folder tree, meta content of documents, permissions,etc. The advantage of UCB is that it provides a common perspective for developer irrespective of the data source. UCPs mask the difference between access protocols for various sources by providing interfaces for access to that particular data source. Currently, UCPs exist for FILE(file-system),  WebDAV-HTTP(web-based file-systems and HTTP), FTP(file transfer protocol), Hierarchy(Virtual file hierarchy), ZIP/JAR, Help files(OpenOffice Help files) and Extension Package Content(access to openoffice extensions).

 

Now, my project is to create a UCP for access to CMIS compliant repositories. Various functionalities include: access to folder tree, changing document properties, version control, creation/deletion documents, setting access permissions, moving files, etc. A sidebar will also be created which will provide access to all these features.

 

My work can be broken down into these components:

1. Familiarising myself with APIs

    This includes both OpenCMIS and OpenOffice API. -- JAVA

2. Designing the UI for sidebar

3. Module: Authentication to repositories using a standard authorization protocol. (One that already exists in OpenOffice API).

4. Module: Browser for file hierarchy of the repository. --viewing the folder tree, copying, moving, deleting files.

5. Module: File Properties Editor. -- Changing Permissions, meta data, etc.

6. Module: Version Control.

7. Changes in OpenOffice: Save/Saveas in repository. Recent files accessed from CMIS in recent documents list,etc.

8. UCP: Using all the modules built to create a UCP for CMIS. Integrating the UCP with the UCB.

 

References:

1. http://wiki.openoffice.org/wiki/Documentation/DevGuide/UCB/

2. http://chemistry.apache.org/project/cmis.html

3. http://www.oldschooltechie.com/blog/2009/11/23/introduction-cmis

4. CMIS and Apache Chemistry in action - Book.

5. http://en.wikipedia.org/wiki/Content_Management_Interoperability_Services

for updates and latest source code check github repository:
https://www.github.com/rajaths589/CMISContentProvider.git
