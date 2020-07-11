(ns clojit.views.status
  (:require [clojit.git :as git])
  (:import [javax.swing JLabel JList JMenuItem JPopupMenu JScrollPane])
  (:import [java.awt.event ActionListener MouseAdapter]))

(defn configure-files-list [jlist list-content popup-menu]
  (doto jlist
    (.setListData (into-array list-content))
    (.addMouseListener
     (proxy [MouseAdapter] []
       (mousePressed [e]
         (when (.isPopupTrigger e)
           (.show popup-menu (.getComponent e) (.getX e) (.getY e))))
       (mouseReleased [e]
         (when (.isPopupTrigger e)
           (.show popup-menu (.getComponent e) (.getX e) (.getY e))))))))

(defn add-status-header [pane repo-path]
  (.add pane (JLabel. (str "<html>Branch: <b>"
                           (git/get-cur-branch repo-path)
                           "</b> "
                           (git/get-last-commit-message repo-path)
                           "</html>"))
        "wrap"))

(defn get-unstage-menuitem [list repo-path view-handler]
  (doto (JMenuItem. "Unstage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/unstage (.getSelectedValuesList list) repo-path)
                            (view-handler 'status))))))

(defn get-stage-menuitem [list repo-path view-handler]
  (doto (JMenuItem. "Stage")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (git/stage (.getSelectedValuesList list) repo-path)
                            (view-handler 'status))))))

(defn get-status-values-by-type [type repo-path view-handler]
  (let [list (JList.)]
    (case type
      staged {:files (git/get-staged-files repo-path)
              :label (JLabel. "Staged:")
              :list list
              :popup-menu-item (get-unstage-menuitem list repo-path view-handler)}
      modified {:files (git/get-modified-files repo-path)
                :label (JLabel. "Modified:")
                :list list
                :popup-menu-item (get-stage-menuitem list repo-path view-handler)}
      untracked {:files (git/get-untracked-files repo-path)
                 :label (JLabel. "Untracked:")
                 :list list
                 :popup-menu-item (get-stage-menuitem list repo-path view-handler)})))

(defn add-files-pane [pane type repo-path view-handler]
  (let [{:keys [files label list popup-menu-item]} (get-status-values-by-type type repo-path view-handler)]
    (when files
      (let [popup-menu (JPopupMenu.)]
        (doto popup-menu
          (.add popup-menu-item))
        (configure-files-list list files popup-menu)
        (doto pane
          (.add label "wrap")
          (.add (JScrollPane. list) "wrap, growx"))))))

(defn add-status-body [pane repo-path view-handler]
  (doseq [type ['staged 'modified 'untracked]]
    (add-files-pane pane type repo-path view-handler)))

(defn make-git-status-component [pane repo-path view-handler]
  (add-status-header pane repo-path)
  (add-status-body pane repo-path view-handler))

(defn make-config-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn status-view [pane repo-path view-handler]
  (if (git/repository? repo-path)
    (make-git-status-component pane repo-path view-handler)
    (make-config-repo-message pane)))