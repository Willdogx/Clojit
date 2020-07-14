(ns clojit.views.menu
  (:require [clojit.git :as git])
  (:import [javax.swing JFileChooser JMenuItem JMenuBar JMenu KeyStroke])
  (:import [java.awt.event ActionListener KeyEvent]))

(def repo-chooser (doto (JFileChooser.)
                    (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)))

(defn menu-item-open-repository [frame view-handler config-updater]
  (doto (JMenuItem. "Open Repository...")
    ;; key bindings should be in config
    (.setAccelerator (KeyStroke/getKeyStroke KeyEvent/VK_O KeyEvent/CTRL_DOWN_MASK))
    (.addActionListener (reify ActionListener
                          (actionPerformed [this _]
                            (when (= (.showOpenDialog repo-chooser frame) JFileChooser/APPROVE_OPTION)
                              ; Save the path of the repo in a config file and initialize git commands
                              (let [repo-path (-> repo-chooser (.getSelectedFile) (.getAbsolutePath))]
                                (if (git/repository? repo-path)
                                  (do (config-updater :repository-path repo-path)
                                    (view-handler 'status))
                                  ;; TODO: Make this a dialog
                                  (println "Directory " repo-path " is not a git repository!")))))))))

(defn menu-item-update-status [view-handler]
  (doto (JMenuItem. "Status")
    ;; key bindings should be in config
    (.setAccelerator (KeyStroke/getKeyStroke KeyEvent/VK_S KeyEvent/ALT_DOWN_MASK))
    (.addActionListener (reify ActionListener
                          (actionPerformed [this _]
                            (view-handler 'status))))))

(defn menu-item-execute-command
  "TODO"
  [view-handler]
  (doto (JMenuItem. "Execute command")
    ;; key bindings should be in config
    (.setAccelerator (KeyStroke/getKeyStroke KeyEvent/VK_X KeyEvent/ALT_DOWN_MASK))
    (.addActionListener (reify ActionListener
                          (actionPerformed [this _]
                            (view-handler 'execute-command))))))

(defn commit
  [view-handler]
  (doto (JMenuItem. "Commit")
    ;; key bindings should be in config
    (.setAccelerator (KeyStroke/getKeyStroke KeyEvent/VK_C KeyEvent/ALT_DOWN_MASK))
    (.addActionListener (reify ActionListener
                          ;; TODO: open dialog to input commit title and message
                          (actionPerformed [this _]
                            (view-handler 'commit))))))

(defn menu-bar [frame view-handler config-updater]
  (doto (JMenuBar.)
    (.add (doto (JMenu. "Menu")
            (.add (menu-item-open-repository frame view-handler config-updater))
            (.add (menu-item-update-status view-handler))
            (.add (menu-item-execute-command view-handler))
            (.add (commit view-handler))))))