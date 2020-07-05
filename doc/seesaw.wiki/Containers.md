There are container creation functions which basically create `JPanel` instances with particular layouts. Here are some examples. Any place that a widget or list of widgets is expected, the widget coercion rules described above apply.

The widgets held by most containers, with the exception of `(border-panel)`, are set with the `:items` option. As noted in [[Widgets]], widget coercion rules apply. In the simplest cases, `:items` is just a list of widgets, but the exact format of the option varies with container type. For example, `(mig-panel)` takes a sequence of widget/constraint pairs.

A `FlowLayout` with some items:

```clojure
(flow-panel
   :align :left
   :hgap 20
   :items ["Label" (action :handler (fn [e] (alert "Hello!")) :name "Button") "Another label"])
```
A `GridLayout` with 2 columns and a titled border:

```clojure
(grid-panel
  :border "Properties"
  :columns 2
  :items ["Name" (text "Frank")
          "Address" (text "123 Main St")])
```

A `BorderLayout` with labels at each position:

```clojure
(border-panel :hgap 10 :vgap 10
   :center "CENTER"
   :north  "NORTH"
   :south  "SOUTH"
   :east   "EAST"
   :west   "WEST")
```

There's also `(seesaw.mig/mig-panel)` which uses [MigLayout](http://www.miglayout.com/), `(vertical-panel)`, `(horizontal-panel)`, etc.

The `:items` option can also be modified with `(config!)` which will have the effect of clearing the container and repopulating it. If you're familiar with Swing, you'll appreciate that the irritating details of this operation are taken care of by Seesaw.

## A Note on GridBagLayout
Seesaw has rudimentary support for `GridBagLayout` in the `seesaw.core/form-panel` function. I don't recommend using this because it's half-baked and not as powerful as using MigLayout (`seesaw.mig`) or JGoodies (`seesaw.forms`). If you feel like you can improve this situation, by all means improve it and submit a pull request :)

As an example, here's the same form using first `MigLayout` and then `GridBagLayout`. See how much simpler mig is? _Example provided by Rémy Mouëza_.

```clojure
;; MigLayout
(mig-panel
  :constraints ["wrap 2"
                "[shrink 0]20px[200, grow, fill]"
                "[shrink 0]5px[]"]
  :items [ ["name:"     ] [(text (or name     ""))]
           ["category:" ] [(text (or category ""))]
           ["date:"     ] [(text (or date     ""))]
           ["comment:"  ] [(text (or comment  ""))]]))

;; GridBagLayout (DON'T DO THIS!)
(let [lw 100, tw 200, wh 25]
  (form-panel
    :border 10
    :items   [[(label :text "Name:"
                      :preferred-size [lw :by wh])
                :gridy 0 :gridx 0 ]
              [(text :text (or name "")
                      :preferred-size [tw :by wh])
                :gridy 0 :gridx 1
                :gridwidth :remainder]

              [(label :text "category:"
                      :preferred-size [lw :by wh])
                :gridy 1 :gridx 0
                :gridwidth 1]
              [(text :text (or category "")
                      :preferred-size [tw :by wh])
                :gridy 1 :gridx 1
                :gridwidth :remainder]

              [(label :text "date:"
                      :preferred-size [lw :by wh])
                :gridy 2 :gridx 0
                :gridwidth 1]
              [(text :text (or date "")
                      :preferred-size [tw :by wh])
                :gridy 2 :gridx 1
                :gridwidth :remainder ]

              [(label :text "comment:"
                      :preferred-size [lw :by wh])
                :gridy 3 :gridx 0
                :gridwidth 1]
              [(text  :text (or comment "")
                      :preferred-size [tw :by wh])
                :gridy 3 :gridx 1
                :gridwidth :remainder]
              ]))
```