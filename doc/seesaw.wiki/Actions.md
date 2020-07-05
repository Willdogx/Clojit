It's typical in Swing apps to use actions for [[Menus]], buttons, etc rather than manually installing handlers. An action needs a handler function and some properties. Here's an example of creating an action and adding it to a toolbar:

```clojure
(use 'seesaw.core)
(let [open-action (action
                    :handler (fn [e] (alert "I should open a new something."))
                    :name "Open ..."
                    :key  "menu O"
                    :tip  "Open a new something something.")
      exit-action (action
                    :handler (fn [e] (.dispose (to-frame e)))
                    :name "Exit"
                    :tip  "Close this window")]
  (frame
    :title "Toolbar action test"
    :content (border-panel
                :north (toolbar :items [open-action exit-action])
                :center "Insert content here")))
```

_Note that actions can be localized. See [[Resource bundles and i18n]]._

`(action)` also supports an `:icon` property which can be a `javax.swing.Icon`, a `java.net.URL` or something that looks like a file or URL after `(str)` has been applied to it. See `seesaw/action.clj` for an accurate list of options.

The `:key` property takes an argument which is passed to `seesaw.keystroke/keystroke`. This sets the accelerator key for the action.

Like widgets, actions can be modified with the `(config!)` function:

```clojure
(def a (action :name "Fire Missiles" :enabled? false))

(config! a :name "Fire Missiles!!!" :enabled? true :handler (fn [e] (println "FIRE")))
```
