([original version](https://web.archive.org/web/20160319131533/http://blog.darevay.com/2011/07/seesaw-widget-binding/))

# Seesaw Widget Binding

*July 11th, 2011*

Lately I’ve been working on the problem of binding widgets and widgets (and data) together in [Seesaw](https://github.com/daveray/seesaw). I think it’s in a state to throw it out there and solicit feedback from anyone who’s interested. This work is similar to the arrow work done in [Hafni](https://github.com/odyssomay/Hafni), but takes a somewhat different approach and builds on top of the pretty extensive infrastructure that’s already in place in Seesaw.

The full source the for example shown here is [here](https://github.com/daveray/seesaw/blob/develop/src/seesaw/examples/bind.clj).

## Motivation

Let’s say you were creating a search box where the user can enter a regular expression. You’d like to give some feedback whether the expression is valid or not, say by turning the text box red and updating a status label. Additionally, you’d like an “enabled” checkbox which enables or disables the search. Yeah, it’s contrived, deal with it :)

[bad bind example img]

So, the usual approach would be something like this:

* Set up the widgets
* Add a selection change listener to the check box. When it fires, check its value and set the enablement of the search box
* Add a document change listener to the text box. When it fires, parse the regex, set the color of the text box and update the status label appropriately

Not horrible, but kind of a hassle. If you turn your head and squint, you can kind of see a couple data flows going on here. First, the boolean value of the checkbox flows to the boolean enablement of the searchbox. Second, the text of the search box is transformed into a regex and then into a color (or status label) depending on success of failure. That color is then fed into the background color of the search box.

[good bind example img]

Seesaw’s **binding** (from Java Beans Binding, better names welcome) framework let’s you express this dataflow more directly.

## Solution

So, the framework’s workhorse function is `(seesaw.bind/bind)`. It takes a list of “Bindables” (see below) and hooks their values together into a chain. When a value at the start of a chain changes, it’s passed through the rest of the chain. `bind` returns a composite `Bindable` which can be composed into other chains.

So, for example, we can bind the value of a text box to an atom:

```clojure
(let [txt (text)
      a   (atom)]
(bind txt a))
```
 
when the user types in the text box, the atom’s value changes to match it. There are several bindables already supported:

* Text boxes, labels, sliders, atoms, etc are all bindable in the way you (or at least I) would expect.
* `(property widget property-name)` – bind to a property of a widget
* `(selection widget)` – bind to the current selection of a widget
* `(transform f)` – transform a value with a function and pass it along
* `(some pred)` – like (clojure.core/some) only pass along truthy values returned by a predicate.
* `(tee ... bindables ...)` – split (demux) the chain into several independent chains.

Now back to our example. Here’s the annotated code for binding the search pattern logic. Here `pattern` is a text box, and `status` is a label:

```clojure
(b/bind 
  ; As the text of the textbox changes ...
  pattern
  ; Convert it to a regex, or nil if it's invalid
  (b/transform #(try (re-pattern %) (catch Exception e nil)))
  ; Now tee into two chains ...
  (b/tee
    ; The first path sets the color of the text box depending
    ; on whether the regex was valid
    (b/bind 
      (b/transform #(if % "white" "lightcoral")) 
      (b/property pattern :background))
    ; The second path sets the text of the status label
    (b/bind 
      (b/transform #(if % "Ready" "Invalid regex")) 
      status)))
```

Kinda cool? Similarly, we can hook a checkbox to the textbox enablement:

```clojure
(b/bind (b/selection (select f [:#enable]))
        (b/property  (select f [:#search]) :enabled?))
```

In this example, we’re using Seesaw selectors to find the checkbox (`:#enable`) and search text box (`:#search`).

[bind disabled img]

## Conclusion

I think this covers a lot of the tedious UI state management tasks that come up. Although atom binding is supported, it doesn’t seem like something you want to be doing often. It’s just doesn’t seem Clojure-y to me. Maybe a bindable atom that works more like [`(clojure.core/swap!)`](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/swap!) than [`(clojure.core/reset!)`](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/reset!) would be more appealing.

I’m looking for feedback, so if you find this interesting, useful, or offensive, please let me know!