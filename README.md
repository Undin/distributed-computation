## Build

Run `./gradlew build`. It requires local rabbitmq to pass tests successfully.

## Run
Application supports two different modes: __server__ and __worker__. 
Modes are implemented with Spring profiles with corresponding names: `server` and `worker`.
Profiles can be set through `--spring.profiles.active` command line argument or property in configuration file.
Application __can be__ launched in server and worker modes at same time.
  
### Server
Run `java -jar build/libs/solanteq-test.jar --data=path-to-datafile --f1=path-to-f1-script.groovy --f2=path-to-f2-script.groovy`
to start application.

### Worker
Run `java -jar build/libs/solanteq-test.jar` to start new worker instance.

## Configuration

```yaml
rabbitmq:
  task-queue:       # Name of task queue. Default value is 'tasks'
  result-queue:     # Name of result queue. Default value is 'results'
  concurrency:      # Number of consumers for message queue events. Default value is 1

server:
  task-size:        # Maximum number of lines of data file one task contains. Default value is 1000
```

## Groovy scripts
Each groovy script represents binary integer function with integer return value 
where x is the first argument and y is the second one. For example: 
```groovy
x + y
```
or
```groovy
fib(x) - fib(y)

def fib(int x) {
    if (0 == x || 1 == x) x else fib(x - 1) + fib(x - 2)
}

```
It assumes that `f2` script is associative function.
