Seesaw has a bunch of functions for constructing "widgets". Widgets are user interface components that are displayed on the screen. They're called widgets for no good reason other than the name is shorter than "component" and it might help people forget that Swing lives under Seesaw :)

That said, a Seesaw widget is just a Swing component. There is no wrapping involved. Seesaw functions that take a "widget" as an argument can take any old Swing component as well.

### Widget Construction with MakeWidget
_Note that `(seesaw.core/make-widget)` is usually called automatically by Seesaw when appropriate so you rarely need to call it yourself._

Seesaw includes a protocol (`MakeWidget`) for constructing widgets from arbitrary objects. This is usually invoked on the values of the `:items` and other "member" options for containers. It allows for a hopefully more natural UI construction process. For example, to make a label (`(label)` in Seesaw, or just `JLabel` in Swing), just pass a String:

```clj
(vertical-panel :items ["This" "is" "a" "vertical" "stack of" "JLabels"])
```

The following table outlines the widget coercions that are provided in Seesaw out of the box. If you feel like it, you can provide coercions for your own types. See the [make-widget] (https://github.com/daveray/seesaw/blob/master/src/seesaw/examples/make_widget.clj) example for details. See `(seesaw.core/make-widget)`.

Without further ado, here are the coercions:

<table>
  <tr><td>Input</td><td>Result</td></tr>
  <tr><td>java.awt.Component</td><td>return argument unchanged</td></tr>
  <tr><td>java.util.EventObject (for example in an event handler)</td><td>return the event source</td></tr>
  <tr><td>java.awt.Dimension</td><td>return Box/createRigidArea</td></tr>
  <tr><td>java.swing.Action</td><td>return a button using the action</td></tr>
  <tr><td>:fill-h</td><td>Box/createHorizontalGlue - fill extra horizontal space</td></tr>
  <tr><td>:fill-v</td><td>Box/createVerticalGlue - fill extra vertical space </td></tr>
  <tr><td>[:fill-h n], e.g. <code>[:fill-h 99]<code></td><td>Box/createHorizontalStrut with width n</td></tr>
  <tr><td>[:fill-v n]</td><td>Box/createVerticalStrut with height n</td></tr>
  <tr><td>[width :by height]</td><td>create rigid area with given dimensions</td></tr>
  <tr><td>A URL</td><td>a label with the image located at the url</td></tr>
  <tr><td>A non-url string</td><td>a label with the given text</td></tr>
</table>

Most of Seesaw's container functions (`flow-panel`, `grid-panel`, etc) take an `:items` property which is a list of these widget-able values. For example:

```clj
(let [choose (fn [e] (alert "I should open a file chooser"))]
  (flow-panel
    :items ["File"                                 [:fill-h 5]
            (text (System/getProperty "user.dir")) [:fill-h 5]
            (action :handler choose :name "...")]))
```

creates a panel with a "File" label, a text entry field initialized to the current working directory and a button that doesn't do much. Each component is separated by 5 pixel padding.

New coercions can be added by extending the `MakeWidget` protocol. See the `make-widget` example.

See [[Containers]] for more info on container widgets.

### Widget Conversion with ToWidget
_Note that `(seesaw.core/to-widget)` is usually called automatically by Seesaw when appropriate so you rarely need to call it yourself._

Additionally, there is a protocol, `(ToWidget)`, which performs conversion of objects to existing widgets, i.e. no new widgets are constructed. This is used in most cases where you have something that indirectly refers to a widget (like an event object) and you'd like to pass it to widget manipulation functions as if it were the widget itself. Out of the box, the following conversions are provided:

<table>
  <tr><td>Input</td><td>Result</td></tr>
  <tr><td>nil</td><td>nil</td>
  <tr><td>java.awt.Component</td><td>return argument unchanged</td></tr>
  <tr><td>java.util.EventObject (for example in an event handler)</td><td>return the event source</td></tr>
</table>

New conversions can be added by extending the `ToWidget` protocol.

### Default Options
_See [[Resource bundles and i18n]] for details on localizing options_

All of Seesaw's widget creation functions (`label`, `text`, `horizontal-panel`, etc) take a list of options expressed as keyword/value pairs passed to the function. There used to be a little table here describing some of the common options, but it's hard to keep up-to-date and now there's a better way to get this info with functions from `seesaw.dev`. At the repl:
```clj
user=> (use 'seesaw.core)
nil
user=> (use 'seesaw.dev)
nil
user=> (show-options (label))
javax.swing.JLabel
                    Option  Notes/Examples
--------------------------  --------------
              :background  :aliceblue
                            "#f00"
                            "#FF0000"
                            (seesaw.color/color 255 0 0 0 224)
                  :border  5
                            "Border Title"
                            [5 "Compound" 10]
                            See (seesaw.border/*)
                  :bounds  :preferred
                            [x y w h]
                            Use :* to leave component unchanged:
                            [x :* :* h]

... and so on ...
```

`show-options` combined with the function's docstring is a great way to explore what's available for each widget type..

After the widget has been created, most options can also be manipulated with the `(seesaw.core/config!)` function which applies them to an existing widget or widgets:

```clj
(config! (select root [:#my-widget]) :enabled? false :text "I'm disabled.")
```

`(config!)` can be applied to a single widget, or list of widgets, or things that can be turned into widgets.
 The current value of an option can be retrieved with `(seesaw.core/config)`:

```clj
(config (select root [:#my-widget]) :enabled?)
=> false
```

Note that implementation for all options is incomplete. If you ask for an option that hasn't been implemented yet, you'll get an exception about "unsuppored option X". File an issue and it will be fixed shortly.

Also, `(config)` usually returns the low-level java value for an option. For example, although you can set `:foreground` with a string color name, when you retrieve it, you'll get a `java.awt.Color` object back.

### Scrolling

Use the `(scrollable)` function to make a widget scrollable:

```clj
(scrollable (text :multi-line? true))
```

Note that this returns a `JScrollPane` object, not the widget passed in.

To programatically scroll a widget which has been wrapped with `(scrollable)` a unified set of scroll functions are provided in the `(seesaw.scroll)` namespace. They cover common cases that are awkward in Swing, e.g. scroll to top, bottom, cell in a table, line in a text area, etc.

### Splitters

Use the `(top-bottom-split)` or `(left-right-split)` functions to make a splitter each takes two widget args:

```clj
(top-bottom-split "Top" "Bottom")
(left-right-split "Top" "Bottom")
```

# Integrating Custom Widgets
_Note that though sub-classing is very common in Swing tutorials, it's actually often not as necessary as you'd think._

If you're using custom widgets, either sub-classes of your own, or from a library of custom widgets, you can still take advantage of Seesaw. As noted above, there is nothing special about widgets created by Seesaw. So, if you have an instance of a widget that's a sub-class of some Swing class (java.swing.JComponent and below), you can use `config!`, `listen`, etc, etc on it. For example:

```clj
(let [my-list (org.jdesktop.swingx.JXList.)]
  (config! my-list :id :my-jxlist ... other listbox options ...))
```

_note that Seesaw has native Swingx support in the `seesaw.swingx` namespace.

