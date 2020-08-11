## Requirements
JRE >= 1.8
Can be downloaded from https://adoptopenjdk.net/

## Build

The following command compiles the jvm & javascript part and creates an electron package for darwin
```
./gradlew electronPackage
```

## Run

The code is simple : It searches for all the git repositories on your local file system (and hence, when you run, it may ask you access permissions)
The list of directories to search can be modified in `src/jvmMain/App.kt`

* Manually
  ```
  cd electron_out
  ./start
  ```

  This should spawn the jvm, wait for 5 seconds and print the git repositories found

* Via Npm
  ```
  cd electron_out
  npm start
  ```

  This will spawn the jvm; wait for 2.5seconds and then start a browser which loads a blank page with a dark grey background.\\
  Meanwhile, the java process prints the list of git repositories found (after waiting 5 seconds) and exits

* Via OSX app
  
  After the first build, an OSX app is created in `electron_out/Basic-Packaging-darwin-x64`.\\
  When I tried it out, this doesn't work.\\
  I guess it needs expert attention :)

## TODOs
[ Electron ](https://electronjs.org/) was chosen since it caters to in-place updates and runs the given javascript in chromium.

An alternative would be to go for pure Java and render the javascript via JavaFX Webview. This approach may be simpler, but I'm not sure how in-place updates would work.
