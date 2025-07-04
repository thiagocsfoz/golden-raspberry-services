# Golden Raspberry Awards API

This is a RESTful API developed for the back-end challenge, designed to return the producers with the longest and shortest intervals between two consecutive "Worst Picture" awards from the Golden Raspberry Awards.

The application is built using the Quarkus framework and Maven.

## Technology Stack

Based on the project's `pom.xml`, the main technologies and versions are:

* **Quarkus**: `3.23.4`
* **Java**: `21`
* **Maven**: `3.9+` (Build Tool)
* **REST-assured**: `5.5.0` (for testing)
* **JaCoCo**: `0.8.12` (for code coverage)

## Data Source (CSV File)

On startup, the application loads movie data from a CSV file.

### CSV File Location

By default, the application expects to find the data file in the `src/main/resources` directory.

### CSV File Format

The CSV file must have a header row and use a semicolon (`;`) as a delimiter. The required columns are, in order:

| Column    | Type    | Description                                                                                             | Example                        |
| :-------- | :------ | :------------------------------------------------------------------------------------------------------ | :----------------------------- |
| `year`    | Integer | The year the movie was released.                                                                        | `1980`                         |
| `title`   | String  | The title of the movie.                                                                                 | `Can't Stop the Music`         |
| `studios` | String  | The studio(s) that produced the movie.                                                                  | `Associated Film Distribution` |
| `producers` | String  | The producer(s) of the movie.                                                                           | `Allan Carr`                   |
| `winner`  | String  | Indicates if the movie won. Only a case-insensitive value of `yes` marks it as a winner. Any other value, **including an empty value**, is considered a non-winner. | `yes`                          |

## Environment Configuration

The application uses a `.env` file at the project root to manage environment-specific settings. This allows for easy configuration without modifying the source code.

**Example `.env` file:**

```
# Data File Configuration
CSV_FILE_NAME=movielist.csv

# H2 Database Configuration
DB_URL=jdbc:h2:file:./target/goldenraspberrydb;DB_CLOSE_DELAY=-1
DB_USER=goldenraspberrydb
DB_PASSWORD=goldenraspberrydb

# Hibernate SQL Logging
LOG_SQL=false
```

**Important:** The `.env` file should be added to `.gitignore` to keep credentials and sensitive data out of version control.

## How to Run the Application

### Development Mode

To start the application in development mode (with live reload), run the following command:

```bash
./mvnw quarkus:dev
```

The API will be available at `http://localhost:8080`.

### Production Mode

To run the application in a production-like environment, you must first build the executable JAR.

1.  **Build the application:**
    ```bash
    ./mvnw package
    ```
2.  **Run the generated JAR file:**
    The command above creates a runnable JAR in the `target/quarkus-app/` directory. Run it with the following command:
    ```bash
    java -jar target/quarkus-app/quarkus-run.jar
    ```

This will start the application using the default `prod` profile. For a real production deployment, externalize your `.env` file or use system environment variables to configure the database, API keys, etc.

## Interacting with the API

- **Endpoint:** `GET http://localhost:8080/api/v1/producers/award-intervals`

**Example `cURL` command:**

```bash
curl --location 'http://localhost:8080/api/v1/producers/award-intervals'
```

## Building and Testing

The project includes a comprehensive test suite with both integration (`@QuarkusTest`) and unit tests (with Mockito).

### Running Tests Only

To execute all tests, use the command:

```bash
./mvnw test
```

### Running the Full Build with Quality Checks

To execute the complete Maven lifecycle, which includes compiling, testing, and quality checks, use the `verify` command:

```bash
./mvnw clean verify
```

### Code Coverage Rule (JaCoCo)

The project is configured with JaCoCo to enforce code quality standards.

- **Rule**: The build is configured to **fail** if the instruction coverage for the main business logic classes falls below **80%**.
- **Report**: After running the `verify` command, a detailed HTML coverage report is generated at `target/site/jacoco/index.html`. This report can be opened in a browser to inspect which lines of code were covered by the tests.