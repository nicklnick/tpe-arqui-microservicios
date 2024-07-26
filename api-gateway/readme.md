
Correr para hot reload en 2 terminales separadas

terminal 1
```
#linux
> ./gradlew build --continuous -xtest
#windows
> gradlew.bat build --continuous -xtest
```


terminal 2

``` 
#linux
> ./gradlew bootrun
#windows
> gradlew.bat bootrun
```