Secrets
================

Secrets is a plugin that allows you to hide Secrets signs on your server.

All data is stored in a MySQL database, so the plugin is multi-server compatible.

If you want to use the money reward, you need Vault and an economy system that supports Vault.

Information
=================

Spigot page: https://www.spigotmc.org/resources/secrets-schilder-secrets-für-die-lobby.63853/ (Old Version)

Developmental builds: https://jenkins.fantacs.de/job/Secrets/

For questions/help join our discord at: https://discord.com/invite/4nGrfZxb2a

Maven Dependency
=================
The API is available for Maven.

```xml
<repositories>
    <repository>
        <id>fantanexus</id>
        <url>https://nexus.fantacs.de/repository/maven-snapshots/</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>de.fanta.secrets</groupId>
        <artifactId>Secrets</artifactId>
        <version>2.0.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
