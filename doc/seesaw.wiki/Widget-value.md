_Note that the idea here is based on Stathis Sideris' work on [Clarity](https://github.com/stathissideris/clarity)_

The value of a widget is similar to, but not exactly the same as widget selection (see [[Handling Selection]]). For example, the selection of a text box is whatever range of characters the user has selected, while the current value is the text it contains.

Anyway, Seesaw provides the functions `(seesaw.core/value)` and `(seesaw.core/value!)` to retrieve and set the value of a widget respectively. The nice thing about value is that it's defined for composite widgets (containers) as well. So if you have a form with a bunch of widgets and ask for its value, you'll get back a map of all the sub-widgets' values, keyed by `:id`. For example:

```clj
user=> (def p (grid-panel :columns 2
                 :items ["First Name" (text :id :first-name :text "Harry")
                         "Last Name"  (text :id :last-name :text  "Truman")]))
user=> (value p)
{ :first-name "Harry" :last-name "Truman" }
```

This is very useful for dialog forms.

The `(seesaw.core/value!)` function works similarly but _sets_ the value of the widget or widgets.

