Seesaw supports general CSS-style selectors for finding and styling widgets in an app. The `(seesaw.core/select)` function supports locating a widget by `:id` as set at creation time:

```clj
(button :id :the-button :text "Push me")

; ... later ...

(listen (select root [:#the-button])
        :action (fn [e] 
                  ; ... do something ...
                  ))
```

Note that the first argument to `(select)` is always the root of the widget hierarchy to search from and the second argument is always a vector containing the selector. I wish the root wasn't necessary, but not requiring it makes it very difficult to support multiple instances of the same frame in one app.

If `(select)` is given a simple id selector, like `[:#my-id]` it will always return a single widget for convenience. Otherwise, the return value is always a lazy sequence of widgets.

The "all" selector is also supported which will match everything in a sub-tree including the root. For example to disable an entire sub-tree:

```clj
(config! (select my-panel [:*]) :enabled? false)
```

Selectors follow the conventions established by [Enlive] (https://github.com/cgrand/enlive) with the following modifications:

* A "tag" selector, e.g. `[:JLabel]` matches the literal Java class name (without package) of the widget type.
* A full Java class can be matched with a selector like `[:<javax.swing.JLabel>]`. This will also match sub-classes of JLabel.
* An exact Java class can be matched by including an exclamation mark in a Java class selector: `[:<javax.swing.JLabel!>]`. This will not match sub-classes of JLabel.

See also [A Brief Note on Seesaw Selectors](http://blog.darevay.com/2011/06/a-brief-note-on-seesaw-selectors/) and the documentation for `(seesaw.core/select)`.

## group-by-id
The `(seesaw.core/group-by-id)` function is useful if you have several widgets in a form that you'd like to quickly grab and work with. Say you have widgets `:name`, `:address`, and `:phone` buried in some form. You could manually `(select)` for each one as described above, or use `(group-by-id)` and map destructuring:

```clj
(let [{:keys [name address phone]} (group-by-id root)]
  (... do something with name, address, and phone widgets ...))
```

`(group-by-id)` returns a map of widgets, keyed by their id. Widgets with no id are ignored. See doc for `(group-by-id)` for more info.
