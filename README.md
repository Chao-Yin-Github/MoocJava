# 关于本项目

-   描述:

    java核心技术(进阶)Mooc期末作业:

    实现自己的Tomcat!

-   难度等级:6

-   题目:

    实现一个客户端和服务端(8001端口)。客户端发送字符串hello.html, 服务端读取hello-server.html的内容并返回。
    hello.html的内容是以下字符串<head><body>hello world</body></html>。
    客户端接受到反馈字符串后，保存为文件hello.html。
    客户端发送字符串HelloWorld.action, 服务端动态调用执行HelloWorld.class。
    HelloWorld.class将输出以下字符串<head><body>hello world 222</body></html>。
    客户端接受到反馈字符串后，保存为文件hello2.html。
    服务端采用Executors线程池实现。
    服务器端的文件关联配置，如hello.html和hello-server.xml, HelloWorld.action和HelloWorld.class的对应关系，请从以下的server-mapping.xml读取。

    ```xml
    ======================server.xml start============
    <file-mapping>
    	<mapping>
    		<client>hello.html</client>
    		<server>hello-server.html</server>
    	</mapping>
    	<mapping>
    		<client>HelloWorld.action</client>
    		<server>HelloWorld.class</server>
    	</mapping>
    </file-mapping>	
    ======================server.xml end============
    ```

    

    另外，服务端需要以Thread的方式，记录客户端的访问到数据库表t_client_access(id, ip_address, access_time, parameters)中。
    例如，数据(1, 192.168.1.100, '2019-03-18 12:00:00', 'hello.html') 。
    要求采用数据库连接池和PreparedStatement访问。(100分)

-   不足之处:

    1.  还是用Java调用命令行编译运行文件时,输入的指令应该是`java+路径+生成的.class文件名`.

        但是这样再IDEA中运行不成功,不知道是不是编辑器的问题,但是由于是多文件,用命令行直接编译运行有问题.

        最后也只能错就错,直接改成`java路径+目标源文件名+.java`,最后在IDEA中运行成功.

    2.  IO处理不好,用BufferedReader.readLine()不知道Server端什么时候发送完response字符串.

        最后只是在Server端每次发送完response之后,再发送一条"Over"来做为Client端读入结束标记.

    3.  还有感觉耦合程度太高

    4.  最后老师指出,Druid数据库连接池滥用,自己看了一下,果然最后改着改着就改错了,竟然把主要数据库连接配置步骤放到了runnable接口里......

    5.  虽然作业是这么布置的,但是自己并不清楚正真的Tomcat是不是通过类似的机制实现的.即对Tomcat原理不清楚,等等……

-   收获:

    1.  实践方面:

        -   将看似很难的大项目细分成一个个小项目,再逐步完善功能。

    2.  技术方面:

        1.  System.getProperty("line.separator")获取到当前系统的换行符。

        2.  运用了数据库连接池Druid、线程池、Java调用命令行、Socket网络编程相关知识。

        3.  String的.equals()方法来判断两个字符串是否相等,而不用==来判断.

            因为后者是来判断两个字符串的内存空间是否一样.
