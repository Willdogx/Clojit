(ns clojit.views.status
  (:require [clojit.git :as git])
  (:require [clojit.components :refer [jlist]])
  (:import [javax.swing JLabel]))

(defn status-header [pane repo-path]
  (.add pane (JLabel. (str
                        "<html>Branch: <b>"
                        (git/get-cur-branch repo-path)
                        "</b> "
                        (git/get-last-commit-message repo-path)
                        "</html>"))
        "wrap"))

(defn status-file-types [repo-path view-handler]
  (let [unstage-menu-item {:name     "Unstage"
                           :on-click (fn [list]
                                       (git/unstage (.getSelectedValuesList list) repo-path)
                                       (view-handler 'status))}
        stage-menu-item   {:name     "Stage"
                           :on-click (fn [list]
                                       (git/stage (.getSelectedValues list) repo-path)
                                       (view-handler 'status))}
        discard-changes   (fn [list]
                            (git/discard-changes (.getSelectedValues list) repo-path)
                            (view-handler 'status))
        discard-untracked (fn [list]
                            (git/discard-untracked (.getSelectedValues list) repo-path)
                            (view-handler 'status))
        discard-staged    (fn [list] ; TODO: it should first unstaged the files and then discard them.
                            )]
    [{:label          "Staged:"
      :files          (git/get-staged-files repo-path)
      :menu-item      unstage-menu-item
      :discard-action discard-changes}
     {:label          "Unstaged:"
      :files          (git/get-modified-files repo-path)
      :menu-item      stage-menu-item
      :discard-action discard-changes}
     {:label          "Untracked:"
      :files          (git/get-untracked-files repo-path)
      :menu-item      stage-menu-item
      :discard-action discard-untracked}]))

(defn file-changes [pane repo-path view-handler]
  (doseq [{:keys [label files menu-item discard-action]} (status-file-types repo-path view-handler)]
    (when files
      (doto pane
        (.add (JLabel. label) "wrap")
        (.add (jlist files
                     :popup-menu-items [menu-item
                                        {:name "Discard"
                                         :on-click discard-action}])
              "wrap, growx")))))

(defn recent-commits [pane repo-path]
  (doto pane
    (.add (JLabel. "Recent commits:") "wrap")
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
