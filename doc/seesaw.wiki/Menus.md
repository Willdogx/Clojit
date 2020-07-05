Here's how you can make a menu bar full of menus:

```clojure
(frame :title "MENUS!"
  :menubar 
    (menubar :items 
      [(menu :text "File" :items [new-action open-action save-action exit-action])
       (menu :text "Edit" :items [copy-action paste-action])]))
```

_Note that calling the `(native!)` function at startup will ensure that the menu bar goes in the right spot on OSX._

`(menubar)` has a list of `(menus)`, while each `(menu)` has text and a list of actions, or items. Note that in addition to using Actions as menu items, you can also use `(menu-item)`, `(checkbox-menu-item)`, and `(radio-menu-item)`, each of which has the exact same behavior (and options) as a button.

Popup menus (context/right-click) can be easily added to a widget with the `:popup` property.  Just give it a single-argument function that returns a list of actions or menu items. The function will be called each time the menu needs to be displayed. For example,

```clojure
(listbox :popup (fn [e] [action1 action2 ...]))
```

Seesaw takes care of registering the mouse handler and showing the popup at the right time depending on the platform. See `src/seesaw/examples/popup.clj`.
