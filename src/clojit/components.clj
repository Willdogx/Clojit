(ns clojit.components
  (:import [javax.swing JList JMenuItem JPopupMenu])
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
  [coll & {:keys [popup-menu]
                     :or {popup-menu nil}}]
  (let [list (JList. (into-array coll))]
    (when-let* [popup-menu popup-menu
                jpopup-menu (JPopupMenu.)]
               (doto jpopup-menu
                 (.add (doto (JMenuItem. (:name popup-menu))
                         (.addActionListener (proxy [ActionListener] []
                                               (actionPerformed [e]
                                                 ((:on-click popup-menu) list)))))))
               (.addMouseListener list (proxy [MouseAdapter] []
                                         (mousePressed [e]
                                           (when (.isPopupTrigger e)
                                             (.show jpopup-menu (.getComponent e) (.getX e) (.getY e))))
                                         (mouseReleased [e]
                                           (when (.isPopupTrigger e)
                                             (.show jpopup-menu (.getComponent e) (.getX e) (.getY e)))))))
    list))