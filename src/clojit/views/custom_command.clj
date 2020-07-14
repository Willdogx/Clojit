(ns clojit.views.custom-command
  (:require [clojit.git :as git])
  (:import [javax.swing JTextField JTextArea JScrollPane])
  (:import [java.awt BorderLayout])
  (:import [java.awt.event ActionListener]))

(defn execute-command-view
  "TODO"
  [pane repo-path]
  (let [output-text (JTextArea.)
        input (JTextField.)]
    (.setEditable output-text false)
    (.addActionListener input (reify ActionListener
                                (actionPerformed [this _]
                                  (.setText output-text (git/command repo-path (.getText input)))
                                  (.setText input ""))))
    (.setLayout pane (BorderLayout.))
    (.add pane (JScrollPane. output-text))
    (.add pane input BorderLayout/PAGE_END)
    (.requestFocusInWindow input)))