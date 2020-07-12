(ns clojit.views.commit
  (:require [clojit.git :as git])
  (:import [javax.swing JPanel JLabel JTextArea JButton JScrollPane])
  (:import [java.awt.event ActionListener])
  (:import [java.awt FlowLayout]))

(defn commit
  [pane view-handler repo-path]
  (let [textarea (JTextArea.)]
    (doto pane
      (.add (JLabel. "Commit message:") "wrap")
      (.add (JScrollPane. textarea) "wrap, grow, height :200:")
      (.add (doto (JPanel.)
              (.setLayout (FlowLayout.))
              (.add (doto (JButton. "Commit")
                      (.addActionListener (reify ActionListener
                                            (actionPerformed [this _]
                                              (git/commit (.getText textarea) repo-path)
                                              (view-handler 'status))))))
              (.add (doto (JButton. "Cancel")
                      (.addActionListener (reify ActionListener
                                            (actionPerformed [this _]
                                              (view-handler 'status)))))))
            "wrap"))))