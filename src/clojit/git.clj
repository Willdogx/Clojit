(ns clojit.git
  (:require [clojure.java.shell :refer [sh]])
  (:require [clojure.string :refer [split join split-lines blank? trim]]))

(defn maybe-split-lines [s]
  (when-not (blank? s)
    (split-lines s)))

(defn get-staged-files [repo-path]
  (maybe-split-lines (:out (sh "git" "diff" "--name-only" "--cached" :dir repo-path))))

(defn get-untracked-files [repo-path]
  (maybe-split-lines (:out (sh "git" "ls-files" "--others" "--exclude-standard" :dir repo-path))))

(defn get-modified-files [repo-path]
  (maybe-split-lines (:out (sh "git" "ls-files" "-m" :dir repo-path))))

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

(defn commit
  [message repo-path]
  (sh "git" "commit" "-m" message :dir repo-path))

(defn command
  [repo-path command]
  (:out (apply sh (flatten ["git" (split command #" ") :dir repo-path]))))