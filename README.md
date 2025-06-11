# cdb-jdbc

A JDBC database driver for the database [CDB](https://github.com/chirst/cdb). I
don't know anything about Java so don't bother reading this code. I just wanted
to see if I could make this database work with DataGrip.

## Running in DataGrip

- Download the `.jar` file for this repository.
- Create a user driver in DataGrip. Provide the `.jar` file and select the 
  driver class `com.cdb.Cdb`.
- Under the "Advanced" tab provide `--enable-preview` for the VM options.
- Once the driver is created, create a datasource with the driver. Select "No 
  Auth" for the authentication and provide a filename for your database in the 
  URL field. `:memory:` is a good option if you don't want to create a database 
  file.
