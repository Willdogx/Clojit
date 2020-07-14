(ns clojit.git
  (:require [clojure.java.shell :refer [sh]])
  (:require [clojure.string :refer [split join split-lines blank? trim]]))

(defn maybe-split-lines [s]
  (when-not (blank? s)
    (split-lines s)))

(defn get-cur-branch [repo-path]
  (:out (sh "git" "symbolic-ref" "--short" "HEAD" :dir repo-path)))

(defn get-last-commit-message [repo-path]
  (:out (sh "git" "log" "-1" "--pretty=%s" :dir repo-path)))

(defn get-commits-log [repo-path limit]
  (let [commits (maybe-split-lines (:out (sh "git" "log" "--oneline" "-n" (str limit) :dir repo-path)))]
    (map #(-> %
              (split #" ")
              ((fn [coll]
                 {:hash  (first coll)
                  :title (join " " (rest coll))})))
      commits)))

(defn get-tracking-branch
  [repo-path]
  ;; TODO: handle case in which there is no tracking branch
  (trim (:out (sh "git" "rev-parse" "--symbolic-full-name" "--abbrev-ref" "@{u}" :dir repo-path))))

(defn get-unmerged-into-tracking-branch
  [tracking repo-path]
  (let [commits  (maybe-split-lines (:out (sh "git" "log" (str tracking ".." "HEAD") "--oneline" :dir repo-path)))]
    (when commits
      (map #(-> %
                (split #" ")
                ((fn [coll]
                   {:hash  (first coll)
                    :title (join " " (rest coll))})))
        commits))))

(defn repository? [path]
  (= 0 (:exit (sh "git" "-C" path "rev-parse"))))

(defn fetch
  [repo-path]
  (sh "git" "fetch" :dir repo-path))

(defn unstage [filenames repo-path]
  (apply sh (flatten ["git" "reset" "--" (seq filenames) :dir repo-path])))

(defn stage [filenames repo-path]
  (apply sh (flatten ["git" "add" (seq filenames) :dir repo-path])))

(defn discard-changes
  [filenames repo-path]
  (apply sh (flatten ["git" "checkout" "--" (seq filenames) :dir repo-path])))

(defn discard-untracked
  [filenames repo-path]
  (apply sh (flatten ["git" "clean" "-f" (seq filenames) :dir repo-path])))

(defn discard
  "TODO"
  [files repo-path]
  )

(defn commit
  [message repo-path]
  (sh "git" "commit" "-m" message :dir repo-path))

(defn status
  [repo-path]
  (let [changes (maybe-split-lines (:out (sh "git" "status" "--short" :dir repo-path)))
        split-changes (partial split-at 2)
        ->strings (partial map (partial apply str))
        parse-changes (fn [state-and-filename]
                        (let [type-of-change #(case (trim %)
                                                "M" 'modified
                                                "A" 'added
                                                "D" 'deleted
                                                'untracked)
                              state-of-change #(case (subs % 0 1)
                                                 " " 'unstaged
                                                 "?" 'untracked
                                                 'staged)]
                          {:type (type-of-change (first state-and-filename))
                           :state (state-of-change (first state-and-filename))
                           :filename (trim (second state-and-filename))}))]
    (when changes
      (map (comp parse-changes ->strings split-changes) changes))))

(defn command
  [repo-path command]
  (:out (apply sh (flatten ["git" (split command #" ") :dir repo-path]))))

(defn diff
  [file repo-path]
  (let [readfile #(slurp (str repo-path "/" (:filename %)))]
    (case (:type file)
      added (readfile file)
      untracked (readfile file)
      modified (:out (sh "git" "diff" (:filename file) :dir repo-path))
      deleted (:out (sh "git" "show" (str "HEAD^:" (:filename file)) :dir repo-path))
      (println file (:type file)))))