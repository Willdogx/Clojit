Fonts can be specified in the following ways (using the `:font` property as an example):

```clojure
:font "ARIAL-BOLD-18"                             (Swing-style font spec string)
:font {:name "ARIAL" :style :bold :size 18}       (using a properties hash)
:font (font :name "ARIAL" :style :bold :size 18)  (using properties with font function)
```

So, you could make a monospaced text area like this:

```clojure
(text :text "Type some code here" 
      :multi-line? true 
      :font {:name :monospaced :size 15})
```

Of course, a raw `Font` object can also be used.
