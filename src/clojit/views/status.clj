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
  (when-let [file-changes (git/status repo-path)]
    (let [unstage-menu-item {:name     "Unstage"
                             :on-click (fn [list]
                                         (git/unstage (.getSelectedValuesList list) repo-path)
                                         (view-handler 'status))}
          stage-menu-item {:name     "Stage"
                           :on-click (fn [list]
                                       (git/stage (.getSelectedValues list) repo-path)
                                       (view-handler 'status))}
          discard-changes (fn [list]
                            (git/discard-changes (.getSelectedValues list) repo-path)
                            (view-handler 'status))
          discard-untracked (fn [list]
                              (git/discard-untracked (.getSelectedValues list) repo-path)
                              (view-handler 'status))
          discard-staged (fn [list] ; TODO: it should first unstaged the files and then discard them.
                           )
          staged-files (filter #(= (:state %) 'staged) file-changes)
          unstaged-files (filter #(= (:state %) 'unstaged) file-changes)
          untracked-files (filter #(= (:state %) 'untracked) file-changes)]
      [{:label          (str "Staged (" (count staged-files) ")" ":")
        :files          staged-files
        :menu-item      unstage-menu-item
        :discard-action discard-changes}
       {:label          (str "Unstaged (" (count unstaged-files) ")" ":")
        :files          unstaged-files
        :menu-item      stage-menu-item
        :discard-action discard-changes}
       {:label          (str "Untracked (" (count untracked-files) ")" ":")
        :files          untracked-files
        :menu-item      stage-menu-item
        :discard-action discard-untracked}])))

(defn file-changes [pane repo-path view-handler]
  (doseq [{:keys [label files menu-item discard-action]} (status-file-types repo-path view-handler)]
    (when (seq files)
      (doto pane
        (.add (JLabel. label) "wrap")
        (.add (jlist (map #(str
                             "<html><b>" (:type %) ":</b>"
                             (apply str (repeat (- 10 (count (str (:type %)))) "&ensp;"))
                             (:filename %)
                             "</html>")
                       files)
                :popup-menu-items
                [menu-item
                 {:name "Discard"
                  :on-click discard-action}
                 {:name "Diff"
                  :on-click (fn [list]
                              (view-handler 'diff (first (filter #(= (:filename %) (.getSelectedValue list)) files))))}])
              "wrap, growx")))))

(defn maybe-unmerged-in-tracking-branch
  [pane repo-path]
  (let [tracking         (git/get-tracking-branch repo-path)
        unmerged-commits (git/get-unmerged-into-tracking-branch tracking repo-path)]
    (when unmerged-commits
      (doto pane
        (.add (JLabel. (str "<html>" "Unmerged into <b>" tracking "</b>:" "</html>")) "wrap")
        (.add (jlist (map #(str "<html><b>" (:hash %) "</b> " (:title %) "</html>") (git/get-unmerged-into-tracking-branch tracking repo-path)))
              "wrap, growx")))))

(defn recent-commits [pane repo-path]
  (doto pane
    (.add (JLabel. "Recent commits:") "wrap")
    (.add (jlist (map #(str "<html><b>"(:hash %) "</b> " (:title %) "</html>") (git/get-commits-log repo-path 10)))
          "wrap, growx")))

(defn status-component [pane repo-path view-handler]
  (status-header pane repo-path)
  (file-changes pane repo-path view-handler)
  (maybe-unmerged-in-tracking-branch pane repo-path)
  (recent-commits pane repo-path))

(defn select-repo-message [pane]
  (.add pane (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn status [pane repo-path view-handler]
  (if (git/repository? repo-path)
    (status-component pane repo-path view-handler)
    (select-repo-message pane)))
