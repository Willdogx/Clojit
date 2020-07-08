(ns clojit.git
  (:require [clojure.java.shell :refer [sh]])
  (:require [clojure.string :refer [split-lines blank?]])
  (:require [clojure.string :refer [join]]))


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

(defn repository? [path]
  (= 0 (:exit (sh "git" "-C" path "rev-parse"))))

(defn unstage [filenames repo-path]
  (apply sh (flatten ["git" "reset" "--" (seq filenames) :dir repo-path])))

(defn stage [filenames repo-path]
  (apply sh (flatten ["git" "add" (seq filenames) :dir repo-path])))
