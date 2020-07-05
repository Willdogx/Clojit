Widget borders can be passed to the `:border` property to create many border styles:

```clojure
:border "Title"         (creates a plain title border)
:border 10              (creates an empty 10 pixel border)
:border [10 "Title" 5]  (compound empty/title/empty border)
:border (line-border :thickness 3 :color "#FF0000")     (red, 3 pixel border)
:border (line-border :top 5 :left 5)     (5 pixel black border on top and left)
```
Of course, a raw `Border` object can also be used.
