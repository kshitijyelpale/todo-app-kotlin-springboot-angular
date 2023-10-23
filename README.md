# coding-challenge-ksitij

## Todo-app

### How to run

- Open the project in any IDE.
- Project is developed with `Spring boot 3.1.4`, `Kotlin`, and `Java 21` supported.
- For the frontend, `Angular 16` with `Material UI` is used.
- Run both frontend and backend separately. For the backend execute `TodoAppApplication.kt` file and it will also spin up the Postgres container.
- For the frontend, `cd frontend` and `npm run start` would run the application.
- Jar file can also be created using `./gradle build` and jar is created in `build/libs` folder. And run jar file `todo-app-0.0.1-SNAPSHOT.jar`. But Postgres needs to run separately, jar can not start postgres container
- Docker image build of app is in progress.

### Improvements

- Add authentication with Spring security
- 
