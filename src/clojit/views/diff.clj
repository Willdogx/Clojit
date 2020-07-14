(ns clojit.views.diff
  (:require [clojit.git :as git])
  (:require [clojure.string :as s])
  (:import [javax.swing JEditorPane JScrollPane])
  (:import [java.awt BorderLayout]))

(defn color-removed [line]
  (str "<p style=\"background-color: #fee8e9; margin: 0;\">" line "</p>"))

(defn color-added [line]
  (str "<p style=\"background-color: #ddffdd; margin: 0;\">" line "</p>"))

(defn color [line]
  (case (subs line 0 1)
    "+" (color-added line)
    "-" (color-removed line)
    (str "<p style=\"margin: 0;\">" line "</p>")))

(defn spaces->htmltags [line]
  (s/replace line #" " "&ensp;"))

(defn diff
  [pane repo-path view-handler file]
  (let [diff (git/diff file repo-path)
        editorpane (JEditorPane.)]
    (.setLayout pane (BorderLayout.))
    (doto editorpane
      (.setEditable false)
      (.setContentType "text/html")
      (.setText (case (:type file)
                  modified (apply str (map (comp color spaces->htmltags) (s/split-lines diff)))
                  deleted (apply str (map (comp color-removed spaces->htmltags) (s/split-lines diff)))
                  (apply str (map (comp color-added spaces->htmltags) (s/split-lines diff))))))
    (.add pane (JScrollPane. editorpane))))