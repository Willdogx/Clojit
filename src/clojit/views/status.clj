(ns clojit.views.status
  (:require [clojit.git :as git])
  (:require [clojit.components :refer [jlist]])
  (:import [javax.swing JLabel])
  (:import [net.miginfocom.swing MigLayout]))

(defn status-header [panel repo-path]
  (.add panel (JLabel. (str
                        "<html>Branch: <b>"
                        (git/get-cur-branch repo-path)
                        "</b> "
                        (git/get-last-commit-message repo-path)
                        "</html>"))
        "wrap"))

(defn status-file-types [repo-path view-handler]
  (when-let [file-changes (git/status repo-path)]
    (let [unstage-menu-item (fn [changes]
                              {:name "Unstage"
                               :on-click (fn [list]
                                           (git/unstage
                                             (vals (select-keys (vec changes) (.getSelectedIndices list)))
                                             repo-path)
                                           (view-handler 'status))})
          stage-menu-item (fn [changes]
                            {:name "Stage"
                             :on-click (fn [list]
                                         (git/stage
                                           (vals (select-keys (vec changes) (.getSelectedIndices list)))
                                           repo-path)
                                         (view-handler 'status))})
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
        :menu-item      (unstage-menu-item staged-files)
        :discard-action discard-changes}
       {:label          (str "Unstaged (" (count unstaged-files) ")" ":")
        :files          unstaged-files
        :menu-item      (stage-menu-item unstaged-files)
        :discard-action discard-changes}
       {:label          (str "Untracked (" (count untracked-files) ")" ":")
        :files          untracked-files
        :menu-item      (stage-menu-item untracked-files)
        :discard-action discard-untracked}])))

(defn file-changes [panel repo-path view-handler on-render]
  (doseq [{:keys [label files menu-item discard-action]} (status-file-types repo-path view-handler)]
    (when (seq files)
      (doto panel
        (.add (JLabel. label) "wrap")
        (.add (jlist
                (map #(str
                        "<html><b>" (:type %) ":</b>"
                        (apply str (repeat (- 10 (count (str (:type %)))) "&ensp;"))
                        (:filename %)
                        "</html>")
                  files)
                on-render
                panel
                :popup-menu-items
                [menu-item
                 {:name "Discard"
                  :on-click discard-action}
                 {:name "Diff"
                  :on-click (fn [list]
                              (view-handler 'diff (nth files (.getSelectedIndex list))))}])
              "wrap, growx")))))

(defn maybe-unmerged-in-tracking-branch
  [panel repo-path on-render]
  (let [tracking (git/get-tracking-branch repo-path)
        unmerged-commits (git/get-unmerged-into-tracking-branch tracking repo-path)]
    (when unmerged-commits
      (doto panel
        (.add (JLabel. (str "<html>" "Unmerged into <b>" tracking "</b>:" "</html>")) "wrap")
        (.add (jlist (map
                       #(str "<html><b>" (:hash %) "</b> " (:title %) "</html>")
                       (git/get-unmerged-into-tracking-branch tracking repo-path))
                on-render
                panel)
              "wrap, growx")))))

(defn recent-commits [panel repo-path on-render]
  (doto panel
    (.add (JLabel. "Recent commits:") "wrap")
    (.add (jlist (map #(str "<html><b>" (:hash %) "</b> " (:title %) "</html>") (git/get-commits-log repo-path 10))
            on-render
            panel)
          "wrap, growx")))

(defn status-component [panel repo-path view-handler on-render]
  (.setLayout panel (MigLayout. "" "[grow]"))
  (status-header panel repo-path)
  (file-changes panel repo-path view-handler on-render)
  (maybe-unmerged-in-tracking-branch panel repo-path on-render)
  (recent-commits panel repo-path on-render))

(defn select-repo-message [panel]
  (.add panel (doto (JLabel. "Select a repository in Menu -> Open Repository..."))))

(defn status [panel repo-path view-handler on-render]
  (if (git/repository? repo-path)
    (status-component panel repo-path view-handler on-render)
    (select-repo-message panel)))
