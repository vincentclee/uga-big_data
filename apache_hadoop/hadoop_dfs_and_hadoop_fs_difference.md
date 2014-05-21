##Difference between 'hadoop dfs' and 'hadoop fs'

With my latest assignment I have started exploring Hadoop and related technologies. When exploring HDFS and playing with it, I came across these two syntaxes of querying HDFS:

> hadoop dfs
  
> hadoop fs

Initally could not differentiate between the two and keep wondering why we have two different syntaxes for a common purpose. I googled the web and found people too having the same question and below are there reasonings:

Per [Chris](http://stackoverflow.com/questions/8384479/differnce-between-hadoop-dfs-and-hadoop-fs) explanation looks like there's no difference between the two syntaxes. If we look at the definitions of the two commands (hadoop fs and hadoop dfs) in $HADOOP_HOME/bin/hadoop


```bash
...
elif [ "$COMMAND" = "datanode" ] ; then
  CLASS='org.apache.hadoop.hdfs.server.datanode.DataNode'
  HADOOP_OPTS="$HADOOP_OPTS $HADOOP_DATANODE_OPTS"
elif [ "$COMMAND" = "fs" ] ; then
  CLASS=org.apache.hadoop.fs.FsShell
  HADOOP_OPTS="$HADOOP_OPTS $HADOOP_CLIENT_OPTS"
elif [ "$COMMAND" = "dfs" ] ; then
  CLASS=org.apache.hadoop.fs.FsShell
  HADOOP_OPTS="$HADOOP_OPTS $HADOOP_CLIENT_OPTS"
elif [ "$COMMAND" = "dfsadmin" ] ; then
  CLASS=org.apache.hadoop.hdfs.tools.DFSAdmin
  HADOOP_OPTS="$HADOOP_OPTS $HADOOP_CLIENT_OPTS"
...
```

that's his reasoning behind the difference.

I am not convinced with this, I looked out for a more convincing answer and here's are a few excerpts which made better sense to me:

> FS relates to a generic file system which can point to any file systems like local, HDFS etc. But dfs is very specific to HDFS. So when we use FS it can perform operation with from/to local or hadoop distributed file system to destination . But specifying DFS operation relates to HDFS.

Below are the excerpts from hadoop documentation which describes these two as different shells.

##FS Shell
The FileSystem (FS) shell is invoked by bin/hadoop fs . All the FS shell commands take path URIs as arguments. The URI format is scheme://autority/path. For HDFS the scheme is hdfs, and for the local filesystem the scheme is file. The scheme and authority are optional. If not specified, the default scheme specified in the configuration is used. An HDFS file or directory such as /parent/child can be specified as hdfs://namenodehost/parent/child or simply as /parent/child (given that your configuration is set to point to hdfs://namenodehost). Most of the commands in FS shell behave like corresponding Unix commands. 


##DFShell
The HDFS shell is invoked by bin/hadoop dfs . All the HDFS shell commands take path URIs as arguments. The URI format is scheme://autority/path. For HDFS the scheme is hdfs, and for the local filesystem the scheme is file. The scheme and authority are optional. If not specified, the default scheme specified in the configuration is used. An HDFS file or directory such as /parent/child can be specified as hdfs://namenode:namenodeport/parent/child or simply as /parent/child (given that your configuration is set to point to namenode:namenodeport). Most of the commands in HDFS shell behave like corresponding Unix commands. 


So from the above it can be concluded that it all depends upon the scheme configure. When using this two command with absolute URI, i.e. scheme://a/b the behavior shall be identical. Only its the default configured scheme value for file and hdfs for fs and dfs respectively which is the cause for difference in behavior.

---

Posted 1st June 2012 by [Abhishek Jain](https://plus.google.com/100553966937866814133)

[Source Article](http://nsinfra.blogspot.in/2012/06/difference-between-hadoop-dfs-and.html)
