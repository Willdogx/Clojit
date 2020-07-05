I've set up a Google Group for Seesaw discussion and questions: [[https://groups.google.com/group/seesaw-clj]]

Also see the API docs here: [[http://daveray.github.com/seesaw/]]

Also, there are a number of blog posts on various Seesaw features here: [[https://web.archive.org/web/20160323104954/http://blog.darevay.com/category/seesaw/]]

and [[Release Notes]].

[Here's a brief tutorial](https://gist.github.com/1441520) that covers some Seesaw basics. It assumes no knowledge of Swing or Java.

## Do I Have To Know Java and Swing? I used Swing once in 1999 and I hated it.

For simple applications, NO. You might have to occasionally consult Javadocs. The doc string for most functions includes a link to the relevant Javadocs as necessary. BUT, Seesaw does it's best to make this unnecessary. Foremost in this effort are the `(seesaw.dev/show-options)` and `(seesaw.dev/show-events)` functions. If you're ever wondering what options a widget takes, or what events it generates, you can ask at the repl. For example, with a label:

```clojure
user=> (use 'seesaw.core)
nil
user=> (use 'seesaw.dev)
nil
user=> (show-options (label))
; ... prints a long list of options along with example values ...
user=> (show-events (label))
; ... prints a long list of supported events ...
```
For complex applications, probably. Seesaw does its best to make the common case easy and everything else possible. Seesaw operates on and returns raw Swing objects so you can always drop down to raw Swing if you need to do something fancy. If you find you're doing this a lot, please ask on the mailing list. I'd love to hear about any holes in Seesaw's API.

## I think Swing looks like !@#$. What can I do about that?
Try out the [Substance/Insubstantial](https://github.com/Insubstantial/insubstantial) skins. There's an example of using Seesaw with Substance [here](https://github.com/daveray/seesaw/tree/develop/examples/substance). It's really pretty easy to add and totally non-invasive. Just add a dependency and a function call or command-line option.

## Usage
See [tests](https://github.com/daveray/seesaw/tree/develop/test/seesaw/test) and [test/seesaw/test/examples](https://github.com/daveray/seesaw/tree/develop/test/seesaw/test/examples). Seriously, there are a lot of tests and they're pretty descriptive of how things work.

Let's create a `JFrame`:

```clojure
(-> (frame :title "Hello" :content "Hi there") pack! show!)
```

This will create a `JFrame` with title "Hello" and a single label "Hi there". The `:content` property expects something that can be turned into a widget and uses it as the content pane of the frame. Any place where a widget is expected, one will be created depending on the argument (see [[Widgets]]). Note that by default a new frame is invisible. The `pack!` function sizes the frame for its content. The `show!` function makes it visible. Same goes for dialogs.

There are many examples. They're all in the [test/seesaw/test/examples](https://github.com/daveray/seesaw/tree/develop/test/seesaw/test/examples) directory. There's a launcher app that will run examples which you can start like this:

    $ lein examples

_Note that Seesaw uses Leiningen 2 now!_

or you can run individual examples like this:

    $ lein run -m seesaw.test.examples.<name-of-example>


To run the tests:

    $ ./lazytest.sh

Hopefully you see a nice wall of green. Also, `./lazytest-watch.sh` runs lazytest in "watch" mode. The tests will be re-run every time you modify a file. Basically all my development is like this:

* Start the autotester and make sure everything's green
* Write the test and watch for red or an exception
* Write the code to make the test turn green again.
* Refactor/cleanup, etc.

One nice thing is that if you have an example (like in `test/seesaw/test/examples`) if you call the main function there, lazytest will basically run the example over and over again. So you get a workflow like:

* Write some of the example
* Save
* The window or whatever pops up automatically
* Play with it, close it, and write some more

Of course, the examples should be tested, but they aren't :)

## Topics
Here are topics covering most areas of Seesaw usage:

* [[Project Setup]] - how to setup a project using Seesaw
* [[Frames]] - How to create top-level frames/windows to contain your app.
* [[Widgets]] - How to create widgets to populate your frames. Also how to integrate custom widget you create or get from 3d parties.
* [[Containers]] - All about container widgets, i.e. UI elements that simply contain other widgets like buttons, labels, etc.
* [[Selectors]] - Using selectors to find widgets in your app
* [[Handling Events]] - Handling widget events
* [[Handling Selection]] - Handling the current selection in a widget
* [[Widget Value]] - The "value" of a widget
* [[Binding]] - Creating widget dataflows, for example hooking the value of a checkbox to the enabled state of another widget.
* [[Actions]] - Defining "actions" that can be reused throughout your app in menus, buttons, etc.
* [[Dialogs]] - Tell the user stuff. Ask the user stuff.
* [[Menus]] - Menu bars, context menus, etc
* [[Colors]], [[Fonts]] - Specifying colors and fonts
* [[Borders]] - Put borders on widgets
* [[Tables]] - Working with tables
* [[Graphics]] - Drawing stuff on the screen and making custom widgets
* [[Applets]] - How to jam Seesaw in an applet
* [[Resource Bundles and i18n]] - Super-easy internationalization support. But it's also generally useful for uni-language apps!!
* [[Window Builder]] - Using the Google Window Builder tool to layout forms

## Native Look and Feel
Call the `(native!)` function early in your program (like before any other Swing or Seesaw function is called) to get a more "native" behavior. This includes correct menu bar placement in OSX, etc.

## A Note on Threading

As noted [here](http://download.oracle.com/javase/6/docs/api/javax/swing/package-summary.html#threading) Swing is single threaded nearly all UI operations should be executed on the Swing UI dispatch thread. To facilitate this, Seesaw includes the `(invoke-now)` and `(invoke-later)` macros. The former executes forms on the UI thread and waits for their completion, while the latter simply schedules the forms for execution sometime in the future.

A typical use for `(invoke-later)` is to get things going in an app:

```clojure
(defn -main [& args]
  (invoke-later
    (show! (frame :title "Hello" :content (button :text "Push me")))))
```