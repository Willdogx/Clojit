(ns clojit.views.commit
  (:require [clojit.git :as git])
  (:import [javax.swing JPanel JLabel JTextArea JButton JScrollPane])
  (:import [java.awt.event ActionListener])
  (:import [java.awt FlowLayout])
  (:import [net.miginfocom.swing MigLayout]))

(defn commit
  [panel view-handler repo-path]
  (.setLayout panel (MigLayout. "" "[grow]"))
  (let [textarea (JTextArea.)]
    (doto panel
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