## 🚀 Getting Started

### Starting the Docker Compose Infrastructure

To start the Docker Compose infrastructure, run the following command:

```bash
docker-compose up -d
```

This command will start the necessary services defined in the `docker-compose.yml` file.

### Building the Application with Gradle

Once the infrastructure is up, build the application using Gradle with the following command:

```bash
./gradlew build
```

This will compile the application and create the necessary artifacts.

### Running the Service

After building the application, you can run the service using the following command:

```bash
./gradlew run
```

This command will start the service, and you should see output indicating that it is running successfully.