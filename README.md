# Servlet-firebase
Simple ''Hello world'' app to learn firebase among servlet



## How-to Heroku

- make .war
- embedded tomcat -> heroku [webapp-runner.jar](https://mvnrepository.com/artifact/com.github.jsimone/webapp-runner)
- test local `` java --jar  target/dependency/webapp-runner.jar  target/[app.war] ``
- make a project and put this ``pom.xml`` and ``Procfile`` on /root and the [app.war] file in /target-> `` mvn archetype:generate -DarchetypeArtifactId=maven-archetype-webapp ``
- create app `` heroku create [name] --no-remote``
- ``mvn package heroku:deploy -e``
- see logs `` heroku logs --app [name] --tail ``
