(ns clojit.components
  (:import [javax.swing JList JMenuItem JPopupMenu BorderFactory JScrollPane])
  (:import [javax.swing.border EtchedBorder])
  (:import [java.awt Dimension])
  (:import [java.awt.event ActionListener MouseAdapter]))

(defn set-sizes
  [list scrollpane pane]
  ;; rethink the design of components. Maybe this should be outside this ns, and handled in the view?
  (.setMaximumSize scrollpane (.getSize list))
  ;; maybe this is not needed
  #_(doto pane
    (.revalidate)
    (.repaint)))

(defn jlist
  "Returns a JList with an optional popupmenu triggered with right-click.
   The popup-menu should be a map
   {:name name shown on menu
    :on-click fn called when clicking the menu item. the list is passed as an argument.}"
  [coll on-render pane & {:keys [popup-menu-items]}]
  (let [list (JList. (into-array coll))
        scrollpane (JScrollPane. list)]
    (when popup-menu-items
      (let [popup-menu (JPopupMenu.)]
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
                                      (.show popup-menu (.getComponent e) (.getX e) (.getY e))))))))
    ;; refactor. This should be a function in core like set-event-listener that already handles the atom.
    (swap! on-render conj #(set-sizes list scrollpane pane))
    scrollpane))