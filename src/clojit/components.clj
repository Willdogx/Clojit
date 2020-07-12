(ns clojit.components
  (:import [javax.swing JList JMenuItem JPopupMenu BorderFactory JScrollPane])
  (:import [javax.swing.border EtchedBorder])
  (:import [java.awt.event ActionListener MouseAdapter]))

(defmacro when-let*
  "when-let with multiple bindings"
  [bindings & body]
  `(let ~bindings
     (if (and ~@(take-nth 2 bindings))
       (do ~@body))))

(defn jlist
  "Returns a JList with an optional popupmenu triggered with right-click.
   The popup-menu should be a map
   {:name name shown on menu
    :on-click fn called when clicking the menu item. the list is passed as an argument.}"
  [coll & {:keys [popup-menu-items]}]
  (let [list (JList. (into-array coll))]
    (when-let* [popup-menu-items popup-menu-items
                popup-menu (JPopupMenu.)]
      (doseq [menu-item popup-menu-items]
        (.add popup-menu (doto (JMenuItem. (:name menu-item))
                           (.addActionListener (reify ActionListener
                                                 (actionPerformed [this _]
                                                   ((:on-click menu-item) list)))))))
      (.addMouseListener list (proxy [MouseAdapter] []
                                (mousePressed [e]
                                  (when (.isPopupTrigger e)
                                    (.show popup-menu (.getComponent e) (.getX e) (.getY e))))
                                (mouseReleased [e]
                                  (when (.isPopupTrigger e)
                                    (.show popup-menu (.getComponent e) (.getX e) (.getY e)))))))
    ;; TODO: make scrollpane fit size of list
    (JScrollPane. list)))