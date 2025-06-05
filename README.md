# ☕ cdb-jdbc

A [JDBC](https://en.wikipedia.org/wiki/Java_Database_Connectivity) database 
driver for [github.com/chirst/cdb](https://github.com/chirst/cdb)

## Running in DataGrip

1. Download the `.jar` file for this repository.
2. Create a user driver in DataGrip. Provide the `.jar` file and select the
driver class `com.cdb.Cdb`.
3. Under the "Advanced" tab provide `--enable-preview --enable-native-access=ALL-UNNAMED`
for the VM options. This is necessary because this driver uses the 
[Java foreign function memory API](https://docs.oracle.com/en/java/javase/21/core/foreign-function-and-memory-api.html)
(a preview feature) to invoke CDB's C interface.
4. Once the driver is created, create a datasource with the driver. Select "No 
Auth" for the authentication and provide a filename for your database in the 
URL field. `:memory:` is a good option if you don't want to create a database 
file.
