# Multinational Fantasy Soccer League Backend
This project will involve the development of a Multinational Fantasy Soccer
League (MFSL) platform, which will allow soccer enthusiasts to manage soccer fantasy teams
by using players from different soccer leagues together. The team is eager to create MFSL,
which will revolutionize the current fantasy soccer ecosystem. At this time, there is no other
platform that enables multiple national leagues to be selected in one fantasy game, allowing
more diversity of soccer fans to play together in the same game mode. This is a much
demanded feature amid the soccer community, since key players are now scattered across
different European leagues such as Ligue 1 (France), Bundesliga (Germany), La Liga (Spain),
Serie A (Italy) and the Premier League (England). The expected outcomes will consist of a
functioning backend combined with a web application. The backend will be developed with Java
Spring while the frontend will be implemented using the React framework
## Requirements
* Java 17
* Maven
* Docker
## Running the app

### Docker
* `docker run --env-file YOUR_ENVIRONMENT_FILE -p 8080:8080 mfsl-backend:0.0.1-SNAPSHOT`
* `mvn spring-boot:build-image`

You might need to change the version of the docker image from latest if you are experiencing difficulties 
with the second command. You can find the version of the image in the outputs of the first command

### Locally
* `source YOUR_ENVIRONMENT_FILE` 
* `mvn spring-boot:run`

`YOUR_ENVIRONMENT_FILE` should be replaced with the absolute path to a file with real environment variables.
