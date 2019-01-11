**Shim Configuration Utility** 
**Last update** - _01.10.2019_  

**Properties setup** 
1. _Common properties_:  
  a. Path to shim - path to the shim folder ( ex: D:\pdi_client_80_0137\data-integration\plugins\pentaho-big-data-plugin\hadoop-configurations\cdh511 )  
  b. First cluster node - exactly first cluster node - n1, for EMR/HDI use master DNS  
  c. .pem file location - path to pem file, use only for EMR clusters
2. _Additional properties_:  
  a. Path to test.properties - path to test.properties, used for configuring run_all job sequence  
  b. DFS install dir - setup property pmr.kettle.dfs.install.dir in plugin.properties to /opt/pentaho/mapreduce_%VALUE%   
  c. Named cluster name - name for creating named cluster, if unset will use first node as name  
3. _Security properties_:  
  a. Kerberos - username/password for kerberos auth, if you don't want kerberos wrapping just leave this fields empty  
  b. REST - username/password for REST authentication, used for HDP/CDH/HDI clusters, use for MAPR only if you want to configure the local MAPR client  
  c. SSH - username/password for ssh authentication, necessary for all clusters  
  d. EMR keys - public/private keys for EMR, added to core-site.xml

**Profile setup**
1. _Profile drop box_ - show all saved profiles, put profile file ( ex: chd513.properties ) to /profiles  
2. _Profile name_ - set profile name, existing profile will be overwritten
3. Only setup properties are saved to profile ( checkboxes are not saved )

**Additional setup**
1. _Download krb5.conf_ - download krb5.conf in 3 places: root tool directory, PENTAHO_JAVA_HOME, JAVA_HOME, and /etc for Linux . If environment variables are absent or the user doesn't have permissions to copy files into these locations the file will not be copied  
For kerberos wrapping will search krb5.conf file in the same sequence
2. _Configure mapr client_ - will configure the local mapr client. You should have a local MAPR client installed. Exactly:  
  a. copy ssl_truestore and -site.xml files to mapr client hadoop configs folder  
  b. execute configure.bat with the necessary parameters.  
3. _Copy drivers_ - copy driver jars, mysql to /lib folder others to shim/lib folder
4. _Use BiServer setup_ - will show installed shims and download and configure choosing shim in Spoon, PRD, PME and Server, also can be used with separate installed Spoon 

**Using tool**:

Unzip -bin.zip folder and run java -jar shim-config-loader-1.0.1.jar . Before running check if the user has all necessary permissions.

**What exactly does this tool do**:

1. Downloads -site.xml files and krb5.conf
2. Creates a new named cluster
3. Configures pentaho products to use the selected shim
  a. Sets up config.properties ( also setup mapr classpath )  
  b. Sets up plugin.properties
4. Edits -site.xml files  
  a. Adds the cross-platform property  
  b. Edits core-site.xml for mapr with uid property  
  c. Edits core-site.xml for EMR with keys
4. Configures the mapr client
5. Puts driver jars into pentaho product 

_Any bugs and suggestions leave in **Issues** section on git._
