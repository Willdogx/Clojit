## 1.4.5 (January 5, 2015)
It's been a while. This release has several bug fixes accumulated over the last year. Thanks to: @MIvanchev, @mpietrzak, @schmee, @gudmunduregill, @violahs, @ypsilon-takai, @ska2342, @marcliberatore, @jes5199.

## 1.4.4 (October 10, 2013)
* Improve retrieval of available language mappings in rsyntaxttextarea. Thanks to @AdamClements 
* Fix for IllegalArgumentException when getting (config x :size). Thanks to @houghcr
* Add ability to specify column class in table-model. Thanks to @chiaolun
* Handle component tab titles in tab panel selection.
* New `seesaw.core/confirm` function for confirmation dialogs. Thanks to @ummels
* Improved :import option on default-transfer-handler to allow checking of data before accepting import. Thanks to @davesann 
* Support for `:listen` options when constructing a frame
* Support for multiple frame icons. Thanks to @ummels
* Layout manipulation for `card-panel`

## 1.4.3 (March 2, 2013)
* Add `org.clojure/clojure` exclusion for `j18n` (#102)
* Windows scripts for running tests. Thanks to @AtKaaZ
* Fixed bug in button-group doc (#116)
* Fix behavior of `(seesaw.table/value-at)` when row index is out of bounds (#113)
* Fix behavior of `(seesaw.table/update-at!)` when given multiple arguments (#110)
* Fix `add!` for Mig layout
* Moved to lein2 for development
* Upgrade `rsyntaxarea` to 2.0.4.1

## 1.4.2 (July 17, 2012)

* Improved full-screen toggling support. Thanks to @autheredelstein
* Basic clipboard support. See `seesaw.clipboard`
* Added `:layout-orientation` support to listbox
* Fixes for "hidden" table columns being killed by `update-at!` calls
* Support for `:all-files?` option on file chooser. Thanks @kotarak.
* Use `j18n v1.0.1`.
* Support for RSyntax highlighting editor. See `seesaw.rsyntax/text-area`.

## 1.4.1 (April 28, 2012)

* Modified `(listen)` to accept vars (presumably pointing to functions) as handlers in addition to functions. This can make working the repl a little more pleasant because you can redefine handler functions after they've been registered.
* Meta data values on frames were inexplicably stored as weak references causing random disappearance of values and general mayhem. Fixed.
* Fixed bug in `:font` property of styled-text. Issue #81
* Added an example of using the [Substance/Insubstantial](https://github.com/Insubstantial/insubstantial) look and feel(s) with Seesaw. See `examples/substance`. It's standalone because I'm not adding Insubstantial to the Seesaw deps just yet, or wrapping it.

## 1.4.0 (March 5, 2012)

This release is primarily bug fixes and minor API extension driven by the Overtone widget effort.

* Seesaw is now built and tested on [travis-ci] (https://secure.travis-ci.org/#!/daveray/seesaw)
* BREAKING CHANGE: The first argument to `(seesaw.core/paintable)` must now be a class. Passing the name of a widget constructor function no londer works. So instead of `(paintable label ...)`, do `(paintable javax.swing.JLabel ...)` instead. Note below that everything supports the `:paint` option now so `paintable` isn't really needed anymore.
* BREAKING CHANGE: Eliminated `with-widget` which was a failed extensibility experiment. If you want to customize an Swing existing class, just subclass it, instantiate it, and call `config!` on it.
* All widgets created by Seesaw now support the `:paint` option originally only supported by `canvase` and `paintable`.
* full-screen support for frames. See `seesaw.core/toggle-full-screen!` and `seesaw.test.examples.full-screen`.
* Support for undecorated frames. See `seesaw.core/window`.
* Added `seesaw.core/request-focus!` function which correctly gives keyboard focus to a widget.
* `(seesaw.core/paintable)` is now deprecated (also see change below).
* All widgets now support a `:user-data` option at creation time which associates an arbitrary value with the widget. Accessible later with `(seesaw.core/user-data)`.
* Extended bindable to combobox, toggle button and friends
* Extended (seesaw.bind/bind) to return a callable object. When called all registered listeners, etc are backed out. That is, is works like `(listen)` now. Note that the returned object is still a `Bindable`, it's just also callable as a function now.
* Introduced `seesaw.mouse` with some helper functions for dealing with the mouse.
* Extended `:foreground` and `:background` properties of `(seesaw.graphics/style)` to accept Paint objects instead of just colors. This allows use of gradients.
* Support for linear and radial gradients
* Added support for undecorated frames, i.e `JWindow`. See `(seesaw.core/window)`
* Support for read-only `:location-on-screen` property of widgets.
* Fixed occasional weird issues with arity exceptions from `invoke-later` macro.
* Support for hyperlink events in editor pane. Also added an example of doing something with these links. See `test/seesaw/test/examples/editor_pane_hyperlink.clj`.
* Fixed various problems with `styled-text`
* Extended selection support to the active tab of tabbed panels.

## 1.3.0 (December 20, 2011)

This release includes several new features and bug fixes:

* **Interactive Development** New functions, `(seesaw.dev/show-options)` and `(seesaw.dev/show-events)` which can be used at a REPL to query a widget for all supported options or events, respectively. This is part of an effort to reduce the level of Swing knowledge needed for Seesaw. See [here](https://gist.github.com/1450241) for example output.
* **Key Mapping** Implemented support for key mapping. See `(seesaw.keymap/map-key)`.
* **Value Semantics** Implemented widget value semantics inspired heavily by the Clarity work of Stathis Sedaris. See `(seesaw.core.value)` and `(seesaw.core.value!)`. This is a more convenient and sometimes more powerful unification of the existing `config`, `selection`, and `text` functions.
  * Includes support for all widgets besides tables and trees
  * Includes basic binding support so you can map the value of an atom (or whatever binding source) into the value of a widget
* **Improved Interop** `config` and friends can now be invoked on any Swing widget, even those not created with a Seesaw widget constructor.
* **Example Launcher** Implemented a single launcher for the Seesaw examples. Just run `lein run -m seesaw.test.examples.launcher` and you'll get a list of all available examples.
* Added an experimental auto-scrolling log window in `(seesaw.widgets.log-window)`. This is generally useful, but mostly an experiment is how painful custom widget development is in Seesaw vs. raw Java.
* Added `(seesaw.bind/filter)`
* Added `:layout` option. Gets/sets widget layout manager.
* The default frame size is now 0x0 rather than 100x100.
* Integrated Vladimir Matveev's (dpx-infinity) widget creation macro `(seesaw.core/with-widgets)` from issue #68.
* Fixed bug in `mig-panel` that broke most common options
* Fixed a number of bugs in i18n resources when used with actions.
* Fixed missing `bind` support for spinner and spinner models.
* BREAKING CHANGE: Renamed `(seesaw.invoke/signaller)` to `(seesaw.invoke/signaller*)` and added helper macro.

## 1.2.2 (December 4, 2011)

* Basic SwingX support in [seesaw.swingx](https://github.com/daveray/seesaw/blob/develop/src/seesaw/swingx.clj). This is not comprehensive, but covers the biggies. Let me know if there's something missing that deserves wrapping. A demo/example of some of the features is [here](https://github.com/daveray/seesaw/blob/develop/test/seesaw/test/examples/swingx.clj)
* All widget constructor functions now have publicly visible partner option maps. For example, `label` and `label-options`. This allows them to be reused as well as inspected. The whole `with-widget` thing just isn't working out.
* Added a simple exception reporter for debugging. Call `(seesaw.dev/debug!)` to initialize. After that, any unhandled exceptions in the UI thread will pop up a window with info, rather than possibly being lost or not noticed in the console.
* Support for firing events from `(seesaw.tree/simple-tree-model)`. Thanks to [Stuart Campbell](https://github.com/harto) for this contribution.
* `:border` option can now take an i18n keyword to create a titled border.
* Added `seesaw.bind/b-do`, which is a binding equivalent of `clojure.core/do`. Execute arbitrary code when triggered by a change in a bound value.
* `:selection` event wasn't implemented on `slider`. Fixed.
* Major refactoring and cleanup of event handling code. No API or functional change though.

## 1.2.1 (October 25, 2011)

* Made sure that all tests and examples work on Clojure 1.3
* Fixed [issue 61](https://github.com/daveray/seesaw/issues/61), a Clojure 1.3 compatibility issue
* Spinner support. See [test/seesaw/test/examples/spinner.clj](https://github.com/daveray/seesaw/blob/develop/test/seesaw/test/examples/spinner.clj)
* Added `group-by-id` to easily grab several widgets in a hierarchy. See doc and notes in [[Selectors]].
* Added `invoke-soon` which runs code immediately if already on the swing thread, otherwise sends it over with `invoke-later.
* Support for binding to agents, i.e. as the value of the agent changes, route that value through a binding chain
* Support for marshalling values to the swing thread in a binding chain. `bind/notify-later`, `bind/notify-now`, and `notify-soon`.
* Added `:mnemonic` support for all buttons, not just actions. This includes all buttons, menu items, etc.
* Added `(all-frames)` function that nominally returns all frames in the JVM. Only useful for debugging and live editing. See doc.
* Support for creating custom-drawn borders `(custom-border)`.
* Convenience function `(select-with)` which acts a more powerful version of `(partial (to-widget x) select)`.


## 1.2.0 (September 8, 2011)

* Support for resource bundles and i18n. [[Resource bundles and i18n]]. Special thanks to Meikel Brandmeyer for [j18n](https://bitbucket.org/kotarak/j18n) and Seesaw integration help.
* Basic drag-n-drop support. See [dnd.clj](https://github.com/daveray/seesaw/blob/develop/src/seesaw/dnd.clj) and [the example](https://github.com/daveray/seesaw/blob/develop/src/seesaw/examples/dnd.clj).
* Added `(bind/funnel)` support. Multiplex multiple binding chains together into a vector.
* Added `(bind/b-swap!)` support. Friendlier use of atoms as in binding chains.
* `(seesaw.chooser/choose-color)`
* Support for custom file filters in `(seesaw.chooser/choose-file)`. Thanks to [Odyssomay](https://github.com/odyssomay)

## 1.1.0 (August 3, 2011)

* Implemented `(config)` function which is dual of `(config!)`, i.e. it can read options from widgets. For example, `(config my-panel :items)` will return a sequence of the items in the given panel. The implementation isn't complete for every option on every widget type, but the biggies are covered. If you hit one that's missing, let me know.
* Selection support, `(selection)`, `(selection!)` and `(listen :selection)`, for text widgets.
* Support for programmatic scrolling, e.g. `(scroll! table :to :end)`. See `seesaw.examples.scroll`.
* Passing a "slurpable" value (URL, File, Reader, etc) to the `:text` option of any widget will now slurp that value into the text value. So, you can point text areas at files or URLs or whatever to populate them.
* Minor graphics improvements
* A paint app example, seesaw.examples.scribble
* Improved arg checking so maybe debugging is a little less painful

Breaking Changes:

* The `ToWidget` protocol was broken into two pieces, `ToWidget` and `MakeWidget`. This should only affect users that implemented `ToWidget`.
* Originally any non-string values passed to the `:text` option were automatically passed through `(str)`. This is no longer the case. If you want a non-string value in a label or whatever, convert it manually.
* `(id-of)` now *always* returns a keyword regardless of whether a string or keyword was given as the `:id` option on widget construction. This makes for more readable, consistent code.

## 1.0.10 (July 21, 2011)

* Fix for issue 49, crash when removing last row from table
* text selection support
* CaretListener support
* :divider-size, :resize-weight, and :one-touch-expandable? for splitter
* Support for corners, row headers and column headers and scrollables
* :selection-mode support of lists and trees
* TreeExpansionListener and TreeWillExpandListener support
* Eliminated almost all reflection

## 1.0.9 (July 16, 2011)

Well, that's embarrassing:

* Fixed pretty major bug in `(progress-bar :value)` and a couple broken examples.
* Another bind example

## 1.0.8 (July 16, 2011)

Lots of outside help on this one:

* Support for JTextPane `(seesaw.core/styled-text)`. Contributed by [Odyssomay] (https://github.com/odyssomay)
* Support for CardLayout `(seesaw.core/card-panel)`. Contributed by [Odyssomay] (https://github.com/odyssomay)
* Widget data flow binding. See [here] (http://blog.darevay.com/2011/07/seesaw-widget-binding/)
* Started working on removing all the reflection
* Added :selection-mode support for `(seesaw.chooser/choose-file)`. Conributed by [frericksm] (https://github.com/frericksm)
* Support for [JGoodies forms] (http://www.jgoodies.com/freeware/forms/), `(seesaw.forms)`. Contributed by [Meikel Brandmeyer] (http://kotka.de/)
* Support for CSS color names and 3-digit hex colors
* Row keys not present in a table's column spec are still remembered and returned by `(seesaw.table/value-at)`. This provides an easy mechanism for associating hidden data with rows.

Breaking changes:

* There was support for passing an atom to an option and syncing the value of that atom with the property on the widget. This has been removed in favor of the binding framework in `(seesaw.bind)`.

## 1.0.7 (June 20, 2011)

* Much improved API `(paintable)` for custom painting on widgets
* Cursor support, e.g. `(flow-panel :cursor :hand)` to use the "hand" cursor on a panel.
* Breaking change: Mig support is now in its own namespace, not in core. So you'll have to use/require seesaw.mig from now on.
* Proportional `:divider-location` now works on split panels, use a double or ratio.
* Other cleanup and tests.
