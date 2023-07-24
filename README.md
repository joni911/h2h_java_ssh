# H2HDownloadUpload

Java application that is responsible for fetching and handling data from a MySQL database via SSH tunnel and managing files related to this data.

## Prerequisites

- Java Development Kit (JDK) 8 or later
- NetBeans IDE or similar Java-supporting IDE
- MySQL database server
- Access to the server via SSH

## Dependencies

- Jcraft JSch library for SSH connections
- json-simple for JSON parsing and writing
- MySQL JDBC driver for database connections

## How it works

This application sets up an SSH tunnel to a remote MySQL database, retrieves certain data, manages it and then writes it into text files. These text files are then updated in the database once the writing operation is successful.

## Usage

To use this application, you must:

1. Ensure you have the correct dependencies installed and imported into your project.
2. Set the SSH and database credentials within the JSON file `setting.json`. This file should contain:

   ```json
   {
   "url": "127.0.0.1",
    "db": "<db name>",
    "user": "<db username>",
    "password": "<db password>",
    "input": "D:\\Pelatihan\\PUSAT\\dishph2h\\api\\txt\\input\\",
    "output": "D:\\Pelatihan\\PUSAT\\dishph2h\\api\\txt\\output\\",
    "sshHost": "<SSH Host>",
    "sshUser": "<SSH Username>",
    "sshPassword": "<SSH Password>",
    "sshPort": <SSH Port>,
    "dbHost": "<Database Host>",
    "dbName": "<Database Name>",
    "dbUser": "<Database Username>",
    "dbPassword": "<Database Password>",
    "dbPort": <Database Port>
    }
   ```

3. Run the application in your IDE or from the command line using `java H2HDownloadUpload`.

## Main Functions

- `getKoneksi()`: Establishes an SSH tunnel and connects to the MySQL database. Connection is established only once and reused for further queries.

- `loadSshSettingsFromJson(String jsonFilePath)`: Reads SSH and database settings from a JSON file.

- `getdatanow()`: Fetches data from the `transferins` table in the database, writes it into text files and then updates the corresponding rows in the table to indicate the successful write operation.

- `tambahHasil()`: Fetches data from the `java_request_data` table, reads corresponding text files and updates the table with the data read.

- `addJava_request_data(String id)`: Adds a new record in `java_request_data` table.

- `bacafile()`: Reads all text files in the output directory.

- `jsonread(String x)`: Reads a JSON file and loads the settings from it.

- `main(String[] args)`: The main method of the program, which starts the application.

## Notes

- Make sure you set up your database and SSH settings correctly in the `setting.json` file.
- This application requires a specific structure in your database. Ensure the structure matches with the queries made in the code.
- Ensure the paths for input and output are correctly set.
