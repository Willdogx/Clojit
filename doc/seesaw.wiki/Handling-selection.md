The `(selection)` and `(selection!)` functions handle the details of selection management for listboxes, checkboxes, toggle buttons, combo boxes, tables, etc. To get the current selection, just pass a widget (or something convertible to a widget) to `(selection)`. It will always return the selected value, or `nil` if there is no selection:

```clojure
(if-let [s (selection my-widget)]
  (println "Current selection is " s)
  (println "No selection"))
```

For multi-selection, `(selection)` takes an options map:

```clojure
(doseq [s (selection my-list {:multi? true})]
  (println "Selected: " s))
```

Note that you can apply `(selection)` to event objects as well:

```clojure
(listen (select [:#my-list]) :selection
  (fn [e]
   (println "Current selection of my-list is: " (selection e))))
```

The `(selection!)` function will set the current selection:

```clojure
(let [my-list (listbox :model ["jim" "bob" "al"])]
  (selection! my-list "bob"))
```

Pass `nil` to clear the selection. Like with `(selection)`, use the `multi?` option to interpret the new selection value as a list of values to select.

## Text Selection
The selection of text widgets is also handled by the `(selection)` and `(selection!)` functions. As above, if the selection is empty, `nil` is returned. Otherwise, a vector with the start and end positions of the selection range will be returned.

_Selection is sometimes, but not always, the same as the "value" of a widget. See [[Widget Value]] for more info._

