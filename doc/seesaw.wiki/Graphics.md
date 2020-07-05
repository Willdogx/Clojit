Seesaw has some basic support for Java2D drawing, using the `(canvas)` function to create a paintable panel. See `src/seesaw/graphics.clj` and `test/seesaw/test/examples/canvas.clj`.

`test/seesaw/test/examples/scribble.clj` show how to do mouse interaction with a canvas.

Note that the `(seesaw.core/paintable)` macro can also be used if you'd like to paint custom graphics over an existing widget type. There's an example in `test/seesaw/test/examples/xyz_panel.clj`. See also [this post](http://blog.darevay.com/2011/06/painting-widgets-with-seesaw/) for more info on custom painted widgets.

