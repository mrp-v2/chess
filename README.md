# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

[API](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkzowUAJ4TcRNAHMYARgB0ABkzGoEAK7YYAYjTAqumACUUxpBI6gkgQaK4A7gAWSGAciKikALQAfCzUlABcMADaAAoA8mQAKgC6MAD0DipQADpoAN5VlO4AtigANDC4UuHQMp0oLcBICAC+mBTpsClpbOJZUH4BsVAAFE1QrR1dyhK9UP0wg8MIAJQTrOwwMwJCouJSWcYoYACq1evVW+e3ImKSEmuqXUWTIAFEADJguBFGAbLYwABm9hacOqmF+9wB1xmkzmKCyaAcCAQF2oVxu8ixjxgIEWghQ70on2awDanW6ez6AyGIx+VP+UiBagUWQAkgA5cHeWHwtnbTn7Q7HEYwSVFfJo2h4impTGCiRZOkoBnCBxgSIszby-l3A3CkFqqVgmVa63smDAc2RIoQADW6CdGs93oxAoegMSuMu8xDFt9AbQZPxONSeMycZ9-vQycoqfgyG0WQATFYrHVGl95Z0vfHs2hxugZJo7I5nC5oLxnjAIRB-GEXFEYnFC0k06wM3lCqUKiopCE0BW5R7FX1xunpuPybGEH2kGgrVsObslecdeJhfqI08Xky1suULa-hGHaLyJDobKq20kSi3WG7RfKMt3xLINmTXUYCvAEjXpWIzQtQ8bQA59sRmR1JWlL9WQ9Wss0TINNTwlDqUjaNtwJTMExzc9VGA2YMzw6ikw3fN4iLGBS3LBoHxrb1mMbNBm1sewnFcOwUEDXtjEcZhB2iWJMHYscGKgLJsj4D8ijBMpyjnCQFzqJj61zTdZnYLJdxk81VmMxMzxjOi9XDGCYH3KhgGQORYls-j6yfUjXyyTSoW0qj61-CBUWI6ChXo2ish0BwOFo-MN0SqBktMtjRzAEsyy0ISW1E9tFhkHtlhgABxeVAXk4clNy4V0pyKqwRncpjHlIy-MTbLyNAmBnjAGq2gkXy63siCL0pQDXI8ryGQm-D0AC+10LfcEoRhcKCORKLM2mpyoJcmlhtGyRVjWoDgU2j8dv21EuraEj1oGiyhtqo60onNTPrG-qZmUvLOLLCtnskQThNbMSXGwBwoGwBBVDgODVAuiIFJHBJmAGycCmKXSIZ6yb0HB+UJXlddfuaxzYJNWILuW5jOghym2gcijL1Ow0YAWpBvJQZn-Nem6RVBe7YTswNHsO1LZtQmljQZJm2eQ2LI1u8VnVdNWf3VTUIdFtD3vmb74t+jKstY+jgfymwm2KttXF0FASQgcIYAAKQgfdqvlVwFAQUA-UanHaamdTcleDrifcXqyYaJHgFdqA4AgXcoFZ+UxT4ampgjwaACtfbQJnpbQDlg9T9PM+ztpc85lMFdIrJ+cF4Wpo1oL322qWE7CWXiPl5y5rOl5VZzvhrrQrXe8-f6UFzyKnvlY24tNyijZHk6x95pxBcnjn1818WYFeXI+GEMKIZXxeT5+iiMnNvG-qSlKadt3L7cK6GSvEqAKcCwmlgMAbASNCDBFCJjBqwNC6ThCu1HSFR1D9RAh9EAyM8BXXNqPRWvNMEgJwd3DawUtJgjvhsOoiCwpD29NQ8hd8jYkM3lbD+XMLaRxgO-QGqQ7agwdkVIAA)
