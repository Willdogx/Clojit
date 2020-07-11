(ns clojit.views.status
  (:require [clojit.git :as git])
  (:require [clojit.components :refer [jlist]])
  (:import [javax.swing JLabel]))

(defn status-header [pane repo-path]
  (.add pane (JLabel. (str "<html>Branch: <b>"
                           (git/get-cur-branch repo-path)
                           "</b> "
                           (git/get-last-commit-message repo-path)
                           "</html>"))
        "wrap"))

(defn status-file-types [repo-path view-handler]
  (let [unstage (fn [list]
                  (git/unstage (.getSelectedValuesList list) repo-path)
                  (view-handler 'status))
        stage (fn [list]
                (git/stage (.getSelectedValues list) repo-path)
                (view-handler 'status))]
    [{:label "Staged:" :files (git/get-staged-files repo-path) :menu-label "Unstage" :on-click unstage}
     {:label "Modified:" :files (git/get-modified-files repo-path) :menu-label "Stage" :on-click stage}
     {:label "Untracked:" :files (git/get-untracked-files repo-path) :menu-label "Stage" :on-click stage}]))

(defn file-changes [pane repo-path view-handler]
  (doseq [{:keys [label files menu-label on-click]} (status-file-types repo-path view-handler)]
    (when files
      (doto pane
        (.add (JLabel. label) "wrap")
        (.add (jlist files
                     :popup-menu {:name menu-label
                                  :on-click on-click})
              "wrap, growx")))))

(defn recent-commits [pane repo-path]
  (doto pane
    (.add (JLabel. "Recent commits") "wrap")
    (.add (jlist (map #(str (:hash %) " " (:title %)) (git/get-commits-log repo-path 10)))
          "wrap, growx")))

(defn status-component [pane repo-path view-handler]
  (status-header pane repo-path)
  (file-changes pane repo-path view-handler)
  (recent-commits pane repo-path))

(defn select-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn status-view [pane repo-path view-handler]
  (if (git/repository? repo-path)
    (status-component pane repo-path view-handler)
    (select-repo-message pane)))
