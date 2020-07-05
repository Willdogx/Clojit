The `(input)` function asks the user for some input. The simplest case just gets a string:

```clojure
(input "Bang the keyboard like a monkey")
```

... but there are many options including choosing from a set of Clojure objects:

```clojure
(input "Pick a city"
    :choices [{ :name "New York"   :population 8000000 }
              { :name "Ann Arbor"  :population 100000 }
              { :name "Twin Peaks" :population 5201 }]
    :to-string :name)
```
Please see the doc for `(seesaw.core/input)` for all the options.

To tell the user something, use `(seesaw.core/alert)`:

```clojure
(alert "Something terrible has happened")
```

Please see the doc for `(seesaw.core/alert)` for all the options.

Note that both functions take an optional first argument, passed through `(to-widget)` which is used as the parent component for the dialog. This ensures that the dialog is positioned correctly.

There's also a more general function for modal or non-modal dialogs, `(seesaw.core/dialog)`. There are also various choosers in `seesaw.chooser`.
