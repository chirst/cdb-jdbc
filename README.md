# cdb-jdbc

A JDBC database driver for the database [CDB](https://github.com/chirst/cdb). I
don't know anything about Java so don't bother reading this code. I just wanted
to see if I could make this database work with DataGrip.

## Running in DataGrip

- Download the `.jar` file for this repository.
- Create a user driver in DataGrip. Provide the `.jar` file and select the 
  driver class `com.cdb.Cdb`
- Under the "Advanced" tab provide `--enable-preview` under the command args.
- Create a datasource for the driver select no user auth and provide a filename.
