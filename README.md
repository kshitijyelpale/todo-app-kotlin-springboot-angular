## Todo-app

### How to run

- Open the project in any IDE.
- Project is developed with `Spring boot 3.1.4`, `Kotlin`, and `Java 21` supported.
- For the frontend, `Angular 16` with `Material UI` is used.
- Run both frontend and backend separately. For the backend execute `TodoAppApplication.kt` file and it will also spin up the Postgres container.
- For the frontend, `cd frontend` and `npm run start` would run the application. Visit `localhost:4200`
- Jar file can also be created using `./gradle build` and jar is created in `build/libs` folder. And run jar file `todo-app-0.0.1-SNAPSHOT.jar`.
  Since frontend is packaged into the jar with Spring Boot, now the project is on `localhost:9090`.
  But Postgres needs to run separately, jar can not start postgres container.
  To start the postgres container, run `docker-compose start` on project's root directory.
- Docker image build of app is in progress.

### Improvements

- Add authentication with Spring security
- Increase the Test coverage
