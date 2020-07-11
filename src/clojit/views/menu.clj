(ns clojit.views.menu
  (:require [clojit.git :as git])
  (:import [javax.swing JFileChooser JMenuItem JMenuBar JMenu])
  (:import [java.awt.event ActionListener]))

(def repo-chooser (doto (JFileChooser.)
                    (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)))

(defn menu-item-open-repository [frame view-handler config-updater]
  (doto (JMenuItem. "Open Repository...")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (when (= (.showOpenDialog repo-chooser frame) JFileChooser/APPROVE_OPTION)
                              ; Save the path of the repo in a config file and initialize git commands
                              (let [repo-path (-> repo-chooser (.getSelectedFile) (.getAbsolutePath))]
                                (if (git/repository? repo-path)
                                  (do (config-updater :repository-path repo-path)
                                      (view-handler 'status))
                                  (println "Directory " repo-path " is not a git repository!")))))))))

(defn menu-item-update-status [view-handler]
  (doto (JMenuItem. "Update status")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e]
                            (view-handler 'status))))))

(defn menu-item-execute-command
  "TODO"
  []
  (doto (JMenuItem. "Execute command")
    (.addActionListener (proxy [ActionListener] []
                          (actionPerformed [e])))))

(defn menu-bar [frame view-handler config-updater]
  (doto (JMenuBar.)
    (.add (doto (JMenu. "Menu")
            (.add (menu-item-open-repository frame view-handler config-updater))
            (.add (menu-item-update-status view-handler))
            (.add (menu-item-execute-command))))))