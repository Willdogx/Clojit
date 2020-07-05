Seesaw has built-in support for internationalization (i18n) and generally off-loading an apps text and appearance to a [ResourceBundle](http://download.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html). It relies on Meikel Brandmeyer's [j18n](https://bitbucket.org/kotarak/j18n) library.

[Here's and simple example usage](https://github.com/daveray/seesaw/blob/develop/src/seesaw/examples/j18n.clj) along with [English](https://github.com/daveray/seesaw/blob/develop/src/seesaw/examples/j18n.properties) and [German](https://github.com/daveray/seesaw/blob/develop/src/seesaw/examples/j18n_de.properties) translations.

## Basics
The basic idea is to create one or more properties files called resource bundles. A base file used for the default locale, and then any number of locale-specific specializations. The properties files must be on the classpath. Here's an example set of bundles:

    src/my_app/core.properties
    src/my_app/core_fr.properties
    src/my_app/core_de.properties

_(note that these could also go in a resources folder rather than src)_

So, let's say the title of the main frame will be stored in the `frame-title` property:

    # core.properties
    frame-title=My Application

The French and German locale files would modify title appropriately. Then in `src/my-app/core.clj`:

```clj
(ns my-app.core
  (:use seesaw.core))

(defn -main [& args]
  (-> (frame :title ::frame-title) pack! show!))
```

That's it! Note that the value of the frame's `:title` option is given as a namespace-qualified keyword. It could also have been written more explicitly as `:my-app.core/frame-title`. Seesaw passes the keyword to **j18n** which uses Java's ResourceBundle support to lookup the keyword in the properties file.

## Other Properties
Resource properties can be used in any text options in Seesaw. e.g., `:title`, `:text`, `:name`, `:tip`, etc. Furthermore, fonts, colors, and icons can also be specified. For example:

    my-button.font=ARIAL-BOLD-20
    my-button.icon=path/or/url/to/icon.png
    my-button.foreground=red

## The `:resource` Prefix Option
Several widgets (buttons, labels, menu items, etc) and actions also support the `:resource` option. It is given as a prefix for a set of properties in the resource bundle to be applied to the target object. For example, create a button like this:

```clj
(button :resource :my-app.core/my-button)
```

and the `my-button.text`, `my-button.foreground`, etc properties from `src/my_app/core.properties` (or locale-specific file) will be applied to the button.

## Changing the Default Locale
To change the default locale for testing, set the `user.language` system property. With Leiningen, you can do it like this:

```sh
$ export JVM_OPTS=-Duser.language=de
$ lein run
```
(this sets the locale to German).