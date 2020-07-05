Seesaw provides a unified event registration abstraction that eliminates the need to know about lots of different "listener" interfaces and registration methods. All events are identified by a keyword and handled by a client-provided handler function.

Event handler functions are single-argument functions that take an event object whose type depends on the event being fired, e.g. `MouseEvent`. Event handlers are installed with the `(listen)` function. Its first argument is a widget, or sequence of widgets, and then one or more event specs of the form event-name/function. For the most part, the name of the event is the name of a Swing event listener method. Here's an example that listens for some mouse events, assuming that `p` is bound to a widget:

```clojure
(listen p
  :mouse-clicked (fn [e] ... do something ...)
  :mouse-entered (fn [e] ... do something ...)
  :mouse-exited  (fn [e] ... do something ...))
```

Note that these same arguments can be given to the `:listen` property when the widget is constructed:

```clojure
(canvas ... :listen [:mouse-clicked (fn [e] ...) ...])
```

`(listen)` returns a function which, when called, will remove all listeners installed by the `(listen)` call. There is no "remove-listener" function.

_BUT HTF DO I KNOW WHAT EVENTS THERE ARE?!?_

You might be asking yourself this question. As with widget options the `(seesaw.dev/show-events)` function will give details of the events supported by a widget. So in the repl:

```clojure
user=> (use 'seesaw.core)
nil
user=> (use 'seesaw.dev)
nil
user=> (show-events (label))
:component [java.awt.event.ComponentListener]
  :component-hidden
  :component-moved
  :component-resized
  :component-shown
:focus [java.awt.event.FocusListener]
  :focus-gained
  :focus-lost
:key [java.awt.event.KeyListener]
  :key-pressed
  :key-released
  :key-typed
:mouse [java.awt.event.MouseListener]
  :mouse-clicked
  :mouse-entered
  :mouse-exited
  :mouse-pressed
  :mouse-released
:mouse-motion [java.awt.event.MouseMotionListener]
  :mouse-dragged
  :mouse-moved
:mouse-wheel [java.awt.event.MouseWheelListener]
  :mouse-wheel-moved
:property-change [java.beans.PropertyChangeListener]
  :property-change
```
Note that events are grouped. Registering for just `:mouse` is equivalent to registering the same handler for all the events listed under `:mouse`.

See `seesaw.events/listen` for more details.


