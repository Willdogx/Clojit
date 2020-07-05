Colors can be specified in the following ways (using the `:foreground` property as an example):

```clojure
:foreground java.awt.Color/BLACK      (a raw color object)
:foreground (color 255 255 224)       (RGB bytes)
:foreground (color 255 255 224 128)   (RGBA bytes)
:foreground "#FFEEDD"                 (hex color string or keyword)
:foreground "#FED"                    ("short" CSS-style hex color string or keyword)
:foreground "aliceblue"               (CSS-style named color string or keyword)
:foreground (color "#FFEEDD" 128)     (hex color string (or name) + alpha)
```

Here's a label with blue text and a red background:

```clojure
(label :text "Hideous"
       :opaque? true
       :foreground (color 0 0 255)
       :background "#FF0000")
```
Of course, a raw `Color` object can also be used.