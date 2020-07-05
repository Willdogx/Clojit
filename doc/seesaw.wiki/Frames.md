A typical Swing app will stick everything inside an instance of [JFrame] (http://download.oracle.com/javase/6/docs/api/javax/swing/JFrame.html).  A frame is just a top-level window with a border and the usual decorations provided by the OS or window manager. Create a frame in Seesaw with the `(frame)` function:

```clojure
(frame :title "An example", :on-close :exit, :content "Some Content")
```

The `(frame)` function will return a non-visible frame. Use `(seesaw.core/show!)` to make it visible.

`(frame)` takes a number of options (see the code and tests), but a few are pretty important:

<table>
<tr><td><b>Name</b></td><td><b>Notes</b></td></tr>
<tr>
<td>:on-close</td>
<td>sets the default behavior when the frame is closed by the user. Valid values are :exit, :hide, :dispose, and :nothing. Note that :exit will cause the entire JVM to exit when the frame is closed, which may be reasonable for a standalone app, but probably isn't for use in the REPL. The default value is `:hide`.</td>
</tr>
<tr><td>:size</td><td>Initial size of the frame as a `[width :by height]` vector. For example `:size [640 :by 480]`.
<tr>
<td>:content</td>
<td>Sets the content of the frame which can be any widget (with widget coercion applied), but is usually a panel of some sort.</td>
</tr>
</table>

Like with [[Widgets]], the `(seesaw.dev/show-options)` function can be used to explore the options available on a frame.

As mentioned above, use `(show!)` to make a frame visible. Additionally, to automatically size the frame to use the preferred size of its content, use the `(pack!)` function before showing the frame. This will override any `:size` setting you may have set on the frame. YMMV with `pack!`. Here's an idiomatic way of creating, packing and showing a frame:

```clojure
(->
  (frame :title "HI!" ... more options ...)
  pack!
  show!)
```

You can always get back your frame from an event or one of its constituent widgets using the `(seesaw.core/to-root)` function.

Now, go read about [[Widgets]] so you'll have something to stick in the frame.
