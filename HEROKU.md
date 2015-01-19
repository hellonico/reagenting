## Deploy to Heroku

To make Rente run on Heroku, you need to let Leiningen on Heroku use the "package" build task.

To make this work you need to point Heroku to this build pack:
https://github.com/heroku/heroku-buildpack-clojure

To do this, and point Leiningen on Heroku to the "package" target, add the following two config variables to Heroku by running this command:

```
heroku config:add BUILDPACK_URL=https://github.com/heroku/heroku-buildpack-clojure LEIN_BUILD_TASK=package
```

Deploy to Heroku as usual, and enjoy.
